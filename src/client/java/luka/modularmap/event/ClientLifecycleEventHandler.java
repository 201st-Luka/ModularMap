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

import luka.modularmap.world.ChunkProcessingThread;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class ClientLifecycleEventHandler {
    public static void register() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ChunkProcessingThread.startProcessing());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ChunkProcessingThread.stopProcessing());
    }
}
