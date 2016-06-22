package com.banki.ahgora.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Batida {

    private Date horaBatida;

    public Batida(Date horaBatida) {
        this.horaBatida = horaBatida;
    }

    public int tempoDecorridoAte(Calendar date) {
        Calendar batida = Calendar.getInstance();
        batida.setTime(horaBatida);
        int tempo = (int)(date.getTimeInMillis() - batida.getTimeInMillis()) / 1000;
        return tempo;
    }

    @Override
    public String toString() {
        Locale PT_BR = new Locale("pt","BR");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", PT_BR);
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        String hora = timeFormat.format(horaBatida.getTime());
        return hora;
    }
}
