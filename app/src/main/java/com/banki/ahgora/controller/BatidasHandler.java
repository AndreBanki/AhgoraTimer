package com.banki.ahgora.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.banki.ahgora.MainActivity;
import com.banki.ahgora.R;
import com.banki.ahgora.contador.ActivityHandler;
import com.banki.ahgora.contador.ServiceActivity;
import com.banki.ahgora.webservice.AhgoraWS;

public class BatidasHandler extends ActivityHandler {

    private TargetController targetController;
    private AhgoraController ahgoraController;

    public BatidasHandler(ServiceActivity view) {
        super(view);
        targetController = new TargetController(this, getView());
        ahgoraController = new AhgoraController(this, getView());
    }

    private MainActivity getView() {
        return (MainActivity)view;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        ahgoraController.onSaveInstanceState(state);
        targetController.onSaveInstanceState(state);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        ahgoraController.onRestoreInstanceState(state);
        targetController.onRestoreInstanceState(state);
    }

    @Override
    protected void atualizaResultadoContagem(int count) {
        ahgoraController.atualizaResultadoContagem(count);
    }

    public Context getApplicationContext() {
        return view.getApplicationContext();
    }

    public String getString(int resId) {
        return view.getString(resId);
    }

    public void toastErrorMessage(int errorMessage) {
        getView().toast(view.getString(errorMessage));
    }

    public void refreshDataFromWS() {
        if (!AhgoraController.webServiceRunning && !TargetController.webServiceRunning) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
            String pis = settings.getString("pis", "");
            String login = settings.getString("loginTarget", "");

            if (verificaDadosConfigurados(pis, login)) {
                getView().iniciaIndicacaoProgresso();

                AhgoraController.webServiceRunning = true;
                ahgoraController.initWebService(pis);

                TargetController.webServiceRunning = true;
                targetController.initWebService(login);
            }
        }
    }

    private boolean verificaDadosConfigurados(String pis, String login) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
        String empresa = settings.getString("empresa", "");
        if (pis.trim().isEmpty()) {
            getView().toast(view.getString(R.string.erro_pis_vazio));
            return false;
        } else if (login.trim().isEmpty()) {
            getView().toast(view.getString(R.string.erro_login_vazio));
            return false;
        } else if (!AhgoraWS.validaEmpresa(empresa)) {
            getView().toast(view.getString(R.string.erro_empresa_invalida));
            return false;
        } else {
            return true;
        }
    }

    public void consultaTargetEncerrada() {
        if (!AhgoraController.webServiceRunning)
            terminaIndicacaoProgresso();
    }

    public void consultaAhgoraEncerrada() {
        if (!TargetController.webServiceRunning)
            terminaIndicacaoProgresso();
    }

    private void terminaIndicacaoProgresso() {
        NotificadorDiferencaApontamento notificador = new NotificadorDiferencaApontamento();
        notificador.criaNotificacaoSeNecessario(
                view.getApplicationContext(),
                ahgoraController.getBatidas(),
                targetController.getSecondsCount());

        getView().terminaIndicacaoProgresso();
    }
}
