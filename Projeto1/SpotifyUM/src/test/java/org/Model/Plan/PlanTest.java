package org.Model.Plan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Comprehensive unit tests for the Plan hierarchy classes.
 * Tests PlanFree, PlanPremiumBase, and PlanPremiumTop.
 */
@DisplayName("Plan Classes Comprehensive Tests")
class PlanTest {

    // ==================== PlanFree Tests ====================

    @Test
    @DisplayName("PlanFree: default constructor initializes with 0 points")
    void planFree_defaultConstructor_initializesWithZeroPoints() {
        PlanFree plan = new PlanFree();
        assertEquals(0, plan.getPoints());
    }

    @Test
    @DisplayName("PlanFree: copy constructor copies points")
    void planFree_copyConstructor_copiesPoints() {
        PlanFree original = new PlanFree();
        original.setPoints(50);
        
        PlanFree copy = new PlanFree(original);
        assertEquals(50, copy.getPoints());
    }

    @Test
    @DisplayName("PlanFree: addPoints adds 5 points")
    void planFree_addPoints_adds5() {
        PlanFree plan = new PlanFree();
        plan.addPoints();
        assertEquals(5, plan.getPoints());
        
        plan.addPoints();
        assertEquals(10, plan.getPoints());
        
        plan.addPoints();
        assertEquals(15, plan.getPoints());
    }

    @Test
    @DisplayName("PlanFree: addPoints multiple times")
    void planFree_addPoints_multipleTimes() {
        PlanFree plan = new PlanFree();
        for (int i = 0; i < 10; i++) {
            plan.addPoints();
        }
        assertEquals(50, plan.getPoints());
    }

