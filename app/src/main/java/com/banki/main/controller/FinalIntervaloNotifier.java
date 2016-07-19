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

public class FinalIntervaloNotifier {

    private boolean avisarFinalIntervalo;
    private int minutosAntesIntervalo;
    private int minutosDuracaoIntervalo;
    Context context;

    public FinalIntervaloNotifier(Context context) {
        this.context = context;
        loadSettings();
    }

    public void defineAlarmeSeNecessario(Batidas batidas) {
        if (deveVerificarAlarme(batidas)) {
            Calendar horaAviso = horaAviso(batidas);
            Calendar horaAtual = Calendar.getInstance();
            if (horaAviso.after(horaAtual)) {
                PendingIntent pendingIt = criaIntentQueSeraExecutadoPeloAlarme(batidas, horaAviso);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, horaAviso.getTimeInMillis(), pendingIt);
            }
        }
    }

    private void loadSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String avisoEntry = settings.getString("avisoFinalIntervalo", "5");
        avisarFinalIntervalo = !avisoEntry.isEmpty();
        minutosAntesIntervalo = avisarFinalIntervalo ? Integer.valueOf(avisoEntry) : 0;

        String jornadaEntry = settings.getString("jornadaTrabalho", "8");
        minutosDuracaoIntervalo = jornadaEntry.equals("8") ? 60 : 15;
    }

    private boolean deveVerificarAlarme(Batidas batidas) {
        return avisarFinalIntervalo &&
               batidas.statusJornada() == Batidas.INTERVALO;
    }

    private Calendar horaAviso(Batidas batidas) {
        Batida batidaRef = batidas.ultimaBatida();
        Calendar horaAviso = batidaRef.getAsDate();
        horaAviso.add(Calendar.MINUTE, (minutosDuracaoIntervalo - minutosAntesIntervalo));
        return horaAviso;
    }

    private PendingIntent criaIntentQueSeraExecutadoPeloAlarme(Batidas batidas, Calendar horaAviso) {
        Intent intent = new Intent("ALERTA_INTERVALO");
        intent.putExtra("batidas", batidas);
        intent.putExtra("horaAviso", horaAviso);

        final int requestCode = R.string.app_name;
        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
