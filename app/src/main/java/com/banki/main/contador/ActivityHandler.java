package com.banki.main.contador;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ActivityHandler extends Handler {

    protected ServiceActivity view;
    protected ContadorService contadorService;

    public ActivityHandler(ServiceActivity view) {
        this.view = view;
    }

    public void onSaveInstanceState(Bundle state) {}

    public void onRestoreInstanceState(Bundle state) {}

    public void setContadorService(ContadorService contadorService) {
        this.contadorService = contadorService;
        contadorService.setActivityHandler(this);
        atualizaResultadoContagem(contadorService.getCount());
    }

    public ContadorService getContadorService() {
        return contadorService;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle envelope = msg.getData();
        int totalSegundos = envelope.getInt("count");
        atualizaResultadoContagem(totalSegundos);
    }

    protected void atualizaResultadoContagem(int count) {}
}
