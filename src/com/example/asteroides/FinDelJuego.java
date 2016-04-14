package com.example.asteroides;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 

public class FinDelJuego extends Activity {

    /** Called when the activity is first created. */
	private Button bAceptar;
	private EditText campoNombre;
	private String nombre = "Usuario";

    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fin_juego);
   
        campoNombre =(EditText) findViewById(R.id.nombre);

		bAceptar =(Button) findViewById(R.id.okNombre);
		bAceptar.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) { 
				nombre = campoNombre.getText().toString();
				if(nombre.equals("")) nombre = "Usuario";
				salir(); 
			} 
		});

    }
    
    private void salir() {
    	Bundle bundle = new Bundle();
    	bundle.putString("nombre", nombre);
    	Intent intent = new Intent();
    	intent.putExtras(bundle);
    	this.setResult(Activity.RESULT_OK, intent);
    	this.finish();
    }

}
