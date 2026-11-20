package com.ludwici.integratedlore.client;

import com.ludwici.anchorui.api.LoreEntity;
import com.ludwici.anchorui.client.LoreLoader;
import com.ludwici.integratedlore.IntegratedLore;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = IntegratedLore.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {
    private record Pending(ItemStack stack, ResourceLocation document) {
    }

    private static Pending pending;

    private ClientGameEvents() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("integratedlore")
                .then(Commands.literal("inspect").executes(context -> inspectHeldItem()))
                .then(Commands.literal("demo").executes(context -> inspect(new ItemStack(Items.CRAFTING_TABLE)))));
    }

    private static int inspectHeldItem() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.player.getMainHandItem().isEmpty()) {
            if (client.player != null) {
                client.player.displayClientMessage(Component.literal("Hold an item to inspect it."), false);
            }
            return 0;
        }
        return inspect(client.player.getMainHandItem());
    }

    private static int inspect(ItemStack item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.getItem());
        ResourceLocation document = ResourceLocation.fromNamespaceAndPath(IntegratedLore.MOD_ID,
                "screens/" + itemId.getNamespace() + "_" + itemId.getPath());
        if (!LoreLoader.INSTANCE.getData().containsKey(document)) {
            document = ResourceLocation.fromNamespaceAndPath(IntegratedLore.MOD_ID, "screens/default");
        }
        pending = new Pending(item.copy(), document);
        return 1;
    }

    @SubscribeEvent
    public static void afterClientTick(ClientTickEvent.Post event) {
        if (pending == null) {
            return;
        }
        Pending opening = pending;
        pending = null;
        Minecraft client = Minecraft.getInstance();
        LoreEntity document = LoreLoader.INSTANCE.getData().get(opening.document());
        if (document == null) {
            if (client.player != null) {
                client.player.displayClientMessage(Component.literal("IntegratedLore inspection layout is missing: " + opening.document()), false);
            }
            return;
        }
        client.setScreen(new InspectionScreen(client.screen, opening.stack(), document, opening.document()));
    }
}
