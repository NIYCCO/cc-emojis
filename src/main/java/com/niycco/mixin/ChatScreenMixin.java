package com.niycco.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.niycco.CCEmojis;
import com.niycco.interfaces.EmojiInsertable;
import com.niycco.utils.EmojiRegistry;
import com.niycco.widgets.EmojiButtonWidget;
import com.niycco.widgets.EmojiOpenButtonWidget;
import com.niycco.widgets.EmojiSelectionPanel;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements EmojiInsertable {

    @Unique private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CCEmojis.MOD_ID, "emoji_button");

    @Shadow protected EditBox input;

    @Unique private static final int TEXT_BOX_CURSOR_WIDTH = 8;

    @Unique
    EmojiOpenButtonWidget emojiOpenButton;

    @Unique
    EmojiSelectionPanel emojiSelectionPanel;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Unique
    public void addEmojiComponents() {
        int padding = 2;

        int emojiButtonX = this.width - padding - 12;
        int emojiButtonY = this.height - padding - 12;

        int panelHeight = 200;
        int panelWidth = 120;

        int panelX = this.width - panelWidth - padding;
        int panelY = emojiButtonY - padding - panelHeight;

        emojiSelectionPanel = new EmojiSelectionPanel(panelX, panelY, panelWidth, panelHeight);
        this.addRenderableWidget(emojiSelectionPanel);

        GridLayout grid = new GridLayout();
        grid.spacing(2);
        GridLayout.RowHelper adder = grid.createRowHelper(7);

        for (String emoji : EmojiRegistry.snippets.keySet()) {
            EmojiButtonWidget button = new EmojiButtonWidget(0, 0, 15, 15, Component.literal(emoji));
            adder.addChild(button);
        }

        grid.setX(panelX + padding);
        grid.setY(panelY + 12 + padding);
        grid.arrangeElements();

        grid.visitWidgets(emojiSelectionPanel::addChildren);

        emojiOpenButton = new EmojiOpenButtonWidget(12, 12, CommonComponents.EMPTY, 12, 12, TEXTURE, button -> {
            button.setOutlined(!button.isOutlined());
            emojiSelectionPanel.toggleVisible();
        }, null);

        emojiOpenButton.setPosition(emojiButtonX, emojiButtonY);
        this.addRenderableWidget(emojiOpenButton);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        addEmojiComponents();
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/MouseButtonEvent;button()I", ordinal = 0), cancellable = true)
    private void callSuperMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if(super.mouseClicked(event, doubleClick)) cir.setReturnValue(true);
        else this.setFocused(input);

    }

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"))
    public boolean skipArrowKeys(ChatScreen instance, KeyEvent event, Operation<Boolean> original) {
        int keyCode = event.key();
        return ((keyCode != GLFW.GLFW_KEY_UP && keyCode != GLFW.GLFW_KEY_DOWN) || !this.input.isFocused()) && original.call(this, event);
    }

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        int emojiButtonWidth = 12 + 2;
        return original + TEXT_BOX_CURSOR_WIDTH + emojiButtonWidth + 2;
    }

    @ModifyArgs(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void fillBackgroundAtTextBox(Args args) {
        args.set(0, this.input.getX() - 2);
        args.set(1, this.input.getY() - 2);
        args.set(2, this.input.getRight() + TEXT_BOX_CURSOR_WIDTH);
        args.set(3, this.input.getBottom() - 2);
    }

    @Override
    public void insertEmoji(String emoji) {
        this.input.insertText(emoji);
        this.minecraft.schedule(() -> {
            if(minecraft.screen == this) this.setFocused(this.input);
        });
    }

}
