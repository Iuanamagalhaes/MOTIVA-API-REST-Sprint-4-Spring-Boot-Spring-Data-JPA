package br.com.motiva.model;

public interface MonitoravelViaIoT {

    void transmitirDadosSensor(double leituraDoSensor);

    boolean isSensorAtivo();
}