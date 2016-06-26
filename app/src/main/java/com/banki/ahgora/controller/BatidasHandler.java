package com.banki.ahgora.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.banki.ahgora.MainActivity;
import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.webservice.AhgoraWS;
import com.banki.ahgora.webservice.BatidasTask;

import java.util.Calendar;

public class BatidasHandler extends ActivityHandler implements AsyncResponse {

    private Batidas batidas = new Batidas();
    private static boolean webServiceRunning = false;

    public BatidasHandler(MainActivity view) {
        super(view);
    }

    public void onSaveInstanceState(Bundle state) {
        state.putSerializable("batidas", batidas);
    }

    public void onRestoreInstanceState(Bundle state) {
        batidas = (Batidas)state.getSerializable("batidas");
        view.atualizaListaBatidas(batidas.listaBatidasAsString());
    }

    @Override
    protected void atualizaResultadoContagem(int count) {
        if (batidas != null) {
            if (batidas.statusJornada() == Batidas.VAZIO) {
                view.atualizaHorasTrabalhadas(0, false);
                view.atualizaIntervalo(0, false);
            } else if (batidas.statusJornada() == Batidas.TRABALHANDO) {
                view.atualizaHorasTrabalhadas(count, true);
                view.atualizaIntervalo(batidas.tempoIntervalo(), false);
            } else if (batidas.statusJornada() == Batidas.INTERVALO) {
                view.atualizaHorasTrabalhadas(batidas.horasJaTrabalhadas(), false);
                view.atualizaIntervalo(count, true);
            } else { // Batidas.ENCERRADO, Batidas.EXCESSOBATIDAS
                view.atualizaHorasTrabalhadas(batidas.horasJaTrabalhadas(), false);
                view.atualizaIntervalo(batidas.tempoIntervalo(), false);
            }
        }
    }

    public void refreshDataFromWS() {
        if (!webServiceRunning) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
            String pis = settings.getString("pis", "");
            String empresa = settings.getString("empresa", "");
            if (pis.trim().isEmpty())
                view.toast("Informe o seu PIS na tela de Configurações.");
            else if (!AhgoraWS.validaEmpresa(empresa))
                view.toast("Código da empresa inválido. Este aplicativo é destinado apenas ao uso dos colaboradores da AltoQi.");
            else {
                webServiceRunning = true;
                BatidasTask task = new BatidasTask(this);
                task.execute(pis);
            }
        }
    }

    @Override
    public void processFinish(Batidas result) {
        batidas = result;
        if (batidas == null)
            view.toast("Erro na comunicação com o serviço de batidas.");
        else {
            view.atualizaListaBatidas(batidas.listaBatidasAsString());

            int count = valorCronometro();
            atualizaContadorService(count);
            atualizaResultadoContagem(count);
        }
        webServiceRunning = false;
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
