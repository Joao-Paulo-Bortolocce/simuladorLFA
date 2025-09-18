package unoeste.termo6.simulador.gramatica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class GramaticaController {

    @FXML
    private VBox grammarsContainer; // Injeta o VBox principal do FXML

    /**
     * Chamado quando o botão "+ Adicionar Variável" é clicado.
     * Cria uma nova linha completa para uma nova variável da gramática.
     */
    @FXML
    void addVariableRule(ActionEvent event) {
        // HBox principal para a linha da variável
        HBox variableRow = new HBox(10);
        variableRow.setAlignment(Pos.CENTER_LEFT);

        // Campo para o nome da variável (ex: A, B, S)
        TextField variableField = new TextField();
        variableField.setPromptText("Variável");
        variableField.setPrefWidth(80);

        // Seta "->"
        Label arrowLabel = new Label("->");
        arrowLabel.setFont(new Font(14));

        // VBox para conter todas as transições desta variável
        VBox transitionsVBox = new VBox(5);

        // Adiciona a primeira linha de transição obrigatória
        HBox firstTransition = createTransitionHBox();
        transitionsVBox.getChildren().add(firstTransition);

        // Botão "+" para adicionar mais transições para ESTA variável
        Button addTransitionButton = new Button("+");
        addTransitionButton.setOnAction(e -> {
            // A ação é adicionar uma nova linha de transição ao VBox de transições
            transitionsVBox.getChildren().add(createTransitionHBox());
        });

        // Adiciona todos os componentes à linha da variável
        variableRow.getChildren().addAll(variableField, arrowLabel, transitionsVBox, addTransitionButton);
        HBox.setMargin(addTransitionButton, new Insets(0, 0, 0, 5));

        // Adiciona a nova linha da variável ao container principal
        grammarsContainer.getChildren().add(variableRow);
    }

    /**
     * Método auxiliar que cria uma linha de transição (lê, transforma).
     * @return um HBox contendo os campos para uma transição.
     */
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