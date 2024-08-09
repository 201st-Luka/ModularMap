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

package luka.modularmap;

import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.ClientChunkEventHandler;
import luka.modularmap.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModularMapClient implements ClientModInitializer {
    public static final String MOD_ID = "modularmap";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "_client");

    @Override
    public void onInitializeClient() {
        // load config
        ConfigManager.loadConfig();

        // Register event handlers
        KeyInputHandler.register();
        ClientChunkEventHandler.register();

        // finished
        LOGGER.info("ModularMap clientside initialized!");
    }
}