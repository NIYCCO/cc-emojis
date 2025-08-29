package com.niycco.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class EmojiSelectionPanel extends ContainerWidget {

    private final List<ClickableWidget> children = new ArrayList<>();

    public EmojiSelectionPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.visible = false;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void toggleVisible() {
        this.visible = !this.visible;
    }

    public void addChildren(ClickableWidget element) {
        this.children.add(element);
    }

    @Override
    public List<ClickableWidget> children() {
        return this.children;
    }

    public void clearElements() {
        this.children.clear();
    }

    @Override
    public void setX(int x) {
        for (ClickableWidget child : children) {
            child.setX(child.getX() - this.getX() + x);
        }
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        for (ClickableWidget child : children) {
            child.setY(child.getY() - this.getY() + y);
        }
        super.setY(y);
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return height;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 0;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {

        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(0.0f, 0.0f);
        drawContext.fill(this.getX(), this.getY() + 12 + 1 + 1, this.getX() + width, this.getY() + height, MinecraftClient.getInstance().options.getTextBackgroundColor(Integer.MIN_VALUE));
        drawContext.getMatrices().popMatrix();

        for (Element child : this.children) {
            if(child instanceof Drawable drawable) drawable.render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
