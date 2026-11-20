package com.ludwici.integratedlore.inspection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ludwici.anchorui.api.LoreEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InspectionState {
    private static final ResourceLocation VIEWER_TYPE = ResourceLocation.fromNamespaceAndPath("integratedlore", "viewer");

    private final ItemStack item;
    private final List<InspectionHotspot> hotspots;
    private static final long REVEAL_DURATION_NANOS = 280_000_000L;
    private final Set<String> discovered = new LinkedHashSet<>();
    private final Map<String, Long> discoveryTimes = new HashMap<>();
    private float yaw = 45.0F;
    private float pitch = 0.0F;
    private float zoom = 4.0F;

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

    public float revealProgress(InspectionHotspot hotspot) {
        Long discoveryTime = discoveryTimes.get(hotspot.id());
        if (discoveryTime == null) {
            return isDiscovered(hotspot) ? 1.0F : 0.0F;
        }
        float progress = Math.max(0.0F, Math.min(1.0F, (System.nanoTime() - discoveryTime) / (float) REVEAL_DURATION_NANOS));
        return progress * progress * (3.0F - 2.0F * progress);
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public float zoom() {
        return zoom;
    }

    public void zoom(float delta) {
        zoom = Math.max(2.0F, Math.min(10.0F, zoom + delta));
    }

    public void rotate(float yawDelta, float pitchDelta) {
        yaw = ViewAngles.normalize(yaw + yawDelta);
        pitch = ViewAngles.normalize(pitch + pitchDelta);
        updateDiscovery();
    }

    public void resetView() {
        yaw = 45.0F;
        pitch = 0.0F;
        zoom = 4.0F;
        updateDiscovery();
    }

    private void updateDiscovery() {
        for (InspectionHotspot hotspot : hotspots) {
            if (hotspot.visibleAt(yaw, pitch) && discovered.add(hotspot.id())) {
                discoveryTimes.put(hotspot.id(), System.nanoTime());
            }
        }
    }
}
