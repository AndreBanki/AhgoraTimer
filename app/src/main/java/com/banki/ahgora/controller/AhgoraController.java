package com.banki.ahgora.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.banki.ahgora.R;
import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.webservice.BatidasTask;
import com.banki.ahgora.webservice.IAhgoraResponse;
import com.banki.main.controller.BatidasHandler;

import java.util.Calendar;

public class AhgoraController implements IAhgoraResponse {

    private IAhgoraView view;
    private BatidasHandler handler;
    private Batidas batidas = new Batidas();

    public static boolean webServiceRunning = false;

    public AhgoraController(BatidasHandler handler, IAhgoraView view) {
        this.view = view;
        this.handler = handler;
    }

    public Batidas getBatidas() {
        return this.batidas;
    }

    public void onSaveInstanceState(Bundle state) {
        state.putSerializable("batidas", batidas);
    }

    public void onRestoreInstanceState(Bundle state) {
        batidas = (Batidas) state.getSerializable("batidas");
        if (batidas != null)
            view.atualizaListaBatidas(batidas.listaBatidasAsString());
    }

    public void trataAberturaViaNotificacao(Intent intent) {
        batidas = (Batidas) intent.getSerializableExtra("batidas");
        if (batidas != null)
            view.atualizaListaBatidas(batidas.listaBatidasAsString());
    }

    public void atualizaResultadoContagem(int count) {
        if (batidas != null) {
            if (batidas.statusJornada() == Batidas.VAZIO) {
                view.atualizaHorasTrabalhadas(0, false);
                view.atualizaIntervalo(0, false);
            } else if (batidas.statusJornada() == Batidas.TRABALHANDO) {
                view.atualizaHorasTrabalhadas(count, true);
                view.atualizaIntervalo(batidas.tempoIntervalo(), false);
            } else if (batidas.statusJornada() == Batidas.INTERVALO) {
                view.atualizaHorasTrabalhadas(batidas.segundosTrabalhados(), false);
                view.atualizaIntervalo(count, true);
            } else { // Batidas.ENCERRADO, Batidas.EXCESSOBATIDAS
                view.atualizaHorasTrabalhadas(batidas.segundosTrabalhados(), false);
                view.atualizaIntervalo(batidas.tempoIntervalo(), false);
            }
        }
    }

    public void initWebService(String pis) {
        BatidasTask taskAhgora = new BatidasTask(this);
        taskAhgora.execute(pis);
    }

    @Override
    public void processFinish(Batidas result) {
        batidas = result;
        if (batidas == null)
            handler.toastErrorMessage(R.string.erro_comunicacao_Ahgora);
        else {
            view.atualizaListaBatidas(batidas.listaBatidasAsString());

            int count = valorCronometro();
            atualizaContadorService(count);
            atualizaResultadoContagem(count);
        }
        AhgoraController.webServiceRunning = false;
        handler.consultaAhgoraEncerrada();
    }

    private int valorCronometro() {
        int count = 0;
        Batida batidaRef = batidas.ultimaBatida();
        if (batidaRef != null) {
            Calendar agora = Calendar.getInstance();
            count = batidaRef.tempoDecorridoAte(agora);

            if (batidas.statusJornada() == Batidas.TRABALHANDO)
                count += batidas.segundosTrabalhados();
        }
        return count;
    }

    private void atualizaContadorService(int count) {
        handler.getContadorService().setCount(count);
        if ((batidas.statusJornada() == Batidas.TRABALHANDO) ||
                (batidas.statusJornada() == Batidas.INTERVALO))
            handler.getContadorService().iniciar();
        else
            handler.getContadorService().pausar();
    }
}
