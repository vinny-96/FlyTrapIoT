package com.vincenzoratti.flytrapiot;

public class Trampa {

    public final long id;
    public final String codigoSector;
    public final String tipoTrampa;
    public final int nivelCaptura;
    public final String observaciones;

    public Trampa(long id, String codigoSector, String tipoTrampa,
                  int nivelCaptura, String observaciones) {
        this.id = id;
        this.codigoSector = codigoSector;
        this.tipoTrampa = tipoTrampa;
        this.nivelCaptura = nivelCaptura;
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return codigoSector + " · " + tipoTrampa
                + " · Capturas: " + nivelCaptura;
    }
}