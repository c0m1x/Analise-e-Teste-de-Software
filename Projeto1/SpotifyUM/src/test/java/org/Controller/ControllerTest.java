package org.Controller;

import org.Model.SpotifUM;
import org.Model.Plan.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Controller Comprehensive Tests")
class ControllerTest {

    private Controller controller;
    private SpotifUM spotifUM;

    @BeforeEach
    void setUp() {
        spotifUM = new SpotifUM();
        controller = new Controller(spotifUM);
    }

    // ==================== User Management Tests ====================

    @Test
    @DisplayName("Controller: addNewUser with valid details creates new user")
    void addNewUser_whenNew_returnsSuccessMessage() {
        String msg = controller.addNewUser("alice", "a@b.com", "addr", "pw");
        assertTrue(msg.contains("registado") || msg.contains("sucesso"));
        assertTrue(controller.getSpotifUM().userExists("alice"));
    }

    @Test
    @DisplayName("Controller: addNewUser with duplicate username returns error")
    void addNewUser_whenDuplicate_returnsWarningMessage() {
        controller.addNewUser("bob", "b@c.com", "addr", "pw");
        String msg = controller.addNewUser("bob", "b@c.com", "addr", "pw");
        assertTrue(msg.contains("Já existe") || msg.contains("existe um utilizador"));
    }

    @Test
    @DisplayName("Controller: addNewUser with special characters")
    void addNewUser_specialCharacters() {
        String msg = controller.addNewUser("user@#$", "test@email.com", "street", "pass@123");
        assertNotNull(msg);
    }

