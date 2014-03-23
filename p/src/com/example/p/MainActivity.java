package com.example.p;

import com.example.p.R;
import com.example.p.UsuariosSQLiteHelper;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private EditText txtCodigo;
	private EditText txtNombre;
	private TextView txtResultado;
	private Button btnInsertar;
	private SQLiteDatabase db;

	private Button btnConsultar;
	
	private Button btnActualizar;
	private Button btnDesactivar;
	private TextView lblLatitud; 
	private TextView lblLongitud;
	private TextView lblPrecision;
	private TextView lblEstado;
	
	private LocationManager locManager;
	private LocationListener locListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
	//	txtCodigo = (EditText)findViewById(R.id.txtReg);
		//txtNombre = (EditText)findViewById(R.id.txtVal);
		txtResultado = (TextView)findViewById(R.id.txtResultado);
		
		btnInsertar = (Button)findViewById(R.id.btnInsertar);
		btnConsultar = (Button)findViewById(R.id.btnConsultar);
		
		btnActualizar = (Button)findViewById(R.id.BtnActualizar);
        btnDesactivar = (Button)findViewById(R.id.BtnDesactivar);
        lblLatitud = (TextView)findViewById(R.id.LblPosLatitud);
        lblLongitud = (TextView)findViewById(R.id.LblPosLongitud);
        //lblPrecision = (TextView)findViewById(R.id.LblPosPrecision);
       // lblEstado = (TextView)findViewById(R.id.LblEstado);
    	//Abrimos la base de datos 'DBUsuarios' en modo escritura
        UsuariosSQLiteHelper usdbh =
            new UsuariosSQLiteHelper(this, "DBUsuarios", null, 1);
 
        db = usdbh.getWritableDatabase();
        

		btnInsertar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				//Recuperamos los valores de los campos de texto
				String cod = lblLatitud.getText().toString();
				String nom = lblLongitud.getText().toString();
				
				//Alternativa 1: método sqlExec()
				//String sql = "INSERT INTO Usuarios (codigo,nombre) VALUES ('" + cod + "','" + nom + "') ";
				//db.execSQL(sql);
				
				//Alternativa 2: método insert()
				ContentValues nuevoRegistro = new ContentValues();
				nuevoRegistro.put("codigo", cod);
				nuevoRegistro.put("nombre", nom);
				db.insert("Usuarios", null, nuevoRegistro);
			}
		});
		
		btnConsultar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				//Alternativa 1: método rawQuery()
				Cursor c = db.rawQuery("SELECT codigo, nombre FROM Usuarios", null);
							
				//Alternativa 2: método delete()		 
				//String[] campos = new String[] {"codigo", "nombre"};
				//Cursor c = db.query("Usuarios", campos, null, null, null, null, null);
				
				//Recorremos los resultados para mostrarlos en pantalla
				txtResultado.setText("");
				if (c.moveToFirst()) {
				     //Recorremos el cursor hasta que no haya más registros
				     do {
				          String cod = c.getString(0);
				          String nom = c.getString(1);
				          
				          txtResultado.append(" " + cod + " - " + nom + "\n");
				     } while(c.moveToNext());
				}
			}
		});
		
        btnActualizar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				comenzarLocalizacion();
			}
		});
        
        btnDesactivar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		    	locManager.removeUpdates(locListener);
			}
		});
	}
	
    private void comenzarLocalizacion()
    {
    	//Obtenemos una referencia al LocationManager
    	locManager = 
    		(LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	
    	//Obtenemos la última posición conocida
    	Location loc = 
    		locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
    	//Mostramos la última posición conocida
    	mostrarPosicion(loc);
    	
    	//Nos registramos para recibir actualizaciones de la posición
    	locListener = new LocationListener() {
	    	public void onLocationChanged(Location location) {
	    		mostrarPosicion(location);
	    	}
	    	public void onProviderDisabled(String provider){
	    //		lblEstado.setText("Provider OFF");
	    	}
	    	public void onProviderEnabled(String provider){
	    	//	lblEstado.setText("Provider ON ");
	    	}
	    	public void onStatusChanged(String provider, int status, Bundle extras){
	    		Log.i("", "Provider Status: " + status);
	    	//	lblEstado.setText("Provider Status: " + status);
	    	}
    	};
    	
    	locManager.requestLocationUpdates(
    			LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }
     
    private void mostrarPosicion(Location loc) {
    	if(loc != null)
    	{
    		lblLatitud.setText(String.valueOf(loc.getLatitude()));
    		lblLongitud.setText(String.valueOf(loc.getLongitude()));
    	//	lblPrecision.setText("Precision: " + String.valueOf(loc.getAccuracy()));
    		Log.i("", String.valueOf(loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
    	}
    	else
    	{
    		lblLatitud.setText("Latitud: (sin_datos)");
    		lblLongitud.setText("Longitud: (sin_datos)");
    		lblPrecision.setText("Precision: (sin_datos)");
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
