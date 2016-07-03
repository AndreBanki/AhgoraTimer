package com.banki.ahgora.controller;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.banki.ahgora.MainActivity;
import com.banki.ahgora.R;
import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.contador.ActivityHandler;
import com.banki.ahgora.contador.ServiceActivity;
import com.banki.ahgora.webservice.AhgoraWS;
import com.banki.ahgora.webservice.BatidasTask;
import com.banki.ahgora.webservice_target.TargetTask;

import java.util.Calendar;

public class BatidasHandler extends ActivityHandler implements AsyncResponse {

    private Batidas batidas = new Batidas();
    private static boolean webServiceAhgoraRunning = false;

    private int secondsCountTarget;
    private static boolean webServiceTargetRunning = false;

    public BatidasHandler(ServiceActivity view) {
        super(view);
    }

    private MainActivity getView() {
        return (MainActivity)view;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putSerializable("batidas", batidas);
        state.putInt("countTarget", secondsCountTarget);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        batidas = (Batidas)state.getSerializable("batidas");
        if (batidas != null)
            getView().atualizaListaBatidas(batidas.listaBatidasAsString());

        secondsCountTarget = state.getInt("countTarget");
        getView().atualizaHorasTarget(secondsCountTarget);
    }

    @Override
    protected void atualizaResultadoContagem(int count) {
        if (batidas != null) {
            if (batidas.statusJornada() == Batidas.VAZIO) {
                getView().atualizaHorasTrabalhadas(0, false);
                getView().atualizaIntervalo(0, false);
            } else if (batidas.statusJornada() == Batidas.TRABALHANDO) {
                getView().atualizaHorasTrabalhadas(count, true);
                getView().atualizaIntervalo(batidas.tempoIntervalo(), false);
            } else if (batidas.statusJornada() == Batidas.INTERVALO) {
                getView().atualizaHorasTrabalhadas(batidas.segundosTrabalhados(), false);
                getView().atualizaIntervalo(count, true);
            } else { // Batidas.ENCERRADO, Batidas.EXCESSOBATIDAS
                getView().atualizaHorasTrabalhadas(batidas.segundosTrabalhados(), false);
                getView().atualizaIntervalo(batidas.tempoIntervalo(), false);
            }
        }
    }

    public void refreshDataFromWS() {
        if (!webServiceAhgoraRunning && !webServiceTargetRunning) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
            String pis = settings.getString("pis", "");
            String login = settings.getString("loginTarget", "");

            if (verificaDadosConfigurados(pis, login)) {
                getView().iniciaIndicacaoProgresso();

                webServiceAhgoraRunning = true;
                BatidasTask taskAhgora = new BatidasTask(this);
                taskAhgora.execute(pis);

                webServiceTargetRunning = true;
                TargetTask taskTarget = new TargetTask(this);
                taskTarget.execute(login);
            }
        }
    }

    private boolean verificaDadosConfigurados(String pis, String login) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(view.getApplicationContext());
        String empresa = settings.getString("empresa", "");
        if (pis.trim().isEmpty()) {
            getView().toast(view.getString(R.string.erro_pis_vazio));
            return false;
        } else if (login.trim().isEmpty()) {
            getView().toast(view.getString(R.string.erro_login_vazio));
            return false;
        } else if (!AhgoraWS.validaEmpresa(empresa)) {
            getView().toast(view.getString(R.string.erro_empresa_invalida));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void processFinishAhgora(Batidas result) {
        batidas = result;
        if (batidas == null)
            getView().toast(view.getString(R.string.erro_comunicacao_Ahgora));
        else {
            getView().atualizaListaBatidas(batidas.listaBatidasAsString());

            int count = valorCronometro();
            atualizaContadorService(count);
            atualizaResultadoContagem(count);
        }
        webServiceAhgoraRunning = false;
        if (!webServiceTargetRunning)
            terminaIndicacaoProgresso();
    }

    @Override
    public void processFinishTarget(float timeSpent) {
        secondsCountTarget = (int)(timeSpent * 3600);
        getView().atualizaHorasTarget(secondsCountTarget);

        webServiceTargetRunning = false;
        if (!webServiceAhgoraRunning)
            terminaIndicacaoProgresso();
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
        contadorService.setCount(count);
        if ((batidas.statusJornada() == Batidas.TRABALHANDO) ||
                (batidas.statusJornada() == Batidas.INTERVALO))
            contadorService.iniciar();
        else
            contadorService.pausar();
    }

    private void terminaIndicacaoProgresso() {
        float horasANotificar = horasNaNotificacaoTarget();
        if (horasANotificar != 0)
            criaNotificacaoTarget(view.getApplicationContext(), horasANotificar);

        getView().terminaIndicacaoProgresso();
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
                .setTicker(view.getString(R.string.app_name))
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
