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

package luka.modularmap.mixin.client;

import luka.modularmap.map.MapManager;
import luka.modularmap.util.IModularMapClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class ModularMapClientMixin implements IModularMapClient {
    @Unique
    private MapManager mapManager;

    @Override
    public MapManager modularMap$getMapManager() {
        return mapManager;
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    protected void onWorldJoin(CallbackInfo ci) {
        mapManager = new MapManager();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("TAIL"))
    protected void onWorldLeave(CallbackInfo ci) {
        mapManager = null;
    }
}