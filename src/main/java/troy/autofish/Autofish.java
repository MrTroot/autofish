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
import net.minecraft.util.StringHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import troy.autofish.monitor.FishMonitorMP;
import troy.autofish.monitor.FishMonitorMPMotion;
import troy.autofish.monitor.FishMonitorMPSound;
import troy.autofish.scheduler.ActionType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Autofish {

    private MinecraftClient client;
    private FabricModAutofish modAutofish;
    private FishMonitorMP fishMonitorMP;

    private boolean hookExists = false;
    private long hookRemovedAt = 0L;

    public long timeMillis = 0L;

    public Autofish(FabricModAutofish modAutofish) {
        this.modAutofish = modAutofish;
        this.client = MinecraftClient.getInstance();
        setDetection();

        //Initiate the repeating action for persistent mode casting
        modAutofish.getScheduler().scheduleRepeatingAction(10000, () -> {
            if(!modAutofish.getConfig().isPersistentMode()) return;
            if(!isHoldingFishingRod()) return;
            if(hookExists) return;
            if(modAutofish.getScheduler().isRecastQueued()) return;

            useRod();
        });
    }

    public void tick(MinecraftClient client) {

        if (client.world != null && client.player != null && modAutofish.getConfig().isAutofishEnabled()) {

           timeMillis = Util.getMeasuringTimeMs(); //update current working time for this tick

            if (isHoldingFishingRod()) {
                if (client.player.fishHook != null) {
                    hookExists = true;
                    //MP catch listener
                    if (shouldUseMPDetection()) {//multiplayer only, send tick event to monitor
                        fishMonitorMP.hookTick(this, client, client.player.fishHook);
                    }
                } else {
                    removeHook();
                }
            } else { //not holding fishing rod
                removeHook();
            }
        }
    }

    /**
     * Callback from mixin for the catchingFish method of the EntityFishHook
     * for singleplayer detection only
     */
    public void tickFishingLogic(Entity owner, int ticksCatchable) {
        if (modAutofish.getConfig().isAutofishEnabled() && !shouldUseMPDetection()) {
            //null checks for sanity
            if (client.player != null && client.player.fishHook != null) {
                //hook is catchable and player is correct
                if (ticksCatchable > 0 && owner.getUuid().compareTo(client.player.getUuid()) == 0) {
                    catchFish();
                }
            }
        }
    }

    /**
     * Callback from mixin when sound and motion packets are received
     * For multiplayer detection only
     */
    public void handlePacket(Packet<?> packet) {
        if (modAutofish.getConfig().isAutofishEnabled()) {
            if (shouldUseMPDetection()) {
                fishMonitorMP.handlePacket(this, packet, client);
            }
        }
    }

    /**
     * Callback from mixin when chat packets are received
     * For multiplayer detection only
     */
    public void handleChat(GameMessageS2CPacket packet) {
        if (modAutofish.getConfig().isAutofishEnabled()) {
            if (!client.isInSingleplayer()) {
                if (isHoldingFishingRod()) {
                    //check that either the hook exists, or it was just removed
                    //this prevents false casts if we are holding a rod but not fishing
                    if (hookExists || (timeMillis - hookRemovedAt < 2000)) {
                        //make sure there is actually something there in the regex field
                        if (org.apache.commons.lang3.StringUtils.deleteWhitespace(modAutofish.getConfig().getClearLagRegex()).isEmpty())
                            return;
                        //check if it matches
                        Matcher matcher = Pattern.compile(modAutofish.getConfig().getClearLagRegex(), Pattern.CASE_INSENSITIVE).matcher(StringHelper.stripTextFormat(packet.getMessage().getString()));
                        if (matcher.find()) {
                            queueRecast();
                        }
                    }
                }
            }
        }
    }

    public void catchFish() {
        if(!modAutofish.getScheduler().isRecastQueued()) { //prevents double reels
            //queue actions
            queueRodSwitch();
            queueRecast();

            //reel in
            useRod();
        }
    }

    public void queueRecast() {
        modAutofish.getScheduler().scheduleAction(ActionType.RECAST, modAutofish.getConfig().getRecastDelay(), () -> {
            //State checks to ensure we can still fish once this runs
            if(hookExists) return;
            if(!isHoldingFishingRod()) return;
            if(modAutofish.getConfig().isNoBreak() && getHeldItem().getDamage() >= 63) return;

            useRod();
        });
    }

    private void queueRodSwitch(){
        modAutofish.getScheduler().scheduleAction(ActionType.ROD_SWITCH, modAutofish.getConfig().getRecastDelay() - 500, () -> {
            if(!modAutofish.getConfig().isMultiRod()) return;

            switchToFirstRod(client.player);
        });
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

    public void switchToFirstRod(ClientPlayerEntity player) {
        if(player != null) {
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < inventory.main.size(); i++) {
                ItemStack slot = inventory.main.get(i);
                if (slot.getItem() == Items.FISHING_ROD) {
                    if (i < 9) { //hotbar only
                        if (modAutofish.getConfig().isNoBreak()) {
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
        }
    }

    public void useRod() {
        if(client.player != null && client.world != null) {
            Hand hand = getCorrectHand();
            ActionResult actionResult = client.interactionManager.interactItem(client.player, client.world, hand);
            if (actionResult.isAccepted()) {
                if (actionResult.shouldSwingHand()) {
                    client.player.swingHand(hand);
                }
                client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            }
        }
    }

    public boolean isHoldingFishingRod() {
        return isItemFishingRod(getHeldItem().getItem());
    }

    private Hand getCorrectHand() {
        if (!modAutofish.getConfig().isMultiRod()) {
            if (isItemFishingRod(client.player.getOffHandStack().getItem())) return Hand.OFF_HAND;
        }
        return Hand.MAIN_HAND;
    }

    private ItemStack getHeldItem() {
        if (!modAutofish.getConfig().isMultiRod()) {
            if (isItemFishingRod(client.player.getOffHandStack().getItem()))
                return client.player.getOffHandStack();
        }
        return client.player.getMainHandStack();
    }

    private boolean isItemFishingRod(Item item) {
        return item == Items.FISHING_ROD || item instanceof FishingRodItem;
    }

    public void setDetection() {
        if (modAutofish.getConfig().isUseSoundDetection()) {
            fishMonitorMP = new FishMonitorMPSound();
        } else {
            fishMonitorMP = new FishMonitorMPMotion();
        }
    }

    private boolean shouldUseMPDetection(){
        if(modAutofish.getConfig().isForceMPDetection()) return true;
        return !client.isInSingleplayer();
    }
}
