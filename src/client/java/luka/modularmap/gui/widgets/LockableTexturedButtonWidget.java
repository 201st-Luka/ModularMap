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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LockableTexturedButtonWidget extends TexturedButtonWidget {
    private boolean _locked = false;

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction) {
        super(x, y, width, height, textures, pressAction);
    }

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, Text text) {
        super(x, y, width, height, textures, pressAction, text);
    }

    public LockableTexturedButtonWidget(int width, int height, ButtonTextures textures, PressAction pressAction, Text text) {
        super(width, height, textures, pressAction, text);
    }

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures,
                                        PressAction pressAction, boolean locked) {
        this(x, y, width, height, textures, pressAction);

        this._locked = locked;
    }

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures,
                                        PressAction pressAction, Text text, boolean locked) {
        this(x, y, width, height, textures, pressAction, text);

        this._locked = locked;
    }

    public LockableTexturedButtonWidget(int width, int height, ButtonTextures textures, PressAction pressAction,
                                        Text text, boolean locked) {
        this(width, height, textures, pressAction, text);

        this._locked = locked;
    }

    public void setLocked(boolean locked) {
        this._locked = locked;
    }

    public void toggleLocked() {
        _locked = !_locked;
    }

    public void lock() {
        _locked = true;
    }

    public void unlock() {
        _locked = false;
    }

    public boolean isLocked() {
        return _locked;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier id = textures.get(_locked, isSelected());
        context.drawGuiTexture(id, getX(), getY(), width, height);
    }
}
