package com.duam.porky.tasks;

import static com.duam.porky.ConstantesPorky.GET_MOVIMIENTOS_URI;
import static com.duam.porky.ConstantesPorky.URL_PORKY;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import com.duam.porky.model.Movimiento;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class DownloadMovimientosTask extends AsyncTask<String, Void, List<Movimiento>>
{
	private static final String TAG = DownloadMovimientosTask.class.getName();
	
	private String username;
	
	public DownloadMovimientosTask(String username)
	{
		this.username = username;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected List<Movimiento> doInBackground(String... params) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String fromDate = params[0];
		String toDate = params[1];
		String fromRow = params[2];
		String toRow = params[3];
		
		String url = URL_PORKY + GET_MOVIMIENTOS_URI +"/"+ username +"/"+ fromDate +"/"+ toDate +"/"+ fromRow +"/"+ toRow;		
		Log.d(TAG, "Consultando ["+ url +"]");
		
		try 
		{
			JSONArray array = new JSONArray(HttpRequest.get(url).body());
			List<Movimiento> movimientos = new ArrayList<Movimiento>();
			
			for (int i = 0; i < array.length(); i++)
			{
				JSONObject obj = array.getJSONObject(i);
				JSONObject objConcepto = obj.getJSONObject("concepto");
				Movimiento m = new Movimiento();
				
				m.setWebServiceId(obj.getLong("id"));
				m.setConceptoId(objConcepto.getLong("id"));
				m.setDetalle(obj.getString("detalle"));
				m.setFecha(sdf.parse(obj.getString("fecha")));
				m.setImporte((float) obj.getDouble("importe"));
				
				movimientos.add(m);
			}
			
			return movimientos;
		} 
		catch (ParseException e)
		{
			Log.e(TAG, "Error parseando fecha", e);
		}
		catch (HttpRequestException e) 
		{
			Log.e(TAG, "Error consultando movimientos", e);
		} 
		catch (JSONException e) 
		{
			Log.e(TAG, "Error con JSON", e);
		}
				
		return null;
	}

}
