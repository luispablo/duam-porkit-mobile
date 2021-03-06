package com.duam.porky.tasks;

import android.os.AsyncTask;

import com.duam.porky.ConstantesPorky;
import com.github.kevinsawicki.http.HttpRequest;

public class RemoveMovimientoTask extends AsyncTask<Long, Void, Boolean>
{

	@Override
	protected Boolean doInBackground(Long... params)
	{
		String url = ConstantesPorky.PorkitAPI.URL +
						String.format(ConstantesPorky.PorkitAPI.URI_MOVIMIENTOS_DELETE, params[0]);
		
		return HttpRequest.delete(url).ok();
	}

}
