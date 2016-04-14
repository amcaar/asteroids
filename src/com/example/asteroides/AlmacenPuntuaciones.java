package com.example.asteroides;

import java.util.Vector;

public interface AlmacenPuntuaciones {
	
	//guardar la puntuación de una partida
	public void guardarPuntuacion(int puntos,String nombre,long fecha);
	
	//obtener una lista de puntuaciones previamente almacenadas. 
	//El parámetro cantidad indica el número máximo de puntuaciones que ha de devolver.
    public Vector<String> listaPuntuaciones(int cantidad);

}

