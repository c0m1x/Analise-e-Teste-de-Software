package org.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.Controller.Controller;
import org.Model.SpotifUM;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Menu abstract class using a concrete test implementation
 */
public class MenuTest {
    
    private Controller controller;
    private MenuManager menuManager;
    private TestMenu testMenu;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private InputStream originalIn;
    
    // Concrete implementation of Menu for testing
    private static class TestMenu extends Menu {
        public TestMenu(Controller controller, MenuManager menuManager) {
            super(controller, menuManager);
        }
        @Override public void show() { /* no-op */ }
        @Override public void handleInput() { /* no-op */ }
    }
    
    @BeforeEach
    void setUp() {
        outputStream.reset();
        originalOut = System.out;
        originalIn = System.in;

        // Set up output stream capture
        System.setOut(new PrintStream(outputStream));
        
        // Create the necessary objects
        SpotifUM spotifUM = new SpotifUM();
        controller = new Controller(spotifUM);
        menuManager = new MenuManager(controller);
        testMenu = new TestMenu(controller, menuManager);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    @Test
    void testControllerGetterAndSetter() {
        // Create a new controller
        Controller newController = new Controller(new SpotifUM());
        
        // Set the new controller
        testMenu.setController(newController);
        
        // Check that the controller was updated
        assertEquals(newController, testMenu.getController());
    }
    
    @Test
    void testMenuManagerGetterAndSetter() {
        // Create a new menu manager
        MenuManager newMenuManager = new MenuManager(controller);
        
        // Set the new menu manager
        testMenu.setMenuManager(newMenuManager);
        
        // Check that the menu manager was updated
        assertEquals(newMenuManager, testMenu.getMenuManager());
    }
    
    @Test
    void testScannerGetterAndSetter() {
        // Create a new scanner
        Scanner newScanner = new Scanner(System.in);
        
        // Set the new scanner
        testMenu.setScanner(newScanner);
        
        // Check that the scanner was updated
        assertEquals(newScanner, testMenu.getScanner());
        
        // Clean up
        newScanner.close();
    }
    
    @Test
    void testRunningStateGetterAndSetter() {
        // Default should be true
        assertTrue(testMenu.isRunning());
        
        // Set running to false
        testMenu.setRunning(false);
        
        // Check that running was updated
        assertFalse(testMenu.isRunning());
    }
    
    @Test
    void testAskConfirmationYes() {
        // Set up input stream with "s" (yes)
        String input = "s\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        
        // Create a fresh menu with the new input stream
        TestMenu menu = new TestMenu(controller, menuManager);
        menu.setScanner(new Scanner(System.in));
        
        // Test confirmation
        boolean result = menu.askConfirmation("Confirm?");
        
        // Should return true for "s"
        assertTrue(result);
    }
    
    @Test
    void testAskConfirmationNo() {
        // Set up input stream with "n" (no)
        String input = "n\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        
        // Create a fresh menu with the new input stream
        TestMenu menu = new TestMenu(controller, menuManager);
        menu.setScanner(new Scanner(System.in));
        
        // Test confirmation
        boolean result = menu.askConfirmation("Confirm?");
        
        // Should return false for "n"
        assertFalse(result);
    }
}