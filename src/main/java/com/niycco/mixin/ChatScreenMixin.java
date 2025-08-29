package com.niycco.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.niycco.CCEmojis;
import com.niycco.interfaces.EmojiInsertable;
import com.niycco.utils.EmojiRegistry;
import com.niycco.widgets.EmojiButtonWidget;
import com.niycco.widgets.EmojiOpenButtonWidget;
import com.niycco.widgets.EmojiSelectionPanel;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

    @Unique private static final Identifier TEXTURE = Identifier.of(CCEmojis.MOD_ID, "emoji_button");

    @Shadow protected TextFieldWidget chatField;

    @Unique private static final int TEXT_BOX_CURSOR_WIDTH = 8;

    @Unique
    EmojiOpenButtonWidget emojiOpenButton;

    @Unique
    EmojiSelectionPanel emojiSelectionPanel;

    protected ChatScreenMixin(Text title) {
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
        this.addDrawableChild(emojiSelectionPanel);

        GridWidget grid = new GridWidget();
        grid.setSpacing(2);
        GridWidget.Adder adder = grid.createAdder(7);

        for (String emoji : EmojiRegistry.snippets.keySet()) {
            EmojiButtonWidget button = new EmojiButtonWidget(0, 0, 15, 15, Text.of(emoji));
            adder.add(button);
        }

        grid.setX(panelX + padding);
        grid.setY(panelY + 12 + padding);
        grid.refreshPositions();

        grid.forEachChild(emojiSelectionPanel::addChildren);

        emojiOpenButton = new EmojiOpenButtonWidget(12, 12, ScreenTexts.EMPTY, 12, 12, TEXTURE, button -> {
            button.setOutlined(!button.isOutlined());
            emojiSelectionPanel.toggleVisible();
        }, null);

        emojiOpenButton.setPosition(emojiButtonX, emojiButtonY);
        this.addDrawableChild(emojiOpenButton);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        addEmojiComponents();
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;mouseClicked(DDI)Z"), cancellable = true)
    private void callSuperMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if(super.mouseClicked(mouseX, mouseY, button)) cir.setReturnValue(true);
        else this.setFocused(chatField);

    }

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"))
    public boolean skipArrowKeys(ChatScreen instance, int keyCode, int scanCode, int modifiers, Operation<Boolean> original) {
        return ((keyCode != GLFW.GLFW_KEY_UP && keyCode != GLFW.GLFW_KEY_DOWN) || !this.chatField.isFocused()) && original.call(this, keyCode, scanCode, modifiers);
    }

    @ModifyConstant(method = "init",constant = @Constant(intValue = 4, ordinal = 1),require = 1)
    private int changeTextBoxWidth(int original) {
        int emojiButtonWidth = 12 + 2;
        return original + TEXT_BOX_CURSOR_WIDTH + emojiButtonWidth + 2;
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void fillBackgroundAtTextBox(Args args) {
        args.set(0, this.chatField.getX() - 2);
        args.set(1, this.chatField.getY() - 2);
        args.set(2, this.chatField.getRight() + TEXT_BOX_CURSOR_WIDTH);
        args.set(3, this.chatField.getBottom() - 2);
    }

    @Override
    public void insertEmoji(String emoji) {
        this.chatField.write(emoji);
        if(client != null) this.client.send( () -> {
            if(client.currentScreen == this) this.setFocused(this.chatField);
        });
    }

}
