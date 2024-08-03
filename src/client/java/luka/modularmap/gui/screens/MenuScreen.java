package luka.modularmap.gui.screens;

import luka.modularmap.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;

public class MenuScreen extends BaseScreen {
    public static final int PADDING = 5,
            TEXT_WIDTH = 100,
            TEXT_HEIGHT = 20;

    public MenuScreen(String title, Screen parent) {
        super(title, parent);
    }

    public MenuScreen(String title) {
        this(title, null);
    }

    protected int calculateHeaderHeight() {
        return height / 10;
    }

    protected int calculateContentHeight() {
        return height - calculateHeaderHeight() - calculateFooterHeight();
    }

    protected int calculateFooterHeight() {
        return height / 10;
    }

    protected int calculateSpacingWidth() {
        return width / 8;
    }

    protected int calculateColumnWidth() {
        return width - calculateSpacingWidth() * 2;
    }

    protected int calculateTitleTextX() {
        return width / 2 - textRenderer.getWidth(title) / 2;
    }

    protected int calculateTitleTextY() {
        return calculateHeaderHeight() / 2 - TEXT_HEIGHT / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int headerHeight = calculateHeaderHeight(),
                contentHeight = calculateContentHeight();

        renderBackground(context, mouseX, mouseY, delta);

        // header
        context.fill(0, 0, width, headerHeight, ConfigManager.getConfig().headerColor);
        context.drawText(
                textRenderer,
                title,
                calculateTitleTextX(), calculateTitleTextY(),
                ConfigManager.getConfig().headerTextColor,
                true
        );
        // background
        context.fill(0, headerHeight, width, headerHeight + contentHeight, ConfigManager.getConfig().backgroundColor);
        // footer
        context.fill(0, headerHeight + contentHeight, width, height, ConfigManager.getConfig().footerColor);

        for (Drawable drawable : drawables)
            drawable.render(context, mouseX, mouseY, delta);
    }
}
