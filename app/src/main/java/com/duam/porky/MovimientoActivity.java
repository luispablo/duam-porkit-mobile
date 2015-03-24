package com.duam.porky;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.duam.porky.helpers.PorkyOpenHelper;
import com.duam.porky.tasks.DownloadConceptosTask;

@ContentView(R.layout.activity_movimiento)
public class MovimientoActivity extends PorkyActivity 
{
	private static final String TAG = MovimientoActivity.class.getName();
	
	private long conceptoId = -1;
	
	@InjectView(R.id.editFecha) EditText editFecha;
	
	@Override
	protected void onCreate(Bundle savedState) 
	{
		super.onCreate(savedState);
		
		editFecha.setOnClickListener(new OnClickListener() 
		{			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) 
			{
				final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");				
				final Calendar cal = Calendar.getInstance();
				
				try 
				{
					cal.setTime(sdf.parse(editFecha.getText().toString()));
				} 
				catch (ParseException e) 
				{
					Log.e(TAG, "Error parseando fecha", e);
				}
				
				OnDateSetListener listener = new OnDateSetListener() 
				{					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
					{
						cal.set(Calendar.YEAR, year);
						cal.set(Calendar.MONTH, monthOfYear);
						cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						
						
						editFecha.setText(sdf.format(cal.getTime()));
					}
				};
				DatePickerDialog dialog = new DatePickerDialog(MovimientoActivity.this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
				dialog.show();			
			}
		});
		
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[]{"nombre"}, new int[]{android.R.id.text1}, 0);		

		AutoCompleteTextView concepto = (AutoCompleteTextView) findViewById(R.id.editConcepto);
		concepto.setAdapter(adapter);
		concepto.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				Log.d(TAG, "Almacenando el concepto con id "+ id);
				conceptoId = id;
				
				updateGrabarButtonState();
			}
		});

		final PorkyOpenHelper poh = new PorkyOpenHelper(getApplicationContext());
		
		adapter.setFilterQueryProvider(new FilterQueryProvider() 
		{			
			@Override
			public Cursor runQuery(CharSequence str) 
			{
				Log.d(TAG, "Quitando el concepto almacenado");
				conceptoId = -1;
				
				if (str != null)
				{
					return poh.findConceptosByNombre(str.toString());
				}
				else
				{
					return null;
				}
			}
		});
		adapter.setStringConversionColumn(1);
		
		ContentWatcher watcher = new ContentWatcher();
		
		// Watch for changes in the four edit text fields.
		for (int id : new int[]{R.id.editConcepto, R.id.editDetalle, R.id.editFecha, R.id.editImporte})
		{
			((EditText) findViewById(id)).addTextChangedListener(watcher);
		}
		
		Button btnGrabar = (Button) findViewById(R.id.buttonGrabarMovimiento);
		btnGrabar.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				if (grabarMovimiento())
				{
					// After successfully saving, restart activity.
					Intent intent = new Intent(MovimientoActivity.this, MovimientosActivity.class);
					finish();
					startActivity(intent);
				}				
			}
		});
		
		Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
		btnCancelar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MovimientoActivity.this, MovimientosActivity.class);
				startActivity(intent);
			}			
		});
	}

	@SuppressLint("SimpleDateFormat")
	private boolean grabarMovimiento()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try 
		{
			Date fecha = sdf.parse(((EditText) findViewById(R.id.editFecha)).getText().toString());
			String detalle = ((EditText) findViewById(R.id.editDetalle)).getText().toString();
			float importe = Float.parseFloat(((EditText) findViewById(R.id.editImporte)).getText().toString());
			
			PorkyOpenHelper poh = new PorkyOpenHelper(getApplicationContext());
			poh.addMovimiento(fecha, conceptoId, detalle, importe);
			
			return true;
		} 
		catch (ParseException e) 
		{
			Log.d(getLocalClassName(), "Error al parsear la fecha", e);
			
			Toast mensaje = Toast.makeText(MovimientoActivity.this, "La fecha ingresada es incorrecta", Toast.LENGTH_LONG);
			mensaje.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			mensaje.show();
			
			return false;
		}
	}
	
	class ContentWatcher implements TextWatcher
	{

		@Override
		public void afterTextChanged(Editable s) 
		{
			updateGrabarButtonState();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) 
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) 
		{
		}		
	}
	
	private void updateGrabarButtonState()
	{
		boolean enabled = fechaValida() && conceptoValido() && detalleValido() && importeValido();
		
		Log.d(getClass().getName(), "El estado del boton sera: "+ enabled);
		Button btnGrabar = (Button) findViewById(R.id.buttonGrabarMovimiento);
		btnGrabar.setEnabled(enabled);
	}
	
	private boolean importeValido()
	{
		String importe = ((EditText) findViewById(R.id.editImporte)).getText().toString();		
		Log.d(getClass().getName(), "El importe ["+ importe +"] es valido: "+ !importe.isEmpty());
		
		return !importe.isEmpty();
	}
	
	private boolean detalleValido()
	{
		String detalle = ((EditText) findViewById(R.id.editDetalle)).getText().toString();
		
		Log.d(getClass().getName(), "El detalle ["+ detalle +"] es valido: "+ !detalle.isEmpty());
		return !detalle.isEmpty();
	}
	
	private boolean conceptoValido()
	{
		return conceptoId > 0;
	}
	
	@SuppressLint("SimpleDateFormat")
	private boolean fechaValida()
	{
		boolean fechaValida = false;
		
		EditText editFecha = (EditText) findViewById(R.id.editFecha);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaText = editFecha.getText().toString();
				
		Log.d(getClass().getName(), "Parseando la fecha ["+ fechaText +"]");
		
		try 
		{
			sdf.parse(fechaText);
			fechaValida = true;
		} 
		catch (ParseException e) 
		{
		}
		
		return fechaValida;
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EditText editFecha = (EditText) findViewById(R.id.editFecha);
		editFecha.setText(sdf.format(new Date()));
		
		PorkyOpenHelper poh = new PorkyOpenHelper(getApplicationContext());
		
		SharedPreferences pref = getSharedPreferences(ConstantesPorky.PORKY_PREFS, MODE_PRIVATE);
		final long usuarioId = pref.getLong(ConstantesPorky.PREF_ID_USUARIO, -1);
		final String nombreUsuario = pref.getString(ConstantesPorky.PREF_NOMBRE_USUARIO, "-");
		
		if (!poh.hasConceptos(usuarioId))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Usted no posee conceptos. �Desea descargarlos ahora?").setPositiveButton(R.string.si, new DialogInterface.OnClickListener() 
			{						
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					final ProgressDialog pd = new ProgressDialog(MovimientoActivity.this);
							
					DownloadConceptosTask task = new DownloadConceptosTask(MovimientoActivity.this, usuarioId)
					{						
						@Override
						protected void onPreExecute() 
						{
							pd.setTitle("Operaci�n en curso");
							pd.setMessage("Descargando conceptos. Aguarde por favor...");
							pd.setIndeterminate(false);
							pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							pd.setMax(100);
							pd.show();				
						}
					
						@Override
						protected void onProgressUpdate(Integer... values) 
						{
							pd.setProgress(values[0]);
						}

						@Override
						protected void onPostExecute(Void result) 
						{
							pd.dismiss();
							
							Toast mensaje = Toast.makeText(MovimientoActivity.this, "Conceptos descargados con �xito", Toast.LENGTH_SHORT);
							mensaje.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
							mensaje.show();									
						}
					};
					task.execute(nombreUsuario);
				}
			}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

}
