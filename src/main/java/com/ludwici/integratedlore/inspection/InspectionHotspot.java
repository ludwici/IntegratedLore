package com.ludwici.integratedlore.inspection;

import com.google.gson.JsonObject;

public record InspectionHotspot(String id, String title, String description, float yaw, float pitch, float tolerance,
                                float markerX, float markerY) {
    public static InspectionHotspot fromJson(JsonObject json) {
        String id = string(json, "id", "");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Inspection clue must define a nonempty id");
        }
        String title = string(json, "title", id);
        String description = string(json, "description", "");
        float yaw = number(json, "yaw", 0.0F);
        float pitch = number(json, "pitch", 0.0F);
        float tolerance = number(json, "tolerance", 14.0F);
        if (!Float.isFinite(yaw) || !Float.isFinite(pitch) || !Float.isFinite(tolerance)
                || tolerance <= 0.0F || tolerance > 90.0F || Math.abs(pitch) > 180.0F) {
            throw new IllegalArgumentException("Invalid viewing angles or tolerance for clue '" + id + "'");
        }
        float markerX = number(json, "markerX", 0.0F);
        float markerY = number(json, "markerY", 0.0F);
        if (!Float.isFinite(markerX) || !Float.isFinite(markerY)
                || Math.abs(markerX) > 1.0F || Math.abs(markerY) > 1.0F) {
            throw new IllegalArgumentException("Invalid marker position for clue '" + id + "'");
        }
        return new InspectionHotspot(id, title, description, ViewAngles.normalize(yaw), pitch, tolerance, markerX, markerY);
    }

    private static String string(JsonObject json, String key, String fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : fallback;
    }

    private static float number(JsonObject json, String key, float fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsFloat() : fallback;
    }

    public boolean visibleAt(float currentYaw, float currentPitch) {
        return ViewAngles.matches(currentYaw, currentPitch, yaw, pitch, tolerance);
    }
}
