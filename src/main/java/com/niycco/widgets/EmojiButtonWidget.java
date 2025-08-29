package com.niycco.widgets;

import com.niycco.interfaces.EmojiInsertable;
import com.niycco.utils.EmojiRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class EmojiButtonWidget extends ClickableWidget {

    private boolean outlined = false;
    private int buttonColor = 0xA0000000;
    private int buttonColorActive = 0xA0202020;

    public EmojiButtonWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        this.setTooltip(Tooltip.of(Text.of(EmojiRegistry.snippets.get(message.getString()))));
    }

    public void setOutlined(boolean outlined) { this.outlined = outlined;}

    public boolean isOutlined() { return outlined; }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int textColor = (this.isHovered() || outlined) ? 0xFFFFFFFF : 0xFFA0A0A0;

        context.drawText(textRenderer, this.getMessage(), this.getX(), this.getY() + 4, textColor, false);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (MinecraftClient.getInstance().currentScreen instanceof EmojiInsertable emojiInsertable) {
            emojiInsertable.insertEmoji(":" + EmojiRegistry.snippets.get(this.getMessage().getString()) + ":");
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

}
