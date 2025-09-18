package unoeste.termo6.simulador.gramatica;

import javafx.event.ActionEvent;
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

    //exibir no console as variaveis e suas transicoes
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