package unoeste.termo6.simulador.automato;

public class Transicao {
    private final Estado origem;
    private final Estado destino;
    private String condicao;

    public Transicao(Estado origem, Estado destino, String condicao) {
        this.origem = origem;
        this.destino = destino;
        this.condicao = condicao;
    }

    public Estado getOrigem() {
        return origem;
    }

    public Estado getDestino() {
        return destino;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }
}