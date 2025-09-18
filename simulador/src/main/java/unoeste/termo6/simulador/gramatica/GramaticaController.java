package unoeste.termo6.simulador.gramatica;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GramaticaController implements Initializable {

    @FXML
    private VBox grammarsContainer;

    @FXML
    private TextField wordField;

    // ATRIBUTOS FXML PARA A ÁREA DE RESULTADOS ATUALIZADOS
    @FXML
    private VBox resultsPane;

    @FXML
    private VBox verificationResultBox; // Container superior

    @FXML
    private Label resultLabel;

    @FXML
    private VBox generationResultBox; // Container inferior

    @FXML
    private ListView<String> generatedWordsListView;

    private ArrayList<Variavel> variaveis;

    private static int id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        variaveis = new ArrayList<>();
        id = 0;
        // Esconde o painel principal e as seções no início
        resultsPane.setVisible(false);
        resultsPane.setManaged(false);

        verificationResultBox.setVisible(false);
        verificationResultBox.setManaged(false);

        generationResultBox.setVisible(false);
        generationResultBox.setManaged(false);
    }

    private void showResultsPane() {
        if (!resultsPane.isVisible()) {
            resultsPane.setVisible(true);
            resultsPane.setManaged(true);
        }
    }

    // ... (código addVariableRule e createTransitionHBox permanecem os mesmos) ...
    @FXML
    void addVariableRule(ActionEvent event) {
        HBox variableRow = new HBox(10);
        variableRow.setAlignment(Pos.CENTER_LEFT);

        TextField variableField = new TextField();
        variableField.setPromptText("Variável");
        variableField.setPrefWidth(80);
        String variableId = "" + id++;
        variableField.setId(variableId);
        variaveis.add(new Variavel(variableId,""));

        variableField.textProperty().addListener((observable, oldValue, newValue) -> {
            int pos;
            for (pos=0;pos<variaveis.size() && variaveis.get(pos).getId()!=null&&variaveis.get(pos).getId().compareToIgnoreCase(variableId)!=0;pos++);

            if(pos<variaveis.size())
                variaveis.get(pos).setNome(newValue);

        });

        Label arrowLabel = new Label("->");
        arrowLabel.setFont(new Font(14));

        VBox transitionsVBox = new VBox(5);

        HBox firstTransition = createTransitionHBox(variableId);
        transitionsVBox.getChildren().add(firstTransition);

        Button addTransitionButton = new Button("+");
        addTransitionButton.setOnAction(e -> {
            transitionsVBox.getChildren().add(createTransitionHBox(variableId));
        });

        variableRow.getChildren().addAll(variableField, arrowLabel, transitionsVBox, addTransitionButton);
        HBox.setMargin(addTransitionButton, new Insets(0, 0, 0, 5));
        grammarsContainer.getChildren().add(variableRow);
    }

    private HBox createTransitionHBox(String variableId) {
        HBox transitionRow = new HBox(5);
        transitionRow.setAlignment(Pos.CENTER_LEFT);

        No no = new No("",null,null);
        int pos;

        for (pos=0;pos<variaveis.size() && variaveis.get(pos).getId()!=null&&variaveis.get(pos).getId().compareToIgnoreCase(variableId)!=0;pos++);

        if(pos<variaveis.size())
        {
            variaveis.get(pos).getTransicao().insere(no);
        }

        TextField readsField = new TextField();
        readsField.setPromptText("Lê");
        readsField.setPrefWidth(60);
        readsField.textProperty().addListener((observable, oldValue, newValue) -> {
            no.setLeitura(newValue);
        });


        TextField becomesField = new TextField();
        becomesField.setPromptText("Transforma");
        becomesField.setPrefWidth(100);
        becomesField.textProperty().addListener((observable, oldValue, newValue) -> {
            no.setTransicao(newValue);
        });

        transitionRow.getChildren().addAll(readsField, becomesField);
        return transitionRow;
    }


    @FXML
    void verifyWord(ActionEvent event) {
        showResultsPane();
        verificationResultBox.setVisible(true);
        verificationResultBox.setManaged(true);

        String palavra = wordField.getText();
        if (palavra.isEmpty()) {
            resultLabel.setText("Por favor, digite uma palavra.");
            resultLabel.setStyle("-fx-text-fill: #FFC107;");
            return;
        }

        //verificar se aceita a palavra


        if (aceita) {
            resultLabel.setText("A palavra '" + palavra + "' é ACEITA!");
            resultLabel.setStyle("-fx-text-fill: #8BC34A; -fx-font-weight: bold;"); // Verde para sucesso
        } else {
            resultLabel.setText("A palavra '" + palavra + "' NÃO é aceita.");
            resultLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Vermelho para falha
        }
    }

    @FXML
    void generateWords(ActionEvent event) {
        showResultsPane();
        generationResultBox.setVisible(true);
        generationResultBox.setManaged(true);
        generatedWordsListView.getItems().clear();

        ArrayList<String> palavrasGeradas = new ArrayList<>();
        //gerar as palavras
        generatedWordsListView.setItems(FXCollections.observableArrayList(palavrasGeradas));
    }

    private void Show (){
        for (Variavel variavel : variaveis) {
            System.out.print(variavel.getNome() + " -> ");
            No aux = variavel.getTransicao().getCabeca();
            while (aux != null) {
                System.out.print("[" + aux.getLeitura() + " : " + aux.getTransicao() + "] ");
                aux = aux.getProx();
            }
            System.out.println();
        }
    }
}