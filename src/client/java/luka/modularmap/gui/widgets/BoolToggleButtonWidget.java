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

package luka.modularmap.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class BoolToggleButtonWidget extends TexturedButtonWidget {
    protected boolean _state = false;

    public BoolToggleButtonWidget(final int x, final int y,
                                  final int width, final int height,
                                  @NotNull final ButtonTextures textures) {
        this(x, y, width, height, textures, ScreenTexts.EMPTY);
    }

    public BoolToggleButtonWidget(final int x, final int y,
                                  final int width, final int height,
                                  @NotNull final ButtonTextures textures, @NotNull final Text text) {
        super(x, y, width, height, textures, button -> ((BoolToggleButtonWidget) button).toggle(), text);
    }

    public BoolToggleButtonWidget(final int width, final int height,
                                  @NotNull final ButtonTextures textures, @NotNull final Text text) {
        this(0, 0, width, height, textures, text);
    }

    public void toggle() {
        _state = !_state;
    }

    public boolean getState() {
        return _state;
    }

    @Override
    public void renderWidget(@NotNull final DrawContext context,
                             final int mouseX, final int mouseY,
                             final float delta) {
        Identifier id = textures.get(true, _state);
        context.drawGuiTexture(id, getX(), getY(), width, height);
    }
}
