package troy.autofish.monitor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import troy.autofish.Autofish;

public class FishMonitorMPSound implements FishMonitorMP {

    public static final double HOOKSOUND_DISTANCESQ_THRESHOLD = 25D;

    @Override
    public void hookTick(Autofish autofish, MinecraftClient minecraft, FishingBobberEntity hook) {
    }

    @Override
    public void handleHookRemoved() {
    }

    @Override
    public void handlePacket(Autofish autofish, Packet<?> packet, MinecraftClient minecraft) {

        if (packet instanceof PlaySoundS2CPacket || packet instanceof PlaySoundFromEntityS2CPacket) {
            //TODO investigate PlaySoundFromEntityS2CPacket; i dont think its ever used for fishing but whatever

            String soundName;
            double x, y, z;

            if (packet instanceof PlaySoundS2CPacket) {
                PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket) packet;
                SoundEvent soundEvent = soundPacket.getSound().value();
                soundName = soundEvent.getId().toString();
                x = soundPacket.getX();
                y = soundPacket.getY();
                z = soundPacket.getZ();
            } else {
                return;
            }

            if (soundName.equalsIgnoreCase("minecraft:entity.fishing_bobber.splash") || soundName.equalsIgnoreCase("entity.fishing_bobber.splash")) {
                if(minecraft.player != null) {
                    FishingBobberEntity hook = minecraft.player.fishHook;
                    if (hook != null) {
                        if (hook.squaredDistanceTo(x, y, z) < HOOKSOUND_DISTANCESQ_THRESHOLD) {
                            autofish.catchFish();
                        }
                    }
                }
            }
        }

    }
}
