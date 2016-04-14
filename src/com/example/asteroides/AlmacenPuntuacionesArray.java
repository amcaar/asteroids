package com.example.asteroides;

import java.util.Vector;

public class AlmacenPuntuacionesArray implements AlmacenPuntuaciones{

	private Vector<String> puntuaciones;

    public AlmacenPuntuacionesArray() {
         puntuaciones= new Vector<String>();
         puntuaciones.add("123000 Mochi Mochuelo");
         puntuaciones.add("104573 Rabbid Bwaaaa");
         puntuaciones.add("000258 Paco Mer");
    }

    //insertar en la primera posición del array un String con los puntos y el nombre. La fecha no es almacenada.
    @Override public void guardarPuntuacion(int puntos, String nombre, long fecha) {
         puntuaciones.add(0, puntos + " "+ nombre);
    }

    //devuelve el vector de String entero, sin tener en cuenta el parámetro cantidad que debería limitar el número de Strings devueltos. 
    @Override public Vector<String> listaPuntuaciones(int cantidad) {
         return  puntuaciones;
    }
}
