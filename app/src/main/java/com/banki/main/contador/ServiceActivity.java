package com.banki.main.contador;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;


public abstract class ServiceActivity  extends AppCompatActivity implements ServiceConnection {

    private ContadorService contadorService;
    protected ActivityHandler activityHandler;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inicializaHandler();
        serviceIntent = new Intent(ServiceActivity.this, ContadorService.class);
        startService(serviceIntent);
    }

    protected void inicializaHandler() {
        activityHandler = new ActivityHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, ServiceActivity.this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(ServiceActivity.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        activityHandler.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        inicializaHandler();
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
}

