package unoeste.termo6.simulador;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;

public class GraphApp extends Application {
    @Override
    public void start(Stage stage) {
        SwingNode swingNode = new SwingNode();

        // Cria o grafo em Swing
        SwingUtilities.invokeLater(() -> {
            mxGraph graph = new mxGraph();
            Object parent = graph.getDefaultParent();

            graph.getModel().beginUpdate();
            try {
                Object v1 = graph.insertVertex(parent, null, "A", 20, 20, 80, 30);
                Object v2 = graph.insertVertex(parent, null, "B", 240, 150, 80, 30);
                graph.insertEdge(parent, null, "Conexão", v1, v2);
            } finally {
                graph.getModel().endUpdate();
            }

            mxGraphComponent graphComponent = new mxGraphComponent(graph);
            swingNode.setContent(graphComponent);
        });

        // Layout JavaFX
        StackPane root = new StackPane(swingNode);
        Scene scene = new Scene(root, 500, 400);

        stage.setScene(scene);
        stage.setTitle("JavaFX + JGraphX");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
