package com.banki.main.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.banki.ahgora.R;
import com.banki.ahgora.model.Batidas;

import java.util.Calendar;

public abstract class BatidasNotifier {

    protected boolean emitirAviso;
    protected int minutosAntesFinal;
    protected int minutosDuracaoPeriodo;

    Context context;

    public BatidasNotifier(Context context) {
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

    protected abstract void loadSettings();

    protected abstract boolean deveVerificarAlarme(Batidas batidas);

    protected abstract Calendar horaAviso(Batidas batidas);
}
