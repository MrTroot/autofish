package troy.autofish.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import troy.autofish.FabricModAutofish;
import troy.autofish.config.Config;

import java.util.function.Function;

public class AutofishScreenBuilder {
    private static final Function<Boolean, Text> yesNoTextSupplier = bool -> {
        if (bool) return MutableText.of(new TranslatableTextContent("options.autofish.toggle.on"));
        else return MutableText.of(new TranslatableTextContent("options.autofish.toggle.off"));
    };

    public static Screen buildScreen(FabricModAutofish modAutofish, MinecraftClient client) {

        Config defaults = new Config();
        Config config = modAutofish.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(MutableText.of(new TranslatableTextContent(("options.autofish.title"))))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> {
                    modAutofish.getConfig().enforceConstraints();
                    modAutofish.getConfigManager().writeConfig(true);
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(MutableText.of(new TranslatableTextContent("options.autofish.config")));


        //Enable Autofish
        AbstractConfigListEntry toggleAutofish = entryBuilder.startBooleanToggle(MutableText.of(new TranslatableTextContent("options.autofish.enable.title")), config.isAutofishEnabled())
                .setDefaultValue(defaults.isAutofishEnabled())
                .setTooltip(MutableText.of(new TranslatableTextContent("options.autofish.enable.tooltip")))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutofishEnabled(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable MultiRod
        AbstractConfigListEntry toggleMultiRod = entryBuilder.startBooleanToggle(MutableText.of(new TranslatableTextContent("options.autofish.multirod.title")), config.isMultiRod())
                .setDefaultValue(defaults.isMultiRod())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.multirod.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.multirod.tooltip_1")),
                        MutableText.of(new TranslatableTextContent("options.autofish.multirod.tooltip_2"))
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setMultiRod(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Break Protection
        AbstractConfigListEntry toggleBreakProtection = entryBuilder.startBooleanToggle(MutableText.of(new TranslatableTextContent("options.autofish.break_protection.title")), config.isNoBreak())
                .setDefaultValue(defaults.isNoBreak())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.break_protection.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.break_protection.tooltip_1"))
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setNoBreak(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Persistent Mode
        AbstractConfigListEntry togglePersistentMode = entryBuilder.startBooleanToggle(MutableText.of(new TranslatableTextContent("options.autofish.persistent.title")), config.isPersistentMode())
                .setDefaultValue(defaults.isPersistentMode())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.persistent.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.persistent.tooltip_1")),
                        MutableText.of(new TranslatableTextContent("options.autofish.persistent.tooltip_2")),
                        MutableText.of(new TranslatableTextContent("options.autofish.persistent.tooltip_3")),
                        MutableText.of(new TranslatableTextContent("options.autofish.persistent.tooltip_4")),
                        MutableText.of(new TranslatableTextContent("options.autofish.persistent.tooltip_5"))
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setPersistentMode(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();


        //Enable Sound Detection
        AbstractConfigListEntry toggleSoundDetection = entryBuilder.startBooleanToggle(MutableText.of(new TranslatableTextContent("options.autofish.sound.title")), config.isUseSoundDetection())
                .setDefaultValue(defaults.isUseSoundDetection())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_1")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_2")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_3")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_4")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_5")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_6")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_7")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_8")),
                        MutableText.of(new TranslatableTextContent("options.autofish.sound.tooltip_9"))
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setUseSoundDetection(newValue);
                    modAutofish.getAutofish().setDetection();
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Force MP Detection
        AbstractConfigListEntry toggleForceMPDetection = entryBuilder.startBooleanToggle(MutableText.of(new TranslatableTextContent("options.autofish.multiplayer_compat.title")), config.isForceMPDetection())
                .setDefaultValue(defaults.isPersistentMode())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.multiplayer_compat.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.multiplayer_compat.tooltip_1")),
                        MutableText.of(new TranslatableTextContent("options.autofish.multiplayer_compat.tooltip_2"))
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setForceMPDetection(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Recast Delay
        AbstractConfigListEntry recastDelaySlider = entryBuilder.startLongSlider(MutableText.of(new TranslatableTextContent("options.autofish.recast_delay.title")), config.getRecastDelay(), 1000, 5000)
                .setDefaultValue(defaults.getRecastDelay())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.recast_delay.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.recast_delay.tooltip_1"))
                )
                .setTextGetter(value -> MutableText.of(new TranslatableTextContent("options.autofish.recast_delay.value", value)))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setRecastDelay(newValue);
                })
                .build();

        //ClearLag Regex
        AbstractConfigListEntry clearLagRegexField = entryBuilder.startTextField(MutableText.of(new TranslatableTextContent("options.autofish.clear_regex.title")), config.getClearLagRegex())
                .setDefaultValue(defaults.getClearLagRegex())
                .setTooltip(
                        MutableText.of(new TranslatableTextContent("options.autofish.clear_regex.tooltip_0")),
                        MutableText.of(new TranslatableTextContent("options.autofish.clear_regex.tooltip_1")),
                        MutableText.of(new TranslatableTextContent("options.autofish.clear_regex.tooltip_2"))
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setClearLagRegex(newValue);
                })
                .build();


        SubCategoryBuilder subCatBuilderBasic = entryBuilder.startSubCategory(MutableText.of(new TranslatableTextContent("options.autofish.basic.title")));
        subCatBuilderBasic.add(toggleAutofish);
        subCatBuilderBasic.add(toggleMultiRod);
        subCatBuilderBasic.add(toggleBreakProtection);
        subCatBuilderBasic.add((togglePersistentMode));
        subCatBuilderBasic.setExpanded(true);

        SubCategoryBuilder subCatBuilderAdvanced = entryBuilder.startSubCategory(MutableText.of(new TranslatableTextContent("options.autofish.advanced.title")));
        subCatBuilderAdvanced.add(toggleSoundDetection);
        subCatBuilderAdvanced.add(toggleForceMPDetection);
        subCatBuilderAdvanced.add(recastDelaySlider);
        subCatBuilderAdvanced.add(clearLagRegexField);
        subCatBuilderAdvanced.setExpanded(true);

        configCat.addEntry(subCatBuilderBasic.build());
        configCat.addEntry(subCatBuilderAdvanced.build());

        return builder.build();

    }
}
