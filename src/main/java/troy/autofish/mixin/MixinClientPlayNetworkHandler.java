package troy.autofish.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import troy.autofish.FabricModAutofish;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow private MinecraftClient client;

    @Inject(method = "onPlaySound", at = @At("HEAD"))
    public void onPlaySound(PlaySoundS2CPacket playSoundS2CPacket_1, CallbackInfo ci) {
        if (client.isOnThread()) FabricModAutofish.getInstance().handlePacket(playSoundS2CPacket_1);
    }

    @Inject(method = "onPlaySoundId", at = @At("HEAD"))
    public void onPlaySoundId(PlaySoundIdS2CPacket playSoundIdS2CPacket_1, CallbackInfo ci) {
        if (client.isOnThread()) FabricModAutofish.getInstance().handlePacket(playSoundIdS2CPacket_1);
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
