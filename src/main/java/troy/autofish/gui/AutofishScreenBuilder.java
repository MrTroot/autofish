package troy.autofish.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import troy.autofish.FabricModAutofish;
import troy.autofish.config.Config;

import java.util.function.Function;

public class AutofishScreenBuilder {

    private static final Function<Boolean, Text> yesNoTextSupplier = bool -> {
        if (bool) return new TranslatableText("options.autofish.toggle.on");
        else return new TranslatableText("options.autofish.toggle.off");
    };

    public static Screen buildScreen(FabricModAutofish modAutofish, MinecraftClient client) {

        Config defaults = new Config();
        Config config = modAutofish.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(new TranslatableText("options.autofish.title"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> {
                    modAutofish.getConfig().enforceConstraints();
                    modAutofish.getConfigManager().writeConfig(true);
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory configCat = builder.getOrCreateCategory(new TranslatableText("options.autofish.config"));


        //Enable Autofish
        AbstractConfigListEntry toggleAutofish = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.enable.title"), config.isAutofishEnabled())
                .setDefaultValue(defaults.isAutofishEnabled())
                .setTooltip(
                        new TranslatableText("options.autofish.enable.tooltip")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutofishEnabled(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable MultiRod
        AbstractConfigListEntry toggleMultiRod = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.multirod.title"), config.isMultiRod())
                .setDefaultValue(defaults.isMultiRod())
                .setTooltip(
                        new TranslatableText("options.autofish.multirod.tooltip_0"),
                        new TranslatableText("options.autofish.multirod.tooltip_1"),
                        new TranslatableText("options.autofish.multirod.tooltip_2")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setMultiRod(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Break Protection
        AbstractConfigListEntry toggleBreakProtection = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.break_protection.title"), config.isNoBreak())
                .setDefaultValue(defaults.isNoBreak())
                .setTooltip(
                        new TranslatableText("options.autofish.break_protection.tooltip_0"),
                        new TranslatableText("options.autofish.break_protection.tooltip_1")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setNoBreak(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Persistent Mode
        AbstractConfigListEntry togglePersistentMode = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.persistent.title"), config.isPersistentMode())
                .setDefaultValue(defaults.isPersistentMode())
                .setTooltip(
                        new TranslatableText("options.autofish.persistent.tooltip_0"),
                        new TranslatableText("options.autofish.persistent.tooltip_1"),
                        new TranslatableText("options.autofish.persistent.tooltip_2"),
                        new TranslatableText("options.autofish.persistent.tooltip_3"),
                        new TranslatableText("options.autofish.persistent.tooltip_4"),
                        new TranslatableText("options.autofish.persistent.tooltip_5")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setPersistentMode(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();


        //Enable Sound Detection
        AbstractConfigListEntry toggleSoundDetection = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.sound.title"), config.isUseSoundDetection())
                .setDefaultValue(defaults.isUseSoundDetection())
                .setTooltip(
                        new TranslatableText("options.autofish.sound.tooltip_0"),
                        new TranslatableText("options.autofish.sound.tooltip_1"),
                        new TranslatableText("options.autofish.sound.tooltip_2"),
                        new TranslatableText("options.autofish.sound.tooltip_3"),
                        new TranslatableText("options.autofish.sound.tooltip_4"),
                        new TranslatableText("options.autofish.sound.tooltip_5"),
                        new TranslatableText("options.autofish.sound.tooltip_6"),
                        new TranslatableText("options.autofish.sound.tooltip_7"),
                        new TranslatableText("options.autofish.sound.tooltip_8"),
                        new TranslatableText("options.autofish.sound.tooltip_9")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setUseSoundDetection(newValue);
                    modAutofish.getAutofish().setDetection();
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Enable Force MP Detection
        AbstractConfigListEntry toggleForceMPDetection = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.multiplayer_compat.title"), config.isForceMPDetection())
                .setDefaultValue(defaults.isPersistentMode())
                .setTooltip(
                        new TranslatableText("options.autofish.multiplayer_compat.tooltip_0"),
                        new TranslatableText("options.autofish.multiplayer_compat.tooltip_1"),
                        new TranslatableText("options.autofish.multiplayer_compat.tooltip_2")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setForceMPDetection(newValue);
                })
                .setYesNoTextSupplier(yesNoTextSupplier)
                .build();

        //Recast Delay
        AbstractConfigListEntry recastDelaySlider = entryBuilder.startLongSlider(new TranslatableText("options.autofish.recast_delay.title"), config.getRecastDelay(), 1000, 5000)
                .setDefaultValue(defaults.getRecastDelay())
                .setTooltip(
                        new TranslatableText("options.autofish.recast_delay.tooltip_0"),
                        new TranslatableText("options.autofish.recast_delay.tooltip_1")
                )
                .setTextGetter(value -> new TranslatableText("options.autofish.recast_delay.value", value))
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setRecastDelay(newValue);
                })
                .build();

        //ClearLag Regex
        AbstractConfigListEntry clearLagRegexField = entryBuilder.startTextField(new TranslatableText("options.autofish.clear_regex.title"), config.getClearLagRegex())
                .setDefaultValue(defaults.getClearLagRegex())
                .setTooltip(
                        new TranslatableText("options.autofish.clear_regex.tooltip_0"),
                        new TranslatableText("options.autofish.clear_regex.tooltip_1"),
                        new TranslatableText("options.autofish.clear_regex.tooltip_2")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setClearLagRegex(newValue);
                })
                .build();

        //UseAutoturning 使用自动转向
        AbstractConfigListEntry useAutoturning = entryBuilder.startBooleanToggle(new TranslatableText("options.autofish.useautoturning"), config.isUseAutoturning())
                .setDefaultValue(defaults.isUseAutoturning())
                .setTooltip(
                        new TranslatableText("options.autofish.useautoturning.tooltip_0"),
                        new TranslatableText("options.autofish.useautoturning.tooltip_1"),
                        new TranslatableText("options.autofish.useautoturning.tooltip_2")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setUseAutoturning(newValue);
                })
                .build();

        //setAutoturning Angle 设置自动转向角度
        AbstractConfigListEntry autoturningAngle = entryBuilder.startFloatField(new TranslatableText("options.autofish.autoturningangle"), config.getAutoturningAngle())
                .setDefaultValue(defaults.getAutoturningAngle())
                .setTooltip(
                        new TranslatableText("options.autofish.autoturningangle.tooltip_0"),
                        new TranslatableText("options.autofish.autoturningangle.tooltip_1"),
                        new TranslatableText("options.autofish.autoturningangle.tooltip_2")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutoturnAngle(newValue);
                })
                .build();

        //SetAutoturning Regex 设置自动转向正则表达式
        AbstractConfigListEntry autoturningRegex = entryBuilder.startTextField(new TranslatableText("options.autofish.autoturningregex"), config.getAutoturningRegex())
                .setDefaultValue(defaults.getAutoturningRegex())
                .setTooltip(
                        new TranslatableText("options.autofish.autoturningregex.tooltip_0"),
                        new TranslatableText("options.autofish.autoturningregex.tooltip_1"),
                        new TranslatableText("options.autofish.autoturningregex.tooltip_2")
                )
                .setSaveConsumer(newValue -> {
                    modAutofish.getConfig().setAutoturningRegex(newValue);
                })
                .build();

        SubCategoryBuilder subCatBuilderBasic = entryBuilder.startSubCategory(new TranslatableText("options.autofish.basic.title"));
        subCatBuilderBasic.add(toggleAutofish);
        subCatBuilderBasic.add(toggleMultiRod);
        subCatBuilderBasic.add(toggleBreakProtection);
        subCatBuilderBasic.add((togglePersistentMode));
        subCatBuilderBasic.setExpanded(true);

        SubCategoryBuilder subCatBuilderAdvanced = entryBuilder.startSubCategory(new TranslatableText("options.autofish.advanced.title"));
        subCatBuilderAdvanced.add(toggleSoundDetection);
        subCatBuilderAdvanced.add(toggleForceMPDetection);
        subCatBuilderAdvanced.add(recastDelaySlider);
        subCatBuilderAdvanced.add(clearLagRegexField);
        subCatBuilderAdvanced.add(useAutoturning);
        subCatBuilderAdvanced.add(autoturningRegex);
        subCatBuilderAdvanced.add(autoturningAngle);
        subCatBuilderAdvanced.setExpanded(true);

        configCat.addEntry(subCatBuilderBasic.build());
        configCat.addEntry(subCatBuilderAdvanced.build());

        return builder.build();

    }
}