    @Test
    @DisplayName("Controller: addNewUser multiple different users")
    void addNewUser_multipleUsers() {
        assertTrue(controller.addNewUser("user1", "u1@email.com", "addr1", "pw1").contains("sucesso"));
        assertTrue(controller.addNewUser("user2", "u2@email.com", "addr2", "pw2").contains("sucesso"));
        assertTrue(controller.addNewUser("user3", "u3@email.com", "addr3", "pw3").contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: loginWithMessage with correct credentials")
    void loginWithMessage_successAndFailure() {
        SpotifUM model = new SpotifUM();
        model.addNewUser("carlos", "c@d.com", "addr", "secret");

        Controller ctrl = new Controller(model);

        assertFalse(ctrl.loginWithMessage("carlos", "wrong"));
        assertFalse(ctrl.loginWithMessage("noone", "x"));
        assertTrue(ctrl.loginWithMessage("carlos", "secret"));
        assertNotNull(ctrl.getSpotifUM().getCurrentUser());
        assertEquals("carlos", ctrl.getSpotifUM().getCurrentUser().getUsername());
    }

    @Test
    @DisplayName("Controller: loginWithMessage with invalid credentials")
    void loginWithMessage_invalidCredentials() {
        assertFalse(controller.loginWithMessage("nonexistent", "password"));
        assertFalse(controller.loginWithMessage("", ""));
    }

    // ==================== Album Management Tests ====================

    @Test
    @DisplayName("Controller: createAlbum creates new album")
    void createAlbum_addsNewAlbum() {
        String msg = controller.createAlbum("TestAlbum", "TestArtist");
        assertTrue(msg.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: createAlbum multiple albums")
    void createAlbum_multipleAlbums() {
        String msg1 = controller.createAlbum("Album1", "Artist1");
        String msg2 = controller.createAlbum("Album2", "Artist2");
        String msg3 = controller.createAlbum("Album3", "Artist3");
        
        assertTrue(msg1.contains("sucesso"));
        assertTrue(msg2.contains("sucesso"));
        assertTrue(msg3.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: albumExists returns true for existing album")
    void albumExists_existingAlbum_returnsTrue() {
        controller.createAlbum("ExistAlbum", "Artist");
        assertTrue(controller.albumExists("ExistAlbum"));
    }

    @Test
    @DisplayName("Controller: albumExists returns false for non-existing album")
    void albumExists_nonExistingAlbum_returnsFalse() {
        assertFalse(controller.albumExists("NonExistent"));
    }

    @Test
    @DisplayName("Controller: createAlbum with special characters")
    void createAlbum_specialCharacters() {
        String msg = controller.createAlbum("Album @#$", "Artist &*()");
        assertNotNull(msg);
    }

    // ==================== Music Management Tests ====================

    @Test
    @DisplayName("Controller: addMusic with valid parameters")
    void addMusic_validParameters() {
        controller.createAlbum("TestAlbum", "TestArtist");
        String msg = controller.addMusic("TestMusic", "TestArtist", "TestPublisher", 
                                        "Lyrics", "MusicalFigures", "Pop", "TestAlbum", 
                                        180, false, "");
        assertTrue(msg.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: addMusic with explicit flag")
    void addMusic_explicitFlag() {
        controller.createAlbum("Album", "Artist");
        String msg = controller.addMusic("Song", "Artist", "Publisher", "Lyrics", "Figures", 
                                        "Genre", "Album", 200, true, "url");
        assertTrue(msg.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: addMusic with different genres")
    void addMusic_differentGenres() {
        controller.createAlbum("Album", "Artist");
        
        String msg1 = controller.addMusic("Song1", "Artist", "Pub", "Lyrics", "Fig", "Pop", "Album", 180, false, "");
        String msg2 = controller.addMusic("Song2", "Artist", "Pub", "Lyrics", "Fig", "Rock", "Album", 200, false, "");
        String msg3 = controller.addMusic("Song3", "Artist", "Pub", "Lyrics", "Fig", "Jazz", "Album", 220, false, "");
        
        assertTrue(msg1.contains("sucesso"));
        assertTrue(msg2.contains("sucesso"));
        assertTrue(msg3.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: addMusic with long duration")
    void addMusic_longDuration() {
        controller.createAlbum("Album", "Artist");
        String msg = controller.addMusic("LongSong", "Artist", "Pub", "Lyrics", "Fig", 
                                        "Genre", "Album", 3600, false, "");
        assertTrue(msg.contains("sucesso"));
    }

    // ==================== User Profile Tests ====================

    @Test
    @DisplayName("Controller: changeCurrentUserUserName updates username")
    void changeCurrentUserUserName_modifiesUsername() {
        controller.addNewUser("oldname", "user@email.com", "street", "pass");
        controller.loginWithMessage("oldname", "pass");
        String msg = controller.changeCurrentUserUserName("newname");
        assertTrue(msg.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: changeCurrentUserEmail updates email")
    void changeCurrentUserEmail_modifiesEmail() {
        controller.addNewUser("user", "old@email.com", "street", "pass");
        controller.loginWithMessage("user", "pass");
        String msg = controller.changeCurrentUserEmail("new@email.com");
        assertTrue(msg.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: isPasswordCorrect verifies password")
    void isPasswordCorrect_verifiesPassword() {
        controller.addNewUser("passuser", "pass@email.com", "street", "mypassword");
        controller.loginWithMessage("passuser", "mypassword");
        assertTrue(controller.isPasswordCorrect("mypassword"));
        assertFalse(controller.isPasswordCorrect("wrongpassword"));
    }

    @Test
    @DisplayName("Controller: changeCurrentUserPassword updates password")
    void changeCurrentUserPassword_updatesPassword() {
        controller.addNewUser("user", "user@email.com", "street", "oldpass");
        controller.loginWithMessage("user", "oldpass");
        String msg = controller.changeCurrentUserPassword("newpass");
        assertTrue(msg.contains("sucesso"));
    }

    @Test
    @DisplayName("Controller: getCurrentUser returns user information")
    void getCurrentUser_returnsUserInfo() {
        controller.addNewUser("infouser", "info@email.com", "street", "pass");
        controller.loginWithMessage("infouser", "pass");
        String msg = controller.getCurrentUser();
        assertNotNull(msg);
        assertTrue(msg.contains("Perfil"));
    }

    // ==================== Plan Management Tests ====================

    @Test
    @DisplayName("Controller: setFreePlan changes to FreePlan")
    void setFreePlan_changesPlan() {
        controller.addNewUser("planuser", "plan@email.com", "street", "pass");
        controller.loginWithMessage("planuser", "pass");
        String msg = controller.setFreePlan();
        assertTrue(msg.contains("Free"));
    }

    @Test
    @DisplayName("Controller: setPremiumBasePlan changes to PremiumBase")
    void setPremiumBasePlan_changesPlan() {
        controller.addNewUser("baseuser", "base@email.com", "street", "pass");
        controller.loginWithMessage("baseuser", "pass");
        String msg = controller.setPremiumBasePlan();
        assertTrue(msg.contains("PremiumBase"));
    }

    @Test
    @DisplayName("Controller: setPremiumTopPlan changes to PremiumTop")
    void setPremiumTopPlan_changesPlan() {
        controller.addNewUser("topuser", "top@email.com", "street", "pass");
        controller.loginWithMessage("topuser", "pass");
        String msg = controller.setPremiumTopPlan();
        assertTrue(msg.contains("topo") || msg.contains("PremiumTop"));
    }

    @Test
    @DisplayName("Controller: plan changes in sequence")
    void planChanges_inSequence() {
        controller.addNewUser("sequser", "seq@email.com", "street", "pass");
        controller.loginWithMessage("sequser", "pass");
        
        String msg1 = controller.setFreePlan();
        assertTrue(msg1.contains("Free"));
        
        String msg2 = controller.setPremiumBasePlan();
        assertTrue(msg2.contains("PremiumBase"));
        
        String msg3 = controller.setPremiumTopPlan();
        assertTrue(msg3.contains("topo") || msg3.contains("PremiumTop"));
    }

    // ==================== Data Import/Export Tests ====================

    @Test
    @DisplayName("Controller: exportAndImportModel roundtrip")
    void exportAndImportModel_roundTrip(@TempDir Path tempDir) {
        Controller controller1 = new Controller(new SpotifUM());
        controller1.getSpotifUM().addNewUser("u1", "u1@x.com", "addr", "pw");

        Path file = tempDir.resolve("db.ser");
        String exportMsg = controller1.exportModel(file.toString());
        assertTrue(exportMsg.contains("exportada") || exportMsg.contains("sucesso"));

        Controller controller2 = new Controller();
        String importMsg = controller2.importModel(file.toString());
        assertTrue(importMsg.contains("importada") || importMsg.contains("sucesso"));
        assertTrue(controller2.getSpotifUM().userExists("u1"));
    }

    @Test
    @DisplayName("Controller: importModel with missing file returns error")
    void importModel_missingFile_returnsErrorMessage(@TempDir Path tempDir) {
        Controller ctrl = new Controller();
        Path missing = tempDir.resolve("missing.ser");
        String msg = ctrl.importModel(missing.toString());
        assertTrue(msg.startsWith("❌") || msg.contains("Erro ao importar"));
    }

    @Test
    @DisplayName("Controller: exportModel with invalid path returns error")
    void exportModel_invalidPath_returnsErrorMessage(@TempDir Path tempDir) {
        Controller ctrl = new Controller(new SpotifUM());
        Path invalid = tempDir.resolve("no_such_dir").resolve("file.ser");
        String msg = ctrl.exportModel(invalid.toString());
        assertTrue(msg.startsWith("❌") || msg.contains("Erro ao exportar"));
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Controller: default constructor")
    void constructor_default() {
        Controller ctrl = new Controller();
        assertNotNull(ctrl);
        assertNotNull(ctrl.getSpotifUM());
    }

    @Test
    @DisplayName("Controller: copy constructor")
    void constructor_copy() {
        Controller ctrl1 = new Controller(spotifUM);
        Controller ctrl2 = new Controller(ctrl1);
        assertNotNull(ctrl2);
        assertNotNull(ctrl2.getSpotifUM());
    }

    @Test
    @DisplayName("Controller: getSpotifUM and setSpotifUM")
    void getSpotifUM_and_setSpotifUM() {
        assertNotNull(controller.getSpotifUM());
        SpotifUM newSpotifUM = new SpotifUM();
        controller.setSpotifUM(newSpotifUM);
        assertNotNull(controller.getSpotifUM());
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Controller: complete user and album workflow")
    void completeWorkflow() {
        // Register user
        String registerMsg = controller.addNewUser("workflowuser", "workflow@email.com", "street", "pass");
        assertTrue(registerMsg.contains("sucesso"));
        
        // Login
        assertTrue(controller.loginWithMessage("workflowuser", "pass"));
        
        // Create album
        String albumMsg = controller.createAlbum("WorkAlbum", "WorkArtist");
        assertTrue(albumMsg.contains("sucesso"));
        assertTrue(controller.albumExists("WorkAlbum"));
        
        // Add music
        String musicMsg = controller.addMusic("WorkMusic", "WorkArtist", "Pub", "Lyrics", "Fig", 
                                             "Genre", "WorkAlbum", 180, false, "");
        assertTrue(musicMsg.contains("sucesso"));
        
        // Change plan
        String planMsg = controller.setPremiumBasePlan();
        assertTrue(planMsg.contains("PremiumBase"));
    }

    @Test
    @DisplayName("Controller: library status check")
    void currentUserHasLibrary() {
        controller.addNewUser("libuser", "lib@email.com", "street", "pass");
        controller.loginWithMessage("libuser", "pass");
        boolean result = controller.currentUserHasLibrary();
        assertNotNull(result);
    }

    @Test
    @DisplayName("Controller: multiple albums and music")
    void multipleAlbumsAndMusic() {
        controller.createAlbum("Album1", "Artist1");
        controller.createAlbum("Album2", "Artist2");
        controller.createAlbum("Album3", "Artist3");
        
        controller.addMusic("Music1", "Artist1", "Pub", "Lyrics", "Fig", "Pop", "Album1", 180, false, "");
        controller.addMusic("Music2", "Artist2", "Pub", "Lyrics", "Fig", "Rock", "Album2", 200, false, "");
        controller.addMusic("Music3", "Artist3", "Pub", "Lyrics", "Fig", "Jazz", "Album3", 220, false, "");
        
        assertTrue(controller.albumExists("Album1"));
        assertTrue(controller.albumExists("Album2"));
        assertTrue(controller.albumExists("Album3"));
    }
}
