package com.banki.ahgora.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.banki.ahgora.MainActivity;
import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.contador.ActivityHandler;
import com.banki.ahgora.contador.ServiceActivity;
import com.banki.ahgora.webservice.AhgoraWS;
import com.banki.ahgora.webservice.BatidasTask;
import com.banki.ahgora.webservice_target.TargetTask;

import java.util.Calendar;

public class BatidasHandler extends ActivityHandler implements AsyncResponse {

    private Batidas batidas = new Batidas();
    private static boolean webServiceRunning = false;

    public BatidasHandler(ServiceActivity view) {
        super(view);
    }

    private MainActivity getView() {
        return (MainActivity)view;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putSerializable("batidas", batidas);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        batidas = (Batidas)state.getSerializable("batidas");
        if (batidas != null)
            getView().atualizaListaBatidas(batidas.listaBatidasAsString());
    }

    @Override
    protected void atualizaResultadoContagem(int count) {
        if (batidas != null) {
            if (batidas.statusJornada() == Batidas.VAZIO) {
                getView().atualizaHorasTrabalhadas(0, false);
                getView().atualizaIntervalo(0, false);
            } else if (batidas.statusJornada() == Batidas.TRABALHANDO) {
                getView().atualizaHorasTrabalhadas(count, true);
                getView().atualizaIntervalo(batidas.tempoIntervalo(), false);
            } else if (batidas.statusJornada() == Batidas.INTERVALO) {
                getView().atualizaHorasTrabalhadas(batidas.horasJaTrabalhadas(), false);
                getView().atualizaIntervalo(count, true);
            } else { // Batidas.ENCERRADO, Batidas.EXCESSOBATIDAS
                getView().atualizaHorasTrabalhadas(batidas.horasJaTrabalhadas(), false);
                getView().atualizaIntervalo(batidas.tempoIntervalo(), false);
            }
        }
    }

    public void refreshDataFromWS() {
        if (!webServiceRunning) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
            String pis = settings.getString("pis", "");
            String empresa = settings.getString("empresa", "");
            if (pis.trim().isEmpty())
                getView().toast("Informe o seu PIS na tela de Configurações.");
            else if (!AhgoraWS.validaEmpresa(empresa))
                getView().toast("Código da empresa inválido. Este aplicativo é destinado apenas ao uso dos colaboradores da AltoQi.");
            else {
                webServiceRunning = true;
                getView().iniciaIndicacaoProgresso();

                BatidasTask taskAhgora = new BatidasTask(this);
                taskAhgora.execute(pis);

                TargetTask taskTarget = new TargetTask(this);
                taskTarget.execute("3");
            }
        }
    }

    @Override
    public void processFinishAhgora(Batidas result) {
        batidas = result;
        if (batidas == null)
            getView().toast("Erro na comunicação com o serviço de batidas.");
        else {
            getView().atualizaListaBatidas(batidas.listaBatidasAsString());

            int count = valorCronometro();
            atualizaContadorService(count);
            atualizaResultadoContagem(count);
        }
        webServiceRunning = false;
        getView().terminaIndicacaoProgresso();
    }

    @Override
    public void processFinishTarget(float timeSpent) {
        int count = (int)(timeSpent * 3600);
        getView().atualizaHorasTarget(count);

        webServiceRunning = false;
        getView().terminaIndicacaoProgresso();
    }

    private int valorCronometro() {
        int count = 0;
        Batida batidaRef = batidas.ultimaBatida();
        if (batidaRef != null) {
            Calendar agora = Calendar.getInstance();
            count = batidaRef.tempoDecorridoAte(agora);

            if (batidas.statusJornada() == Batidas.TRABALHANDO)
                count += batidas.horasJaTrabalhadas();
        }
        return count;
    }

    private void atualizaContadorService(int count) {
        contadorService.setCount(count);
        if ((batidas.statusJornada() == Batidas.TRABALHANDO) ||
                (batidas.statusJornada() == Batidas.INTERVALO))
            contadorService.iniciar();
        else
            contadorService.pausar();
    }
}
