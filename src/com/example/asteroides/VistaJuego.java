package com.example.asteroides;

import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class VistaJuego extends View {

    // //// ASTEROIDES //////
    private Vector<Grafico> Asteroides; // Vector con los Asteroides
    private int numAsteroides= 5; // Número inicial de asteroides
    private int numFragmentos= 3; // Fragmentos en que se divide
    //Cambiamos la variable local por una global tipo array para fragmentar los asteroides
    private Drawable drawableAsteroide[]= new Drawable[3];
    
    // //// NAVE //////
    private Grafico nave;// Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private float aceleracionNave; // aumento de velocidad
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;
    private float mX=0, mY=0;
    private boolean disparo=false;
    
    // //// MISIL //////
    private Grafico misil;
    private static int PASO_VELOCIDAD_MISIL = 12;
    private boolean misilActivo = false;
    private int tiempoMisil;
    
    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;
    
    // //// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;
    
    // //// VIBRACION ///////
    private Vibrator vibracion;
    //private static long[] TIPO_VIBRACION = {130,30,25,40,45,60,25,65,35,200,45,40,40,80,30,40};
    
    // //// PUNTUACION //////
    private int puntuacion = 0;
    private Activity padre;
    

    public VistaJuego(Context context, AttributeSet attrs) {
          super(context, attrs);
          //Drawable drawableNave, drawableAsteroide, drawableMisil;
          Drawable drawableNave, drawableMisil;
          
          //drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
          //drawableNave = context.getResources().getDrawable(R.drawable.nave);
          
          //Consultamos las preferencias de usuario para saber si es bitmap o vectorial
          SharedPreferences pref = context.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
        		if (pref.getString("graficos", "1").equals("0")) {
        			   //Comprobamos version de android
        			   desactivarAceleracionHardware();
        			   //Definimos el asteroide en modo vectorial
        		       Path pathAsteroide = new Path();
        		       pathAsteroide.moveTo((float) 0.3, (float) 0.0);
        		       pathAsteroide.lineTo((float) 0.6, (float) 0.0);
        		       pathAsteroide.lineTo((float) 0.6, (float) 0.3);
        		       pathAsteroide.lineTo((float) 0.8, (float) 0.2);
        		       pathAsteroide.lineTo((float) 1.0, (float) 0.4);
        		       pathAsteroide.lineTo((float) 0.8, (float) 0.6);
        		       pathAsteroide.lineTo((float) 0.9, (float) 0.9);
        		       pathAsteroide.lineTo((float) 0.8, (float) 1.0);
        		       pathAsteroide.lineTo((float) 0.4, (float) 1.0);
        		       pathAsteroide.lineTo((float) 0.0, (float) 0.6);
        		       pathAsteroide.lineTo((float) 0.0, (float) 0.2);
        		       pathAsteroide.lineTo((float) 0.3, (float) 0.0);
        		       
        		       //Inicializacion sin fragmentacion de asteroides
        		       /*ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
        		       dAsteroide.getPaint().setColor(Color.WHITE);
        		       dAsteroide.getPaint().setStyle(Style.STROKE);
        		       dAsteroide.setIntrinsicWidth(50);
        		       dAsteroide.setIntrinsicHeight(50);
        		       drawableAsteroide = dAsteroide;*/
        		       
        		       //Inicializacion para fragmentacion de asteroides
        		       for (int i=0; i<3; i++) {
        		              ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
        		              dAsteroide.getPaint().setColor(Color.WHITE);
        		              dAsteroide.getPaint().setStyle(Style.STROKE);
        		              dAsteroide.setIntrinsicWidth(50 - i * 14);
        		              dAsteroide.setIntrinsicHeight(50 - i * 14);
        		              drawableAsteroide[i] = dAsteroide;
        		       }
        		       
        		       setBackgroundColor(Color.BLACK);
        		       
        		       //Definimos la nave en modo vectorial
        		       Path pathNave = new Path();
        		       pathNave.moveTo((float) 0.0, (float) 0.0);
        		       pathNave.lineTo((float) 0.0, (float) 1.0);
        		       pathNave.lineTo((float) 1.0, (float) 0.5);
        		       pathNave.lineTo((float) 0.0, (float) 0.0);
        		       ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
        		       dNave.getPaint().setColor(Color.WHITE);
        		       dNave.getPaint().setStyle(Style.STROKE);
        		       dNave.setIntrinsicWidth(50);
        		       dNave.setIntrinsicHeight(50);
        		       drawableNave = dNave;
        		       setBackgroundColor(Color.BLACK);
        		       
        		       //Definimos el misil
        		       ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
        		       dMisil.getPaint().setColor(Color.WHITE);
        		       dMisil.getPaint().setStyle(Style.STROKE);
        		       dMisil.setIntrinsicWidth(15);
        		       dMisil.setIntrinsicHeight(3);
        		       drawableMisil = dMisil;
        		} else {
        			   //Inicialización vieja
        		       //drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        			   drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
        			   drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
        			   drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
        		       drawableNave = context.getResources().getDrawable(R.drawable.nave);
        		       drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
        		}
          
          Asteroides = new Vector<Grafico>();
          nave = new Grafico(this, drawableNave);
          misil = new Grafico(this, drawableMisil);
          for (int i = 0; i < numAsteroides; i++) {
                //Grafico asteroide = new Grafico(this, drawableAsteroide);
                Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
                asteroide.setIncY(Math.random() * 4 - 2);
                asteroide.setIncX(Math.random() * 4 - 2);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                Asteroides.add(asteroide);
          }
          
          if (pref.getBoolean("efectos", true)){
        	  //Inicializamos los efectos de sonido
        	  soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
        	  idDisparo = soundPool.load(context, R.raw.disparo, 0);
        	  idExplosion = soundPool.load(context, R.raw.explosion, 0);
          }
          
          //Para saber el numero de fragmentos que quiere el usuario
          //POR HACER!!
          //numFragmentos = pref.getInt("fragmentos", 0);
          /*String numFragmentosS = pref.getString("fragmentos", "0");
          try{
        	  numFragmentos = Integer.parseInt(numFragmentosS);
          }catch (Exception e){}*/
          
          //Inicializamos la vibracion (si el usuario quiere)
          if(pref.getBoolean("vibrar", true)){
        	  vibracion = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
          }
    }

    @Override protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
          super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
          // Una vez que conocemos nuestro ancho y alto.
          for (Grafico asteroide: Asteroides) {
        	//Situamos la nave en la pantalla de juego
              nave.setPosX((ancho-nave.getAncho())/2);
              nave.setPosY((alto-nave.getAlto())/2);
              
        	  //Situamos los asteroides en la pantalla, pero pueden caer encima de la nave!
                //asteroide.setPosX(Math.random()*(ancho-asteroide.getAncho()));
                //asteroide.setPosY(Math.random()*(alto-asteroide.getAlto()));
        	  //Situamos los asteroides en la pantalla de juego en posiciones aleatorias evitando que estén encima de la nave
        	  do{
        	      asteroide.setPosX(Math.random()*(ancho-asteroide.getAncho()));
        	      asteroide.setPosY(Math.random()*(alto-asteroide.getAlto()));
        	} while(asteroide.distancia(nave) < (ancho+alto)/5);
          }
          //Ponemos en marcha la animación de los distintos elementos del juego
          ultimoProceso = System.currentTimeMillis();
          thread.start();
    }

    //synchronized
    @Override synchronized protected void onDraw(Canvas canvas) {
          super.onDraw(canvas);
          for (Grafico asteroide: Asteroides) {
              asteroide.dibujaGrafico(canvas);
              nave.dibujaGrafico(canvas);
              if(misilActivo)
            	  misil.dibujaGrafico(canvas);
          }
    }
    
    //La animación del juego la lleva a cabo el método actualizaFisica() que será ejecutado
    //a intervalos regulares definidos por la constante PERIODO_PROCESO. 
    
    //Metodo para detectar version de android y evitar problemas con graficos vectoriales
    @TargetApi(Build.VERSION_CODES.HONEYCOMB )
    private void desactivarAceleracionHardware(){
         if (android.os.Build.VERSION.SDK_INT>=11) {
             this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
         }
    }
    
    //synchronized 
    synchronized protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
              return;
        }
        // Para una ejecución en tiempo real calculamos retardo           
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de 
        // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        double nIncX = nave.getIncX() + aceleracionNave *
                             Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleracionNave * 
                            Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
        //LÍNEAS AÑADIDAS (FORO)PARA QUE LA NAVE SE DIRIJA HACIA LA MISMA DIRECCIÓN A LA QUE APUNTA
        //http://miriadax.net/es/web/android_programacion/foro/-/message_boards/view_message/3640559
        nIncX = Math.sqrt(Math.pow(nIncX, 2) + Math.pow(nIncY, 2))
                        * Math.cos(Math.toRadians(nave.getAngulo()));
        nIncY = Math.sqrt(Math.pow(nIncX, 2) + Math.pow(nIncY, 2))
                        * Math.sin(Math.toRadians(nave.getAngulo()));
        //FIN LÍNEAS AÑADIDAS
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX,nIncY) <= Grafico.getMaxVelocidad()){
              nave.setIncX(nIncX);
              nave.setIncY(nIncY);
        }
        // Actualizamos posiciones X e Y
        nave.incrementaPos(retardo);
        for (Grafico asteroide : Asteroides) {
              asteroide.incrementaPos(retardo);
        }
        
        // Actualizamos posición de misil
        if (misilActivo) {
               misil.incrementaPos(retardo);
               tiempoMisil-=retardo;
               if (tiempoMisil < 0) {
                     misilActivo = false;
               } else {
        for (int i = 0; i < Asteroides.size(); i++)
                     if (misil.verificaColision(Asteroides.elementAt(i))) {
                            destruyeAsteroide(i);
                            break;
                     }
               }
        }
        
        //Si la nave choca con un asteroide (mod9)
        for (Grafico asteroide : Asteroides) {
        	if (asteroide.verificaColision(nave)) {
        		   //Toast.makeText(padre, "¡Has perdido!", Toast.LENGTH_SHORT).show();
        	       salir();
        	}
        }
    }
    
    private void destruyeAsteroide(int i) {
    	//if(vibracion != null) vibracion.vibrate(TIPO_VIBRACION, -1);
    	if(vibracion != null) vibracion.vibrate(300);
    	//Añadido para la fragmentacion de asteroides
    	int tam;
    	if(Asteroides.get(i).getDrawable()!=drawableAsteroide[2]){
    	   if(Asteroides.get(i).getDrawable()==drawableAsteroide[1]){
    	          tam=2;
    	   } else {
    	          tam=1;
    	   }
    	   for(int n=0;n<numFragmentos;n++){
    	          Grafico asteroide = new Grafico(this,drawableAsteroide[tam]);
    	          asteroide.setPosX(Asteroides.get(i).getPosX());
    	          asteroide.setPosY(Asteroides.get(i).getPosY());
    	          asteroide.setIncX(Math.random()*7-2-tam);
    	          asteroide.setIncY(Math.random()*7-2-tam);
    	          asteroide.setAngulo((int)(Math.random()*360));
    	          asteroide.setRotacion((int)(Math.random()*8-4));
    	          Asteroides.add(asteroide);
    	   }     
    	}
    	
        Asteroides.remove(i);
        misilActivo = false;
        //Sonido al destruir un asteroide
        if(soundPool != null){
        	soundPool.play(idExplosion, 1, 1, 0, 0, 1); 
        }
        
        //Incrementamos la puntuacion
        puntuacion += 100;
        
        //Comprobamos si el usuario ha acabado con todos los asteroides (mod9)
        if (Asteroides.isEmpty()) {
        	//Toast.makeText(padre, "¡Has ganado!", Toast.LENGTH_SHORT).show();
            salir();
        }
    }

  
    private void ActivaMisil() {
        misil.setPosX(nave.getPosX()+ nave.getAncho()/2-misil.getAncho()/2);
        misil.setPosY(nave.getPosY()+ nave.getAlto()/2-misil.getAlto()/2);
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) *
                         PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) *
                         PASO_VELOCIDAD_MISIL);
        tiempoMisil = (int) Math.min(this.getWidth() / Math.abs( misil.
           getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
        misilActivo = true;
        
        //Sonido del misil
        if(soundPool != null){
        	soundPool.play(idDisparo, 1, 1, 1, 0, 1);
        }
    }
    
    
    //Metodo para manejar la nave con teclado fisico cuando se PULSA una tecla
    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
          super.onKeyDown(codigoTecla, evento);
           // Suponemos que vamos a procesar la pulsación
          boolean procesada = true;
          switch (codigoTecla) {
          case KeyEvent.KEYCODE_DPAD_UP:
                 aceleracionNave = +PASO_ACELERACION_NAVE;
                 break;
          case KeyEvent.KEYCODE_DPAD_LEFT:
                 giroNave = -PASO_GIRO_NAVE;
                 break;
          case KeyEvent.KEYCODE_DPAD_RIGHT:
                 giroNave = +PASO_GIRO_NAVE;
                 break;
          case KeyEvent.KEYCODE_DPAD_CENTER:
          case KeyEvent.KEYCODE_ENTER:
                 ActivaMisil();
                 break;
          default:
                 // Si estamos aquí, no hay pulsación que nos interese
                 procesada = false;
                 break;
          }
          return procesada;
    }
    
    //Metodo para manejar la nave con teclado fisico cuando se SUELTA una tecla
    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
          super.onKeyUp(codigoTecla, evento);
          // Suponemos que vamos a procesar la pulsación
          boolean procesada = true;
          switch (codigoTecla) {
          case KeyEvent.KEYCODE_DPAD_UP:
                 aceleracionNave = 0;
                 break;
          case KeyEvent.KEYCODE_DPAD_LEFT:
          case KeyEvent.KEYCODE_DPAD_RIGHT:
                 giroNave = 0;
                 break;
          default:
                 // Si estamos aquí, no hay pulsación que nos interese
                 procesada = false;
                 break;
          }
          return procesada;
    }
    
    //Metodo para manejar la nave a traves de la pantalla tactil
    //Modifica los parámetros de ajuste (<6,>6, /2, /25), para que se adapten de forma adecuada a tu terminal.
    @Override
    public boolean onTouchEvent (MotionEvent event) {
       super.onTouchEvent(event);
       float x = event.getX();
       float y = event.getY();
       switch (event.getAction()) {
       case MotionEvent.ACTION_DOWN:
              disparo=true;
              break;
       case MotionEvent.ACTION_MOVE:
              float dx = Math.abs(x - mX);
              float dy = Math.abs(y - mY);
              if (dy<6 && dx>6){
                     giroNave = Math.round((x - mX) / 2);
                     disparo = false;
              } else if (dx<6 && dy>6){
                     aceleracionNave = Math.round((mY - y) / 25);
                     //Para evitar que no frene la nave (YO QUIERO QUE FRENE)
                     //if(mY>y){aceleracionNave = Math.round((mY - y) / 25);}
                     disparo = false;
              }
              break;
       case MotionEvent.ACTION_UP:
              giroNave = 0;
              aceleracionNave = 0;
              if (disparo){
              ActivaMisil();
              }
              break;
       }
       mX=x; mY=y;       
        return true;
    }

    
    //La clase que representa un hilo de nuestra aplicacion (consume demasiada CPU)
    /*class ThreadJuego extends Thread {
    	   @Override
    	   public void run() {
    	          while (true) {
    	                 actualizaFisica();
    	          }
    	   }
    }*/
    
    //La alternativa a la clase anterior es meterle un sleep para que no abuse tanto de la CPU
    /*class ThreadJuego extends Thread {
    	 @Override
    	 public void run() {
    	        while (true) {
    	               actualizaFisica();
    	               try {
    	                     Thread.sleep(PERIODO_PROCESO);
    	               } catch (InterruptedException e) {
    	                     Thread.currentThread().interrupt();
    	               }
    	         }
    	  }
    }*/
    
    //Alternativa que propone el módulo 6
    class ThreadJuego extends Thread {
    	   private boolean pausa,corriendo;
    	 
    	   public synchronized void pausar() {
    	          pausa = true;
    	   }
    	 
    	   public synchronized void reanudar() {
    	          pausa = false;
    	          //Comentario del foro (http://miriadax.net/es/web/android_programacion/foro/-/message_boards/view_message/4189022)
    	          ultimoProceso = System.currentTimeMillis();
    	          notify();
    	   }
    	 
    	   public void detener() {
    	          corriendo = false;
    	          if (pausa) reanudar();
    	   }
    	  
    	   @Override    public void run() {
    	          corriendo = true;
    	          while (corriendo) {
    	                 actualizaFisica();
    	                 synchronized (this) {
    	                       while (pausa) {
    	                              try {
    	                                     wait();
    	                              } catch (Exception e) {
    	                              }
    	                       }
    	                 }
    	          }
    	   }
    }
    
    //Para obtener el thread desde clases externas (Juego)
    public ThreadJuego getThread() {
        return thread;
    }
    
    //Truco propuesto en el modulo 9 para el calculo de las puntuaciones
    public void setPadre(Activity padre) {
    	this.padre = padre;
    }
    
    private void salir() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("puntuacion", puntuacion);
    	Intent intent = new Intent();
    	intent.putExtras(bundle);
    	padre.setResult(Activity.RESULT_OK, intent);
    	padre.finish();
    }


}
