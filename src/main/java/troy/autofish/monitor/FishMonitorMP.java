package troy.autofish.monitor;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.IPacket;
import troy.autofish.Autofish;

public interface FishMonitorMP {

    void hookTick(Autofish autofish, Minecraft minecraft, FishingBobberEntity hook);

    void handleHookRemoved();

    void handlePacket(Autofish autofish, IPacket<?> packet, Minecraft minecraft);

}
