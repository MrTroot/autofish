package troy.autofish.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import troy.autofish.ForgeModAutofish;

@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow private Minecraft client;

    //handleSoundEffect(SPlaySoundEffectPacket packetIn)
    //handleCustomSound(SPlaySoundPacket packetIn)
    //handleEntityVelocity(SEntityVelocityPacket packetIn)
    //handleChat(SChatPacket packetIn)

    @Inject(method = "handleSoundEffect", at = @At("HEAD"))
    public void onPlaySound(SPlaySoundEffectPacket playSoundS2CPacket_1, CallbackInfo ci) {
        if (client.isOnExecutionThread()) ForgeModAutofish.getInstance().handlePacket(playSoundS2CPacket_1);
    }

    @Inject(method = "handleCustomSound", at = @At("HEAD"))
    public void onPlaySoundId(SPlaySoundPacket playSoundIdS2CPacket_1, CallbackInfo ci) {
        if (client.isOnExecutionThread()) ForgeModAutofish.getInstance().handlePacket(playSoundIdS2CPacket_1);
    }

    @Inject(method = "handleEntityVelocity", at = @At("HEAD"))
    public void onVelocityUpdate(SEntityVelocityPacket entityVelocityUpdateS2CPacket_1, CallbackInfo ci) {
        if (client.isOnExecutionThread()) ForgeModAutofish.getInstance().handlePacket(entityVelocityUpdateS2CPacket_1);
    }

    @Inject(method = "handleChat", at = @At("HEAD"))
    public void onChatMessage(SChatPacket chatMessageS2CPacket_1, CallbackInfo ci) {
        if (client.isOnExecutionThread()) ForgeModAutofish.getInstance().handleChat(chatMessageS2CPacket_1);
    }

}
