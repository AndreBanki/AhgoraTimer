package com.banki.ahgora.webservice;

import com.banki.ahgora.model.Batidas;

public interface IAhgoraResponse {

    void processFinish(Batidas result);
}
