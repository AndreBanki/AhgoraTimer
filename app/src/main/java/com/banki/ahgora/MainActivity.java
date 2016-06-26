package com.banki.ahgora;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.banki.ahgora.controller.BatidasHandler;
import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.model.TimeConverter;
import com.banki.ahgora.service.ContadorService;
import com.banki.ahgora.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private ContadorService contadorService;
    private BatidasHandler activityHandler;
    private FloatingActionButton startPauseBtn;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        activityHandler = new BatidasHandler(this);

        startPauseBtn = (FloatingActionButton) findViewById(R.id.startBtn);
        startPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityHandler.refreshDataFromWS();
            }
        });

        serviceIntent = new Intent(MainActivity.this, ContadorService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, MainActivity.this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(MainActivity.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        activityHandler.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        activityHandler = new BatidasHandler(this);
        activityHandler.onRestoreInstanceState(state);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ContadorService.ContadorBinder binder = (ContadorService.ContadorBinder) service;
        contadorService = binder.getContador();
        activityHandler.setContadorService(contadorService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contadorService.setActivityHandler(null);
        contadorService = null;
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
            Intent config = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(config);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toast(String texto) {
        Toast t = Toast.makeText(MainActivity.this, texto, Toast.LENGTH_LONG);
        t.show();
    }

    public void atualizaHorasTrabalhadas(int segundos, boolean running) {
        TextView horasMinutosTxt = (TextView) findViewById(R.id.horasMinutos);
        horasMinutosTxt.setText(TimeConverter.horasMinutosAsString(segundos));

        TextView segundosTxt = (TextView) findViewById(R.id.segundos);
        if (running)
            segundosTxt.setText(TimeConverter.segundosAsString(segundos));
        else
            segundosTxt.setText("");
    }

    public void atualizaIntervalo(int segundos, boolean running) {
        TextView valorHorasTxt = (TextView) findViewById(R.id.valorHoras);
        String texto = TimeConverter.horasMinutosAsString(segundos);
        if (running)
            texto += TimeConverter.segundosAsString(segundos);
        valorHorasTxt.setText(texto);

        int umaHora = 3600;
        if (segundos > 0 && segundos < umaHora) {
            LinearLayout frame = (LinearLayout) findViewById(R.id.frameIntervalo);
            frame.setBackgroundColor(getResources().getColor(R.color.colorError));
            frame.invalidate();
        }
    }

    public void atualizaListaBatidas(String listaBatidas) {
        TextView listaBatidasTxt = (TextView) findViewById(R.id.txtListaBatidas);
        listaBatidasTxt.setText("Batidas de hoje: " + listaBatidas);
    }

    public void iniciaIndicacaoProgresso() {
        atualizaListaBatidas("CONSULTANDO...");
        startPauseBtn.setAlpha((float)0.5);
    }

    public void terminaIndicacaoProgresso() {
        startPauseBtn.setAlpha((float)1);
    }
}
