/*
 * ModularMap
 * Copyright (c) 2024 201st-Luka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package luka.modularmap.gui.screens;

import luka.modularmap.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

public class MenuScreen extends AbstractScreen {
    public MenuScreen(String title, Screen parent) {
        super(title, parent);
    }

    public MenuScreen(String title) {
        this(title, null);
    }

    @Override
    protected void init() {
        // todo: add waypoints and waypoint menu
    }

    protected int calculateHeaderHeight() {
        return Math.max(height / 10, textRenderer.fontHeight + 4);
    }

    protected int calculateContentHeight() {
        return height - calculateHeaderHeight() - calculateFooterHeight();
    }

    protected int calculateFooterHeight() {
        return Math.max(height / 10, TEXT_HEIGHT + PADDING * 2);
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
        return calculateHeaderHeight() / 2 - textRenderer.fontHeight / 2;
    }

    @Override
    public void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta) {
        int headerHeight = calculateHeaderHeight(),
                contentHeight = calculateContentHeight();

        renderBackground(context, mouseX, mouseY, delta);

        // header
        context.fill(0, 0, width, headerHeight, ConfigManager.getConfig().headerColor.getValue());
        context.drawText(
                textRenderer,
                title,
                calculateTitleTextX(), calculateTitleTextY(),
                ConfigManager.getConfig().headerTextColor.getValue(),
                true
        );
        // background
        context.fill(0, headerHeight, width, headerHeight + contentHeight,
                ConfigManager.getConfig().backgroundColor.getValue());
        // footer
        context.fill(0, headerHeight + contentHeight, width, height, ConfigManager.getConfig().footerColor.getValue());

        for (Drawable drawable : drawables)
            drawable.render(context, mouseX, mouseY, delta);
    }
}
