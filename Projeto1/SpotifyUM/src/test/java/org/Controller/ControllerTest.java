package org.Controller;

import org.Model.SpotifUM;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void addNewUser_whenNew_returnsSuccessMessage() {
        Controller controller = new Controller(new SpotifUM());
        String msg = controller.addNewUser("alice", "a@b.com", "addr", "pw");
        assertTrue(msg.contains("registado") || msg.contains("sucesso"));
        assertTrue(controller.getSpotifUM().userExists("alice"));
    }

    @Test
    void addNewUser_whenDuplicate_returnsWarningMessage() {
        Controller controller = new Controller(new SpotifUM());
        controller.addNewUser("bob", "b@c.com", "addr", "pw");
        String msg = controller.addNewUser("bob", "b@c.com", "addr", "pw");
        assertTrue(msg.contains("Já existe") || msg.contains("existe um utilizador"));
    }

    @Test
    void loginWithMessage_successAndFailure() {
        SpotifUM model = new SpotifUM();
        model.addNewUser("carlos", "c@d.com", "addr", "secret");

        Controller controller = new Controller(model);

        assertFalse(controller.loginWithMessage("carlos", "wrong"));
        assertFalse(controller.loginWithMessage("noone", "x"));
        assertTrue(controller.loginWithMessage("carlos", "secret"));
        assertNotNull(controller.getSpotifUM().getCurrentUser());
        assertEquals("carlos", controller.getSpotifUM().getCurrentUser().getUsername());
    }

    @Test
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
    void importModel_missingFile_returnsErrorMessage(@TempDir Path tempDir) {
        Controller controller = new Controller();
        Path missing = tempDir.resolve("missing.ser");
        String msg = controller.importModel(missing.toString());
        assertTrue(msg.startsWith("❌") || msg.contains("Erro ao importar"));
    }

    @Test
    void exportModel_invalidPath_returnsErrorMessage(@TempDir Path tempDir) {
        Controller controller = new Controller(new SpotifUM());
        Path invalid = tempDir.resolve("no_such_dir").resolve("file.ser");
        String msg = controller.exportModel(invalid.toString());
        assertTrue(msg.startsWith("❌") || msg.contains("Erro ao exportar"));
    }
}
