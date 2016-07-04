package com.banki.ahgora.controller;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.banki.ahgora.R;
import com.banki.ahgora.model.Batidas;

import java.util.Calendar;

public class NotificadorDiferencaApontamento {

    private int secondsCountTarget;
    private Batidas batidas;
    private BatidasHandler handler;

    public NotificadorDiferencaApontamento(BatidasHandler handler, Batidas batidas, int secondsCountTarget) {
        this.handler = handler;
        this.batidas = batidas;
        this.secondsCountTarget = secondsCountTarget;
    }

    public void criaNotificacaoSeNecessario() {
        float horasANotificar = horasNaNotificacaoTarget();
        if (horasANotificar != 0)
            criaNotificacaoTarget(handler.getApplicationContext(), horasANotificar);
    }

    private float horasNaNotificacaoTarget() {
        if (batidas != null) {
            if (batidas.statusJornada() == Batidas.ENCERRADO || batidas.statusJornada() == Batidas.INTERVALO) {
                float diferencaMinutos = (secondsCountTarget - batidas.segundosTrabalhados())/60;
                if (Math.abs(diferencaMinutos) > 10)
                    return diferencaMinutos / 60;
            }
        }
        return 0;
    }

    private void criaNotificacaoTarget(Context context, float horasANotificar) {
        String mensagem = String.format("%1$.2f h ",Math.abs(horasANotificar)) +
                (horasANotificar > 0 ? "sobrando " : "faltando ") +
                "no apontamento de hoje.";

        NotificationCompat.Builder notif = new NotificationCompat.Builder(context)
                .setTicker(handler.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_alert_outline_white_24dp)
                .setContentTitle("Apontamentos do Target")
                .setContentText(mensagem)
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setAutoCancel(true);

        NotificationManager nManager;
        nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int uniqueNumber = R.string.app_name + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        nManager.notify(uniqueNumber, notif.build());
    }
}
