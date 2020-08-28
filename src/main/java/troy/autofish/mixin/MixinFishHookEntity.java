package troy.autofish.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import troy.autofish.ForgeModAutofish;

@Mixin(FishingBobberEntity.class)
public class MixinFishHookEntity {

    @Shadow private int ticksCatchable;

    @Inject(method = "catchingFish", at = @At("TAIL"))
    private void tickFishingLogic(BlockPos blockPos_1, CallbackInfo ci) {
        //func_234616_v_ is the owner of the hook (Entity)
        ForgeModAutofish.getInstance().tickFishingLogic(((FishingBobberEntity) (Object) this).func_234616_v_(), ticksCatchable);
    }
}
