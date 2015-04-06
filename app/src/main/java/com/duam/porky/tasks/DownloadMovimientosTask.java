package com.duam.porky.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.duam.porky.ConstantesPorky;
import com.duam.porky.model.Movimiento;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DownloadMovimientosTask extends AsyncTask<String, Void, List<Movimiento>>
{
	private static final String TAG = DownloadMovimientosTask.class.getName();
	
	private long idUsuario;
	
	public DownloadMovimientosTask(long idUsuario)
	{
		this.idUsuario = idUsuario;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected List<Movimiento> doInBackground(String... params) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String fromDate = params[0];
		String toDate = params[1];
		int fromRow = Integer.parseInt(params[2]);
		int quantity = Integer.parseInt(params[3]);
		
		String url = ConstantesPorky.PorkitAPI.URL +
						String.format(ConstantesPorky.PorkitAPI.URI_MOVIMIENTOS_LIST, idUsuario, fromDate, toDate, fromRow, quantity);
		Log.d(TAG, "Consultando ["+ url +"]");
		
		try 
		{
			JSONObject response = new JSONObject(HttpRequest.get(url).body());
			JSONArray array = response.getJSONArray("result");
			List<Movimiento> movimientos = new ArrayList<Movimiento>();
			
			for (int i = 0; i < array.length(); i++)
			{
				JSONObject obj = array.getJSONObject(i);
				Movimiento m = new Movimiento();
				
				m.setWebServiceId(obj.getLong("mov_id"));
				m.setConceptoId(obj.getLong("con_id"));
				m.setDetalle(obj.getString("mov_detalle"));
				m.setFecha(sdf.parse(obj.getString("mov_fecha")));
				m.setImporte((float) obj.getDouble("mov_importe"));
				
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
