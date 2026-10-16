package com.niycco.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class EmojiSelectionPanel extends AbstractContainerWidget {

    private final List<AbstractWidget> children = new ArrayList<>();

    public EmojiSelectionPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty(), defaultSettings(0));
        this.visible = false;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void toggleVisible() {
        this.visible = !this.visible;
    }

    public void addChildren(AbstractWidget element) {
        this.children.add(element);
    }

    @Override
    public List<AbstractWidget> children() {
        return this.children;
    }

    public void clearElements() {
        this.children.clear();
    }

    @Override
    public void setX(int x) {
        for (AbstractWidget child : children) {
            child.setX(child.getX() - this.getX() + x);
        }
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        for (AbstractWidget child : children) {
            child.setY(child.getY() - this.getY() + y);
        }
        super.setY(y);
    }

    @Override
    protected int contentHeight() {
        return height;
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {

        drawContext.pose().pushMatrix();
        drawContext.pose().translate(0.0f, 0.0f);
        drawContext.fill(this.getX(), this.getY() + 12 + 1 + 1, this.getX() + width, this.getY() + height, Minecraft.getInstance().options.getBackgroundColor(Integer.MIN_VALUE));
        drawContext.pose().popMatrix();

        for (GuiEventListener child : this.children) {
            if(child instanceof Renderable drawable) drawable.extractRenderState(drawContext, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
