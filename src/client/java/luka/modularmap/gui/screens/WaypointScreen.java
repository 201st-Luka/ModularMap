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

import luka.modularmap.event.KeyInputHandler;
import net.minecraft.client.gui.screen.Screen;

public class WaypointScreen extends MenuScreen {
    public WaypointScreen() {
        super("Waypoints");
    }

    public WaypointScreen(Screen parent) {
        super("Waypoints", parent);
    }

    @Override
    protected void init() {

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert client != null;
        assert client.player != null;

        if (KeyInputHandler.openWaypointsKeyBinding.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }

        if (KeyInputHandler.openMapKeyBinding.matchesKey(keyCode, scanCode) && _parent instanceof MapScreen) {
            client.player.closeHandledScreen();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
