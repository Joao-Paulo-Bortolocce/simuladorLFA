package unoeste.termo6.simulador.automato;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Estado {
    private final int id;
    private final DoubleProperty x = new SimpleDoubleProperty(); // encapsula um valor de double, permitindo a gestão observável e bidirecional desse valor. Liga e desvincula valores.
    private final DoubleProperty y = new SimpleDoubleProperty();

    private TipoEstado tipo;
    private StackPane representacaoVisual;
    private Group setaInicialVisual = null;
    private Circle circulo;

    private ArrayList<Transicao> transicoes;

    public enum TipoEstado {
        NORMAL, INICIAL, FINAL, INICIAL_E_FINAL
    }

    public Estado(int id, double x, double y, TipoEstado tipo) {
        this.id = id;
        this.x.set(x);
        this.y.set(y);
        this.tipo = tipo;
        this.transicoes = new ArrayList<>();
        this.circulo = null;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public Circle getCirculo() {
        return circulo;
    }

    public void setCirculo(Circle circulo) {
        this.circulo = circulo;
    }

    public TipoEstado getTipo() {
        return tipo;
    }

    public StackPane getRepresentacaoVisual() {
        return representacaoVisual;
    }

    public Group getSetaInicialVisual() {
        return setaInicialVisual;
    }

    public void setTipo(TipoEstado tipo) {
        this.tipo = tipo;
    }

    public void setSetaInicialVisual(Group setaInicialVisual) {
        this.setaInicialVisual = setaInicialVisual;
    }

    public ArrayList<Transicao> getTransicoes() {
        return transicoes;
    }

    public void adicionarTransicao(Transicao transicao) {
        transicoes.add(transicao);
    }

    public void atualizarTransicao(Transicao transicao, int i) {
        transicoes.set(i, transicao);
    }

    public void removerTransicao(Transicao transicao) {
        transicoes.remove(transicao);
    }

    public void setRepresentacaoVisual(StackPane representacaoVisual) {
        this.representacaoVisual = representacaoVisual;
        representacaoVisual.layoutXProperty().bind(x.subtract(representacaoVisual.widthProperty().divide(2)));
        representacaoVisual.layoutYProperty().bind(y.subtract(representacaoVisual.heightProperty().divide(2)));
    }
}