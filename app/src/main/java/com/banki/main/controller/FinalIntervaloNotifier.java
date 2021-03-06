package com.banki.main.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.banki.ahgora.R;
import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;

import java.util.Calendar;

public class FinalIntervaloNotifier extends BatidasNotifier {

    public FinalIntervaloNotifier(Context context) {
        super(context);
    }

    @Override
    protected void loadSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String avisoEntry = settings.getString("avisoFinalIntervalo", "5");
        emitirAviso = !avisoEntry.isEmpty();
        minutosAntesFinal = emitirAviso ? Integer.valueOf(avisoEntry) : 0;

        String jornadaEntry = settings.getString("jornadaTrabalho", "8");
        minutosDuracaoPeriodo = jornadaEntry.equals("8") ? 60 : 15;
    }

    @Override
    protected boolean deveVerificarAlarme(Batidas batidas) {
        return emitirAviso &&
               batidas.statusJornada() == Batidas.INTERVALO;
    }

    @Override
    protected Calendar horaAviso(Batidas batidas) {
        Batida batidaRef = batidas.ultimaBatida();
        Calendar horaAviso = batidaRef.getAsDate();
        horaAviso.add(Calendar.MINUTE, (minutosDuracaoPeriodo - minutosAntesFinal));
        return horaAviso;
    }
}
