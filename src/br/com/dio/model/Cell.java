package br.com.dio.model;

/**
 * Representa uma célula do tabuleiro de Sudoku.
 */
public class Cell {
    private Integer actual;  // O número atual na célula
    private final boolean fixed;  // Se a célula é fixa (não pode ser alterada pelo jogador)

    /**
     * Construtor para células vazias (para jogos sem solução pré-definida)
     * @param fixed se a célula é fixa
     */
    public Cell(boolean fixed) {
        this.fixed = fixed;
        this.actual = null;  // Começa vazia
    }

    /**
     * Construtor para células com valor inicial (compatibilidade com args)
     * @param initialValue valor inicial da célula
     * @param fixed se a célula é fixa
     */
    public Cell(int initialValue, boolean fixed) {
        this.fixed = fixed;
        if (fixed) {
            this.actual = initialValue;
        } else {
            this.actual = null;  // Células não fixas começam vazias
        }
    }

    public Integer getActual() {
        return actual;
    }

    public void setActual(final Integer actual) {
        if (fixed) return;  // Não permite alterar células fixas
        this.actual = actual;
    }

    public void clearSpace() {
        setActual(null);
    }

    public boolean isFixed() {
        return fixed;
    }

    /**
     * Verifica se a célula está vazia.
     * @return true se a célula não tem valor
     */
    public boolean isEmpty() {
        return actual == null || actual == 0;
    }
}
