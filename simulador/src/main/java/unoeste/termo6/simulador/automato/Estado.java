package unoeste.termo6.simulador.automato;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;

public class Estado {
    private final int id;
    private final DoubleProperty x = new SimpleDoubleProperty(); // encapsula um valor de double, permitindo a gestão observável e bidirecional desse valor. Liga e desvincula valores.
    private final DoubleProperty y = new SimpleDoubleProperty();
    private TipoEstado tipo;
    private StackPane representacaoVisual;
    private Group setaInicialVisual = null;

    public enum TipoEstado {
        NORMAL, INICIAL, FINAL, INICIAL_E_FINAL
    }

    public Estado(int id, double x, double y, TipoEstado tipo) {
        this.id = id;
        this.x.set(x);
        this.y.set(y);
        this.tipo = tipo;
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

    public void setRepresentacaoVisual(StackPane representacaoVisual) {
        this.representacaoVisual = representacaoVisual;
        representacaoVisual.layoutXProperty().bind(x.subtract(representacaoVisual.widthProperty().divide(2)));
        representacaoVisual.layoutYProperty().bind(y.subtract(representacaoVisual.heightProperty().divide(2)));
    }
}