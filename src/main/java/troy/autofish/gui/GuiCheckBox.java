package troy.autofish.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class provides a checkbox style control.
 *
 * @author bspkrs
 */
public class GuiCheckBox extends AbstractPressableButtonWidget {
    private boolean isChecked;
    private boolean isHovered = false;
    private int boxWidth;
    private List<StringRenderable> hoverText;
    private Consumer<GuiCheckBox> clickAction;


    public GuiCheckBox(int xPos, int yPos, String displayString, List<StringRenderable> hoverText, Consumer<GuiCheckBox> clickAction) {
        super(xPos, yPos, 0, 0, Text.method_30163(displayString));
        this.boxWidth = 11;
        this.height = 11;
        this.width = this.boxWidth + 2 + MinecraftClient.getInstance().textRenderer.getWidth(displayString);
        this.hoverText = hoverText;
        this.clickAction = clickAction;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            MinecraftClient mc = MinecraftClient.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.boxWidth && mouseY < this.y + this.height;
            GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, 0);
            this.renderBg(matrixStack, mc, mouseX, mouseY);
            int color = 16777215;

            if (this.isChecked)
                this.drawCenteredString(matrixStack, mc.textRenderer, "x", this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);

            mc.textRenderer.draw(matrixStack, getMessage(), this.x + this.boxWidth + 2, this.y + 2, color);

        }
    }

    public void drawTooltipIfHovered(int mouseX, int mouseY, int width, int height, MatrixStack matrixStack, TextRenderer fontRenderer) {
        if (isHovered && hoverText != null) {
            GuiUtils.drawHoveringText(matrixStack, hoverText, mouseX, mouseY, width, height, 200, fontRenderer);
        }
    }

    @Override
    public void onPress() {
        this.isChecked = !this.isChecked;
        clickAction.accept(this);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


}