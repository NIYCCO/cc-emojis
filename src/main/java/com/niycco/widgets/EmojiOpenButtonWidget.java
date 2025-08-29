package com.niycco.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

public class EmojiOpenButtonWidget extends TextIconButtonWidget.IconOnly {
    private boolean outlined = false;
    private int buttonColor = 0xA0000000;
    private int buttonColorActive = 0xA0202020;

    public EmojiOpenButtonWidget(int width, int height, Text message, int textureWidth, int textureHeight, Identifier texture, PressAction pressAction, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(width, height, message, textureWidth, textureHeight, texture, pressAction, narrationSupplier);
    }

    protected int getBackgroundColor() {
        return this.isHovered() ? buttonColorActive : buttonColor;
    }

    public void setOutlined(boolean outlined) { this.outlined = outlined;}

    public boolean isOutlined() { return outlined; }

    public void drawOutline(DrawContext drawContext, int color) {
        int alphaColor = ColorHelper.withAlpha((int) (this.alpha*255), color);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY(), alphaColor);
        drawContext.drawVerticalLine(this.getX()-1, this.getY()-1, this.getY()+height, alphaColor);
        drawContext.drawHorizontalLine(this.getX()-1, this.getX()+width, this.getY()+height-1, alphaColor);
        drawContext.drawVerticalLine(this.getX()+width, this.getY()-1, this.getY()+height, alphaColor);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta){
        if(outlined) drawOutline(context, 0xFFFFFFFF);
        int textureX = this.getX() + this.getWidth() / 2 - this.textureWidth / 2;
        int textureY = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
        int textColor = (this.isHovered() || outlined) ? 0xFFFFFFFF : 0xFFA0A0A0;
        if(texture != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, textureX, textureY, this.textureWidth, this.textureHeight, ColorHelper.withAlpha((int) (this.alpha*255), textColor));
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.drawMessage(context, minecraftClient.textRenderer, textColor);
    }

    public interface PressAction extends ButtonWidget.PressAction {
        void onPress(EmojiOpenButtonWidget button);
        @Override
        default void onPress(ButtonWidget button) {
            if(button instanceof EmojiOpenButtonWidget emojiOpenButtonWidget) this.onPress(emojiOpenButtonWidget);
        }
    }
}
