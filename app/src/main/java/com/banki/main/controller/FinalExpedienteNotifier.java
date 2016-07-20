package com.banki.main.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;

import java.util.Calendar;

public class FinalExpedienteNotifier extends BatidasNotifier {

    public FinalExpedienteNotifier(Context context) {
        super(context);
    }

    @Override
    protected void loadSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String avisoEntry = settings.getString("avisoFinalExpediente", "");
        emitirAviso = !avisoEntry.isEmpty();
        minutosAntesFinal = emitirAviso ? Integer.valueOf(avisoEntry) : 0;

        String jornadaEntry = settings.getString("jornadaTrabalho", "8");
        minutosDuracaoPeriodo = jornadaEntry.equals("8") ? 8*60 : 6*60;
    }

    @Override
    protected boolean deveVerificarAlarme(Batidas batidas) {
        return emitirAviso &&
               batidas.statusJornada() == Batidas.TRABALHANDO &&
               batidas.segundosTrabalhados() > 0;
    }

    @Override
    protected Calendar horaAviso(Batidas batidas) {
        Batida batidaRef = batidas.ultimaBatida();
        Calendar horaAviso = batidaRef.getAsDate();
        int segundosPeriodoDaTarde = 60*minutosDuracaoPeriodo - batidas.segundosTrabalhados();
        horaAviso.add(Calendar.SECOND, (segundosPeriodoDaTarde - 60*minutosAntesFinal));
        return horaAviso;
    }
}
