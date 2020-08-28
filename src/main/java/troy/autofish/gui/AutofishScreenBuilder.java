package troy.autofish.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import troy.autofish.FabricModAutofish;
import troy.autofish.config.Config;

import java.util.function.Function;

public class AutofishScreenBuilder {

    private static final Function<Boolean, Text> yesNoTextSupplier = bool -> {
        if (bool) return Text.of("\u00A7aOn");
        else return Text.of("\u00A7eOff");
    };

    public static Screen buildScreen(FabricModAutofish modAutofish, MinecraftClient client) {

        Config defaults = new Config();
        Config config = modAutofish.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(Text.of("Autofish Settings"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> {
                    modAutofish.getConfig().enforceConstraints();
                    modAutofish.getConfigManager().writeConfig(true);
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(Text.of("autofishconfig"));


        //Enable Autofish
        AbstractConfigListEntry toggleAutofish = entryBuilder.startBooleanToggle(Text.of("Enable Autofish"), config.isAutofishEnabled())
                .setDefaultValue(defaults.isAutofishEnabled())
                .setTooltip(Text.of("Toggles the entire mod on or off."))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutofishEnabled(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable MultiRod
        AbstractConfigListEntry toggleMultiRod = entryBuilder.startBooleanToggle(Text.of("Enable MultiRod"), config.isMultiRod())
                .setDefaultValue(defaults.isMultiRod())
                .setTooltip(
                        Text.of("Cycles through all of the"),
                        Text.of("available rods in the hotbar,"),
                        Text.of("moving to the next as they break.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setMultiRod(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Break Protection
        AbstractConfigListEntry toggleBreakProtection = entryBuilder.startBooleanToggle(Text.of("Enable Break Protection"), config.isNoBreak())
                .setDefaultValue(defaults.isNoBreak())
                .setTooltip(
                        Text.of("Stop using rods with low"),
                        Text.of("durability before they break.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setNoBreak(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Persistent Mode
        AbstractConfigListEntry togglePersistentMode = entryBuilder.startBooleanToggle(Text.of("Enable Persistent Mode"), config.isPersistentMode())
                .setDefaultValue(defaults.isPersistentMode())
                .setTooltip(
                        Text.of("Enable this to always keep the fish hook"),
                        Text.of("cast whenever a rod is in hand."),
                        Text.of("Checks every 10 seconds and recasts"),
                        Text.of("if needed."),
                        Text.of("This is useful for lag issues or when"),
                        Text.of("fishing for long periods of time.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setPersistentMode(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();


        //Enable Sound Detection
        AbstractConfigListEntry toggleSoundDetection = entryBuilder.startBooleanToggle(Text.of("Use Sound Detection"), config.isUseSoundDetection())
                .setDefaultValue(defaults.isUseSoundDetection())
                .setTooltip(
                        Text.of("\u00A76Newer, more accurate detection based"),
                        Text.of("\u00A76on bobber sounds rather than the"),
                        Text.of("\u00A76standard hook movement detection."),
                        Text.of("-You must be somewhat close to the"),
                        Text.of("hook for this to work."),
                        Text.of("-If other players' hooks are near"),
                        Text.of("yours, it can falsely trigger a catch!"),
                        Text.of("\u00A7cNote: this option only affects"),
                        Text.of("\u00A7cmultiplayer. Singleplayer uses its own"),
                        Text.of("\u00A7cdetection.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setUseSoundDetection(newValue);
                    modAutofish.getAutofish().setDetection();
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Recast Delay
        AbstractConfigListEntry recastDelaySlider = entryBuilder.startLongSlider(Text.of("Recast Delay (ms)"), config.getRecastDelay(), 1000, 5000)
                .setDefaultValue(defaults.getRecastDelay())
                .setTooltip(
                        Text.of("Adjusts the delay between catching"),
                        Text.of("a fish and recasting the rod.")
                )
                .setTextGetter(value -> Text.of(value + " ms"))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setRecastDelay(newValue);
                })
                .build();

        //ClearLag Regex
        AbstractConfigListEntry clearLagRegexField = entryBuilder.startTextField(Text.of("ClearLag Chat Pattern"), config.getClearLagRegex())
                .setDefaultValue(defaults.getClearLagRegex())
                .setTooltip(
                        Text.of("Recast the fishing rod when"),
                        Text.of("this pattern is matched in chat."),
                        Text.of("\u00A76This pattern is a \u00A7aRegular Expression\u00A76.")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setClearLagRegex(newValue);
                })
                .build();


        SubCategoryBuilder subCatBuilderBasic = entryBuilder.startSubCategory(Text.of("Basic Options"));
        subCatBuilderBasic.add(toggleAutofish);
        subCatBuilderBasic.add(toggleMultiRod);
        subCatBuilderBasic.add(toggleBreakProtection);
        subCatBuilderBasic.add((togglePersistentMode));
        subCatBuilderBasic.setExpanded(true);

        SubCategoryBuilder subCatBuilderAdvanced = entryBuilder.startSubCategory(Text.of("Advanced Options"));
        subCatBuilderAdvanced.add(toggleSoundDetection);
        subCatBuilderAdvanced.add(recastDelaySlider);
        subCatBuilderAdvanced.add(clearLagRegexField);
        subCatBuilderAdvanced.setExpanded(true);

        configCat.addEntry(subCatBuilderBasic.build());
        configCat.addEntry(subCatBuilderAdvanced.build());

        return builder.build();

    }
}
