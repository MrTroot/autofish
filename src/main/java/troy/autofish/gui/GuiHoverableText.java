package troy.autofish.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;

import java.util.List;

public class GuiHoverableText {

    private final int xPos;
    private final int yPos;
    private final String displayString;
    private final List<StringRenderable> hoverText;

    public GuiHoverableText(int xPos, int yPos, String displayString, List<StringRenderable> hoverText) {

        this.xPos = xPos;
        this.yPos = yPos;
        this.displayString = displayString;
        this.hoverText = hoverText;
    }

    public void drawString(MatrixStack matrixStack, TextRenderer fontRenderer) {
        fontRenderer.draw(matrixStack, displayString, xPos, yPos, 0xFFFFFFFF);
    }

    public void drawTooltipIfHovered(int mouseX, int mouseY, int width, int height, MatrixStack matrixStack, TextRenderer fontRenderer) {
        boolean mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + fontRenderer.getWidth(displayString) && mouseY < yPos + 12;
        if (mouseOver && hoverText != null) {
            GuiUtils.drawHoveringText(matrixStack, hoverText, mouseX, mouseY, width, height, 200, fontRenderer);
        }
    }
}
