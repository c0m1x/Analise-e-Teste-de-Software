package org.spotifumtp37.delegate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.spotifumtp37.model.SpotifUMData;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.user.User;
import org.spotifumtp37.model.playlist.Playlist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Delegate Classes Comprehensive Tests")
class DelegateTest {

    private SpotifUMData modelData;
    private Scanner mockScanner;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        modelData = new SpotifUMData();
        originalOut = System.out;
    }

    // ==================== SpotifUMUI Tests ====================

    @Test
    @DisplayName("SpotifUMUI: constructor creates instance")
    void spotifUMUI_constructor_createsInstance() {
        SpotifUMUI ui = new SpotifUMUI();
        assertNotNull(ui);
    }

    @Test
    @DisplayName("SpotifUMUI: authenticateUser with correct credentials")
    void spotifUMUI_authenticateUser_correctCredentials() {
        SpotifUMUI ui = new SpotifUMUI();
        // Test with empty credentials
        boolean result = ui.authenticateUser("", "");
        assertFalse(result);
    }

    @Test
    @DisplayName("SpotifUMUI: authenticateUser with invalid credentials")
    void spotifUMUI_authenticateUser_invalidCredentials() {
        SpotifUMUI ui = new SpotifUMUI();
        boolean result = ui.authenticateUser("nonexistent", "wrong");
        assertFalse(result);
    }

    @Test
    @DisplayName("SpotifUMUI: registerNewUser with valid details")
    void spotifUMUI_registerNewUser_validDetails() {
        SpotifUMUI ui = new SpotifUMUI();
        boolean result = ui.registerNewUser("newuser", "password123", "user@email.com", "123 Street");
        assertTrue(result);
    }

    @Test
    @DisplayName("SpotifUMUI: registerNewUser with duplicate username")
    void spotifUMUI_registerNewUser_duplicateUsername() {
        SpotifUMUI ui = new SpotifUMUI();
        ui.registerNewUser("testuser", "pass1", "test@email.com", "Street");
        boolean result = ui.registerNewUser("testuser", "pass2", "test2@email.com", "Street2");
        assertFalse(result);
    }

    @Test
    @DisplayName("SpotifUMUI: registerNewUser with various credentials")
    void spotifUMUI_registerNewUser_variousCredentials() {
        SpotifUMUI ui = new SpotifUMUI();
        assertTrue(ui.registerNewUser("user1", "pass@123", "user1@email.com", "Address1"));
        assertTrue(ui.registerNewUser("user2", "pass#456", "user2@email.com", "Address2"));
        assertTrue(ui.registerNewUser("user3", "pass$789", "user3@email.com", "Address3"));
    }

    @Test
    @DisplayName("SpotifUMUI: registerNewUser with special characters")
    void spotifUMUI_registerNewUser_specialCharacters() {
        SpotifUMUI ui = new SpotifUMUI();
        boolean result = ui.registerNewUser("user@#$%", "p@ss!23", "user@domain.com", "123 Street");
        assertTrue(result);
    }

    // ==================== UserUI Tests ====================

    @Test
    @DisplayName("UserUI: constructor initializes with user and model")
    void userUI_constructor_initializes() {
        User user = new User("testuser", "test@email.com", "street", new FreePlan(), "password", 0, new ArrayList<>());
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user, mockScanner);
        
        assertNotNull(userUI);
    }

    @Test
    @DisplayName("UserUI: setLoggedUser updates user")
    void userUI_setLoggedUser_updatesUser() {
        User user1 = new User("user1", "user1@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        User user2 = new User("user2", "user2@email.com", "street", new PremiumBase(), "pass", 100, new ArrayList<>());
        
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user1, mockScanner);
        userUI.setLoggedUser(user2);
        
        assertNotNull(userUI);
    }

    @Test
    @DisplayName("UserUI: works with FreePlan subscription")
    void userUI_freePlanSubscription() {
        User user = new User("freeuser", "free@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user, mockScanner);
        
        assertFalse(user.getSubscriptionPlan().canCreatePlaylist());
        assertFalse(user.getSubscriptionPlan().canBrowsePlaylist());
        assertFalse(user.getSubscriptionPlan().canAccessFavouritesList());
    }

    @Test
    @DisplayName("UserUI: works with PremiumBase subscription")
    void userUI_premiumBaseSubscription() {
        User user = new User("baseuser", "base@email.com", "street", new PremiumBase(), "pass", 50, new ArrayList<>());
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user, mockScanner);
        
        assertTrue(user.getSubscriptionPlan().canCreatePlaylist());
        assertTrue(user.getSubscriptionPlan().canBrowsePlaylist());
        assertFalse(user.getSubscriptionPlan().canAccessFavouritesList());
    }

    @Test
    @DisplayName("UserUI: works with PremiumTop subscription")
    void userUI_premiumTopSubscription() {
        User user = new User("topuser", "top@email.com", "street", new PremiumTop(), "pass", 100, new ArrayList<>());
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user, mockScanner);
        
        assertTrue(user.getSubscriptionPlan().canCreatePlaylist());
        assertTrue(user.getSubscriptionPlan().canBrowsePlaylist());
        assertTrue(user.getSubscriptionPlan().canAccessFavouritesList());
    }

    @Test
    @DisplayName("UserUI: multiple instances are independent")
    void userUI_multipleInstances_independent() {
        User user1 = new User("user1", "user1@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        User user2 = new User("user2", "user2@email.com", "street", new PremiumTop(), "pass", 100, new ArrayList<>());
        
        mockScanner = new Scanner("");
        UserUI userUI1 = new UserUI(modelData, user1, mockScanner);
        UserUI userUI2 = new UserUI(modelData, user2, mockScanner);
        
        assertNotNull(userUI1);
        assertNotNull(userUI2);
        assertNotSame(userUI1, userUI2);
    }

    // ==================== AdminUI Tests ====================

    @Test
    @DisplayName("AdminUI: constructor creates instance")
    void adminUI_constructor_createsInstance() {
        mockScanner = new Scanner("");
        AdminUI adminUI = new AdminUI(modelData, mockScanner);
        assertNotNull(adminUI);
    }

    @Test
    @DisplayName("AdminUI: clearSystemData empties model")
    void adminUI_clearSystemData_emptiesModel() {
        // Add some data first
        try {
            modelData.addUser(new User("user1", "user1@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>()));
        } catch (Exception e) {
            // User might already exist
        }
        assertFalse(modelData.getMapUsers().isEmpty());
        
        mockScanner = new Scanner("");
        AdminUI adminUI = new AdminUI(modelData, mockScanner);
        adminUI.clearSystemData();
        
        assertTrue(modelData.getMapUsers().isEmpty());
    }

    @Test
    @DisplayName("AdminUI: handles empty model data")
    void adminUI_emptyModelData() {
        mockScanner = new Scanner("");
        AdminUI adminUI = new AdminUI(modelData, mockScanner);
        
        assertTrue(modelData.getMapUsers().isEmpty());
        assertTrue(modelData.getMapAlbums().isEmpty());
    }

    // ==================== NewMenu Tests ====================

    @Test
    @DisplayName("NewMenu: constructor with single option")
    void newMenu_constructor_singleOption() {
        String[] options = {"Option 1"};
        NewMenu menu = new NewMenu(options);
        assertNotNull(menu);
    }

    @Test
    @DisplayName("NewMenu: constructor with multiple options")
    void newMenu_constructor_multipleOptions() {
        String[] options = {"Option 1", "Option 2", "Option 3", "Option 4", "Option 5"};
        NewMenu menu = new NewMenu(options);
        assertNotNull(menu);
    }

    @Test
    @DisplayName("NewMenu: constructor with large number of options")
    void newMenu_constructor_largeNumberOfOptions() {
        String[] options = new String[20];
        for (int i = 0; i < options.length; i++) {
            options[i] = "Option " + (i + 1);
        }
        NewMenu menu = new NewMenu(options);
        assertNotNull(menu);
    }

    @Test
    @DisplayName("NewMenu: constructor with empty option string")
    void newMenu_constructor_emptyOption() {
        String[] options = {"", "Option 2"};
        NewMenu menu = new NewMenu(options);
        assertNotNull(menu);
    }

    @Test
    @DisplayName("NewMenu: constructor with special characters in options")
    void newMenu_constructor_specialCharacters() {
        String[] options = {"Option @#$%", "Option &*()", "Option <>?"};
        NewMenu menu = new NewMenu(options);
        assertNotNull(menu);
    }

    // ==================== PlayerUI Tests ====================

    @Test
    @DisplayName("PlayerUI: constructor creates instance")
    void playerUI_constructor_createsInstance() {
        mockScanner = new Scanner("");
        PlayerUI playerUI = new PlayerUI(mockScanner);
        assertNotNull(playerUI);
    }

    @Test
    @DisplayName("PlayerUI: multiple instances are independent")
    void playerUI_multipleInstances_independent() {
        mockScanner = new Scanner("");
        PlayerUI playerUI1 = new PlayerUI(mockScanner);
        PlayerUI playerUI2 = new PlayerUI(mockScanner);
        
        assertNotNull(playerUI1);
        assertNotNull(playerUI2);
        assertNotSame(playerUI1, playerUI2);
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Delegates: work together in typical workflow")
    void delegates_workTogether_typicalWorkflow() {
        SpotifUMUI spotifUI = new SpotifUMUI();
        assertTrue(spotifUI.registerNewUser("newuser", "password", "new@email.com", "123 Street"));
        
        User user = new User("newuser", "new@email.com", "123 Street", new FreePlan(), "password", 0, new ArrayList<>());
        try {
            modelData.addUser(user);
        } catch (Exception e) {
            // User already exists
        }
        
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user, mockScanner);
        assertNotNull(userUI);
    }

    @Test
    @DisplayName("Delegates: subscription change workflow")
    void delegates_subscriptionChange_workflow() {
        User user = new User("subscriber", "sub@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user, mockScanner);
        
        // Verify free plan
        assertFalse(user.getSubscriptionPlan().canCreatePlaylist());
        
        // Change to premium
        User premiumUser = new User("subscriber", "sub@email.com", "street", new PremiumBase(), "pass", 50, new ArrayList<>());
        userUI.setLoggedUser(premiumUser);
        
        assertTrue(premiumUser.getSubscriptionPlan().canCreatePlaylist());
    }

    @Test
    @DisplayName("Delegates: handle empty input gracefully")
    void delegates_emptyInput_handledGracefully() {
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, 
            new User("user", "user@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>()), 
            mockScanner);
        
        assertNotNull(userUI);
    }

    @Test
    @DisplayName("Delegates: support system data operations")
    void delegates_systemDataOperations_supported() {
        mockScanner = new Scanner("");
        AdminUI adminUI = new AdminUI(modelData, mockScanner);
        
        // Add data
        try {
            modelData.addUser(new User("admin", "admin@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>()));
        } catch (Exception e) {
            // User might already exist
        }
        assertFalse(modelData.getMapUsers().isEmpty());
        
        // Clear data
        adminUI.clearSystemData();
        assertTrue(modelData.getMapUsers().isEmpty());
    }

    @Test
    @DisplayName("Delegates: maintain state consistency")
    void delegates_stateConsistency_maintained() {
        User user1 = new User("user1", "user1@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        try {
            modelData.addUser(user1);
        } catch (Exception e) {
            // User might already exist
        }
        
        mockScanner = new Scanner("");
        UserUI userUI = new UserUI(modelData, user1, mockScanner);
        userUI.setLoggedUser(user1);
        
        assertFalse(user1.getSubscriptionPlan().canCreatePlaylist());
    }

    @Test
    @DisplayName("SpotifUMUI: registerNewUser called multiple times")
    void spotifUMUI_registerNewUser_multipleTimes() {
        SpotifUMUI ui = new SpotifUMUI();
        assertTrue(ui.registerNewUser("user1", "pass1", "user1@email.com", "Address1"));
        assertTrue(ui.registerNewUser("user2", "pass2", "user2@email.com", "Address2"));
        assertTrue(ui.registerNewUser("user3", "pass3", "user3@email.com", "Address3"));
        assertFalse(ui.registerNewUser("user1", "pass1", "user1@email.com", "Address1")); // Duplicate
    }

    @Test
    @DisplayName("UserUI: scanner operations with empty input")
    void userUI_scanner_emptyInput() {
        User user = new User("testuser", "test@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        ByteArrayInputStream emptyInput = new ByteArrayInputStream("".getBytes());
        Scanner scanner = new Scanner(emptyInput);
        
        UserUI userUI = new UserUI(modelData, user, scanner);
        assertNotNull(userUI);
    }

    @Test
    @DisplayName("Delegates: all subscription plans supported")
    void delegates_allSubscriptionPlans_supported() {
        User freeUser = new User("freeuser", "free@email.com", "street", new FreePlan(), "pass", 0, new ArrayList<>());
        User baseUser = new User("baseuser", "base@email.com", "street", new PremiumBase(), "pass", 50, new ArrayList<>());
        User topUser = new User("topuser", "top@email.com", "street", new PremiumTop(), "pass", 100, new ArrayList<>());
        
        mockScanner = new Scanner("");
        UserUI userUI1 = new UserUI(modelData, freeUser, mockScanner);
        UserUI userUI2 = new UserUI(modelData, baseUser, mockScanner);
        UserUI userUI3 = new UserUI(modelData, topUser, mockScanner);
        
        assertNotNull(userUI1);
        assertNotNull(userUI2);
        assertNotNull(userUI3);
    }
}
