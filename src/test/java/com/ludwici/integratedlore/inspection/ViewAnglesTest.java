package com.ludwici.integratedlore.inspection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewAnglesTest {
    @Test
    void wrapsAcrossHalfTurn() {
        assertEquals(2.0F, ViewAngles.distance(179.0F, -179.0F));
        assertEquals(0.0F, ViewAngles.distance(360.0F, 0.0F));
    }

    @Test
    void requiresBothAxesInRange() {
        assertTrue(ViewAngles.matches(89.0F, 12.0F, 90.0F, 10.0F, 3.0F));
        assertFalse(ViewAngles.matches(89.0F, 20.0F, 90.0F, 10.0F, 3.0F));
        assertFalse(ViewAngles.matches(80.0F, 12.0F, 90.0F, 10.0F, 3.0F));
    }
}
