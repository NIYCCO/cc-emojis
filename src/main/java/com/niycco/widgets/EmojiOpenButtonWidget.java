package com.niycco.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

public class EmojiOpenButtonWidget extends SpriteIconButton.CenteredIcon {
    private boolean outlined = false;
    private int buttonColor = 0xA0000000;
    private int buttonColorActive = 0xA0202020;

    public EmojiOpenButtonWidget(int width, int height, Component message, int textureWidth, int textureHeight, Identifier texture, PressAction pressAction, @Nullable Button.CreateNarration narrationSupplier) {
        super(width, height, message, textureWidth, textureHeight, new WidgetSprites(texture), pressAction, null, narrationSupplier != null ? narrationSupplier : DEFAULT_NARRATION);
    }

    protected int getBackgroundColor() {
        return this.isHovered() ? buttonColorActive : buttonColor;
    }

    public void setOutlined(boolean outlined) { this.outlined = outlined;}

    public boolean isOutlined() { return outlined; }

    public void drawOutline(GuiGraphicsExtractor drawContext, int color) {
        int alphaColor = ARGB.color((int) (this.alpha*255), color);
        drawContext.horizontalLine(this.getX()-1, this.getX()+width, this.getY(), alphaColor);
        drawContext.verticalLine(this.getX()-1, this.getY()-1, this.getY()+height, alphaColor);
        drawContext.horizontalLine(this.getX()-1, this.getX()+width, this.getY()+height-1, alphaColor);
        drawContext.verticalLine(this.getX()+width, this.getY()-1, this.getY()+height, alphaColor);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta){
        if(outlined) drawOutline(context, 0xFFFFFFFF);
        int textureX = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
        int textureY = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
        int textColor = (this.isHovered() || outlined) ? 0xFFFFFFFF : 0xFFA0A0A0;
        if(sprite != null) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite.enabled(), textureX, textureY, this.spriteWidth, this.spriteHeight, ARGB.color((int) (this.alpha*255), textColor));
        }
    }

    public interface PressAction extends Button.OnPress {
        void onPress(EmojiOpenButtonWidget button);
        @Override
        default void onPress(Button button) {
            if(button instanceof EmojiOpenButtonWidget emojiOpenButtonWidget) this.onPress(emojiOpenButtonWidget);
        }
    }
}
