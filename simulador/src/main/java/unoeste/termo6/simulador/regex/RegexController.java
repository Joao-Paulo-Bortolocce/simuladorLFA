package unoeste.termo6.simulador.regex;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegexController {

    public TextField tf_expressao;
    public TextField tf_palavra;
    public Label lb_palavras;
    public Label lb_resultado;
    String palavra;
    String expressao="";
    Pattern regex;
    ArrayList<String> alfabeto,palavras;
    boolean troca,temMaximo;


    public void onTestar(ActionEvent actionEvent) {
//        Pattern regexJava = criaRegex("(a+b+c)*");
        regex=criaRegex(tf_expressao.getText());
        palavra = tf_palavra.getText().replaceAll(" ","");
        boolean resultado = testaPalavra(palavra);
        if(palavra.compareTo("")==0)
            palavra="ε";
        if(resultado)
            lb_resultado.setText("A palavra "+palavra+" pertence a linguagem");
        else
            lb_resultado.setText("A palavra "+palavra+" não pertence a linguagem");

    }

    private boolean testaPalavra(String palavra) {
        return regex.matcher(palavra).matches();
    }

    public void onGerar(ActionEvent actionEvent) {
        regex=criaRegex(tf_expressao.getText());
        String label="{ ";
        int i = 0;
        if(troca){
            palavras= new ArrayList<>();
            gerarPalavras(palavras);
        }
        if(palavras.get(0).compareTo("")==0){
            label+="ε, ";
            i++;
        }
        for (; i < palavras.size()-1; i++)
            label+=palavras.get(i)+", ";
        label+=palavras.get(palavras.size()-1)+" }";
        lb_palavras.setText(label);
    }

    private void gerarPalavras(ArrayList<String> palavras) {
       int cont=0,ini=0;
       String palavra;
       ArrayList<String> testadas= new ArrayList<>();
       testadas.add("");
       boolean flag=true;
       if(testaPalavra("")){
           palavras.add("");
           cont++;
       }
        while(cont<10 && flag){
           for (;flag && ini<testadas.size() && cont<10;ini++){
               for(int j=0;flag && j<alfabeto.size() && cont<10;j++){
                   palavra=testadas.get(ini)+alfabeto.get(j);
                   testadas.add(palavra);
                   if(testaPalavra(palavra)){
                       palavras.add(palavra);
                       cont++;
                   } else if (palavra.length()>10) {
                       flag=false;
                   }
                   System.out.println(palavra);
               }
           }
       }
    }

    private   Pattern criaRegex(String expressao) {
        // Substitui "+" por "|" e remove "." literal
        troca=false;
        expressao=expressao.replaceAll(" ","");
        String regexEmJava = expressao.replaceAll("\\+", "|").replaceAll("\\.", "").replaceAll("ε","");
        if (regexEmJava.compareTo(this.expressao)!=0){
            if(regexEmJava.contains("*"))
            temMaximo=false;
            else{

            }
            expressao=regexEmJava;
            troca=true;
            alfabeto = new ArrayList<>();
//        System.out.println("Regex convertido: " + regexEmJava);
            for (int i = 0; i < regexEmJava.length(); i++) {
                if(regexEmJava.charAt(i)>='0' && regexEmJava.charAt(i)<='9' || regexEmJava.charAt(i)>='a' && regexEmJava.charAt(i)<='z' || regexEmJava.charAt(i)>='A' && regexEmJava.charAt(i)<='Z'){
                    insereOrdenado(regexEmJava.charAt(i)+"");
                }

            }
        }
        return Pattern.compile("^" + regexEmJava + "$");
    }

    private void insereOrdenado(String s) {
        int i;
        for (i = 0; i < alfabeto.size() && s.compareTo(alfabeto.get(i)+"")>0; i++);
        if(i==alfabeto.size() || s.compareTo(alfabeto.get(i)+"")!=0){
            alfabeto.add(i,s);
        }
    }


}
