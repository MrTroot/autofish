package troy.autofish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.lwjgl.glfw.GLFW;
import troy.autofish.config.Config;
import troy.autofish.config.ConfigManager;
import troy.autofish.gui.AutofishScreenBuilder;
import troy.autofish.scheduler.AutofishScheduler;

public class FabricModAutofish implements ClientModInitializer {

    private static FabricModAutofish instance;
    private Autofish autofish;
    private AutofishScheduler scheduler;
    private KeyBinding autofishGuiKey;
    private ConfigManager configManager;

    @Override
    public void onInitializeClient() {

        if (instance == null) instance = this;

        //Create ConfigManager
        this.configManager = new ConfigManager(this);
        //Register Keybinding
        autofishGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.autofish.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "Autofish"));
        //Register Tick Callback
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        //Create Scheduler instance
        this.scheduler = new AutofishScheduler(this);
        //Create Autofisher instance
        this.autofish = new Autofish(this);

    }

    public void tick(MinecraftClient client) {
        if (autofishGuiKey.wasPressed()) {
            client.setScreen(AutofishScreenBuilder.buildScreen(this, client));
        }
        autofish.tick(client);
        scheduler.tick(client);
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

    public AutofishScheduler getScheduler() {
        return scheduler;
    }
}
