package org.spotifumtp37.model.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Subscription Plans Comprehensive Tests")
class SubscriptionPlanTest {

    // ==================== FreePlan Tests ====================

    @Test
    @DisplayName("FreePlan: addPoints should add 5 points")
    void freePlan_addPoints_addsFixedBonus() {
        FreePlan plan = new FreePlan();
        
        assertEquals(5.0, plan.addPoints(0.0));
        assertEquals(10.0, plan.addPoints(5.0));
        assertEquals(100.0, plan.addPoints(95.0));
        assertEquals(5.5, plan.addPoints(0.5));
    }

    @Test
    @DisplayName("FreePlan: addPoints with negative points")
    void freePlan_addPoints_withNegativePoints() {
        FreePlan plan = new FreePlan();
        
        assertEquals(3.0, plan.addPoints(-2.0));
        assertEquals(-5.0, plan.addPoints(-10.0));
    }

    @Test
    @DisplayName("FreePlan: addPoints with large numbers")
    void freePlan_addPoints_withLargeNumbers() {
        FreePlan plan = new FreePlan();
        
        assertEquals(1000005.0, plan.addPoints(1000000.0));
        // Test with a large but valid number
        assertEquals(100005.0, plan.addPoints(100000.0));
    }

    @Test
    @DisplayName("FreePlan: canCreatePlaylist should return false")
    void freePlan_canCreatePlaylist_returnsFalse() {
        FreePlan plan = new FreePlan();
        
        assertFalse(plan.canCreatePlaylist());
    }

    @Test
    @DisplayName("FreePlan: canBrowsePlaylist should return false")
    void freePlan_canBrowsePlaylist_returnsFalse() {
        FreePlan plan = new FreePlan();
        
        assertFalse(plan.canBrowsePlaylist());
    }

    @Test
    @DisplayName("FreePlan: canAccessFavouritesList should return false")
    void freePlan_canAccessFavouritesList_returnsFalse() {
        FreePlan plan = new FreePlan();
        
        assertFalse(plan.canAccessFavouritesList());
    }

    @Test
    @DisplayName("FreePlan: all permission methods should be false")
    void freePlan_allPermissions_shouldBeFalse() {
        FreePlan plan = new FreePlan();
        
        assertFalse(plan.canCreatePlaylist());
        assertFalse(plan.canBrowsePlaylist());
        assertFalse(plan.canAccessFavouritesList());
    }

    @Test
    @DisplayName("FreePlan: can create multiple instances")
    void freePlan_multipleInstances_shouldBeIndependent() {
        FreePlan plan1 = new FreePlan();
        FreePlan plan2 = new FreePlan();
        
        assertEquals(10.0, plan1.addPoints(5.0));
        assertEquals(10.0, plan2.addPoints(5.0));
        assertNotSame(plan1, plan2);
    }

    // ==================== PremiumBase Tests ====================

    @Test
    @DisplayName("PremiumBase: addPoints should add 10 points")
    void premiumBase_addPoints_addsFixedBonus() {
        PremiumBase plan = new PremiumBase();
        
        assertEquals(10.0, plan.addPoints(0.0));
        assertEquals(15.0, plan.addPoints(5.0));
        assertEquals(110.0, plan.addPoints(100.0));
        assertEquals(10.5, plan.addPoints(0.5));
    }

    @Test
    @DisplayName("PremiumBase: addPoints with negative points")
    void premiumBase_addPoints_withNegativePoints() {
        PremiumBase plan = new PremiumBase();
        
        assertEquals(8.0, plan.addPoints(-2.0));
        assertEquals(0.0, plan.addPoints(-10.0));
    }

    @Test
    @DisplayName("PremiumBase: addPoints with large numbers")
    void premiumBase_addPoints_withLargeNumbers() {
        PremiumBase plan = new PremiumBase();
        
        assertEquals(1000010.0, plan.addPoints(1000000.0));
    }

    @Test
    @DisplayName("PremiumBase: canCreatePlaylist should return true")
    void premiumBase_canCreatePlaylist_returnsTrue() {
        PremiumBase plan = new PremiumBase();
        
        assertTrue(plan.canCreatePlaylist());
    }

    @Test
    @DisplayName("PremiumBase: canBrowsePlaylist should return true")
    void premiumBase_canBrowsePlaylist_returnsTrue() {
        PremiumBase plan = new PremiumBase();
        
        assertTrue(plan.canBrowsePlaylist());
    }

    @Test
    @DisplayName("PremiumBase: canAccessFavouritesList should return false")
    void premiumBase_canAccessFavouritesList_returnsFalse() {
        PremiumBase plan = new PremiumBase();
        
        assertFalse(plan.canAccessFavouritesList());
    }

