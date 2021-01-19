package troy.autofish;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import troy.autofish.config.Config;
import troy.autofish.config.ConfigManager;
import troy.autofish.gui.AutofishScreenBuilder;
import troy.autofish.scheduler.AutofishScheduler;

@Mod("autofish")
public class ForgeModAutofish {

    private static ForgeModAutofish instance;
    private Autofish autofish;
    private AutofishScheduler scheduler;
    private KeyBinding autofishGuiKey;
    private ConfigManager configManager;

    public ForgeModAutofish() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::fmlClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void fmlClientSetup(final FMLClientSetupEvent event) {

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        if (instance == null) instance = this;

        //Create ConfigManager
        this.configManager = new ConfigManager(this);
        //Register Keybinding
        autofishGuiKey = new KeyBinding("key.autofish.open_gui", GLFW.GLFW_KEY_V, "Autofish");
        ClientRegistry.registerKeyBinding(autofishGuiKey);
        //Create Scheduler instance
        this.scheduler = new AutofishScheduler(this);
        //Create Autofisher instance
        this.autofish = new Autofish(this);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (autofishGuiKey.isPressed()) {
            client.displayGuiScreen(AutofishScreenBuilder.buildScreen(this, client));
        }
        autofish.tick(client);
        scheduler.tick(client);
    }

    /**
     * Mixin callback for Sound and EntityVelocity packets (multiplayer detection)
     */
    public void handlePacket(IPacket<?> packet) {
        autofish.handlePacket(packet);
    }

    /**
     * Mixin callback for chat packets
     */
    public void handleChat(SChatPacket packet) {
        autofish.handleChat(packet);
    }

    /**
     * Mixin callback for catchingFish method of EntityFishHook (singleplayer detection)
     */
    public void tickFishingLogic(Entity owner, int ticksCatchable) {
        autofish.tickFishingLogic(owner, ticksCatchable);
    }

    public static ForgeModAutofish getInstance() {
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
