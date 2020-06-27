package troy.autofish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Identifier;
import troy.autofish.config.Config;
import troy.autofish.config.ConfigManager;
import troy.autofish.gui.ScreenAutofish;

public class FabricModAutofish implements ClientModInitializer, ClientTickCallback {

    private static FabricModAutofish instance;
    private Autofish autofish;
    private FabricKeyBinding autofishGuiKey;
    private ConfigManager configManager;

    @Override
    public void onInitializeClient() {

        if (instance == null) instance = this;

        this.configManager = new ConfigManager(this);

        KeyBindingRegistry.INSTANCE.addCategory("Autofish");
        //Key 86 is V
        autofishGuiKey = FabricKeyBinding.Builder.create(new Identifier("autofish", "open_gui"), InputUtil.Type.KEYSYM, 86, "Autofish").build();
        KeyBindingRegistry.INSTANCE.register(autofishGuiKey);

        ClientTickCallback.EVENT.register(this);

        this.autofish = new Autofish(this);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (autofishGuiKey.wasPressed()) {
            client.openScreen(new ScreenAutofish(this));
        }

        autofish.onTick(client);
    }

    /**
     * Mixin callback for Sound and EntityVelocity packets (multiplayer detection)
     */
    public void handlePacket(Packet<?> packet) {
        autofish.handlePacket(packet);
    }

    /**
     * Mixin callback for chat packets
     */
    public void handleChat(GameMessageS2CPacket packet) {
        autofish.handleChat(packet);
    }

    /**
     * Mixin callback for catchingFish method of EntityFishHook (singleplayer detection)
     *
     * @param owner         The player using this fish hook
     * @param ticksCatchable When this is greater than 0, we can reel it in
     */
    public void tickFishingLogic(Entity owner, int ticksCatchable) {
        autofish.tickFishingLogic(owner, ticksCatchable);
    }

    public static FabricModAutofish getInstance() {
        return instance;
    }

    public Autofish getAutofish() {
        return autofish;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Config getConfig() {
        return configManager.getConfig();
    }
}
