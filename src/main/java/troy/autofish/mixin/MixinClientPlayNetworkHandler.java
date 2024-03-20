package troy.autofish.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import troy.autofish.FabricModAutofish;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler extends ClientCommonNetworkHandler implements TickablePacketListener, ClientPlayPacketListener {
    protected MixinClientPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }


    @Inject(method = "onPlaySound", at = @At("HEAD"))
    public void onPlaySound(PlaySoundS2CPacket playSoundS2CPacket_1, CallbackInfo ci) {
        if (client.isOnThread()) FabricModAutofish.getInstance().handlePacket(playSoundS2CPacket_1);
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"))
    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket_1, CallbackInfo ci) {
        if (client.isOnThread()) FabricModAutofish.getInstance().handlePacket(entityVelocityUpdateS2CPacket_1);
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onChatMessage(GameMessageS2CPacket chatMessageS2CPacket_1, CallbackInfo ci) {
        if (client.isOnThread()) FabricModAutofish.getInstance().handleChat(chatMessageS2CPacket_1);
    }

}
