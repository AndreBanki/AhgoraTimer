package com.banki.main.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.banki.main.MainActivity;
import com.banki.ahgora.R;
import com.banki.main.contador.ActivityHandler;
import com.banki.main.contador.ServiceActivity;
import com.banki.ahgora.controller.AhgoraController;
import com.banki.ahgora.webservice.AhgoraWS;
import com.banki.targetprocess.controller.TargetController;

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

    public void trataAberturaViaNotificacao(Intent intent) {
        ahgoraController.trataAberturaViaNotificacao(intent);
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
        DiferencaApontamentoNotifier notificador = new DiferencaApontamentoNotifier(view.getApplicationContext());
        notificador.criaNotificacaoSeNecessario(ahgoraController.getBatidas(),
                                                targetController.getSecondsCount());

        FinalIntervaloNotifier notificador2 = new FinalIntervaloNotifier(view.getApplicationContext());
        notificador2.defineAlarmeSeNecessario(ahgoraController.getBatidas());

        FinalExpedienteNotifier notificador3 = new FinalExpedienteNotifier(view.getApplicationContext());
        notificador3.defineAlarmeSeNecessario(ahgoraController.getBatidas());

        getView().terminaIndicacaoProgresso();
    }
}
