package com.example.asteroides;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class AlmacenPuntuacionesRecurso implements AlmacenPuntuaciones {

    private Context context;

    public AlmacenPuntuacionesRecurso(Context context) {
          this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha){
    	//En este tipo de almacenamiento no se puede escribir!
    }

    public Vector<String> listaPuntuaciones(int cantidad) {
          Vector<String> result = new Vector<String>();
          try {
        	     InputStream f = context.getResources().openRawResource(R.raw.puntuaciones);
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(f));
                 int n = 0;
                 String linea;
                 do {
                        linea = entrada.readLine();
                        if (linea != null) {
                               result.add(linea);
                               n++;
                        }
                 } while (n < cantidad && linea != null);
                 f.close();
          } catch (Exception e) {
                 Log.e("Asteroides", e.getMessage(), e);
          }
          return result;
    }
}
