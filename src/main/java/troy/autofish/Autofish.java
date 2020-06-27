package troy.autofish;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import troy.autofish.monitor.FishMonitorMP;
import troy.autofish.monitor.FishMonitorMPMotion;
import troy.autofish.monitor.FishMonitorMPSound;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Autofish {

    private MinecraftClient minecraft;
    private FishMonitorMP fishMonitorMP;
    private FabricModAutofish mod;

    private boolean hookExists = false;
    private long hookRemovedAt = 0L;

    public long timeMillis = 0L;
    private long queuedCastAt = 0L;
    private boolean queuedRodSwitch = false;

    public Autofish(FabricModAutofish mod) {
        this.mod = mod;
        this.minecraft = MinecraftClient.getInstance();
        setDetection();
    }

    public void onTick(MinecraftClient minecraft) {

        if (minecraft.world != null && minecraft.player != null && mod.getConfig().isAutofishEnabled()) {

            timeMillis = System.currentTimeMillis();//update current working time for this tick

            checkRodSwitch();

            if (holdingFishingRod()) {
                if (minecraft.player.fishHook != null) {
                    hookExists = true;
                    //MP catch listener
                    if (!minecraft.isInSingleplayer()) {//multiplayer only, send tick event to monitor
                        fishMonitorMP.hookTick(this, minecraft, minecraft.player.fishHook);
                    }
                } else {
                    removeHook();
                }

                //cast back out after reeling, if it's time
                checkRecast();

            } else { //not holding fishing rod
                removeHook();
                if (!hasQueuedRodSwitch()) //only stop fishing if we haven't queued a rod switch (rod could have broken)
                    resetRecast();
            }
        }
    }

    /**
     * Callback from mixin when sound and motion packets are received
     * For multiplayer detection only
     *
     * @param packet
     */
    public void handlePacket(Packet<?> packet) {
        if (mod.getConfig().isAutofishEnabled()) {
            if (!minecraft.isInSingleplayer()) {
                fishMonitorMP.handlePacket(this, packet, minecraft);
            }
        }
    }

    /**
     * Callback from mixin when chat packets are received
     * For multiplayer detection only
     *
     * @param packet
     */
    public void handleChat(GameMessageS2CPacket packet) {
        if (mod.getConfig().isAutofishEnabled()) {
            if (!minecraft.isInSingleplayer()) {
                if (holdingFishingRod()) {
                    //check that either the hook exists, or it was just removed
                    //this prevents false casts if we are holding a rod but not fishing
                    if (hookExists || (timeMillis - hookRemovedAt < 2000)) {
                        //make sure there is actually something there in the regex field
                        if (org.apache.commons.lang3.StringUtils.deleteWhitespace(mod.getConfig().getClearLagRegex()).isEmpty())
                            return;
                        //check if it matches
                        Matcher matcher = Pattern.compile(mod.getConfig().getClearLagRegex()).matcher(ChatUtil.stripTextFormat(packet.getMessage().getString()));
                        if (matcher.matches()) {
                            queueRecast();
                        }
                    }
                }
            }
        }
    }

    /**
     * Callback from mixin for the catchingFish method of the EntityFishHook
     * for singleplayer detection only
     */
    public void tickFishingLogic(Entity owner, int ticksCatchable) {
        if (mod.getConfig().isAutofishEnabled() && minecraft.isInSingleplayer()) {
            //null checks for sanity
            if (minecraft.player != null && minecraft.player.fishHook != null) {
                //hook is catchable and player is correct
                if (ticksCatchable > 0 && owner.getUuid().compareTo(minecraft.player.getUuid()) == 0) {
                    if (!hasQueuedRecast()) {
                        reel();
                    }
                }
            }
        }
    }

    /**
     * Reels the rod in and sets necessary variables to recast
     */
    public void reel() {
        queueRecast();
        useRod();

        if (mod.getConfig().isMultirod()) {
            queueRodSwitch();
        }
    }

    /**
     * Checks if it's time yet to recast after reeling in. Recasts if so.
     */
    private void checkRecast() {
        if (hasQueuedRecast() && (timeMillis - queuedCastAt > mod.getConfig().getRecastDelay()))//<recastDelay>ms after reeling
        {
            //TODO dont use 63 since the durability could change if it's a modded rod
            boolean cast = true;
            if (mod.getConfig().isNoBreak() && getHeldItem().getDamage() >= 63) cast = false;
            if (hookExists) cast = false;
            if (cast) {
                useRod();
            }
            resetRecast();
            resetRodSwitch();
        }
    }

    public void queueRecast() {
        queuedCastAt = timeMillis;
    }

    /**
     * Call this when the hook disappears
     */
    private void removeHook() {
        if (hookExists) {
            hookExists = false;
            hookRemovedAt = timeMillis;
            fishMonitorMP.handleHookRemoved();
        }
    }

    public boolean hasQueuedRecast() {
        return queuedCastAt > 0;
    }

    public void resetRecast() {
        queuedCastAt = 0;
    }

    private void checkRodSwitch() {
        if (hasQueuedRodSwitch() && timeMillis - queuedCastAt > mod.getConfig().getRecastDelay() - 100) {
            switchToFirstRod(minecraft.player);
            resetRodSwitch();
        }
    }

    private void queueRodSwitch() {
        queuedRodSwitch = true;
    }

    private boolean hasQueuedRodSwitch() {
        return queuedRodSwitch;
    }

    public void resetRodSwitch() {
        queuedRodSwitch = false;
    }

    public void switchToFirstRod(ClientPlayerEntity player) {
        PlayerInventory inventory = player.inventory;
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack slot = inventory.main.get(i);
            if (slot.getItem() == Items.FISHING_ROD) {
                if (i >= 0 && i < 9) {
                    if (mod.getConfig().isNoBreak()) {
                        if (slot.getDamage() < 63) {
                            inventory.selectedSlot = i;
                            return;
                        }
                    } else {
                        inventory.selectedSlot = i;
                        return;
                    }
                }
            }
        }
        //stop fishing if no suitable rod found
        resetRecast();
        resetRodSwitch();
    }

    public void useRod() {

        Hand hand = getCorrectHand();
        ActionResult actionResult = minecraft.interactionManager.interactItem(minecraft.player, minecraft.world, hand);
        if (actionResult.isAccepted()) {
            if (actionResult.shouldSwingHand()) {
                minecraft.player.swingHand(hand);
            }
            minecraft.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            return;
        }
    }

    public boolean holdingFishingRod() {
        return isItemFishingRod(getHeldItem().getItem());
    }

    private Hand getCorrectHand() {
        if (!mod.getConfig().isMultirod()) {
            if (isItemFishingRod(minecraft.player.getOffHandStack().getItem())) return Hand.OFF_HAND;
        }
        return Hand.MAIN_HAND;
    }

    private ItemStack getHeldItem() {
        if (!mod.getConfig().isMultirod()) {
            if (isItemFishingRod(minecraft.player.getOffHandStack().getItem()))
                return minecraft.player.getOffHandStack();
        }
        return minecraft.player.getMainHandStack();
    }

    private boolean isItemFishingRod(Item item) {
        return item == Items.FISHING_ROD || item instanceof FishingRodItem;
    }

    public void setDetection() {
        if (mod.getConfig().isUseSoundDetection()) {
            fishMonitorMP = new FishMonitorMPSound();
        } else {
            fishMonitorMP = new FishMonitorMPMotion();
        }
    }
}
