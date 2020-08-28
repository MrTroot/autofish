package troy.autofish.monitor;

import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import troy.autofish.Autofish;

public class FishMonitorMPMotion implements FishMonitorMP {

    public static final int PACKET_MOTION_Y_THRESHOLD = -350;

    //True if the hook hit water then started to rise.
    private boolean catchable = false;
    private long catchableAt = 0L;
    private boolean hasHitWater = false;


    @Override
    public void hookTick(Autofish autofish, Minecraft minecraft, FishingBobberEntity hook) {
        if (worldContainsBlockWithMaterial(hook.world, hook.getBoundingBox(), Material.WATER)) {
            hasHitWater = true;
        }
    }

    @Override
    public void handleHookRemoved() {
        hasHitWater = false;
    }

    @Override
    public void handlePacket(Autofish autofish, IPacket<?> packet, Minecraft minecraft) {
        if (packet instanceof SEntityVelocityPacket) {
            SEntityVelocityPacket velocityPacket = (SEntityVelocityPacket) packet;
            if (minecraft.player != null && minecraft.player.fishingBobber != null && minecraft.player.fishingBobber.getEntityId() == velocityPacket.getEntityID()) {

                //hook starts to rise after sinking in water
                if (hasHitWater && !catchable && velocityPacket.getMotionY() > 0) {
                    catchable = true;
                    catchableAt = autofish.timeMillis;
                }

                if (hasHitWater && catchable && (autofish.timeMillis - catchableAt > 500)) {
                    if (velocityPacket.getMotionX() == 0 && velocityPacket.getMotionZ() == 0 && velocityPacket.getMotionY() < PACKET_MOTION_Y_THRESHOLD) {
                        autofish.catchFish();
                        hasHitWater = false;
                    }
                }
            }
        }
    }

    public static boolean worldContainsBlockWithMaterial(World world, AxisAlignedBB box, Material material) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        BlockMaterialMatcher materialPredicate = BlockMaterialMatcher.forMaterial(material);
        return BlockPos.getAllInBox(i, k, m, j - 1, l - 1, n - 1).anyMatch((blockPos) -> {
            return materialPredicate.test(world.getBlockState(blockPos));
        });
    }
}
