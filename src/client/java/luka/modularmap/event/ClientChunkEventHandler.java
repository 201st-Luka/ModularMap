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

import luka.modularmap.util.IModularMapClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

public class ClientChunkEventHandler {
    private static void onChunkLoad(@NotNull ClientWorld world, @NotNull Chunk chunk) {
        var modularMapClient = (IModularMapClient) MinecraftClient.getInstance();

        modularMapClient.modularMap$getWorldMap().addChunk(chunk);
    }

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(ClientChunkEventHandler::onChunkLoad);
    }
}
