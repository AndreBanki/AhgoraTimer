package com.banki.main.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.main.MainActivity;
import com.banki.ahgora.R;

import java.util.Calendar;

public class BatidasReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Batidas batidas = (Batidas) intent.getSerializableExtra("batidas");
        Calendar horaAviso = (Calendar) intent.getSerializableExtra("horaAviso");

        PendingIntent pendingIt = criaIntentQueSeraDisparadoPelaNotificacao(context, batidas);

        String titulo, mensagem;
        if (batidas.statusJornada() == Batidas.INTERVALO) {
            titulo = "Final do intervalo";
            mensagem = "O intervalo do almoço está acabando";
        } else {
            titulo = "Final do expediente";
            mensagem = "O expediente de trabalho está acabando";
        }
        criaNotificacao(context, pendingIt, horaAviso, titulo, mensagem);
    }

    private PendingIntent criaIntentQueSeraDisparadoPelaNotificacao(Context context, Batidas batidas) {
        Intent forwardIt = new Intent(context, MainActivity.class);
        forwardIt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        forwardIt.putExtra("batidas", batidas);

        final int requestCode = R.string.app_name;
        return PendingIntent.getActivity(
                context,
                requestCode,
                forwardIt,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void criaNotificacao(Context context, PendingIntent pendingIt, Calendar horaAviso,
                                 String titulo, String mensagem) {
        NotificationCompat.Builder notif = new NotificationCompat.Builder(context)
                .setTicker(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setContentIntent(pendingIt)
                .setWhen(horaAviso.getTimeInMillis())
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        NotificationManager nManager;
        nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int uniqueNumber = R.string.app_name;
        nManager.notify(uniqueNumber, notif.build());
    }
}
