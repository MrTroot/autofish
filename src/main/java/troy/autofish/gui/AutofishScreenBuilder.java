package troy.autofish.gui;

import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigBuilder;
import me.shedaniel.clothconfig2.forge.api.ConfigCategory;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import troy.autofish.ForgeModAutofish;
import troy.autofish.config.Config;

import java.util.function.Function;

public class AutofishScreenBuilder {

    private static final Function<Boolean, ITextComponent> yesNoTextSupplier = bool -> {
        if (bool) return text("\u00A7aOn");
        else return text("\u00A7eOff");
    };

    public static Screen buildScreen(ForgeModAutofish modAutofish, Minecraft client) {

        Config defaults = new Config();
        Config config = modAutofish.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(text("Autofish Settings"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> {
                    modAutofish.getConfig().enforceConstraints();
                    modAutofish.getConfigManager().writeConfig(true);
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(text("autofishconfig"));


        //Enable Autofish
        AbstractConfigListEntry toggleAutofish = entryBuilder.startBooleanToggle(text("Enable Autofish"), config.isAutofishEnabled())
                .setDefaultValue(defaults.isAutofishEnabled())
                .setTooltip(text("Toggles the entire mod on or off."))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutofishEnabled(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable MultiRod
        AbstractConfigListEntry toggleMultiRod = entryBuilder.startBooleanToggle(text("Enable MultiRod"), config.isMultiRod())
                .setDefaultValue(defaults.isMultiRod())
                .setTooltip(
                        text("Cycles through all of the"),
                        text("available rods in the hotbar,"),
                        text("moving to the next as they break.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setMultiRod(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Break Protection
        AbstractConfigListEntry toggleBreakProtection = entryBuilder.startBooleanToggle(text("Enable Break Protection"), config.isNoBreak())
                .setDefaultValue(defaults.isNoBreak())
                .setTooltip(
                        text("Stop using rods with low"),
                        text("durability before they break.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setNoBreak(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Persistent Mode
        AbstractConfigListEntry togglePersistentMode = entryBuilder.startBooleanToggle(text("Enable Persistent Mode"), config.isPersistentMode())
                .setDefaultValue(defaults.isPersistentMode())
                .setTooltip(
                        text("Enable this to always keep the fish hook"),
                        text("cast whenever a rod is in hand."),
                        text("Checks every 10 seconds and recasts"),
                        text("if needed."),
                        text("This is useful for lag issues or when"),
                        text("fishing for long periods of time.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setPersistentMode(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();


        //Enable Sound Detection
        AbstractConfigListEntry toggleSoundDetection = entryBuilder.startBooleanToggle(text("Use Sound Detection"), config.isUseSoundDetection())
                .setDefaultValue(defaults.isUseSoundDetection())
                .setTooltip(
                        text("\u00A76Newer, more accurate detection based"),
                        text("\u00A76on bobber sounds rather than the"),
                        text("\u00A76standard hook movement detection."),
                        text("-You must be somewhat close to the"),
                        text("hook for this to work."),
                        text("-If other players' hooks are near"),
                        text("yours, it can falsely trigger a catch!"),
                        text("\u00A7cNote: this option only affects"),
                        text("\u00A7cmultiplayer. Singleplayer uses its own"),
                        text("\u00A7cdetection.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setUseSoundDetection(newValue);
                    modAutofish.getAutofish().setDetection();
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Recast Delay
        AbstractConfigListEntry recastDelaySlider = entryBuilder.startLongSlider(text("Recast Delay (ms)"), config.getRecastDelay(), 1000, 5000)
                .setDefaultValue(defaults.getRecastDelay())
                .setTooltip(
                        text("Adjusts the delay between catching"),
                        text("a fish and recasting the rod.")
                )
                .setTextGetter(value -> text(value + " ms"))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setRecastDelay(newValue);
                })
                .build();

        //ClearLag Regex
        AbstractConfigListEntry clearLagRegexField = entryBuilder.startTextField(text("ClearLag Chat Pattern"), config.getClearLagRegex())
                .setDefaultValue(defaults.getClearLagRegex())
                .setTooltip(
                        text("Recast the fishing rod when"),
                        text("this pattern is matched in chat."),
                        text("\u00A76This pattern is a \u00A7aRegular Expression\u00A76.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setClearLagRegex(newValue);
                })
                .build();


        SubCategoryBuilder subCatBuilderBasic = entryBuilder.startSubCategory(text("Basic Options"));
        subCatBuilderBasic.add(toggleAutofish);
        subCatBuilderBasic.add(toggleMultiRod);
        subCatBuilderBasic.add(toggleBreakProtection);
        subCatBuilderBasic.add((togglePersistentMode));
        subCatBuilderBasic.setExpanded(true);

        SubCategoryBuilder subCatBuilderAdvanced = entryBuilder.startSubCategory(text("Advanced Options"));
        subCatBuilderAdvanced.add(toggleSoundDetection);
        subCatBuilderAdvanced.add(recastDelaySlider);
        subCatBuilderAdvanced.add(clearLagRegexField);
        subCatBuilderAdvanced.setExpanded(true);

        configCat.addEntry(subCatBuilderBasic.build());
        configCat.addEntry(subCatBuilderAdvanced.build());

        return builder.build();

    }

    private static ITextComponent text(String str) {
        return ITextComponent.func_244388_a(str);
    }
}
