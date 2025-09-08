module unoeste.termo6.simulador {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.github.vlsi.mxgraph.jgraphx;
    requires javafx.swing;
    requires javafx.graphics;
    requires java.desktop;


    opens unoeste.termo6.simulador to javafx.fxml;
    exports unoeste.termo6.simulador;
}