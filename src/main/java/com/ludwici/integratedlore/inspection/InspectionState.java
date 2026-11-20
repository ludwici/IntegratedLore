package com.ludwici.integratedlore.inspection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ludwici.anchorui.api.LoreEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class InspectionState {
    private static final ResourceLocation VIEWER_TYPE = ResourceLocation.fromNamespaceAndPath("integratedlore", "viewer");

    private final ItemStack item;
    private final List<InspectionHotspot> hotspots;
    private final Set<String> discovered = new LinkedHashSet<>();
    private float yaw = 25.0F;
    private float pitch = 15.0F;

    public InspectionState(ItemStack item, LoreEntity entity) {
        this.item = item.copy();
        List<InspectionHotspot> parsed = new ArrayList<>();
        Set<String> ids = new LinkedHashSet<>();
        for (LoreEntity.UiElement element : entity.entities) {
            if (!VIEWER_TYPE.equals(element.type) || !element.settings.has("clues")
                    || !element.settings.get("clues").isJsonArray()) {
                continue;
            }
            JsonArray clues = element.settings.getAsJsonArray("clues");
            for (JsonElement clue : clues) {
                if (!clue.isJsonObject()) {
                    throw new IllegalArgumentException("Inspection clue must be an object");
                }
                InspectionHotspot hotspot = InspectionHotspot.fromJson(clue.getAsJsonObject());
                if (!ids.add(hotspot.id())) {
                    throw new IllegalArgumentException("Duplicate inspection clue: " + hotspot.id());
                }
                parsed.add(hotspot);
            }
        }
        hotspots = Collections.unmodifiableList(parsed);
        updateDiscovery();
    }

    public ItemStack item() {
        return item;
    }

    public List<InspectionHotspot> hotspots() {
        return hotspots;
    }

    public boolean isDiscovered(InspectionHotspot hotspot) {
        return discovered.contains(hotspot.id());
    }

    public int discoveredCount() {
        return discovered.size();
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public void rotate(float yawDelta, float pitchDelta) {
        yaw = ViewAngles.normalize(yaw + yawDelta);
        pitch = Math.max(-85.0F, Math.min(85.0F, pitch + pitchDelta));
        updateDiscovery();
    }

    public void resetView() {
        yaw = 25.0F;
        pitch = 15.0F;
        updateDiscovery();
    }

    private void updateDiscovery() {
        for (InspectionHotspot hotspot : hotspots) {
            if (hotspot.visibleAt(yaw, pitch)) {
                discovered.add(hotspot.id());
            }
        }
    }
}
