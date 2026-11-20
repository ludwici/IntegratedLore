package com.ludwici.integratedlore.inspection;

public final class ViewAngles {
    private ViewAngles() {
    }

    public static float normalize(float degrees) {
        float angle = degrees % 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    public static float distance(float first, float second) {
        return Math.abs(normalize(first - second));
    }

    public static boolean matches(float yaw, float pitch, float targetYaw, float targetPitch, float tolerance) {
        return distance(yaw, targetYaw) <= tolerance && distance(pitch, targetPitch) <= tolerance;
    }
}
