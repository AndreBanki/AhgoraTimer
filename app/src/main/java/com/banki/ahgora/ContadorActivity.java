package com.banki.ahgora;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.banki.ahgora.model.Batida;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.model.CalculoHoraExtra;
import com.banki.ahgora.settings.SettingsActivity;
import com.banki.ahgora.webservice.AsyncResponse;
import com.banki.ahgora.webservice.RetrieveResultTask;

import java.util.Calendar;

public class ContadorActivity extends AppCompatActivity implements ServiceConnection {

    private ContadorService contadorService;
    private Handler activityHandler;
    private FloatingActionButton startPauseBtn;
    private Intent serviceIntent;
    private Snackbar snackbar = null;
    CalculoHoraExtra calculador;
    private BroadcastReceiver mConnReceiver;

    private Batidas batidas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        inicializaBotoes();
        inicializaHandler();
        atualizaBotoes();

        calculador = new CalculoHoraExtra();

        serviceIntent = new Intent(ContadorActivity.this, ContadorService.class);
        startService(serviceIntent);

        mConnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               String valor = intent.getStringExtra("valor");
                if (valor != null) {
                    int count = Integer.parseInt(valor);
                    contadorService.setCount(contadorService.getCount()+count);
                    atualizaResultadoContagem(count);
                    Toast t = Toast.makeText(ContadorActivity.this, "Ta-daaaa!", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        };
    }

    private void inicializaBotoes() {
        startPauseBtn = (FloatingActionButton) findViewById(R.id.startBtn);

        startPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPausarContagem();
            }
        });
        startPauseBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pararContagem();
                return true;
            }
        });
    }

    private void criaSnackBarOpcaoDesfazerStop() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        final int countToKeep = contadorService.getCount();
        snackbar = Snackbar
                .make(coordinatorLayout, "Contador zerado!", Snackbar.LENGTH_INDEFINITE)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contadorService.setCount(countToKeep);
                        startService(serviceIntent);
                        atualizaResultadoContagem(countToKeep);
                        atualizaBotoes();
                    }
                });
        snackbar.show();
    }

    private void inicializaHandler() {
        activityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle envelope = msg.getData();
                int totalSegundos = envelope.getInt("count");
                atualizaResultadoContagem(totalSegundos);
                atualizaBotoes();
            }
        };
    }

    private void atualizaResultadoContagem(int totalSegundos) {
        TextView horasMinutosTxt = (TextView) findViewById(R.id.horasMinutos);
        horasMinutosTxt.setText(calculador.horasMinutosAsString(totalSegundos));

        TextView segundosTxt = (TextView) findViewById(R.id.segundos);
        segundosTxt.setText(calculador.segundosAsString(totalSegundos));

        TextView valorHorasTxt = (TextView) findViewById(R.id.valorHoras);
        valorHorasTxt.setText(calculador.valorHorasExtrasAsString(totalSegundos));
    }

    private void atualizaBotoes() {
        Drawable playImg = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_play);
        Drawable pauseImg = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_pause);
        if (contadorService == null)
            startPauseBtn.setImageDrawable(playImg);
        else if (contadorService.isRunning())
            startPauseBtn.setImageDrawable(pauseImg);
        else
            startPauseBtn.setImageDrawable(playImg);
    }

    private void iniciarPausarContagem() {
        // se foi clicado no stop, vai destruir ao sair da activity se não for iniciado de novo
        startService(serviceIntent);

        // foi clicado no stop e a snackbar está visível ainda, remover
        if (snackbar != null)
            snackbar.dismiss();

        contadorService.toggleState();
        atualizaBotoes();
    }

    private void pararContagem() {
        if (contadorService.getCount() > 0) {
            criaSnackBarOpcaoDesfazerStop();

            contadorService.reset();
            stopService(serviceIntent);

            atualizaResultadoContagem(0);
            atualizaBotoes();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        calculador.carregaPrefs(settings);
        bindService(serviceIntent, ContadorActivity.this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(ContadorActivity.this);
        atualizaBotoes();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ContadorService.ContadorBinder binder = (ContadorService.ContadorBinder) service;
        contadorService = binder.getContador();
        contadorService.setActivityHandler(activityHandler);
        registerReceiver(mConnReceiver, new IntentFilter("NOME_DA_ACAO"));

        atualizaResultadoContagem(contadorService.getCount());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contadorService.setActivityHandler(null);
        contadorService = null;
        unregisterReceiver(mConnReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent config = new Intent(ContadorActivity.this, SettingsActivity.class);
            startActivity(config);
            return true;
        }
        else if (id == R.id.btnStop) {
            pararContagem();
            pararAlarmManager();
        }
        else if (id == R.id.btnSOAP) {
            testeSOAP();
        }
        else if (id == R.id.testeBtn) {
            testeAlarmManager();
        }
        return super.onOptionsItemSelected(item);
    }

    private void testeSOAP() {
        String pis = "13873795727";
        RetrieveResultTask task = new RetrieveResultTask(new AsyncResponse() {
            @Override
            public void processFinish(Batidas result) {
                batidas = result;
                if (batidas == null) {
                    Toast t = Toast.makeText(ContadorActivity.this, "Erro na comunicação", Toast.LENGTH_LONG);
                    t.show();
                } else {
                    Toast t = Toast.makeText(ContadorActivity.this, batidas.listaBatidas(), Toast.LENGTH_LONG);
                    t.show();

                    Batida batidaRef = batidas.ultimaBatida();
                    Calendar agora = Calendar.getInstance();

                    int count = batidaRef.tempoDecorridoAte(agora);
                    contadorService.setCount(count);
                    atualizaResultadoContagem(count);
                }
            }
        });
        task.execute(pis);
    }

    private void pararAlarmManager() {
        Intent intent = new Intent("NOME_DA_ACAO");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void testeAlarmManager() {
        Intent intent = new Intent("NOME_DA_ACAO");
        intent.putExtra("valor","500");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        long dezSegundos = System.currentTimeMillis() + 10000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dezSegundos, dezSegundos, pendingIntent);
    }
}
