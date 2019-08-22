package troy.autofish.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import troy.autofish.FabricModAutofish;

@Mixin(FishHookEntity.class)
public class MixinFishHookEntity {

    //TODO update this field; ticksCatchable
    @Shadow private int field_7173;

    @Shadow private PlayerEntity owner;

    //TODO update this method; catchingFish
    @Inject(method = "method_6949", at = @At("TAIL"))
    private void catchingFish(BlockPos blockPos_1, CallbackInfo ci) {
        FabricModAutofish.getInstance().catchingFishTick(owner, field_7173);
    }
}