    @Test
    @DisplayName("PremiumBase: permissions should be create=true, browse=true, favorites=false")
    void premiumBase_permissions_shouldMatch() {
        PremiumBase plan = new PremiumBase();
        
        assertTrue(plan.canCreatePlaylist());
        assertTrue(plan.canBrowsePlaylist());
        assertFalse(plan.canAccessFavouritesList());
    }

    @Test
    @DisplayName("PremiumBase: can create multiple instances")
    void premiumBase_multipleInstances_shouldBeIndependent() {
        PremiumBase plan1 = new PremiumBase();
        PremiumBase plan2 = new PremiumBase();
        
        assertEquals(15.0, plan1.addPoints(5.0));
        assertEquals(15.0, plan2.addPoints(5.0));
        assertNotSame(plan1, plan2);
    }

    // ==================== PremiumTop Tests ====================

    @Test
    @DisplayName("PremiumTop: addPoints should apply 2.5% bonus")
    void premiumTop_addPoints_appliesPercentageBonus() {
        PremiumTop plan = new PremiumTop();
        
        assertEquals(0.0, plan.addPoints(0.0));
        assertEquals(5.125, plan.addPoints(5.0), 0.0001);
        assertEquals(102.5, plan.addPoints(100.0), 0.0001);
        assertEquals(1.025, plan.addPoints(1.0), 0.0001);
    }

    @Test
    @DisplayName("PremiumTop: addPoints with exact values")
    void premiumTop_addPoints_withExactValues() {
        PremiumTop plan = new PremiumTop();
        
        // 1.025 * 1000 = 1025
        assertEquals(1025.0, plan.addPoints(1000.0), 0.0001);
        // 1.025 * 40 = 41
        assertEquals(41.0, plan.addPoints(40.0), 0.0001);
    }

    @Test
    @DisplayName("PremiumTop: addPoints with negative points")
    void premiumTop_addPoints_withNegativePoints() {
        PremiumTop plan = new PremiumTop();
        
        // 1.025 * -10 = -10.25
        assertEquals(-10.25, plan.addPoints(-10.0), 0.0001);
    }

    @Test
    @DisplayName("PremiumTop: addPoints with decimal points")
    void premiumTop_addPoints_withDecimals() {
        PremiumTop plan = new PremiumTop();
        
        assertEquals(1.001 * 1.025, plan.addPoints(1.001), 0.0001);
        assertEquals(5.04 * 1.025, plan.addPoints(5.04), 0.0001);
    }

    @Test
    @DisplayName("PremiumTop: addPoints is exactly 1.025x multiplier")
    void premiumTop_addPoints_verifyMultiplier() {
        PremiumTop plan = new PremiumTop();
        double input = 123.45;
        double expected = 123.45 * 1.025;
        
        assertEquals(expected, plan.addPoints(input), 0.0001);
    }

    @Test
    @DisplayName("PremiumTop: canCreatePlaylist should return true")
    void premiumTop_canCreatePlaylist_returnsTrue() {
        PremiumTop plan = new PremiumTop();
        
        assertTrue(plan.canCreatePlaylist());
    }

    @Test
    @DisplayName("PremiumTop: canBrowsePlaylist should return true")
    void premiumTop_canBrowsePlaylist_returnsTrue() {
        PremiumTop plan = new PremiumTop();
        
        assertTrue(plan.canBrowsePlaylist());
    }

    @Test
    @DisplayName("PremiumTop: canAccessFavouritesList should return true")
    void premiumTop_canAccessFavouritesList_returnsTrue() {
        PremiumTop plan = new PremiumTop();
        
        assertTrue(plan.canAccessFavouritesList());
    }

    @Test
    @DisplayName("PremiumTop: all permissions should be true")
    void premiumTop_allPermissions_shouldBeTrue() {
        PremiumTop plan = new PremiumTop();
        
        assertTrue(plan.canCreatePlaylist());
        assertTrue(plan.canBrowsePlaylist());
        assertTrue(plan.canAccessFavouritesList());
    }

    @Test
    @DisplayName("PremiumTop: can create multiple instances")
    void premiumTop_multipleInstances_shouldBeIndependent() {
        PremiumTop plan1 = new PremiumTop();
        PremiumTop plan2 = new PremiumTop();
        
        assertEquals(5.125, plan1.addPoints(5.0), 0.0001);
        assertEquals(5.125, plan2.addPoints(5.0), 0.0001);
        assertNotSame(plan1, plan2);
    }

    // ==================== Cross-Plan Comparison Tests ====================

