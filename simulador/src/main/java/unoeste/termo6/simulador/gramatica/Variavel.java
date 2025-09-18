package unoeste.termo6.simulador.gramatica;

public class Variavel {
    private String nome;
    private Lista transicao;
    private String id;

    public Variavel(String id,String nome, Lista transicao) {
        this.id = id;
        this.nome = nome;
        this.transicao = transicao;
    }

    public Variavel(String id,String nome) {
        this(id,nome, new Lista());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Lista getTransicao() {
        return transicao;
    }

    public void setTransicao(Lista transicao) {
        this.transicao = transicao;
    }
}
