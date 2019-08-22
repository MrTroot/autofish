package troy.autofish.monitor;

import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.network.Packet;
import troy.autofish.Autofish;

public class FishMonitorMPMotion implements FishMonitorMP {

    public static final int PACKET_MOTION_Y_THRESHOLD = -350;

    //True if the hook hit water then started to rise.
    private boolean catchable = false;
    private long catchableAt = 0L;
    private boolean hasHitWater = false;


    @Override
    public void hookTick(Autofish autofish, MinecraftClient minecraft, FishHookEntity hook) {
        if (hook.world.containsBlockWithMaterial(hook.getBoundingBox(), Material.WATER)) {
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
            if (minecraft.player.fishHook != null && minecraft.player.fishHook.getEntityId() == velocityPacket.getId()) {

                //hook starts to rise after sinking in water
                if (hasHitWater && !catchable && velocityPacket.getVelocityY() > 0) {
                    catchable = true;
                    catchableAt = autofish.timeMillis;
                }

                if (hasHitWater && catchable && (autofish.timeMillis - catchableAt > 500)) {
                    if (velocityPacket.getVelocityX() == 0 && velocityPacket.getVelocityZ() == 0 && velocityPacket.getVelocityY() < PACKET_MOTION_Y_THRESHOLD) {
                        if (!autofish.hasQueuedRecast()) {
                            autofish.reel();
                            hasHitWater = false;
                        }
                    }
                }
            }
        }
    }
}
