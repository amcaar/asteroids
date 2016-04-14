package com.example.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import com.example.asteroides.VistaJuego;

public class Juego extends Activity {
	
	   private VistaJuego vistaJuego;
	   private MediaPlayer mp;
	
	   @Override public void onCreate(Bundle savedInstanceState) {
	          super.onCreate(savedInstanceState);
	          setContentView(R.layout.juego);
	          
	          vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
	        
	  		//Consultamos las preferencias del usuario sobre la música
	  		SharedPreferences pref = this.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
	  		if(pref.getBoolean("musica", true)){
	  			//Añadiendo audio a asteroides
	  			mp = MediaPlayer.create(this, R.raw.juego);
	  			//Indicamos que queremos reproducir el audio en modo bucle
	  			mp.setLooping(true);
	  		}
	  		
			
			//Truco para recoger la puntuacion del usuario (mod9)
			vistaJuego.setPadre(this);
	   }
	   
	   @Override protected void onPause() {
		   super.onPause();
		   vistaJuego.getThread().pausar();
	   }
		 
	   @Override protected void onResume() {
		   super.onResume();
		   vistaJuego.getThread().reanudar();
		   SharedPreferences pref = this.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
		   if(mp != null && pref.getBoolean("musica", true)){
			   mp.start();
		   }
	   }
	   
		@Override protected void onStop() {
			super.onStop();
			if(mp != null){
				mp.pause();
			}
		}
		 
	   @Override protected void onDestroy() {
		   vistaJuego.getThread().detener();
		   super.onDestroy();
		   if(mp != null){
			   mp.stop();
		   }
	   }
}