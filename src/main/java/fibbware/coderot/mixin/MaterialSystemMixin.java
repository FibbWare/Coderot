package fibbware.coderot.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialSystem;
import net.minecraft.world.level.levelgen.synth.Noise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaterialSystem.class)
public abstract class MaterialSystemMixin {

    @Shadow @Final private BlockState[] clayBands;
    @Shadow @Final private Noise clayBandsOffsetNoise;

    // stupid hack to fix a crash
    @Inject(method = "getBand", at = @At("HEAD"), cancellable = true)
    private void safeGetBand(int worldX, int y, int worldZ,
                             CallbackInfoReturnable<BlockState> cir) {
        int offset = Math.round(
                this.clayBandsOffsetNoise.get(worldX, 0.0D, worldZ) * 4.0F);
        cir.setReturnValue(this.clayBands[Math.floorMod(y + offset, this.clayBands.length)]);
    }
}