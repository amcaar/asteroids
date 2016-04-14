package com.example.asteroides;

import java.util.Vector;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MiAdaptador extends BaseAdapter {

    private final Activity actividad;
    private final Vector<String> lista;

    public MiAdaptador(Activity actividad, Vector<String> lista) {
          super();
          this.actividad = actividad;
          this.lista = lista;
    }

    //Para crear un adaptador personalizado es necesario exteder de BaseAdapter e implementar los siguientes metodos
    
    //Este método ha de construir un nuevo objeto View con el Layout 
    //correspondiente a la posiciónposition y devolverlo. Opcionalmente
    //podemos partir de una vista base convertView para generar más rápido
    //las vistas. El último parámetro corresponde al padre al que la vista va  a ser añadida.
    
    public View getView(int position, View convertView, 
                                     ViewGroup parent) {
          LayoutInflater inflater = actividad.getLayoutInflater();
          View view = inflater.inflate(R.layout.elemento_lista, null, true);
          TextView textView =(TextView)view.findViewById(R.id.titulo);
          textView.setText(lista.elementAt(position));
          ImageView imageView=(ImageView)view.findViewById(R.id.icono);
          switch (Math.round((float)Math.random()*3)){
          case 0:
                 imageView.setImageResource(R.drawable.asteroide1);
                 break;
          case 1:
                 imageView.setImageResource(R.drawable.asteroide2);
                 break;
          default:
                 imageView.setImageResource(R.drawable.asteroide3);
                 break;
          }
          return view;
    }

    //Devuelve el número de elementos de la lista. 
    public int getCount() {
          return lista.size();
    }

    //Devuelve el elemento en una determinada posición de la lista.
    public Object getItem(int arg0) {
          return lista.elementAt(arg0);
    }

    //Devuelve el identificador de fila de una determinada posición de la lista.
    public long getItemId(int position) {
          return position;
    }
}