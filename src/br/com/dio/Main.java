package br.com.dio;

import br.com.dio.controller.SudokuGame;

import java.util.Scanner;

public class Main {
    private final static Scanner scanner = new Scanner(System.in);
    private final static SudokuGame game = new SudokuGame();

    public static void main(String[] args) {
        loadSudokuFromArgs(args);
        showMainMenu();
    }

    private static void loadSudokuFromArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Nenhum argumento fornecido. Iniciando jogo com solução padrão...");
            game.initializeBoardWithSolution(); // mudei
            return;
        }

        try {
            String sudokuData = String.join(" ", args);
            String[] cells = sudokuData.split(" ");

            for (String cellData : cells) {
                if (cellData.trim().isEmpty()) continue;

                String[] parts = cellData.split(";");
                if (parts.length != 2) {
                    System.err.println("Argumentos inválido para célula: " + cellData);
                    continue;
                }

                String[] position = parts[0].split(",");
                if (position.length != 2) {
                    System.err.println("Posição inválida: " + parts[0]);
                    continue;
                }

                int col = Integer.parseInt(position[0]);
                int row = Integer.parseInt(position[1]);

                String[] valueData = parts[1].split(",");
                if (valueData.length != 2) {
                    System.err.println("Dados da célula inválidos: " + parts[1]);
                    continue;
                }

                int value = Integer.parseInt(valueData[0]);
                boolean isFixed = Boolean.parseBoolean(valueData[1]);
                game.setPresetCellFromArgs(row, col, value, isFixed);
            }

            System.out.println("Sudoku personalizado carregado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao processar os argumentos: " + e.getMessage());
            System.out.println("Iniciando jogo com solução padrão...");
        }
    }

    private static void showMainMenu() {

        var option = -1;
        while (true) {
            System.out.println("Selecione uma das opções a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame();
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> game.displayBoard();
                case 5 -> game.statusGame();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void clearGame() {
        if (!game.isGameInitialized()) {
            System.err.println("O jogo não foi iniciado");
            return;
        }
        game.initializeBoardWithSolution();
        game.displayBoard();
        System.out.println("Jogo limpo com sucesso!");
    }

    private static void finishGame() {
        if (!game.isGameInitialized()) {
            System.err.println("O jogo não foi iniciado");
            return;
        }

        if (game.gameIsFinished()) {
            System.out.println("\n🎉 Parabéns! Você concluiu o jogo com sucesso! 🎉");
            game.displayBoard();
            System.exit(0); // Encerra o jogo após a vitória
        } else if (game.hasErrors()) {
            System.out.println("\n🤔 Seu jogo contém erros. Verifique o tabuleiro e ajuste suas respostas.");
            game.displayBoard();
        } else {
            System.out.println("\nO jogo ainda está incompleto. Continue preenchendo!");
        }
    }

    private static void startGame() {
        System.out.println("### JOGO DE SUDOKU INICIADO ###");

        if (game.isGameInitialized()) {
            System.out.println("O jogo já foi iniciado");
            return;
        }

        game.setGameInitialized(true);
        game.displayBoard();
    }

    private static void inputNumber() {
        if (!game.isGameInitialized()) {
            System.err.println("O jogo não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna que em que o número será inserido");
        var col = scanner.nextInt();
        System.out.println("Informe a linha que em que o número será inserido");
        var row = scanner.nextInt();
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n", col, row);
        var value = scanner.nextInt();

        game.addCell(col, row, value);
    }

    private static void removeNumber() {
        if (!game.isGameInitialized()) {
            System.err.println("O jogo não foi iniciado");
            return;
        }

        System.out.println("Informe a coluna que em que o número será removido");
        var col = scanner.nextInt();
        System.out.println("Informe a linha que em que o número será removido");
        var row = scanner.nextInt();

        game.removeCell(row, col);
    }
}