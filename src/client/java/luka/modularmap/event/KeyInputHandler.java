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

package luka.modularmap.event;

import luka.modularmap.gui.screens.MapScreen;
import luka.modularmap.gui.screens.WaypointScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "key.category.modularmap";
    public static final String KEY_OPEN_MAP = "key.category.modularmap.open_map";
    public static final String KEY_OPEN_WAYPOINTS = "key.category.modularmap.open_waypoints";

    public static KeyBinding openMapKey;
    public static KeyBinding openWaypointsKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMapKey.wasPressed()) {
                client.setScreen(new MapScreen());
            }
            if (openWaypointsKey.wasPressed()) {
                client.setScreen(new WaypointScreen());
            }
        });
    }

    public static void register() {
        openMapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_MAP,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                KEY_CATEGORY
        ));
        openWaypointsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_WAYPOINTS,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                KEY_CATEGORY
        ));

        registerKeyInputs();
    }
}