    @Test
    @DisplayName("PlanFree: canAccessLibrary returns false")
    void planFree_canAccessLibrary_returnsFalse() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canAccessLibrary());
    }

    @Test
    @DisplayName("PlanFree: canSkip returns false")
    void planFree_canSkip_returnsFalse() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canSkip());
    }

    @Test
    @DisplayName("PlanFree: canChooseWhatToPlay returns false")
    void planFree_canChooseWhatToPlay_returnsFalse() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canChooseWhatToPlay());
    }

    @Test
    @DisplayName("PlanFree: hasAccessToFavorites returns false")
    void planFree_hasAccessToFavorites_returnsFalse() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    @DisplayName("PlanFree: all permissions are false")
    void planFree_allPermissions_areFalse() {
        PlanFree plan = new PlanFree();
        assertFalse(plan.canAccessLibrary());
        assertFalse(plan.canSkip());
        assertFalse(plan.canChooseWhatToPlay());
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    @DisplayName("PlanFree: toString includes plan name and points")
    void planFree_toString_format() {
        PlanFree plan = new PlanFree();
        plan.setPoints(25);
        String str = plan.toString();
        
        assertTrue(str.contains("Free"));
        assertTrue(str.contains("25"));
    }

    @Test
    @DisplayName("PlanFree: getPlanName returns 'Free'")
    void planFree_getPlanName_returnsFree() {
        PlanFree plan = new PlanFree();
        assertEquals("Free", plan.getPlanName());
    }

    @Test
    @DisplayName("PlanFree: setPoints and getPoints work correctly")
    void planFree_setAndGetPoints() {
        PlanFree plan = new PlanFree();
        plan.setPoints(100);
        assertEquals(100, plan.getPoints());
        
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
        
        plan.setPoints(999);
        assertEquals(999, plan.getPoints());
    }

    @Test
    @DisplayName("PlanFree: copy constructor with different plan types")
    void planFree_copyConstructor_differentPlans() {
        PlanPremiumBase base = new PlanPremiumBase();
        base.setPoints(50);
        
        PlanFree copiedFree = new PlanFree(base);
        assertEquals(50, copiedFree.getPoints());
    }

    // ==================== PlanPremiumBase Tests ====================

    @Test
    @DisplayName("PlanPremiumBase: default constructor initializes with 0 points")
    void planPremiumBase_defaultConstructor_initializesWithZeroPoints() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertEquals(0, plan.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumBase: copy constructor copies points")
    void planPremiumBase_copyConstructor_copiesPoints() {
        PlanFree free = new PlanFree();
        free.setPoints(75);
        
        PlanPremiumBase base = new PlanPremiumBase(free);
        assertEquals(75, base.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumBase: addPoints adds 10 points")
    void planPremiumBase_addPoints_adds10() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.addPoints();
        assertEquals(10, plan.getPoints());
        
        plan.addPoints();
        assertEquals(20, plan.getPoints());
        
        plan.addPoints();
        assertEquals(30, plan.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumBase: addPoints multiple times")
    void planPremiumBase_addPoints_multipleTimes() {
        PlanPremiumBase plan = new PlanPremiumBase();
        for (int i = 0; i < 5; i++) {
            plan.addPoints();
        }
        assertEquals(50, plan.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumBase: canAccessLibrary returns true")
    void planPremiumBase_canAccessLibrary_returnsTrue() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canAccessLibrary());
    }

    @Test
    @DisplayName("PlanPremiumBase: canSkip returns true")
    void planPremiumBase_canSkip_returnsTrue() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canSkip());
    }

    @Test
    @DisplayName("PlanPremiumBase: canChooseWhatToPlay returns true")
    void planPremiumBase_canChooseWhatToPlay_returnsTrue() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canChooseWhatToPlay());
    }

    @Test
    @DisplayName("PlanPremiumBase: hasAccessToFavorites returns false")
    void planPremiumBase_hasAccessToFavorites_returnsFalse() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    @DisplayName("PlanPremiumBase: permissions match base tier")
    void planPremiumBase_permissions_matchBaseTier() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertTrue(plan.canAccessLibrary());
        assertTrue(plan.canSkip());
        assertTrue(plan.canChooseWhatToPlay());
        assertFalse(plan.hasAccessToFavorites());
    }

    @Test
    @DisplayName("PlanPremiumBase: toString includes plan name and points")
    void planPremiumBase_toString_format() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.setPoints(40);
        String str = plan.toString();
        
        assertTrue(str.contains("PremiumBase"));
        assertTrue(str.contains("40"));
    }

    @Test
    @DisplayName("PlanPremiumBase: getPlanName returns 'PremiumBase'")
    void planPremiumBase_getPlanName_returnsPremiumBase() {
        PlanPremiumBase plan = new PlanPremiumBase();
        assertEquals("PremiumBase", plan.getPlanName());
    }

    @Test
    @DisplayName("PlanPremiumBase: setPoints and getPoints work correctly")
    void planPremiumBase_setAndGetPoints() {
        PlanPremiumBase plan = new PlanPremiumBase();
        plan.setPoints(200);
        assertEquals(200, plan.getPoints());
        
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
    }

    // ==================== PlanPremiumTop Tests ====================

    @Test
    @DisplayName("PlanPremiumTop: default constructor initializes with 0 points")
    void planPremiumTop_defaultConstructor_initializesWithZeroPoints() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertEquals(0, plan.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumTop: copy constructor adds 100 bonus points")
    void planPremiumTop_copyConstructor_adds100Bonus() {
        PlanFree free = new PlanFree();
        free.setPoints(50);
        
        PlanPremiumTop top = new PlanPremiumTop(free);
        assertEquals(150, top.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumTop: copy constructor with 0 points adds 100")
    void planPremiumTop_copyConstructor_addsBonus() {
        PlanFree free = new PlanFree();
        free.setPoints(0);
        
        PlanPremiumTop top = new PlanPremiumTop(free);
        assertEquals(100, top.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumTop: addPoints applies 2.5% bonus")
    void planPremiumTop_addPoints_appliesPercentage() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(100);
        plan.addPoints();
        
        // 100 + 0.025 * 100 = 102.5 -> (int) 102
        int expected = (int) (100 + 0.025 * 100);
        assertEquals(expected, plan.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumTop: addPoints multiple times")
    void planPremiumTop_addPoints_multipleTimes() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(1000);
        
        for (int i = 0; i < 3; i++) {
            plan.addPoints();
        }
        
        // Should accumulate points through percentage calculation
        assertTrue(plan.getPoints() >= 1070);
    }

    @Test
    @DisplayName("PlanPremiumTop: canAccessLibrary returns true")
    void planPremiumTop_canAccessLibrary_returnsTrue() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canAccessLibrary());
    }

    @Test
    @DisplayName("PlanPremiumTop: canSkip returns true")
    void planPremiumTop_canSkip_returnsTrue() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canSkip());
    }

    @Test
    @DisplayName("PlanPremiumTop: canChooseWhatToPlay returns true")
    void planPremiumTop_canChooseWhatToPlay_returnsTrue() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canChooseWhatToPlay());
    }

    @Test
    @DisplayName("PlanPremiumTop: hasAccessToFavorites returns true")
    void planPremiumTop_hasAccessToFavorites_returnsTrue() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.hasAccessToFavorites());
    }

    @Test
    @DisplayName("PlanPremiumTop: all permissions are true")
    void planPremiumTop_allPermissions_areTrue() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertTrue(plan.canAccessLibrary());
        assertTrue(plan.canSkip());
        assertTrue(plan.canChooseWhatToPlay());
        assertTrue(plan.hasAccessToFavorites());
    }

    @Test
    @DisplayName("PlanPremiumTop: toString includes plan name and points")
    void planPremiumTop_toString_format() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(150);
        String str = plan.toString();
        
        assertTrue(str.contains("PremiumTop"));
        assertTrue(str.contains("150"));
    }

    @Test
    @DisplayName("PlanPremiumTop: getPlanName returns 'PremiumTop'")
    void planPremiumTop_getPlanName_returnsPremiumTop() {
        PlanPremiumTop plan = new PlanPremiumTop();
        assertEquals("PremiumTop", plan.getPlanName());
    }

    @Test
    @DisplayName("PlanPremiumTop: setPoints and getPoints work correctly")
    void planPremiumTop_setAndGetPoints() {
        PlanPremiumTop plan = new PlanPremiumTop();
        plan.setPoints(500);
        assertEquals(500, plan.getPoints());
        
        plan.setPoints(0);
        assertEquals(0, plan.getPoints());
    }

    // ==================== Cross-Plan Comparison Tests ====================

    @Test
    @DisplayName("All plans: compare points progression for addPoints")
    void allPlans_pointsProgression_compare() {
        PlanFree free = new PlanFree();
        free.setPoints(100);
        free.addPoints();
        
        PlanPremiumBase base = new PlanPremiumBase();
        base.setPoints(100);
        base.addPoints();
        
        PlanPremiumTop top = new PlanPremiumTop();
        top.setPoints(100);
        top.addPoints();
        
        assertEquals(105, free.getPoints());
        assertEquals(110, base.getPoints());
        // 100 + 0.025*100 = 102.5 -> (int) 102
        assertEquals(102, top.getPoints());
    }

    @Test
    @DisplayName("All plans: compare permissions hierarchy")
    void allPlans_permissionsHierarchy_compare() {
        PlanFree free = new PlanFree();
        PlanPremiumBase base = new PlanPremiumBase();
        PlanPremiumTop top = new PlanPremiumTop();
        
        // Free permissions
        assertFalse(free.canAccessLibrary());
        assertFalse(free.canSkip());
        assertFalse(free.canChooseWhatToPlay());
        assertFalse(free.hasAccessToFavorites());
        
        // Base permissions (except favorites)
        assertTrue(base.canAccessLibrary());
        assertTrue(base.canSkip());
        assertTrue(base.canChooseWhatToPlay());
        assertFalse(base.hasAccessToFavorites());
        
        // Top permissions (all)
        assertTrue(top.canAccessLibrary());
        assertTrue(top.canSkip());
        assertTrue(top.canChooseWhatToPlay());
        assertTrue(top.hasAccessToFavorites());
    }

    @Test
    @DisplayName("All plans: getPlanName consistency")
    void allPlans_getPlanName_consistency() {
        PlanFree free = new PlanFree();
        PlanPremiumBase base = new PlanPremiumBase();
        PlanPremiumTop top = new PlanPremiumTop();
        
        assertEquals("Free", free.getPlanName());
        assertEquals("PremiumBase", base.getPlanName());
        assertEquals("PremiumTop", top.getPlanName());
    }

    @Test
    @DisplayName("All plans: are Serializable")
    void allPlans_shouldBeSerializable() {
        PlanFree free = new PlanFree();
        PlanPremiumBase base = new PlanPremiumBase();
        PlanPremiumTop top = new PlanPremiumTop();
        
        assertTrue(free instanceof java.io.Serializable);
        assertTrue(base instanceof java.io.Serializable);
        assertTrue(top instanceof java.io.Serializable);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("All plans: setPoints with large values")
    void allPlans_setPoints_largeValues() {
        PlanFree free = new PlanFree();
        PlanPremiumBase base = new PlanPremiumBase();
        PlanPremiumTop top = new PlanPremiumTop();
        
        int largeValue = 999999;
        free.setPoints(largeValue);
        base.setPoints(largeValue);
        top.setPoints(largeValue);
        
        assertEquals(largeValue, free.getPoints());
        assertEquals(largeValue, base.getPoints());
        assertEquals(largeValue, top.getPoints());
    }

    @Test
    @DisplayName("All plans: setPoints with zero")
    void allPlans_setPoints_zero() {
        PlanFree free = new PlanFree();
        PlanPremiumBase base = new PlanPremiumBase();
        PlanPremiumTop top = new PlanPremiumTop();
        
        free.setPoints(0);
        base.setPoints(0);
        top.setPoints(0);
        
        assertEquals(0, free.getPoints());
        assertEquals(0, base.getPoints());
        assertEquals(0, top.getPoints());
    }

    @Test
    @DisplayName("PlanFree: repeated addPoints accumulates correctly")
    void planFree_repeatedAddPoints_accumulates() {
        PlanFree plan = new PlanFree();
        for (int i = 0; i < 20; i++) {
            plan.addPoints();
        }
        assertEquals(100, plan.getPoints());
    }

    @Test
    @DisplayName("PlanPremiumBase: repeated addPoints accumulates correctly")
    void planPremiumBase_repeatedAddPoints_accumulates() {
        PlanPremiumBase plan = new PlanPremiumBase();
        for (int i = 0; i < 10; i++) {
            plan.addPoints();
        }
        assertEquals(100, plan.getPoints());
    }

    @Test
    @DisplayName("PlanFree: toString works with various point values")
    void planFree_toString_variousPoints() {
        PlanFree plan = new PlanFree();
        
        plan.setPoints(0);
        String str0 = plan.toString();
        assertNotNull(str0);
        assertTrue(str0.contains("Free"));
        
        plan.setPoints(100);
        String str100 = plan.toString();
        assertNotNull(str100);
        assertTrue(str100.contains("Free"));
        
        plan.setPoints(1000);
        String str1000 = plan.toString();
        assertNotNull(str1000);
        assertTrue(str1000.contains("Free"));
    }

    @Test
    @DisplayName("PlanPremiumBase: toString works with various point values")
    void planPremiumBase_toString_variousPoints() {
        PlanPremiumBase plan = new PlanPremiumBase();
        
        plan.setPoints(0);
        String str0 = plan.toString();
        assertNotNull(str0);
        assertTrue(str0.contains("PremiumBase"));
        
        plan.setPoints(100);
        String str100 = plan.toString();
        assertNotNull(str100);
        assertTrue(str100.contains("PremiumBase"));
        
        plan.setPoints(1000);
        String str1000 = plan.toString();
        assertNotNull(str1000);
        assertTrue(str1000.contains("PremiumBase"));
    }

    @Test
    @DisplayName("PlanPremiumTop: toString works with various point values")
    void planPremiumTop_toString_variousPoints() {
        PlanPremiumTop plan = new PlanPremiumTop();
        
        plan.setPoints(0);
        String str0 = plan.toString();
        assertNotNull(str0);
        assertTrue(str0.contains("PremiumTop"));
        
        plan.setPoints(100);
        String str100 = plan.toString();
        assertNotNull(str100);
        assertTrue(str100.contains("PremiumTop"));
        
        plan.setPoints(1000);
        String str1000 = plan.toString();
        assertNotNull(str1000);
        assertTrue(str1000.contains("PremiumTop"));
    }
}