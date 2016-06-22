package com.banki.ahgora.webservice;

import com.banki.ahgora.model.Batidas;

public interface AsyncResponse {

    void processFinish(Batidas result);
}