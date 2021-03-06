package com.duam.porky;

import static com.duam.porky.ConstantesPorky.PORKY_PREFS;
import static com.duam.porky.ConstantesPorky.PREF_ID_USUARIO;
import static com.duam.porky.ConstantesPorky.PREF_NOMBRE_USUARIO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duam.porky.helpers.PorkyOpenHelper;
import com.duam.porky.model.Concepto;
import com.duam.porky.model.Movimiento;
import com.duam.porky.services.UploadMovimientosService;
import com.duam.porky.tasks.DownloadMovimientosTask;
import com.duam.porky.tasks.RemoveMovimientoTask;

public class MovimientosActivity extends PorkyActivity 
{
	private static final String TAG = MovimientoActivity.class.getName();
	
	private static final int movimientosPorCarga = 15;
	
	private MovimientoAdapter adapter;
	private int cantidadMovimientos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movimientos);
		
		adapter = new MovimientoAdapter(this, new ArrayList<Movimiento>(), new PorkyOpenHelper(this));
		((ListView) findViewById(R.id.lwMovimientos)).setAdapter(adapter);
		
		setTitle("");
		
		((Button) findViewById(R.id.btnCargarMovimientos)).setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				cargarMasMovimientos();
			}
		});
	}

	private void uploadMovimientos()
	{
		Log.d(TAG, "Trying to fire the service intent...");
		Intent intent = new Intent(this, UploadMovimientosService.class);
		startService(intent);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();

		if (adapter.getCount() == 0) {
			cargarMasMovimientos();
		}

		this.cantidadMovimientos = 0;
		uploadMovimientos();
	}

	@SuppressLint("SimpleDateFormat")
	private void cargarMasMovimientos()
	{
		SharedPreferences prefs = getSharedPreferences(PORKY_PREFS, MODE_PRIVATE);
		final ProgressDialog pd = new ProgressDialog(this);
		
		DownloadMovimientosTask task = new DownloadMovimientosTask(prefs.getLong(PREF_ID_USUARIO, -1))
		{
			@Override
			protected void onPreExecute() 
			{
				super.onPreExecute();
				
				pd.setTitle("Descargando movimientos");
				pd.setMessage("Aguarde un momento por favor...");
				pd.show();
			}			
			@Override
			protected void onPostExecute(List<Movimiento> result) 
			{
				super.onPostExecute(result);
				
				if (result != null && !result.isEmpty())
				{
					cantidadMovimientos += result.size();
					adapter.addAll(result);
					adapter.notifyDataSetChanged();
				}
				
				pd.dismiss();
			}
		};
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		task.execute("2014-01-01", sdf.format(new Date()), String.valueOf(this.cantidadMovimientos), String.valueOf(movimientosPorCarga));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.movimientos, menu);
	    
	    PorkyOpenHelper poh = new PorkyOpenHelper(this);
	    long[] ids = poh.getMovimientosIds();
	    
	    if (ids.length > 0)
	    {
		    View count = menu.findItem(R.id.badge).getActionView();
		    ((Button) count.findViewById(R.id.pending_movs_count)).setText(String.valueOf(ids.length));	    	
	    }
	    else
	    {
	    	menu.findItem(R.id.badge).setVisible(false);
	    	menu.findItem(R.id.action_upload).setVisible(false);
	    }
	    	    
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
			case R.id.action_add:
				Intent intent = new Intent(this, MovimientoActivity.class);
				startActivity(intent);
				return true;
            case R.id.action_chart:
                Intent chartIntent = new Intent(MovimientosActivity.this, BalanceActivity.class);
                startActivity(chartIntent);
                return true;
			case R.id.action_upload:
				uploadMovimientos();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}		
	}

	class MovimientoAdapter extends BaseAdapter
	{
		private Context context;
		private List<Movimiento> data;
		private PorkyOpenHelper poh;
		
		@InjectView(R.id.txtFecha) TextView txtFecha;

		public MovimientoAdapter(Context context, List<Movimiento> movimientos, PorkyOpenHelper poh)
		{
			this.data = movimientos;
			this.context = context;
			this.poh = poh;
		}
		
		public void addAll(List<Movimiento> result) 
		{
			data.addAll(result);
		}

		@Override
		public int getCount() 
		{
			return data.size();
		}

		@Override
		public Object getItem(int index) 
		{
			return data.get(index);
		}

		@Override
		public long getItemId(int index) 
		{
			return ((Movimiento) data.get(index)).get_id();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(final int index, final View convertView, ViewGroup parent) 
		{
			View vi = convertView;			
			final Movimiento mov = (Movimiento) data.get(index);			
			final Concepto con = poh.findConceptoById(mov.getConceptoId());
			
			if (vi == null)
			{
				vi = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_movimiento, null);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            txtFecha = (TextView) vi.findViewById(R.id.txtFecha);
			txtFecha.setText(sdf.format(mov.getFecha()));
			
			Date fechaAnterior = (index > 0) ? data.get(index - 1).getFecha(): null;
			int visibility = (fechaAnterior != null && mov.getFecha().equals(fechaAnterior)) ? View.GONE: View.VISIBLE;
			txtFecha.setVisibility(visibility);
			
			((TextView) vi.findViewById(R.id.txtDetalle)).setText(mov.getDetalle());
			((TextView) vi.findViewById(R.id.txtConcepto)).setText((con != null) ? con.getNombre() : "??");
			((TextView) vi.findViewById(R.id.txtImporte)).setText("$ "+ mov.getImporte());

			vi.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					editarMovimiento(mov);
				}
			});

			return vi;
		}
		
		private void editarMovimiento(final Movimiento mov)
		{
			Intent intent = new Intent(MovimientosActivity.this, MovimientoActivity.class);
			intent.putExtra("movimiento", mov);
			startActivity(intent);
		}
	}
}
