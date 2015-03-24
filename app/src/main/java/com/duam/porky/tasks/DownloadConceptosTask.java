package com.duam.porky.tasks;

import static com.duam.porky.ConstantesPorky.URL_PORKY;
import static com.duam.porky.ConstantesPorky.GET_CONCEPTOS_URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.duam.porky.helpers.PorkyOpenHelper;
import com.duam.porky.model.Concepto;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class DownloadConceptosTask extends AsyncTask<String, Integer, Void> 
{
	private Context context;
	private long usuarioId;
	
	public DownloadConceptosTask(Context context, long usuarioId)
	{
		this.context = context;
		this.usuarioId = usuarioId;
	}
	
	@Override
	protected Void doInBackground(String... params) 
	{
		String nombreUsuario = params[0];
		String url = URL_PORKY + GET_CONCEPTOS_URI +"/"+ nombreUsuario;
		
		Log.d(DownloadConceptosTask.class.getName(), "Consultado "+ url);
		
		PorkyOpenHelper poh = new PorkyOpenHelper(this.context);
		
		try 
		{
			// https://github.com/kevinsawicki/http-request		
			JSONArray array = new JSONArray(HttpRequest.get(url).body());

			for (int i = 0; i < array.length(); i++)
			{
				JSONObject item = array.getJSONObject(i);
				
				Concepto c = new Concepto();
				c.set_id(item.getLong("id"));
				// FIXME: Guardar el factor???
				c.setFactorId(0);
				c.setNombre(item.getString("nombre"));
				c.setUsuarioId(this.usuarioId);
				
				poh.insertConcepto(c);
				
				this.publishProgress(i * 100 / array.length());
			}			
		} 
		catch (HttpRequestException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

		return null;
	}

}
