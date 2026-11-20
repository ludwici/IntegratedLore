package com.ludwici.integratedlore.client;

import com.ludwici.anchorui.api.LoreEntity;
import com.ludwici.anchorui.client.gui.ItemLoreScreen;
import com.ludwici.integratedlore.inspection.InspectionState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class InspectionScreen extends ItemLoreScreen {
    private final InspectionState state;

    public InspectionScreen(Screen previous, ItemStack item, LoreEntity document, ResourceLocation documentId) {
        super(previous, item, document, documentId);
        state = new InspectionState(item, document);
    }

    public InspectionState inspection() {
        return state;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xF4080C12);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
