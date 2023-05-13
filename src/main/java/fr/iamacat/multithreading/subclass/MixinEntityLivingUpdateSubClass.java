package fr.iamacat.multithreading.subclass;

import fr.iamacat.multithreading.mixins.common.core.MixinEntityLivingUpdate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


public class MixinEntityLivingUpdateSubClass extends MixinEntityLivingUpdate {
    @Override
    public float getHealth() {
        return 0;
    }

    public MixinEntityLivingUpdateSubClass(EntityLivingBase entity, WorldServer world, IChunkProvider chunkProvider) {
        super(entity, world, (ChunkProviderServer) chunkProvider);
    }

    @Override
    public void onLivingUpdate(CallbackInfo ci) {
        super.onLivingUpdate(ci);
    }
}
