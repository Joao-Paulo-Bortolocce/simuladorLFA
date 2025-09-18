package unoeste.termo6.simulador.gramatica;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GramaticaController implements Initializable {

    @FXML
    private VBox grammarsContainer; // Injeta o VBox principal do FXML

    private ArrayList<Variavel> variaveis;

    private static int id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        variaveis = new ArrayList<>();
        id=0;
    }

    @FXML
    void addVariableRule(ActionEvent event) {
        HBox variableRow = new HBox(10);
        variableRow.setAlignment(Pos.CENTER_LEFT);

        TextField variableField = new TextField();
        variableField.setPromptText("Variável");
        variableField.setPrefWidth(80);
        variableField.setId(""+id);
        variaveis.add(new Variavel(""+id++,""));

        variableField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int pos;
                TextField alterado = (TextField)actionEvent.getSource();

                for (pos=0;pos<variaveis.size() && variaveis.get(pos).getId().compareToIgnoreCase(alterado.getId())!=0;pos++);

                if(pos<variaveis.size())
                    variaveis.get(pos).setNome(alterado.getText());
            }
        });

        Label arrowLabel = new Label("->");
        arrowLabel.setFont(new Font(14));

        VBox transitionsVBox = new VBox(5);

        HBox firstTransition = createTransitionHBox();
        transitionsVBox.getChildren().add(firstTransition);

        Button addTransitionButton = new Button("+");
        addTransitionButton.setOnAction(e -> {
            transitionsVBox.getChildren().add(createTransitionHBox());
        });

        variableRow.getChildren().addAll(variableField, arrowLabel, transitionsVBox, addTransitionButton);
        HBox.setMargin(addTransitionButton, new Insets(0, 0, 0, 5));

        grammarsContainer.getChildren().add(variableRow);
    }
    private HBox createTransitionHBox() {
        HBox transitionRow = new HBox(5);
        transitionRow.setAlignment(Pos.CENTER_LEFT);

        // Campo para o que a variável vai ler (ex: a, b, ε)
        TextField readsField = new TextField();
        readsField.setPromptText("Lê");
        readsField.setPrefWidth(60);

        // Campo para no que a variável vai se transformar (ex: aB, B)
        TextField becomesField = new TextField();
        becomesField.setPromptText("Transforma");
        becomesField.setPrefWidth(100);

        transitionRow.getChildren().addAll(readsField, becomesField);
        return transitionRow;
    }
}