package com.practice.jose;

import java.util.List;

public class Prestamo {
    private final int MONTO_MINIMO = 20000;

    private final double PORCENTAJE_SOLTERO = 0.15;
    private final double PORCENTAJE_CASADO = 0.1;
    private final double PORCENTAJE_PAREJA = 0.2;
    private final double PORCENTAJE_INMUEBLE_SOLTERO = 1.5;
    private final double PORCENTAJE_INMUEBLE_CASADO = 1.2;
    private final double PORCENTAJE_INMUEBLE_PAREJA = 1.5;
    private final double PORCENTAJE_INMUEBLE_1GARANTE = 1.5;
    private final double PORCENTAJE_INMUEBLE_2GARANTE = 2;

    private List<Persona> personas;
    private List<Persona> garantes;
    private boolean esPareja;
    private boolean esCasado;


    boolean esElegible(List<Persona> solicitantes, List<Persona> garantes, double montoSolicitado, boolean esCasado, boolean esPareja) {
        if (montoSolicitado >= MONTO_MINIMO) {
            double montoMinimoAhorroSolicitante = montoSolicitado * (esCasado ? PORCENTAJE_CASADO : esPareja ? PORCENTAJE_PAREJA : PORCENTAJE_SOLTERO);
            double montoMinimoInmuebleSolicitante = montoSolicitado * (esCasado ? PORCENTAJE_INMUEBLE_CASADO : esPareja ? PORCENTAJE_INMUEBLE_PAREJA : PORCENTAJE_INMUEBLE_SOLTERO);
            double montoMinimoInmuebleGarante = montoSolicitado * (esPareja ? PORCENTAJE_INMUEBLE_2GARANTE : PORCENTAJE_INMUEBLE_1GARANTE);

            double ahorroTotal = 0;
            double valorInmueble = 0;
            int personasConTrabajo = 0;

            for (Persona persona : solicitantes) {
                ahorroTotal += persona.getAhorro();
                if (persona.isTieneTrabajo()) {
                    personasConTrabajo++;
                }
                if (persona.getInmueble() != null) {
                    valorInmueble += persona.getInmueble().getValor();
                }
            }
            if (ahorroTotal < montoMinimoAhorroSolicitante && garantes != null && !garantes.isEmpty()) {

                return esGaranteElegible(garantes, montoMinimoAhorroSolicitante, montoMinimoInmuebleGarante) && solicitantes.size() <= personasConTrabajo;
            }


            return solicitantes.size() <= personasConTrabajo && valorInmueble >= montoMinimoInmuebleSolicitante;
        }
        return false;
    }

    //1Solicitante =.TieneTrabajo() && .tieneInmueble && valorInmueble >= montoSolicitado*1.5 &&.getAhorro() >= ahorroMinimo;
    //2solicitante = (s1tieneTrabajo || s2tieneTrabajo) && (s1tieneInmueble && valorInmueble >= valorMinimo) || s2tieneInmueble && valornmueble >= valorMinimo) && (s1ahorros + s2ahorro) >= ahorroMinimo
    //1Garante = (tieneTrabajo || valorInmueble >= montoSolicitado*porcentaje) && ahorro>= ahorroMinimo
    //2Garante = valorInmueble >=montoSolicitado*porcentaje && ahorro >= ahorroMinimo && solicitantesTieneTrabajo

    //En cualquier caso, ya sea 1 persona o pareja pueden tener maximo 2 garantes, en ese  caso, la suma de ahorros de cada garante debe sumar el minimo del ahorro requerido.
//
//  si tiene un garante, el garante debe tener trabajo sino trabaja debera tener un bien inmueble con el valor minimo de 150% del monto solicitado, si son 2 garantes la suma del valor de sus
//inmuebles debera ser minimo el 200% del monto solicitado.
//
//se asume que las personas ya sea 1 o 2, no cumple con el minimo de ahorro requerido, nesecita garantes maximo 2 garantes, ellos deberan tener el ahorro minimo ylos personas deberan
//tener si o si trabajo.`
    boolean esGaranteElegible(List<Persona> garantes, double montoMinimoAhorros,
                              double montoMinimoInmuebleGarante) {
        int conTrabajo = 0;
        double totalAhorros = 0;
        double totalValorInmueble = 0;
        for (Persona persona : garantes) {
            if (persona.isTieneTrabajo()) {
                conTrabajo++;
            }
            if (persona.getInmueble() != null) {
                totalValorInmueble += persona.getInmueble().getValor();
            }
            totalAhorros += persona.getAhorro();

        }
        if (conTrabajo >= 1) {
            return totalValorInmueble >= montoMinimoInmuebleGarante && totalAhorros >= montoMinimoAhorros;
        }
        return totalAhorros > montoMinimoAhorros;
    }
}

    //1Solicitante =.TieneTrabajo() && .tieneInmueble && valorInmueble >= montoSolicitado*1.5 &&.getAhorro() >= ahorroMinimo;
//2solicitante = (s1tieneTrabajo || s2tieneTrabajo) && (s1tieneInmueble && valorInmueble >= valorMinimo) || s2tieneInmueble && valornmueble >= valorMinimo) && (s1ahorros + s2ahorro) >= ahorroMinimo
//1Garante = (tieneTrabajo || valorInmueble >= montoSolicitado*porcentaje) && ahorro>= ahorroMinimo
//2Garante = valorInmueble >=montoSolicitado*porcentaje && ahorro >= ahorroMinimo && solicitantesTieneTrabajo
    class Persona {
        private boolean tieneTrabajo;
        private double ahorro;
        private Inmueble inmueble;

        public Persona(boolean tieneTrabajo, double ahorro, Inmueble inmueble) {
            this.tieneTrabajo = tieneTrabajo;
            this.ahorro = ahorro;
            this.inmueble = inmueble;
        }

        public boolean isTieneTrabajo() {
            return tieneTrabajo;
        }

        public void setTieneTrabajo(boolean tieneTrabajo) {
            this.tieneTrabajo = tieneTrabajo;
        }

        public double getAhorro() {
            return ahorro;
        }

        public void setAhorro(double ahorro) {
            this.ahorro = ahorro;
        }

        public Inmueble getInmueble() {
            return inmueble;
        }

        public void setInmueble(Inmueble inmueble) {
            this.inmueble = inmueble;
        }
    }

    class Inmueble {
        private double valor;

        public Inmueble(double valor) {
            this.valor = valor;
        }

        public double getValor() {
            return valor;
        }

        public void setValor(double valor) {
            this.valor = valor;
        }
    }