    @Test
    @DisplayName("Compare all plans: addPoints consistency")
    void allPlans_addPoints_shouldFollowBusinessLogic() {
        FreePlan free = new FreePlan();
        PremiumBase base = new PremiumBase();
        PremiumTop top = new PremiumTop();
        
        double basePoints = 100.0;
        
        // Free: +5
        // Base: +10
        // Top: *1.025
        double freeResult = free.addPoints(basePoints);
        double baseResult = base.addPoints(basePoints);
        double topResult = top.addPoints(basePoints);
        
        assertEquals(105.0, freeResult);
        assertEquals(110.0, baseResult);
        assertEquals(102.5, topResult, 0.0001);
    }

    @Test
    @DisplayName("Compare all plans: permissions hierarchy")
    void allPlans_permissions_shouldShowHierarchy() {
        FreePlan free = new FreePlan();
        PremiumBase base = new PremiumBase();
        PremiumTop top = new PremiumTop();
        
        // Free: all false
        assertEquals(false, free.canCreatePlaylist());
        assertEquals(false, free.canBrowsePlaylist());
        assertEquals(false, free.canAccessFavouritesList());
        
        // Base: create=true, browse=true, favorites=false
        assertEquals(true, base.canCreatePlaylist());
        assertEquals(true, base.canBrowsePlaylist());
        assertEquals(false, base.canAccessFavouritesList());
        
        // Top: all true
        assertEquals(true, top.canCreatePlaylist());
        assertEquals(true, top.canBrowsePlaylist());
        assertEquals(true, top.canAccessFavouritesList());
    }

    @Test
    @DisplayName("All plans: verify each is Serializable")
    void allPlans_shouldBeSerializable() {
        SubscriptionPlan free = new FreePlan();
        SubscriptionPlan base = new PremiumBase();
        SubscriptionPlan top = new PremiumTop();
        
        assertTrue(free instanceof java.io.Serializable);
        assertTrue(base instanceof java.io.Serializable);
        assertTrue(top instanceof java.io.Serializable);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("All plans: addPoints with zero")
    void allPlans_addPoints_withZero() {
        FreePlan free = new FreePlan();
        PremiumBase base = new PremiumBase();
        PremiumTop top = new PremiumTop();
        
        assertEquals(5.0, free.addPoints(0.0));
        assertEquals(10.0, base.addPoints(0.0));
        assertEquals(0.0, top.addPoints(0.0));
    }

    @Test
    @DisplayName("All plans: addPoints with one")
    void allPlans_addPoints_withOne() {
        FreePlan free = new FreePlan();
        PremiumBase base = new PremiumBase();
        PremiumTop top = new PremiumTop();
        
        assertEquals(6.0, free.addPoints(1.0));
        assertEquals(11.0, base.addPoints(1.0));
        assertEquals(1.025, top.addPoints(1.0), 0.0001);
    }

    @Test
    @DisplayName("FreePlan: repeated addPoints calls are consistent")
    void freePlan_repeatedCalls_consistent() {
        FreePlan plan = new FreePlan();
        double initial = 100.0;
        double result1 = plan.addPoints(initial);
        double result2 = plan.addPoints(initial);
        
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("PremiumBase: repeated addPoints calls are consistent")
    void premiumBase_repeatedCalls_consistent() {
        PremiumBase plan = new PremiumBase();
        double initial = 100.0;
        double result1 = plan.addPoints(initial);
        double result2 = plan.addPoints(initial);
        
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("PremiumTop: repeated addPoints calls are consistent")
    void premiumTop_repeatedCalls_consistent() {
        PremiumTop plan = new PremiumTop();
        double initial = 100.0;
        double result1 = plan.addPoints(initial);
        double result2 = plan.addPoints(initial);
        
        assertEquals(result1, result2, 0.0001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, 5.0, 10.0, 50.0, 100.0, 1000.0})
    @DisplayName("FreePlan: addPoints is consistent for various inputs")
    void freePlan_addPoints_parameterized(double points) {
        FreePlan plan = new FreePlan();
        assertEquals(points + 5.0, plan.addPoints(points));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, 5.0, 10.0, 50.0, 100.0, 1000.0})
    @DisplayName("PremiumBase: addPoints is consistent for various inputs")
    void premiumBase_addPoints_parameterized(double points) {
        PremiumBase plan = new PremiumBase();
        assertEquals(points + 10.0, plan.addPoints(points));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, 5.0, 10.0, 50.0, 100.0, 1000.0})
    @DisplayName("PremiumTop: addPoints is consistent for various inputs")
    void premiumTop_addPoints_parameterized(double points) {
        PremiumTop plan = new PremiumTop();
        assertEquals(points * 1.025, plan.addPoints(points), 0.0001);
    }
}
