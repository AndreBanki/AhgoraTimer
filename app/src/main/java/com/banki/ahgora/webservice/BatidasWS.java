package com.banki.ahgora.webservice;

import com.banki.ahgora.model.Batidas;
import com.banki.ahgora.model.TimeConverter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class BatidasWS {

    private static String METHOD_NAME = "obterBatidas";
    private static String SOAP_ACTION = AhgoraWS.NAMESPACE + METHOD_NAME;

    public Batidas obtemListaBatidas(String pis) {
        SoapObject request = montaRequest(pis);
        SoapObject response = obtemResponse(request);
        return parseBatidas(response);
    }

    private Batidas parseBatidas(SoapObject response) {
        Batidas batidas = null;
        if (response != null) {
            batidas = new Batidas();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject batida = (SoapObject) response.getProperty(i);
                String hora = batida.getProperty("Hora").toString();
                batidas.addBatida(TimeConverter.createBatida(hora));
            }
        }
        return batidas;
    }

    private SoapObject montaRequest(String pis) {
        SoapObject request = new SoapObject(AhgoraWS.NAMESPACE, METHOD_NAME);
        request.addProperty(stringProperty("empresa", AhgoraWS.TOKEN));
        request.addProperty(stringProperty("datai",   TimeConverter.dataHoje()));
        request.addProperty(stringProperty("dataf",   TimeConverter.dataHoje()));
        request.addProperty(stringProperty("pis",     pis));
        return request;
    }

    private PropertyInfo stringProperty(String field, String value) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(field);
        pi.setValue(value);
        pi.setType(String.class);
        return pi;
    }

    private SoapObject obtemResponse(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(AhgoraWS.URL);
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            return (SoapObject)envelope.getResponse();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
