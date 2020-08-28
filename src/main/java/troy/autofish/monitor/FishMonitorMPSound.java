package troy.autofish.monitor;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import troy.autofish.Autofish;

public class FishMonitorMPSound implements FishMonitorMP {

    public static final double HOOKSOUND_DISTANCESQ_THRESHOLD = 25D;

    @Override
    public void hookTick(Autofish autofish, Minecraft minecraft, FishingBobberEntity hook) {
    }

    @Override
    public void handleHookRemoved() {
    }

    @Override
    public void handlePacket(Autofish autofish, IPacket<?> packet, Minecraft minecraft) {

        if (packet instanceof SPlaySoundEffectPacket || packet instanceof SPlaySoundPacket) {

            String soundName;
            double x, y, z;

            if (packet instanceof SPlaySoundEffectPacket) {
                SPlaySoundEffectPacket soundPacket = (SPlaySoundEffectPacket) packet;
                soundName = soundPacket.getSound().getName().toString();
                x = soundPacket.getX();
                y = soundPacket.getY();
                z = soundPacket.getZ();
            } else if (packet instanceof SPlaySoundPacket) {
                SPlaySoundPacket soundPacket = (SPlaySoundPacket) packet;
                soundName = soundPacket.getSoundName().toString();
                x = soundPacket.getX();
                y = soundPacket.getY();
                z = soundPacket.getZ();
            } else {
                return;
            }

            if (soundName.equalsIgnoreCase("minecraft:entity.fishing_bobber.splash") || soundName.equalsIgnoreCase("entity.fishing_bobber.splash")) {
                if (minecraft.player != null) {
                    FishingBobberEntity hook = minecraft.player.fishingBobber;
                    if (hook != null) {
                        if (hook.getDistanceSq(x, y, z) < HOOKSOUND_DISTANCESQ_THRESHOLD) {
                            autofish.catchFish();
                        }
                    }
                }
            }
        }

    }
}
