package com.banki.ahgora.controller;

import android.os.Bundle;

import com.banki.ahgora.webservice_target.ITargetResponse;
import com.banki.ahgora.webservice_target.TargetTask;

public class TargetController implements ITargetResponse {

    private ITargetView view;
    private int secondsCountTarget;
    private BatidasHandler handler;

    public static boolean webServiceRunning = false;

    public TargetController (BatidasHandler handler, ITargetView view) {
        this.view = view;
        this.handler = handler;
    }

    public int getSecondsCount() {
        return this.secondsCountTarget;
    }

    public void onSaveInstanceState(Bundle state) {
        state.putInt("countTarget", secondsCountTarget);
    }

    public void onRestoreInstanceState(Bundle state) {
        secondsCountTarget = state.getInt("countTarget");
        view.atualizaHorasTarget(secondsCountTarget);
    }

    public void initWebService(String login) {
        TargetTask taskTarget = new TargetTask(this);
        taskTarget.execute(login);
    }

    @Override
    public void processFinish(float timeSpent) {
        secondsCountTarget = (int)(timeSpent * 3600);
        view.atualizaHorasTarget(secondsCountTarget);

        TargetController.webServiceRunning = false;
        handler.consultaTargetEncerrada();
    }
}
