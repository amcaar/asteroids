package com.example.asteroides;

import java.util.Vector;

public interface AlmacenPuntuaciones {
	
	//guardar la puntuaci�n de una partida
	public void guardarPuntuacion(int puntos,String nombre,long fecha);
	
	//obtener una lista de puntuaciones previamente almacenadas. 
	//El par�metro cantidad indica el n�mero m�ximo de puntuaciones que ha de devolver.
    public Vector<String> listaPuntuaciones(int cantidad);

}

