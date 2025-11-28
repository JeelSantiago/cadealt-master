package com.cadealt.model;

/**
 * Model para Item do Histórico do Culto
 */
public class ItemHistorico {

    public enum TipoItem {
        HINO("🎵"),
        LOUVOR("🎤"),
        VERSICULO("📖");

        private final String icone;

        TipoItem(String icone) {
            this.icone = icone;
        }

        public String getIcone() {
            return icone;
        }
    }

    private TipoItem tipo;
    private String descricao;
    private Object conteudo; // Pode ser Hino, Louvor ou Versiculo

    public ItemHistorico(TipoItem tipo, String descricao, Object conteudo) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.conteudo = conteudo;
    }

    public TipoItem getTipo() {
        return tipo;
    }

    public void setTipo(TipoItem tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Object getConteudo() {
        return conteudo;
    }

    public void setConteudo(Object conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        return tipo.getIcone() + " " + descricao;
    }

    /**
     * Retorna representação para exportação
     */
    public String toExportString(int numero) {
        return numero + ". " + tipo.getIcone() + " " + descricao;
    }
}
