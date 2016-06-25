package com.banki.ahgora.controller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.banki.ahgora.MainActivity;
import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.service.ContadorService;
import com.banki.ahgora.webservice.BatidasTask;

import java.util.Calendar;

public class BatidasHandler extends ActivityHandler implements AsyncResponse {

    private Batidas batidas = new Batidas();

    public BatidasHandler(MainActivity view, ContadorService contadorService) {
        super(view, contadorService);
    }

    @Override
    protected void atualizaResultadoContagem(int count) {
        if (batidas == null)
            return;
        else if (batidas.statusJornada() == Batidas.VAZIO) {
            view.atualizaHorasTrabalhadas(0);
            view.atualizaIntervalo(0);
        } else if (batidas.statusJornada() == Batidas.TRABALHANDO) {
            view.atualizaHorasTrabalhadas(count);
            view.atualizaIntervalo(batidas.tempoIntervalo());
        } else if (batidas.statusJornada() == Batidas.INTERVALO) {
            view.atualizaHorasTrabalhadas(batidas.horasJaTrabalhadas());
            view.atualizaIntervalo(count);
        } else { // Batidas.ENCERRADO, Batidas.EXCESSOBATIDAS
            view.atualizaHorasTrabalhadas(batidas.horasJaTrabalhadas());
            view.atualizaIntervalo(batidas.tempoIntervalo());
        }
    }

    public void refreshDataFromWS() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
        String pis = settings.getString("pis", "");
        BatidasTask task = new BatidasTask(this);
        task.execute(pis);
    }

    @Override
    public void processFinish(Batidas result) {
        batidas = result;
        if (batidas == null)
            view.toast("Erro na comunicação");
        else {
            view.toast("Batidas de hoje: " + batidas.listaBatidas());

            int count = valorCronometro();
            atualizaContadorService(count);
            atualizaResultadoContagem(count);
        }
    }

    private int valorCronometro() {
        Batida batidaRef = batidas.ultimaBatida();
        Calendar agora = Calendar.getInstance();
        int count = batidaRef.tempoDecorridoAte(agora);

        if (batidas.statusJornada() == Batidas.TRABALHANDO)
            count += batidas.horasJaTrabalhadas();

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
