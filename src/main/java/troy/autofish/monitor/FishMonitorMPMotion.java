package troy.autofish.monitor;

import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
    public void hookTick(Autofish autofish, MinecraftClient minecraft, FishingBobberEntity hook) {
        if (worldContainsBlockWithMaterial(hook.world, hook.getBoundingBox(), Material.WATER)) {
            hasHitWater = true;
        }
    }

    @Override
    public void handleHookRemoved() {
        hasHitWater = false;
    }

    @Override
    public void handlePacket(Autofish autofish, Packet<?> packet, MinecraftClient minecraft) {
        if (packet instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket velocityPacket = (EntityVelocityUpdateS2CPacket) packet;
            if (minecraft.player != null && minecraft.player.fishHook != null && minecraft.player.fishHook.getId() == velocityPacket.getId()) {

                //hook starts to rise after sinking in water
                if (hasHitWater && !catchable && velocityPacket.getVelocityY() > 0) {
                    catchable = true;
                    catchableAt = autofish.timeMillis;
                }

                if (hasHitWater && catchable && (autofish.timeMillis - catchableAt > 500)) {
                    if (velocityPacket.getVelocityX() == 0 && velocityPacket.getVelocityZ() == 0 && velocityPacket.getVelocityY() < PACKET_MOTION_Y_THRESHOLD) {
                        autofish.catchFish();
                        hasHitWater = false;
                    }
                }
            }
        }
    }

    public static boolean worldContainsBlockWithMaterial(World world, Box box, Material material) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        MaterialPredicate materialPredicate = MaterialPredicate.create(material);
        return BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).anyMatch((blockPos) -> {
            return materialPredicate.test(world.getBlockState(blockPos));
        });
    }
}
