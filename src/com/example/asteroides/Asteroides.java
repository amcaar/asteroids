package com.example.asteroides;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Asteroides extends Activity {

	private TextView titulo;
	// private Button bAcercaDe;
	public static AlmacenPuntuaciones almacen;
	private MediaPlayer mp;
	private int puntuacion;
	private String nombre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		titulo = (TextView) findViewById(R.id.titulo);

		// Especificamos una fuente concreta
		Typeface lcdFont = Typeface.createFromAsset(getAssets(),
				"fonts/TechnoHideo.ttf");
		titulo.setTypeface(lcdFont);

		// para que el siguiente codigo funcione (deteccion de evento onClick
		// por codigo), hay que importar:
		// import android.view.View.OnClickListener

		/*
		 * bAcercaDe =(Button) findViewById(R.id.button3);
		 * bAcercaDe.setOnClickListener(new OnClickListener() { public void
		 * onClick(View view) { lanzarAcercaDe(null); } });
		 */
		
		//Para ver el ciclo de vida de la actividad
		//Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		
		//Consultamos las preferencias del usuario sobre la música
		SharedPreferences pref = this.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
		if(pref.getBoolean("musica", true)){
			//Añadiendo audio a asteroides
			mp = MediaPlayer.create(this, R.raw.audio);
			//Indicamos que queremos reproducir el audio en modo bucle
			mp.setLooping(true);
			//mp.start();
		}
		
		//Lanzar el servicio de musica
		//startService(new Intent(Asteroides.this, ServicioMusica.class));

		//Inicializar el almacen de puntuaciones 
		switch (Integer.parseInt(pref.getString("almacenamiento", "1"))) {
		case 0:
			almacen = new AlmacenPuntuacionesArray();
			//Toast.makeText(this, "array", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			almacen = new AlmacenPuntuacionesPreferencias(this);
			//Toast.makeText(this, "preferencias", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			almacen = new AlmacenPuntuacionesFicheroInterno(this);
			//Toast.makeText(this, "fichero interno", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			almacen = new AlmacenPuntuacionesFicheroExterno(this);
			//Toast.makeText(this, "fichero externo", Toast.LENGTH_SHORT).show();
			break;
		case 4:
			almacen = new AlmacenPuntuacionesRecurso(this);
			//Toast.makeText(this, "fichero recursos", Toast.LENGTH_SHORT).show();
			break;
		case 5:
			almacen = new AlmacenPuntuacionesXML_SAX(this);
			//Toast.makeText(this, "XML", Toast.LENGTH_SHORT).show();
			break;
		case 6:
			almacen = new AlmacenPuntuacionesSQLite(this);
			//Toast.makeText(this, "BD", Toast.LENGTH_SHORT).show();
			break;
		/*case 7:
			almacen = new AlmacenPuntuacionesSocket(this);
			//Toast.makeText(this, "Sockets", Toast.LENGTH_SHORT).show();
			break;*/
		}

	}
	
	/*public void lanzarJuego(View view) {
		Intent i = new Intent(this, Juego.class);
		startActivity(i);
	}*/
	
	//Cambiamos la implementacion de lanzarJuego para obtener la puntuacion del jugador (modulo 9)
	public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        startActivityForResult(i, 1234);
    }

    /*@Override protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1234 & resultCode==RESULT_OK & data!=null){
               int puntuacion = data.getExtras().getInt("puntuacion");
               String nombre = "Usuario";
               // Mejor leerlo desde un Dialog o una nueva actividad AlertDialog.Builder
               almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
               Toast.makeText(this, "¡Fin del juego!", Toast.LENGTH_SHORT).show();
               lanzarPuntuaciones(null);
        }
      }*/
    
    @Override protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1234 & resultCode==RESULT_OK & data!=null){
               puntuacion = data.getExtras().getInt("puntuacion");
               lanzarFinDelJuego(null);
        }
        if (requestCode==4321 & resultCode==RESULT_OK & data!=null){
              nombre = data.getExtras().getString("nombre");
              almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
              lanzarPuntuaciones(null);
        }
      }

	public void lanzarAcercaDe(View view) {
		Intent i = new Intent(this, AcercaDe.class);
		startActivity(i);
	}

	public void lanzarPreferencias(View view) {
		Intent i = new Intent(this, Preferencias.class);
		startActivity(i);
	}

	public void lanzarPuntuaciones(View view) {
		Intent i = new Intent(this, Puntuaciones.class);
		startActivity(i);
	}
	
	public void lanzarFinDelJuego(View view) {
		Intent i = new Intent(this, FinDelJuego.class);
		//startActivity(i);
		startActivityForResult(i, 4321);
	}

	public void salir(View view) {
		// Version sencilla (solo finaliza la actividad)
		// finish();

		// Version con un dialogo de alerta
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//builder.setMessage("¿Seguro que deseas salir?").setCancelable(false)
		builder.setMessage(R.string.preguntasalir).setCancelable(false)
				.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// Menu

	// codigo por defecto
	/*
	 * public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this
	 * adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
		/** true -> el menú ya está visible */
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.acercaDe:
			lanzarAcercaDe(null);
			break;
		case R.id.config:
			lanzarPreferencias(null);
			break;
		case R.id.salir:
			salir(null);
			break;
		}
		return true;
		/** true -> consumimos el item, no se propaga */
	}
	
	//Metodos para controlar el ciclo de vida de la actividad
	
	    @Override protected void onStart() {
		   super.onStart();
		   //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
		}
		 
		@Override protected void onResume() {
		   super.onResume();
		   //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		   SharedPreferences pref = this.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
		   if(mp != null && pref.getBoolean("musica", true)){
			   mp.start();
		   }
		}
		 
		@Override protected void onPause() {
		   //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
		   super.onPause();
		   //mp.pause();
		}
		 
		@Override protected void onStop() {
		   //Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
		   super.onStop();
		   if(mp != null){
			   mp.pause();
		   }
		}
		 
		@Override protected void onRestart() {
		   super.onRestart();
		   //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
		}
		 
		@Override protected void onDestroy() {
		   //Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
		   super.onDestroy();
		   if(mp != null){
			   mp.stop();
		   }
		   
		   //Parar el servicio de musica
		   //stopService(new Intent(Asteroides.this, ServicioMusica.class));
		}
		
		//Metodos para salvar el estado de la musica por si cambiamos de orientacion el telefono
		@Override
		   protected void onSaveInstanceState(Bundle estadoGuardado){
		          super.onSaveInstanceState(estadoGuardado);
		          if (mp != null) {
		                 int pos = mp.getCurrentPosition();
		                 estadoGuardado.putInt("posicion", pos);
		          }
		   }
		 
		   @Override
		   protected void onRestoreInstanceState(Bundle estadoGuardado){
		          super.onRestoreInstanceState(estadoGuardado);
		          if (estadoGuardado != null && mp != null) {
		                 int pos = estadoGuardado.getInt("posicion");
		                 mp.seekTo(pos);
		          }
		   }
    
}
