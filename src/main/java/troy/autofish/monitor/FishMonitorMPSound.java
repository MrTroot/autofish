package troy.autofish.monitor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.network.Packet;
import troy.autofish.Autofish;

public class FishMonitorMPSound implements FishMonitorMP {

    public static final double HOOKSOUND_DISTANCESQ_THRESHOLD = 25D;

    @Override
    public void hookTick(Autofish autofish, MinecraftClient minecraft, FishHookEntity hook) {
    }

    @Override
    public void handleHookRemoved() {
    }

    @Override
    public void handlePacket(Autofish autofish, Packet<?> packet, MinecraftClient minecraft) {

        if (packet instanceof PlaySoundS2CPacket || packet instanceof PlaySoundIdS2CPacket || packet instanceof PlaySoundFromEntityS2CPacket) {
            //TODO investigate PlaySoundFromEntityS2CPacket; i dont think its ever used for fishing but whatever

            String soundName;
            double x, y, z;

            if (packet instanceof PlaySoundS2CPacket) {
                PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket) packet;
                soundName = soundPacket.getSound().getId().toString();
                x = soundPacket.getX();
                y = soundPacket.getY();
                z = soundPacket.getZ();
            } else if (packet instanceof PlaySoundIdS2CPacket) {
                PlaySoundIdS2CPacket soundPacket = (PlaySoundIdS2CPacket) packet;
                //func_197698 returns soundName
                soundName = soundPacket.getSoundId().toString();
                x = soundPacket.getX();
                y = soundPacket.getY();
                z = soundPacket.getZ();
            } else {
                return;
            }

            if (soundName.equalsIgnoreCase("minecraft:entity.fishing_bobber.splash") || soundName.equalsIgnoreCase("entity.fishing_bobber.splash")) {

                FishHookEntity hook = minecraft.player.fishHook;
                if (!autofish.hasQueuedRecast() && hook != null) {
                    if (hook.squaredDistanceTo(x, y, z) < HOOKSOUND_DISTANCESQ_THRESHOLD) {
                        autofish.reel();
                    }
                }
            }
        }

    }
}
