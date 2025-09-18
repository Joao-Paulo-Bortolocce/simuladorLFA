package unoeste.termo6.simulador.gramatica;

public class No {
    private String leitura;
    private String transicao;
    private No prox;


    public No(String leitura, String transicao, No prox) {
        this.leitura = leitura;
        this.transicao = transicao;
        this.prox = prox;
    }

    public No(String leitura, No prox) {
        this(leitura,null,prox);
    }

    public String getLeitura() {
        return leitura;
    }

    public void setLeitura(String leitura) {
        this.leitura = leitura;
    }

    public String getTransicao() {
        return transicao;
    }

    public void setTransicao(String transicao) {
        this.transicao = transicao;
    }

    public No getProx() {
        return prox;
    }

    public void setProx(No prox) {
        this.prox = prox;
    }
}
