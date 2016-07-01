package com.banki.ahgora.controller;

import com.banki.ahgora.model.Batidas;

public interface AsyncResponse {

    void processFinishAhgora(Batidas result);

    void processFinishTarget(float timeSpent);
}