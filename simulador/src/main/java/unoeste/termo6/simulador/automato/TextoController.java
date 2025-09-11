package unoeste.termo6.simulador.automato;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TextoController {
    public TextField tf_texto;
    public static String caractere;

    @FXML
    public void initialize() {
        // Define o foco no campo de texto para que o usuário possa digitar imediatamente
        tf_texto.requestFocus();
    }

    public void setTextoInicial(String textoInicial) {
        tf_texto.setText(textoInicial);
        // Posiciona o cursor no final do texto
        tf_texto.positionCaret(textoInicial.length());
    }

    public void onConfirmar(ActionEvent actionEvent) {
        caractere = tf_texto.getText();
        tf_texto.getScene().getWindow().hide();
    }

    public void onCancelar(ActionEvent actionEvent) {
        caractere = "";
        tf_texto.getScene().getWindow().hide();
    }
}
