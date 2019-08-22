package troy.autofish.monitor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.network.Packet;
import troy.autofish.Autofish;

public interface FishMonitorMP {

    void hookTick(Autofish autofish, MinecraftClient minecraft, FishHookEntity hook);

    void handleHookRemoved();

    void handlePacket(Autofish autofish, Packet<?> packet, MinecraftClient minecraft);

}
