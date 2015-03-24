package com.duam.porky.tasks;

import static com.duam.porky.ConstantesPorky.BORRAR_MOVIMIENTO_URI;
import static com.duam.porky.ConstantesPorky.URL_PORKY;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

import com.duam.porky.model.Movimiento;
import com.github.kevinsawicki.http.HttpRequest;

public class RemoveMovimientoTask extends AsyncTask<Movimiento, Void, Boolean> 
{

	@Override
	protected Boolean doInBackground(Movimiento... params) 
	{
		Map<String, String> data = new HashMap<String, String>();
		data.put("id", String.valueOf(params[0].get_id()));
		
		return HttpRequest.post(URL_PORKY + BORRAR_MOVIMIENTO_URI).form(data).ok();
	}

}
