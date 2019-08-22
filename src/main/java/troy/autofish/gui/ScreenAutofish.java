package troy.autofish.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.chat.TranslatableComponent;
import troy.autofish.FabricModAutofish;

import java.util.Arrays;

public class ScreenAutofish extends Screen {

    private GuiCheckBox toggleAutofish;
    private GuiCheckBox toggleMultirod;
    private GuiCheckBox toggleNoBreak;
    private GuiCheckBox toggleSoundDetection;

    private GuiHoverableText chatPatternText;

    private TextFieldWidget clearLagRegexField;

    private FabricModAutofish mod;

    private static final int centerDistance = 20;

    public ScreenAutofish(FabricModAutofish mod) {
        super(new TranslatableComponent("Autofish Settings"));
        this.mod = mod;
    }

    @Override
    public void init() {

        toggleAutofish = new GuiCheckBox(center() - 125 - centerDistance, height / 2 - 80, "Enable Autofish", Arrays.asList("\u00A76Toggles the entire mod on or off."), toggle -> {
            mod.getConfig().setAutofishEnabled(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);

            if (!toggle.isChecked()) {
                //stop autofishing
                mod.getAutofish().resetRodSwitch();
                mod.getAutofish().resetRecast();
            }
        });
        toggleMultirod = new GuiCheckBox(center() - 125 - centerDistance, height / 2 - 60, "Enable MultiRod", Arrays.asList("\u00A76Autofish will cycle through all the fishing rods in your hotbar as they break."), toggle -> {
            mod.getConfig().setMultirod(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);

            if (!toggle.isChecked()) {
                //prevent permanent queuerodswitch if disabled in the middle of one
                mod.getAutofish().resetRodSwitch();
            }
        });
        toggleNoBreak = new GuiCheckBox(center() - 125 - centerDistance, height / 2 - 40, "Enable Break Protection", Arrays.asList("\u00A76Autofish will stop using rods with low durability before they break."), toggle -> {
            mod.getConfig().setNoBreak(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);
        });
        toggleSoundDetection = new GuiCheckBox(center() - 125 - centerDistance, height / 2 + 25, "Use sound-based detection", Arrays.asList("\u00A76Newer, more accurate detection based on bobber sounds rather than the standard hook movement detection.", "-You must be somewhat close to the hook for this to work.", "-If other players' bobbers are near yours, it can falsely trigger a catch!", "\u00A7cNote: This option only affects multiplayer. Singleplayer uses its own detection."), toggle -> {
            mod.getConfig().setUseSoundDetection(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);
            mod.getAutofish().setDetection();
        });

        toggleAutofish.setIsChecked(mod.getConfig().isAutofishEnabled());
        toggleMultirod.setIsChecked(mod.getConfig().isMultirod());
        toggleNoBreak.setIsChecked(mod.getConfig().isNoBreak());
        toggleSoundDetection.setIsChecked(mod.getConfig().isUseSoundDetection());

        addButton(toggleAutofish);
        addButton(toggleMultirod);
        addButton(toggleNoBreak);
        addButton(toggleSoundDetection);


        chatPatternText = new GuiHoverableText(center() - 125 - centerDistance, height / 2 + 52, "ClearLag Chat Pattern:", Arrays.asList("\u00A7aSome servers periodically clear entities to reduce lag, including fish hooks. There will often be a chat message broadcast when this occurs.", "\u00A7aTo circumvent this, Autofish will recast the hook when a chat message matches the pattern below.", "\u00A76This pattern is a \u00A7cregular expression\u00A76."));


        clearLagRegexField = new TextFieldWidget(font, center() - 125 - centerDistance, height / 2 + 65, 250, 16, "Regex Text Field");
        clearLagRegexField.setMaxLength(512);
        clearLagRegexField.setText(mod.getConfig().getClearLagRegex());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        renderBackground();
        drawCenteredString(font, "Autofish Settings", width / 2, height / 2 - 100, 0xFF6600);
        drawCenteredString(font, "Advanced Options", width / 2, height / 2 + 5, 0xFFCC66);

        chatPatternText.drawString(font);
        clearLagRegexField.render(mouseX, mouseY, f);

//        if (!LiteModAutofish.canAutofish)
//            drawCenteredString(fontRenderer, "Autofish disabled by server permissions", center(), height / 2 - 15, 0xAA0000);
        super.render(mouseX, mouseY, f);

        toggleAutofish.drawTooltipIfHovered(mouseX, mouseY, width, height, font);
        toggleMultirod.drawTooltipIfHovered(mouseX, mouseY, width, height, font);
        toggleNoBreak.drawTooltipIfHovered(mouseX, mouseY, width, height, font);
        toggleSoundDetection.drawTooltipIfHovered(mouseX, mouseY, width, height, font);

        chatPatternText.drawTooltipIfHovered(mouseX, mouseY, width, height, font);
    }

    @Override
    public void onClose() {

        if (!clearLagRegexField.getText().equals(mod.getConfig().getClearLagRegex())) {
            mod.getConfig().setClearLagRegex(clearLagRegexField.getText());
            mod.getConfigManager().writeConfig(true);
        }

        super.onClose();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        clearLagRegexField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        clearLagRegexField.charTyped(p_charTyped_1_, p_charTyped_2_);
        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        clearLagRegexField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    public int center() {
        return width / 2;
    }

}

