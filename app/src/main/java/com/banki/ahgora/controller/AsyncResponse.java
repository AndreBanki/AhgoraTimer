package com.banki.ahgora.controller;

import com.banki.ahgora.model.Batidas;

public interface AsyncResponse {

    void processFinish(Batidas result);
}