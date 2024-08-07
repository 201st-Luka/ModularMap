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

import luka.modularmap.map.MapManager;
import luka.modularmap.util.IModularMapClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;

public class ClientChunkEventHandler {
    public static void onChunkLoad(ClientWorld world, Chunk chunk) {
        IModularMapClient client = (IModularMapClient) MinecraftClient.getInstance();
        MapManager mapManager = client.modularMap$getChunkManager();
        mapManager.loadChunk(chunk);
    }

    public static void onChunkUnload(ClientWorld world, Chunk chunk) {
        IModularMapClient client = (IModularMapClient) MinecraftClient.getInstance();
        MapManager mapManager = client.modularMap$getChunkManager();
        mapManager.unloadChunk(chunk);
    }

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(ClientChunkEventHandler::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(ClientChunkEventHandler::onChunkUnload);
    }
}
