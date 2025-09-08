package unoeste.termo6.simulador;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class RegexController {

    public TextField tf_expressao;
    public TextField tf_palavra;
    public Label lb_palavras;
    public Label lb_resultado;
    String palavra;
    Pattern regex;



    public void onTestar(ActionEvent actionEvent) {
//        Pattern regexJava = criaRegex("(a+b+c)*");
        regex=criaRegex(tf_expressao.getText());
        palavra = tf_palavra.getText();
        boolean resultado = regex.matcher(palavra).matches();
        if(resultado)
            lb_resultado.setText("A palavra "+palavra+" pertence a linguagem");
        else
            lb_resultado.setText("A palavra "+palavra+" não pertence a linguagem");

    }

    public void onGerar(ActionEvent actionEvent) {
    }

    public static Pattern criaRegex(String expressao) {
        // Substitui "+" por "|" e remove "." literal
        String regexEmJava = expressao.replaceAll("\\+", "|").replaceAll("\\.", "");
//        System.out.println("Regex convertido: " + regexEmJava);

        return Pattern.compile("^" + regexEmJava + "$");
    }







}
