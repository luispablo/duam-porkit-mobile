package com.duam.porky.services;

import static com.duam.porky.ConstantesPorky.NUEVO_MOVIMIENTO_URI;
import static com.duam.porky.ConstantesPorky.URL_PORKY;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import roboguice.util.Ln;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.duam.porky.R;
import com.duam.porky.helpers.PorkyOpenHelper;
import com.duam.porky.model.Movimiento;
import com.github.kevinsawicki.http.HttpRequest;

public class UploadMovimientosService extends IntentService 
{
	private static final String TAG = UploadMovimientosService.class.getName();

	public UploadMovimientosService()
	{
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		if (hasNetworkConnection())
		{
			PorkyOpenHelper poh = new PorkyOpenHelper(getApplicationContext());
			
			Ln.d("Buscando movimientos a subir...");
			long[] ids = poh.getMovimientosIds();
			
			if (ids.length > 0)
			{
				Ln.i("Hay "+ ids.length +" movimientos para subir.");
				
				int success = 0;
				int noUpload = 0;
				int noDelete = 0;
				
				String message = ids.length + " movimientos. ";
				
				for (long id : ids)
				{
					Ln.d("Buscando el movimiento con id "+ id);
					Movimiento m = poh.findMovimientoById(id);
					
					if (uploadMovimiento(m))
					{
						Ln.d("Movimiento subido con exito");
						if (poh.deleteMovimiento(m.get_id()))
						{
							success++;
						}
						else
						{
							noDelete++;
						}			
					}
					else
					{
						Ln.d("Movimiento no subido");
						noUpload++;
					}			
				}
				
				message += success+ " movimientos subidos con exito. "+ noUpload +" movimientos no se pudieron subir, "+ noDelete +" movimientos no pudieron borrarse de la base local.";
				Log.i(TAG, message);
				notificar(message);
			}
			else
			{
				Log.i(TAG, "No hay movimientos para subir");
			}
		}
		else
		{
			Ln.i("No hay conexion, no se suben los movimientos");
		}
	}
	
	private boolean hasNetworkConnection() 
	{
	    boolean hasConnectedWifi = false;
	    boolean hasConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    
	    for (NetworkInfo ni : netInfo) 
	    {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	        {
	        	hasConnectedWifi = ni.isConnected();
	        }
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	        {
	        	hasConnectedMobile = ni.isConnected();
	        }
	    }
	    
	    return hasConnectedWifi || hasConnectedMobile;
	}
	
	private void notificar(String message)
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.notification);
		builder.setContentTitle("Resultado subida del movimiento");
		builder.setContentText(message);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(7, builder.build());
	}

	@SuppressLint("SimpleDateFormat")
	private boolean uploadMovimiento(Movimiento m)
	{
		Ln.d("A subir el movimiento...");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Ln.d("Armando el mapa con los datos del movimiento");
		Map<String, String> data = new HashMap<String, String>();
		data.put("fecha", sdf.format(m.getFecha()));
		data.put("concepto_id", String.valueOf(m.getConceptoId()));
		data.put("detalle", m.getDetalle());
		data.put("importe", String.valueOf(m.getImporte()));
		
		Ln.d("Enviando el post...");
		return HttpRequest.post(URL_PORKY + NUEVO_MOVIMIENTO_URI).form(data).ok();
	}
	
}
