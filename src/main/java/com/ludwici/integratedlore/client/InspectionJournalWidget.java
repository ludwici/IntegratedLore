package com.ludwici.integratedlore.client;

import com.ludwici.integratedlore.inspection.InspectionHotspot;
import com.ludwici.integratedlore.inspection.InspectionState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class InspectionJournalWidget extends AbstractWidget {
    private final InspectionState state;
    private final Font font;
    private int scroll;

    public InspectionJournalWidget(InspectionState state, Font font) {
        super(0, 0, 150, 120, Component.empty());
        this.state = state;
        this.font = font;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX() + 5;
        int y = getY() + 4 - scroll;
        int wrapWidth = Math.max(32, getWidth() - 13);
        graphics.enableScissor(getX(), getY(), getRight(), getBottom());
        for (InspectionHotspot hotspot : state.hotspots()) {
            boolean found = state.isDiscovered(hotspot);
            String label = found ? "✓ " + hotspot.title() : "◇ ???";
            graphics.drawString(font, label, x, y, found ? 0xFFF4DFB2 : 0xFF778392, false);
            y += 14;
            if (found && !hotspot.description().isBlank()) {
                List<FormattedCharSequence> lines = font.split(Component.literal(hotspot.description()), wrapWidth);
                for (FormattedCharSequence line : lines) {
                    graphics.drawString(font, line, x + 5, y, 0xFFD2D9DD, false);
                    y += 11;
                }
            }
            y += 9;
        }
        if (state.hotspots().isEmpty()) {
            graphics.drawString(font, "No clues in this layout", x, y, 0xFF84929E, false);
        }
        graphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        int limit = Math.max(0, contentHeight() - getHeight());
        scroll = Math.max(0, Math.min(limit, scroll - (int) Math.round(scrollY * 13.0D)));
        return true;
    }

    private int contentHeight() {
        int height = 4;
        int wrapWidth = Math.max(32, getWidth() - 13);
        for (InspectionHotspot hotspot : state.hotspots()) {
            height += 23;
            if (state.isDiscovered(hotspot) && !hotspot.description().isBlank()) {
                height += font.split(Component.literal(hotspot.description()), wrapWidth).size() * 11;
            }
        }
        return height;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
