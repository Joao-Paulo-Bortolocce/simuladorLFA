package unoeste.termo6.simulador.gramatica;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GramaticaController implements Initializable {

    @FXML
    private VBox grammarsContainer;

    @FXML
    private TextField wordField;

    @FXML
    private VBox resultsPane;

    @FXML
    private VBox verificationResultBox;

    @FXML
    private Label resultLabel;

    @FXML
    private VBox generationResultBox;

    @FXML
    private ListView<String> generatedWordsListView;

    @FXML
    private TextField variavelInicial;

    private ArrayList<Variavel> variaveis;

    private static int id;

    private ArrayList<String> alfabeto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        variaveis = new ArrayList<>();
        id = 0;
        alfabeto = new ArrayList<>();
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

    @FXML
    void addVariableRule(ActionEvent event) {
        HBox variableRow = new HBox(10);
        variableRow.setAlignment(Pos.CENTER_LEFT);

        TextField variableField = new TextField();
        variableField.setPromptText("Variável");
        variableField.setPrefWidth(80);
        String variableId = "" + id++;
        Variavel novaVariavel = new Variavel(variableId, "");
        variableField.setId(variableId);
        variaveis.add(novaVariavel);

        variableField.textProperty().addListener((observable, oldValue, newValue) -> {
            int pos;
            for (pos = 0; pos < variaveis.size() && variaveis.get(pos).getId().compareToIgnoreCase(variableId) != 0; pos++)
                ;
            if (pos < variaveis.size()) {
                variaveis.get(pos).setNome(newValue);
            }

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

        No no = new No("", null, null);
        int pos;

        for (pos = 0; pos < variaveis.size() && variaveis.get(pos).getId().compareToIgnoreCase(variableId) != 0; pos++)
            ;

        if (pos < variaveis.size()) {
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

    private void addAlfabeto(String newValue) {
        if(newValue!=""){

            int i;
            for (i = 0; i < alfabeto.size() && newValue.compareToIgnoreCase(alfabeto.get(i)) > 0; i++) ;
            if (i == alfabeto.size() || alfabeto.get(i).compareToIgnoreCase(newValue )!=0) {
                alfabeto.add(i,newValue);
            }
        }

    }

    public boolean aceita(String palavra) {
        //deve começar com a variavel inicial que o user escolheu
        String var = variavelInicial.getText();
        if (!var.isEmpty()) {
            int i;
            for (i = 0; i < variaveis.size() && variaveis.get(i).getNome().compareToIgnoreCase(var) != 0; i++) ;

            if (i == variaveis.size())
                return false;

            Variavel variavel = variaveis.get(i);
            if (palavra.length() == 0) {
                No aux = variavel.getTransicao().getCabeca();
                while (aux != null && aux.getLeitura().compareTo("") != 0)
                    aux = aux.getProx();
                if (aux != null && ( aux.getTransicao() == null  || aux.getTransicao().compareTo("") == 0))
                    return true;
                return false;
            }

            for (i = 0; i < palavra.length(); i++) {
                char letra = palavra.charAt(i);
                No aux = variavel.getTransicao().getCabeca();
                //verificar se na lista adjacente tem essa letra, se tiver muda a variavel atual para a transicao, se nao tiver retorna falso
                boolean encontrou = false;
                while (aux != null && !encontrou) {
                    if (!aux.getLeitura().isEmpty()) {
                        if (aux.getLeitura().charAt(0) == letra) {
                            encontrou = true;
                            String transicao = aux.getTransicao();

                            //verificar se não é a ultima letra da palavra a ser lida e se essa transicao for nula entao deve aprovar também
                            if (i == palavra.length() - 1 && (transicao == null || transicao.isEmpty()))
                                return true;
                            if (i < palavra.length() - 1 && (transicao == null || transicao.isEmpty()))
                                return false;

//                            for (i = 0; i < variaveis.size() && variaveis.get(i).getNome().compareToIgnoreCase(var) != 0; i++)
                            int j;
                            for (j = 0; j < variaveis.size() && variaveis.get(j).getNome().compareToIgnoreCase(transicao) != 0; j++)
                                ;

                            if (j == variaveis.size())
                                return false;

                            variavel = variaveis.get(j);

                        }
                    }
                    aux = aux.getProx();
                }
                if (!encontrou)
                    return false;
            }
            return false;
        }

        return true;
    }


    @FXML
    void verifyWord(ActionEvent event) {
        if (variavelInicial.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null); // deixa sem cabeçalho
            alert.setContentText("Não é possível verificar uma palavra sem uma variável inicial");
            alert.showAndWait();
        }
        else{

            showResultsPane();
            verificationResultBox.setVisible(true);
            verificationResultBox.setManaged(true);

            String palavra = wordField.getText();
            if (palavra.isEmpty()) {
                resultLabel.setText("Por favor, digite uma palavra.");
                resultLabel.setStyle("-fx-text-fill: #FFC107;");
            }
            else{
                int j;
                for (j = 0; j < variaveis.size() && variaveis.get(j).getNome().compareToIgnoreCase(variavelInicial.getText()) != 0; j++);

                if (j == variaveis.size()){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Atenção");
                    alert.setHeaderText(null); // deixa sem cabeçalho
                    alert.setContentText("Informe uma variavel que exista, "+variavelInicial.getText()+" não é uma variavel da gramatica");
                    alert.showAndWait();
                }
                else{



                    boolean aceita = aceita(palavra);

                    if (aceita) {
                        resultLabel.setText("A palavra '" + palavra + "' é ACEITA!");
                        resultLabel.setStyle("-fx-text-fill: #8BC34A; -fx-font-weight: bold;"); // Verde para sucesso
                    } else {
                        resultLabel.setText("A palavra '" + palavra + "' NÃO é aceita.");
                        resultLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Vermelho para falha
                    }
                }
            }
        }
    }

    private void gerarAlfabeto() {
        for (int i = 0; i < variaveis.size(); i++) {
            No aux= variaveis.get(i).getTransicao().getCabeca();
            while(aux!=null ){
                addAlfabeto(aux.getLeitura());
                aux=aux.getProx();
            }
        }
    }

    @FXML
    void generateWords(ActionEvent event) {
        if (variavelInicial.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null); // deixa sem cabeçalho
            alert.setContentText("Não é possível gerar palavras sem uma variável inicial");
            alert.showAndWait();
        }
        else{
            int k;
            for (k = 0; k < variaveis.size() && variaveis.get(k).getNome().compareToIgnoreCase(variavelInicial.getText()) != 0; k++);

            if (k == variaveis.size()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText(null); // deixa sem cabeçalho
                alert.setContentText("Informe uma variavel que exista, "+variavelInicial.getText()+" não é uma variavel da gramatica");
                alert.showAndWait();
            }
            else{

                gerarAlfabeto();
                showResultsPane();
                generationResultBox.setVisible(true);
                generationResultBox.setManaged(true);
                generatedWordsListView.getItems().clear();


                ArrayList<String> palavrasGeradas = new ArrayList<>();
                //gerar as palavrasGeradas

                int cont = 0, ini = 0;
                String palavra;
                ArrayList<String> testadas = new ArrayList<>();
                testadas.add("");
                boolean flag = true;
                if (aceita("")) {
                    palavrasGeradas.add("");
                    cont++;
                }
                while (cont < 10 && flag) {
                    for (; flag && ini < testadas.size() && cont < 10; ini++) {
                        for (int j = 0; flag && j < alfabeto.size() && cont < 10; j++) {
                            palavra = testadas.get(ini) + alfabeto.get(j);
                            testadas.add(palavra);
                            if (aceita(palavra)) {
                                palavrasGeradas.add(palavra);
                                cont++;
                            } else if (palavra.length() > 10) {
                                flag = false;
                            }
                            System.out.println(palavra);
                        }
                    }
                }
                if (palavrasGeradas.get(0).compareTo("") == 0) {
                    palavrasGeradas.set(0, "ε");
                }
                generatedWordsListView.setItems(FXCollections.observableArrayList(palavrasGeradas));
            }
        }
    }

    private void Show() {
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