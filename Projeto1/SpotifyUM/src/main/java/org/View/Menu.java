package org.View;

import java.util.Scanner;

import org.Controller.Controller;

/**
 * Classe abstrata que serve de base para todos os menus.
 * Contém métodos utilitários e referências comuns.
 */
public abstract class Menu {

    /** Controlador principal da aplicação. */
    private Controller controller;
    /** Gestor de menus. */
    private MenuManager menuManager;
    /** Scanner para leitura de input do utilizador. */
    private Scanner scanner;
    private boolean isRunning = true;

    /**
     * Construtor base do menu.
     * @param controller Controlador principal da aplicação.
     * @param menuManager Gestor de menus.
     */
    public Menu(Controller controller, MenuManager menuManager) {
        this.controller = controller;
        this.menuManager = menuManager;
        this.scanner = new Scanner(System.in);
        cleanTerminal();
    }

    /**
     * Obtém o controlador associado ao menu.
     * @return Instância de Controller.
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Define o controlador associado ao menu.
     * @param controller Instância de Controller.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Obtém o gestor de menus associado ao menu.
     * @return Instância de MenuManager.
     */
    public MenuManager getMenuManager() {
        return menuManager;
    }

    /**
     * Define o gestor de menus associado ao menu.
     * @param menuManager Instância de MenuManager.
     */
    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * Obtém o scanner de input.
     * @return Scanner de input.
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Define o scanner de input.
     * @param scanner Scanner de input.
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Fecha o scanner de input.
     */
    public void closeScanner() {
        // Under Maven Surefire the forked JVM uses stdin as a control channel.
        // Closing a Scanner that wraps System.in can close that channel and
        // produce "std/in stream corrupted" warnings.
        if (System.getProperty("surefire.test.class.path") != null) {
            return;
        }
        if (scanner != null) {
            scanner.close();
        }
    }
    
    /**
     * Verifica se o menu está a correr.
     * @return true se o menu estiver a correr, false caso contrário.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Define o estado de execução do menu.
     * @param isRunning true se o menu estiver a correr, false caso contrário.
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
    /**
     * Limpa o terminal.
     */
    public void cleanTerminal() {
        // Maven Surefire runs tests in a forked JVM and uses stdout as a control
        // channel. Any subprocess using inheritIO() can write directly to the
        // native stdout and corrupt that channel ("Corrupted STDOUT").
        // So, never clear the terminal when running under Surefire.
        if (System.getProperty("surefire.test.class.path") != null) {
            return;
        }

        // In non-interactive environments, just skip.
        if (System.console() == null) {
            return;
        }
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Erro ao limpar o terminal: " + e.getMessage());
        }
    }

    /**
     * Pergunta ao utilizador uma confirmação (sim/não).
     * @param mensagem Mensagem a apresentar.
     * @return true se o utilizador confirmar, false caso contrário.
     */
    public boolean askConfirmation(String mensagem) {
        System.out.println("\n" + mensagem + " (s/n)");
        String choice = getScanner().nextLine().trim().toLowerCase();
        return choice.equals("s");
    }

    /**
     * Mostra o menu (método abstrato a ser implementado pelas subclasses).
     */
    public abstract void show();

    /**
     * Lida com a entrada do utilizador (método abstrato a ser implementado pelas subclasses).
     */
    public abstract void handleInput();

}