package com.ludwici.integratedlore.client;

import com.ludwici.integratedlore.inspection.InspectionHotspot;
import com.ludwici.integratedlore.inspection.InspectionState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public final class InspectionViewerWidget extends AbstractWidget {
    private final InspectionState state;
    private boolean dragging;

    public InspectionViewerWidget(InspectionState state) {
        super(0, 0, 128, 128, Component.empty());
        this.state = state;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = getX() + getWidth() / 2;
        int centerY = getY() + getHeight() / 2;
        int size = Math.min(getWidth(), getHeight());
        int radius = Math.max(20, size / 3);
        graphics.fill(centerX - radius, centerY, centerX + radius, centerY + 1, 0x354D9CBA);
        graphics.fill(centerX, centerY - radius, centerX + 1, centerY + radius, 0x354D9CBA);

        if (!state.item().isEmpty()) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(centerX, centerY, 220.0F);
            float modelScale = Math.min(18.0F, Math.max(2.5F, size / 30.0F));
            pose.scale(modelScale, modelScale, modelScale);
            pose.mulPose(Axis.YP.rotationDegrees(state.yaw()));
            pose.mulPose(Axis.XP.rotationDegrees(state.pitch()));
            graphics.renderItem(state.item(), -8, -8);
            pose.popPose();
        }

        for (InspectionHotspot hotspot : state.hotspots()) {
            if (!hotspot.visibleAt(state.yaw(), state.pitch())) {
                continue;
            }
            int markerX = centerX + Math.round(hotspot.markerX() * radius);
            int markerY = centerY + Math.round(hotspot.markerY() * radius);
            int color = state.isDiscovered(hotspot) ? 0xFF88DBC9 : 0xFFFFD181;
            graphics.fill(markerX - 5, markerY - 1, markerX + 6, markerY + 2, 0xDD0B1923);
            graphics.fill(markerX - 1, markerY - 5, markerX + 2, markerY + 6, 0xDD0B1923);
            graphics.fill(markerX - 3, markerY, markerX + 4, markerY + 1, color);
            graphics.fill(markerX, markerY - 3, markerX + 1, markerY + 4, color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !isMouseOver(mouseX, mouseY)) {
            return false;
        }
        dragging = true;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!dragging || button != 0) {
            return false;
        }
        state.rotate((float) dragX * 0.65F, (float) dragY * 0.65F);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0 || !dragging) {
            return false;
        }
        dragging = false;
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
