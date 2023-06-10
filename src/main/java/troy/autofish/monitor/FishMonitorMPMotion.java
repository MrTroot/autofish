package troy.autofish.monitor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import troy.autofish.Autofish;


public class FishMonitorMPMotion implements FishMonitorMP {

    // The threshold of detecting a bobber moving downwards, to detect as a fish.
    public static final int PACKET_MOTION_Y_THRESHOLD = -350;

    // Start catching fish after a 1 second threshold of hitting water.
    public static final int START_CATCHING_AFTER_THRESHOLD = 1000;

    // True if the bobber is in the water.
    private boolean hasHitWater = false;
    
    // Time at which bobber begins to rise in the water.
    // 0 if the bobber has not rose in the water yet.
    private long bobberRiseTimestamp = 0;


    @Override
    public void hookTick(Autofish autofish, MinecraftClient minecraft, FishingBobberEntity hook) {
        if (worldContainsBlockWithMaterial(hook.getWorld(), hook.getBoundingBox(), Blocks.WATER)) {
            hasHitWater = true;
        }
    }

    @Override
    public void handleHookRemoved() {
        hasHitWater = false;
        bobberRiseTimestamp = 0;
    }

    @Override
    public void handlePacket(Autofish autofish, Packet<?> packet, MinecraftClient minecraft) {
        if (packet instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket velocityPacket = (EntityVelocityUpdateS2CPacket) packet;
            if (minecraft.player != null && minecraft.player.fishHook != null && minecraft.player.fishHook.getId() == velocityPacket.getId()) {

                // Wait until the bobber has rose in the water.
                // Prevent remarking the bobber rise timestamp until it is reset by catching.
                if (hasHitWater && bobberRiseTimestamp == 0 && velocityPacket.getVelocityY() > 0) {
                    // Mark the time in which the bobber began to rise.
                    bobberRiseTimestamp = autofish.timeMillis;
                }

                // Calculate the time in which the bobber has been in the water
                long timeInWater = autofish.timeMillis - bobberRiseTimestamp;

                // If the bobber has been in the water long enough, start detecting the bobber movement.
                if (hasHitWater && bobberRiseTimestamp != 0 && timeInWater > START_CATCHING_AFTER_THRESHOLD) {
                    if (velocityPacket.getVelocityX() == 0 && velocityPacket.getVelocityZ() == 0 && velocityPacket.getVelocityY() < PACKET_MOTION_Y_THRESHOLD) {
                        // Catch the fish
                        autofish.catchFish();

                        // Reset the class attributes to default.
                        this.handleHookRemoved();
                    }
                }
            }
        }
    }

    public static boolean worldContainsBlockWithMaterial(World world, Box box, Block block) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        return BlockPos.stream(i, k, m, j - 1, l - 1, n - 1).anyMatch((blockPos) -> world.getBlockState(blockPos).getBlock() == block);
    }
}
