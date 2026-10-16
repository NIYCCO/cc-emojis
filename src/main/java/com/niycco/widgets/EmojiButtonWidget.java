package com.niycco.widgets;

import com.niycco.interfaces.EmojiInsertable;
import com.niycco.utils.EmojiRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class EmojiButtonWidget extends AbstractWidget {

    private boolean outlined = false;
    private int buttonColor = 0xA0000000;
    private int buttonColorActive = 0xA0202020;

    public EmojiButtonWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.setTooltip(Tooltip.create(Component.literal(EmojiRegistry.snippets.get(message.getString()))));
    }

    public void setOutlined(boolean outlined) { this.outlined = outlined;}

    public boolean isOutlined() { return outlined; }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        Font textRenderer = Minecraft.getInstance().font;

        int textColor = (this.isHovered() || outlined) ? 0xFFFFFFFF : 0xFFA0A0A0;

        context.text(textRenderer, this.getMessage(), this.getX(), this.getY() + 4, textColor, false);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (Minecraft.getInstance().screen instanceof EmojiInsertable emojiInsertable) {
            emojiInsertable.insertEmoji(":" + EmojiRegistry.snippets.get(this.getMessage().getString()) + ":");
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

}
