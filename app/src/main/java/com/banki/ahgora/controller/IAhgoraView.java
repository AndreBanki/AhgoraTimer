package com.banki.ahgora.controller;

public interface IAhgoraView {

    void atualizaHorasTrabalhadas(int segundos, boolean exibirSegundos);

    void atualizaIntervalo(int segundos, boolean exibirSegundos);

    void atualizaListaBatidas(String listaBatidas);

}
