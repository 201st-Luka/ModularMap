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

import com.google.common.collect.Lists;
import luka.modularmap.util.IModularMapClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractScreen extends Screen {
    protected final Screen _parent;
    protected final List<Drawable> drawables = Lists.newArrayList();
    protected IModularMapClient _modularMapClient = (IModularMapClient) MinecraftClient.getInstance();

    public static final int FRAME_SPACING = 6,
            PADDING = 6,
            BUTTON_SIZE = 24,
            TEXT_WIDTH = 120,
            TEXT_HEIGHT = 24;

    public AbstractScreen(String title, Screen parent) {
        super(Text.literal(title));
        _parent = parent;
    }

    public AbstractScreen(String title) {
        this(title, null);
    }

    protected boolean isLeftClickButton(int button) {
        return button == 0;
    }

    protected boolean isRightClickButton(int button) {
        return button == 1;
    }

    protected boolean isMiddleClickButton(int button) {
        return button == 2;
    }

    @Override
    protected abstract void init();

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        drawables.add(drawableElement);
        return addSelectableChild(drawableElement);
    }

    @Override
    protected <T extends Drawable> T addDrawable(T drawable) {
        drawables.add(drawable);
        return drawable;
    }

    @Override
    public abstract void render(@NotNull DrawContext context, int mouseX, int mouseY, float delta);

    @Override
    public void close() {
        client.setScreen(_parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
