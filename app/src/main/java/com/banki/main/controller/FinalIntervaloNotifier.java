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

    public void defineAlarmeSeNecessario(Context context, Batidas batidas) {
        if (batidas.statusJornada() == Batidas.INTERVALO) {
            Batida batidaRef = batidas.ultimaBatida();
            PendingIntent pendingIt = criaIntentQueSeraExecutadoPeloAlarme(context, batidaRef);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String avisarMinutosAntes = settings.getString("avisarMinutosAntes", "5");
            if (!avisarMinutosAntes.trim().isEmpty()) {
                Calendar horaAviso = batidaRef.getAsDate();
                int minutosAntes = Integer.valueOf(avisarMinutosAntes);
                horaAviso.add(Calendar.MINUTE, (60 - minutosAntes));

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, horaAviso.getTimeInMillis(), pendingIt);
            }
        }
    }

    private PendingIntent criaIntentQueSeraExecutadoPeloAlarme(Context context, Batida batidaRef) {
        Intent intent = new Intent("ALERTA_INTERVALO");
        intent.putExtra("batidaRef", batidaRef);

        final int requestCode = R.string.app_name;
        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
