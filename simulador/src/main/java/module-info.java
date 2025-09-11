module unoeste.termo6.simulador {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.github.vlsi.mxgraph.jgraphx;
    requires javafx.swing;
    requires javafx.graphics;
    requires java.desktop;
    requires jdk.compiler;


    opens unoeste.termo6.simulador to javafx.fxml;
    exports unoeste.termo6.simulador;
    exports unoeste.termo6.simulador.automato;
    opens unoeste.termo6.simulador.automato to javafx.fxml;
    exports unoeste.termo6.simulador.regex;
    opens unoeste.termo6.simulador.regex to javafx.fxml;
}