package com.banki.ahgora.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Batidas implements Serializable {

    public static int VAZIO = 0;
    public static int TRABALHANDO = 1;
    public static int INTERVALO = 2;
    public static int ENCERRADO = 3;
    public static int EXCESSO_BATIDAS = 4;

    private List<Batida> batidas;

    public Batidas() {
        batidas = new ArrayList<>();
    }

    public void addBatida(Batida batida) {
        batidas.add(batida);
    }

    public Batida ultimaBatida() {
        if (batidas.isEmpty())
            return null;
        else
            return batidas.get(batidas.size()-1);
    }

    public int statusJornada() {
        switch (batidas.size()) {
            case 0: return VAZIO;
            case 1: return TRABALHANDO;
            case 2: return INTERVALO;
            case 3: return TRABALHANDO;
            case 4: return ENCERRADO;
            default: return EXCESSO_BATIDAS;
        }
    }

    public int tempoIntervalo() {
        if (batidas.size() < 3)
            return 0;
        else
            return inicioIntervalo().tempoDecorridoAte(fimIntervalo());
    }

    public int segundosTrabalhados() {
        if (batidas.size() < 2)
            return 0;
        else if (batidas.size() < 4)
            return inicioExpediente().tempoDecorridoAte(inicioIntervalo());
        else
            return inicioExpediente().tempoDecorridoAte(inicioIntervalo()) +
                   fimIntervalo().tempoDecorridoAte(fimExpediente());
    }

    private Batida inicioExpediente() {
        return batidas.get(0);
    }

    private Batida inicioIntervalo() {
        return batidas.get(1);
    }

    private Batida fimIntervalo() {
        return batidas.get(2);
    }

    private Batida fimExpediente() {
        return batidas.get(3);
    }

    public String listaBatidasAsString() {
        String retorno = "";
        for (int i = 0; i < batidas.size()-1; i++) {
            retorno += batidas.get(i).toString() + "-";
        }

        if (batidas.isEmpty())
            retorno = "---";
        else
            retorno += batidas.get(batidas.size()-1).toString();

        return retorno;
    }

}
