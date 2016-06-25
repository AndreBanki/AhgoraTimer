package com.banki.ahgora.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.banki.ahgora.MainActivity;
import com.banki.ahgora.service.ContadorService;

public class ActivityHandler extends Handler {

    protected MainActivity view;
    protected ContadorService contadorService;

    public ActivityHandler(MainActivity view, ContadorService contadorService) {
        this.view = view;
        this.contadorService = contadorService;

        contadorService.setActivityHandler(this);
        atualizaResultadoContagem(contadorService.getCount());
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle envelope = msg.getData();
        int totalSegundos = envelope.getInt("count");
        atualizaResultadoContagem(totalSegundos);
    }

    protected void atualizaResultadoContagem(int count) {
        view.atualizaHorasTrabalhadas(count);
    }
}
