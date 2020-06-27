package troy.autofish.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
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
        super(new TranslatableText("Autofish Settings"));
        this.mod = mod;
    }

    @Override
    public void init() {

        toggleAutofish = new GuiCheckBox(center() - 125 - centerDistance, height / 2 - 80, "Enable Autofish",
                Arrays.asList(
                        StringRenderable.styled("Toggles the entire mod on or off.", Style.EMPTY.withColor(TextColor.fromRgb(16755200)))
                ), toggle -> {
            mod.getConfig().setAutofishEnabled(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);

            if (!toggle.isChecked()) {
                //stop autofishing
                mod.getAutofish().resetRodSwitch();
                mod.getAutofish().resetRecast();
            }
        });
        toggleMultirod = new GuiCheckBox(center() - 125 - centerDistance, height / 2 - 60, "Enable MultiRod",
                Arrays.asList(
                        StringRenderable.styled("Autofish will cycle through all the fishing rods in your hotbar as they break.", Style.EMPTY.withColor(TextColor.fromRgb(16755200)))
                ), toggle -> {
            mod.getConfig().setMultirod(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);

            if (!toggle.isChecked()) {
                //prevent permanent queuerodswitch if disabled in the middle of one
                mod.getAutofish().resetRodSwitch();
            }
        });
        toggleNoBreak = new GuiCheckBox(center() - 125 - centerDistance, height / 2 - 40, "Enable Break Protection",
                Arrays.asList(
                        StringRenderable.styled("Autofish will stop using rods with low durability before they break.", Style.EMPTY.withColor(TextColor.fromRgb(16755200)))
                ), toggle -> {
            mod.getConfig().setNoBreak(toggle.isChecked());
            mod.getConfigManager().writeConfig(true);
        });
        toggleSoundDetection = new GuiCheckBox(center() - 125 - centerDistance, height / 2 + 25, "Use sound-based detection",
                Arrays.asList(
                        StringRenderable.styled("Newer, more accurate detection based on bobber sounds rather than the standard hook movement detection.", Style.EMPTY.withColor(TextColor.fromRgb(16755200))),
                        StringRenderable.styled("-You must be somewhat close to the hook for this to work.", Style.EMPTY),
                        StringRenderable.styled("-If other players' bobbers are near yours, it can falsely trigger a catch!", Style.EMPTY),
                        StringRenderable.styled("Note: This option only affects multiplayer. Singleplayer uses its own detection.", Style.EMPTY.withColor(TextColor.fromRgb(16733525)))
                ), toggle -> {
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


        chatPatternText = new GuiHoverableText(center() - 125 - centerDistance, height / 2 + 52, "ClearLag Chat Pattern:",
                Arrays.asList(
                        StringRenderable.styled("Some servers periodically clear entities to reduce lag, including fish hooks. There will often be a chat message broadcast when this occurs.", Style.EMPTY.withColor(TextColor.fromRgb(5635925))),
                        StringRenderable.styled("To circumvent this, Autofish will recast the hook when a chat message matches the pattern below.", Style.EMPTY.withColor(TextColor.fromRgb(5635925))),
                        StringRenderable.styled("This pattern is a \u00A7cregular expression\u00A76.", Style.EMPTY.withColor(TextColor.fromRgb(16755200)))
                ));


        clearLagRegexField = new TextFieldWidget(textRenderer, center() - 125 - centerDistance, height / 2 + 65, 250, 16, Text.method_30163("Regex Text Field"));
        clearLagRegexField.setMaxLength(512);
        clearLagRegexField.setText(mod.getConfig().getClearLagRegex());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float f) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, textRenderer, "Autofish Settings", width / 2, height / 2 - 100, 0xFF6600);
        drawCenteredString(matrixStack, textRenderer, "Advanced Options", width / 2, height / 2 + 5, 0xFFCC66);

        chatPatternText.drawString(matrixStack, textRenderer);
        clearLagRegexField.render(matrixStack, mouseX, mouseY, f);

        super.render(matrixStack, mouseX, mouseY, f);

        toggleAutofish.drawTooltipIfHovered(mouseX, mouseY, width, height, matrixStack, textRenderer);
        toggleMultirod.drawTooltipIfHovered(mouseX, mouseY, width, height, matrixStack, textRenderer);
        toggleNoBreak.drawTooltipIfHovered(mouseX, mouseY, width, height, matrixStack, textRenderer);
        toggleSoundDetection.drawTooltipIfHovered(mouseX, mouseY, width, height, matrixStack, textRenderer);

        chatPatternText.drawTooltipIfHovered(mouseX, mouseY, width, height, matrixStack, textRenderer);
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

