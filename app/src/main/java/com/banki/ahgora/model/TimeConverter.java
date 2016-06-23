package com.banki.ahgora.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeConverter {

    private static Locale PT_BR = new Locale("pt","BR");
    private static String DATEFORMAT_ATREQUEST = "dd/MM/yyyy";
    private static String TIMEFORMAT_ATRESPONSE = "HHmm";
    private static String DATETIMEFORMAT = DATEFORMAT_ATREQUEST + TIMEFORMAT_ATRESPONSE;

    public static String dataHoje() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_ATREQUEST, PT_BR);
        Calendar hoje = Calendar.getInstance();
        String data = dateFormat.format(hoje.getTime());
        return data;
    }

    public static Batida createBatida(String hora) {
        Batida batida = null;

        String dataHora = TimeConverter.dataHoje() + hora;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        try {
            Date parsedDate = dateFormat.parse(dataHora);
            batida = new Batida(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return batida;
    }

    public static String horasMinutosAsString(int totalSegundos) {
        int horas = totalSegundos / 3600;
        int minutos = (totalSegundos - horas * 3600) / 60;
        return String.format("%02d:%02d",horas,minutos);
    }

    public static String segundosAsString(int totalSegundos) {
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos - minutos * 60;
        return String.format(":%02d",segundos);
    }

}
