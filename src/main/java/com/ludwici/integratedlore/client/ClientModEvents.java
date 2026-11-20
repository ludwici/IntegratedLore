package com.ludwici.integratedlore.client;

import com.ludwici.anchorui.api.widget.AnchorWidgetRegistry;
import com.ludwici.integratedlore.IntegratedLore;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = IntegratedLore.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            AnchorWidgetRegistry.register(ResourceLocation.fromNamespaceAndPath(IntegratedLore.MOD_ID, "viewer"), (context, element, style) -> {
                if (!(context.screen() instanceof InspectionScreen screen)) {
                    throw new IllegalStateException("integratedlore:viewer requires an InspectionScreen");
                }
                return new InspectionViewerWidget(screen.inspection());
            });
            AnchorWidgetRegistry.register(ResourceLocation.fromNamespaceAndPath(IntegratedLore.MOD_ID, "journal"), (context, element, style) -> {
                if (!(context.screen() instanceof InspectionScreen screen)) {
                    throw new IllegalStateException("integratedlore:journal requires an InspectionScreen");
                }
                return new InspectionJournalWidget(screen.inspection(), context.font());
            });
        });
    }
}
