package luka.modularmap.gui.widgets;

import luka.modularmap.config.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;

public class ColorDisplayWidget extends ClickableWidget {
    private Color color;

    public ColorDisplayWidget(int x, int y, int width, int height, Color color) {
        super(x, y, width, height, null);

        this.color = color;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0xFFFFFFFF);
        context.fill(getX(), getY(), getX() + width, getY() + height, color.getValue());
        context.drawBorder(getX(), getY(), width, height, 0xFF000000);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
