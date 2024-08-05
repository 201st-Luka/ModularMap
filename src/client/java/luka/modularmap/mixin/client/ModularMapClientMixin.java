package luka.modularmap.mixin.client;

import luka.modularmap.map.ChunkManager;
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
    private ChunkManager chunkManager;

    @Override
    public ChunkManager modularMap$getChunkManager() {
        return chunkManager;
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    protected void onWorldJoin(CallbackInfo ci) {
        chunkManager = new ChunkManager();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("TAIL"))
    protected void onWorldLeave(CallbackInfo ci) {
        chunkManager = null;
    }
}