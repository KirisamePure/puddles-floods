package pigcart.puddleflood.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import pigcart.puddleflood.PuddleFlood;

import static pigcart.puddleflood.config.ConfigManager.config;

/** Optional compatibility with Waves 0.4.3's exclusive-water material check. */
@Pseudo
@Mixin(targets = "dev.kirisame.waves.iris.IrisBridge", remap = false)
public abstract class WavesIrisBridgeMixin {
    @WrapOperation(method = "waterId", at = @At(value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap$Entry;getKey()Ljava/lang/Object;"),
            remap = false)
    private static Object puddleflood$allowPuddleWaterMaterial(
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<?> entry, Operation<Object> original) {
        Object key = original.call(entry);
        // IdMapMixin deliberately aliases puddles to the shader pack's water material.
        // Treat only that known alias as water during Waves' validation. The Iris map
        // itself stays unchanged, and any other non-water alias still disables Waves.
        if (config.useShaderpackWater && key instanceof BlockState state
                && state.is(PuddleFlood.PUDDLE_BLOCK)) {
            return Blocks.WATER.defaultBlockState();
        }
        return key;
    }
}
