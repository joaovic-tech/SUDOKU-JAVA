package br.com.dio.controller;

import br.com.dio.model.Cell;
import br.com.dio.view.BoardTemplate;

// Record para armazenar as estatísticas do jogo de forma estruturada
record GameStats(int correct, int errors, int empty) {}

public class SudokuGame {
    public final static int BOARD_LIMIT = 9;
    private final Cell[][] board = new Cell[BOARD_LIMIT][BOARD_LIMIT];
    public boolean gameInitialized;

    public SudokuGame() {
        this.gameInitialized = false;
        initializeEmptyBoard(); // Inicializa todas as células como vazias
    }

    private void initializeEmptyBoard() {
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                this.board[row][col] = new Cell(false); // Célula vazia e não fixa
            }
        }
    }

    public boolean isGameInitialized() {
        return gameInitialized;
    }

    public void setGameInitialized(boolean gameInitialized) {
        this.gameInitialized = gameInitialized;
    }

    /**
     * Este método é usado quando não há argumentos fornecidos.
     */
    public void initializeBoardWithSolution() {
        int[][] examplePattern = {
            {0, 0, 9, 0, 8, 6, 2, 0, 0},
            {0, 3, 0, 0, 7, 0, 0, 9, 6},
            {0, 6, 0, 0, 1, 0, 0, 0, 5},
            {5, 0, 3, 0, 0, 0, 0, 8, 0},
            {0, 9, 0, 1, 2, 5, 0, 6, 0},
            {0, 4, 0, 0, 0, 0, 1, 0, 7},
            {7, 0, 0, 0, 3, 0, 0, 1, 0},
            {9, 8, 0, 0, 4, 0, 0, 2, 0},
            {0, 0, 6, 8, 5, 0, 4, 0, 0}
        };
        
        // Inicializa o tabuleiro baseado no padrão
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                int value = examplePattern[row][col];
                boolean isFixed = (value != 0); // Se tem valor, é fixo
                
                if (isFixed) {
                    this.board[row][col] = new Cell(value, true);
                } else {
                    this.board[row][col] = new Cell(false); // Célula vazia
                }
            }
        }
        
        System.out.println("🎲 Tabuleiro padrão carregado! Agora você pode jogar Sudoku de verdade!");
    }

    public void displayBoard() {
        if (!this.isGameInitialized()) {
            System.err.println("O jogo não foi iniciado");
            return;
        }
        Object[] displayValues = new Object[BOARD_LIMIT * BOARD_LIMIT];
        int index = 0;
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                Cell cell = board[row][col];
                if (cell.isEmpty()) {
                    displayValues[index] = " ";
                } else {
                    displayValues[index] = String.valueOf(cell.getActual());
                }
                index++;
            }
        }
        System.out.printf((BoardTemplate.BOARD_TEMPLATE) + "%n", displayValues);
    }

    public void addCell(int col, int row, int value) {
        try {
            isValidNumbers(col, row, value);
            if (this.board[row][col].isFixed()) {
                throw new Exception("Erro: A posição [" + row + "," + col + "] é um número fixo e não pode ser alterada.");
            }

            this.board[row][col].setActual(value);

            if (isValidMove(row, col, value)) {
                System.out.println("✅ Número " + value + " colocado corretamente na posição [" + row + "," + col + "]!");
            }

            displayBoard();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void setPresetCellFromArgs(int row, int col, int initialValue, boolean isFixed) {
        if ((row < 0 || row >= BOARD_LIMIT) || (col < 0 || col >= BOARD_LIMIT)) return;
        this.board[row][col] = new Cell(initialValue, isFixed);
    }

    public void removeCell(int row, int col) {
        try {
            if (this.board[row][col].isFixed()) {
                throw new Exception("Essa posição contém um número fixo e não pode ser removida");
            }
            if (this.board[row][col].isEmpty()) {
                throw new Exception("Essa posição não contém um número");
            }
            this.board[row][col].clearSpace();
            displayBoard();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void isValidNumbers(int col, int row, int value) throws Exception {
        if ((col < 0 || col >= BOARD_LIMIT) || (row < 0 || row >= BOARD_LIMIT)) {
            throw new Exception("Posição inválida, Envie posições entre 0 e 8: " + col + "," + row);
        }
        if (value <= 0 || value > BOARD_LIMIT) {
            throw new Exception("Número inválido, Envie números entre 1 e 9");
        }
    }

    private GameStats getGameStats() {
        int correctCount = 0;
        int errorCount = 0;
        int emptyCount = 0;

        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                Cell cell = board[row][col];

                if (cell.isEmpty()) {
                    emptyCount++;
                } else {
                    // NOVA LÓGICA: Verifica erros usando regras do Sudoku, não solução pré-definida
                    if (hasCellError(row, col)) {
                        errorCount++;
                    } else {
                        correctCount++;
                    }
                }
            }
        }
        return new GameStats(correctCount, errorCount, emptyCount);
    }

    public boolean gameIsFinished() {
        GameStats stats = getGameStats();
        return stats.empty() == 0 && stats.errors() == 0;
    }

    public boolean hasErrors() {
        GameStats stats = getGameStats();
        return stats.errors() > 0;
    }

    public void statusGame() {
        GameStats stats = getGameStats();
        System.out.println("\n📊 === STATUS DO JOGO ===");
        System.out.println("Jogo iniciado: " + (isGameInitialized() ? "Sim" : "Não"));
        System.out.println("Células corretas: " + stats.correct());
        System.out.println("Células com erros: " + stats.errors());
        System.out.println("Células vazias: " + stats.empty());
        
        // Mostra detalhes dos erros se houver
        if (stats.errors() > 0) {
            System.out.println("\n❌ ERROS ENCONTRADOS:");
            showDetailedErrors();
        }

        if (gameIsFinished()) {
            System.out.println("\n🎉 Parabéns! Você completou o Sudoku!");
        } else if (hasErrors()) {
            System.out.println("\n🔄 Existem erros no tabuleiro. Corrija-os para continuar!");
        } else if (stats.empty() > 0) {
            System.out.println("\n✅ Não há erros! Continue preenchendo as células vazias.");
        }
        System.out.println("=".repeat(30));
    }

    private void showDetailedErrors() {
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                if (hasCellError(row, col)) {
                    Integer value = board[row][col].getActual();
                    System.out.println("  ⚠️ Posição [" + col + "," + row + "] com valor " + value + ":");
                    
                    if (!isValidRow(row, value, col)) {
                        System.out.println("     - Já existe o número " + value + " na linha " + row);
                    }
                    if (!isValidColumn(col, value, row)) {
                        System.out.println("     - Já existe o número " + value + " na coluna " + col);
                    }
                    if (!isValidBox(row, col, value)) {
                        int boxRow = (row / 3) * 3;
                        int boxCol = (col / 3) * 3;
                        System.out.println("     - Já existe o número " + value + " no quadrante 3x3 [" + boxRow + "-" + (boxRow+2) + "," + boxCol + "-" + (boxCol+2) + "]");
                    }
                }
            }
        }
    }

    private boolean isValidLineOrColumn(boolean isRow, int index, int value, int excludeIndex) {
        for (int i = 0; i < BOARD_LIMIT; i++) {
            if (i == excludeIndex) continue;
            Integer actualValue = isRow ? board[index][i].getActual() : board[i][index].getActual();
            if (actualValue != null && actualValue == value) {
                return false; // Número já existe
            }
        }
        return true; // Número não existe, pode ser colocado
    }

    private boolean isValidRow(int row, int value, int excludeCol) {
        return isValidLineOrColumn(true, row, value, excludeCol);
    }

    private boolean isValidColumn(int col, int value, int excludeRow) {
        return isValidLineOrColumn(false, col, value, excludeRow);
    }

    private boolean isValidBox(int row, int col, int value) {
        // Calcula o início do quadrante 3x3
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        
        // Verifica todas as células do quadrante 3x3
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (r == row && c == col) continue; // Pula a própria célula
                Integer actualValue = board[r][c].getActual();
                if (actualValue != null && actualValue == value) {
                    return false; // Número já existe neste quadrante
                }
            }
        }
        return true; // Número não existe no quadrante, pode ser colocado
    }

    public boolean isValidMove(int row, int col, int value) {
        return isValidRow(row, value, col) && 
               isValidColumn(col, value, row) && 
               isValidBox(row, col, value);
    }

    public boolean hasCellError(int row, int col) {
        if (board[row][col].isEmpty()) {
            return false; // Célula vazia não tem erro
        }

        if (board[row][col].isFixed()) {
            return false; // Células fixas não têm erro por definição
        }
        
        // Verifica se o valor atual violaria as regras do Sudoku
        Integer value = board[row][col].getActual();
        return !isValidMove(row, col, value);
    }

}
