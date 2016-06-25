package com.banki.ahgora.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Batida implements Serializable {

    private Date horaBatida;

    public Batida(Date horaBatida) {
        this.horaBatida = horaBatida;
    }

    public Calendar getAsDate() {
        Calendar batida = Calendar.getInstance();
        batida.setTime(horaBatida);
        return batida;
    }

    public int tempoDecorridoAte(Calendar date) {
        Calendar batida = Calendar.getInstance();
        batida.setTime(horaBatida);
        return (int)(date.getTimeInMillis() - batida.getTimeInMillis()) / 1000;
    }

    public int tempoDecorridoAte(Batida batida) {
        return tempoDecorridoAte(batida.getAsDate());
    }

    @Override
    public String toString() {
        Locale PT_BR = new Locale("pt","BR");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", PT_BR);
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        return timeFormat.format(horaBatida.getTime());
    }
}
