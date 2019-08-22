package troy.autofish.gui;

import net.minecraft.client.font.TextRenderer;

import java.util.List;

public class GuiHoverableText {

    private final int xPos;
    private final int yPos;
    private final String displayString;
    private final List<String> hoverText;

    public GuiHoverableText(int xPos, int yPos, String displayString, List<String> hoverText) {

        this.xPos = xPos;
        this.yPos = yPos;
        this.displayString = displayString;
        this.hoverText = hoverText;
    }

    public void drawString(TextRenderer fontRenderer) {
        fontRenderer.draw(displayString, xPos, yPos, 0xFFFFFFFF);
    }

    public void drawTooltipIfHovered(int mouseX, int mouseY, int width, int height, TextRenderer fontRenderer) {
        boolean mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + fontRenderer.getStringWidth(displayString) && mouseY < yPos + 12;
        if (mouseOver && hoverText != null) {
            GuiUtils.drawHoveringText(hoverText, mouseX, mouseY, width, height, 200, fontRenderer);
        }
    }
}
