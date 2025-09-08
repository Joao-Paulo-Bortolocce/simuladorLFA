package unoeste.termo6.simulador;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TesteApplication extends Application {

    @Override
    public void start(Stage stage) {
        SwingNode swingNode = new SwingNode();

        SwingUtilities.invokeLater(() -> {
            mxGraph graph = new mxGraph();
            Object parent = graph.getDefaultParent();

            mxGraphComponent graphComponent = new mxGraphComponent(graph);

            // Clique do mouse → cria um estado (nó circular)
            graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    graph.getModel().beginUpdate();
                    try {
                        graph.insertVertex(parent, null, "q", e.getX(), e.getY(), 40, 40,
                                "shape=ellipse;perimeter=ellipsePerimeter");
                    } finally {
                        graph.getModel().endUpdate();
                    }
                }
            });

            swingNode.setContent(graphComponent);
        });

        StackPane root = new StackPane(swingNode);
        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.setTitle("Autômato em JavaFX + JGraphX");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
