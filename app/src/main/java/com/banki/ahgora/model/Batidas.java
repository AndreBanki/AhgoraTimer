package com.banki.ahgora.model;

import java.util.ArrayList;
import java.util.List;
public class Batidas {

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

    public String listaBatidas() {
        String retorno = "";
        for (int i = 0; i < batidas.size()-1; i++) {
            retorno += batidas.get(i).toString() + "-";
        }
        if (!batidas.isEmpty())
            retorno += batidas.get(batidas.size()-1).toString();
        return retorno;
    }

}
