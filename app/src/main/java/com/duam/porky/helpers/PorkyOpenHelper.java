package com.duam.porky.helpers;

import static com.duam.porky.ConstantesPorky.WS_FIELD_SEPARATOR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.duam.porky.model.Concepto;
import com.duam.porky.model.Movimiento;

public class PorkyOpenHelper extends SQLiteOpenHelper 
{
	private static final String TAG = PorkyOpenHelper.class.getName();
	private static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_NAME = "duam_porky";
	
	public static final String CONCEPTOS_TABLE = "conceptos";
	public static final String CONCEPTOS_COLUMN_ID = "_id";
	public static final String CONCEPTOS_COLUMN_USUARIO_ID = "usuario_id";
	public static final String CONCEPTOS_COLUMN_NOMBRE = "nombre";
	public static final String CONCEPTOS_COLUMN_FACTOR_ID = "factor_id";

	public static final String MOVIMIENTOS_TABLE = "movimientos";
	public static final String MOVIMIENTOS_COLUMN_ID = "_id";
	public static final String MOVIMIENTOS_COLUMN_CONCEPTO_ID = "concepto_id";
	public static final String MOVIMIENTOS_COLUMN_FECHA = "fecha";
	public static final String MOVIMIENTOS_COLUMN_DETALLE = "detalle";
	public static final String MOVIMIENTOS_COLUMN_IMPORTE = "importe";

	public static final String USUARIOS_TABLE = "usuarios";
	public static final String USUARIOS_COLUMN_ID = "_id";
	public static final String USUARIOS_COLUMN_NOMBRE = "nombre";
	public static final String USUARIOS_COLUMN_APELLIDO = "apellido";
	public static final String USUARIOS_COLUMN_NOMBRE_USUARIO = "nombre_usuario";
	public static final String USUARIOS_COLUMN_EMAIL = "email";
	public static final String USUARIOS_COLUMN_CLAVE = "clave";
	public static final String USUARIOS_COLUMN_COMUNIDAD_ID = "comunidad_id";

	private static final String USUARIOS_TABLE_CREATE = 
			"CREATE TABLE "+ USUARIOS_TABLE +" ("+
					USUARIOS_COLUMN_APELLIDO +", "+
					USUARIOS_COLUMN_CLAVE +", "+
					USUARIOS_COLUMN_COMUNIDAD_ID +", "+
					USUARIOS_COLUMN_EMAIL +", "+
					USUARIOS_COLUMN_ID +", "+
					USUARIOS_COLUMN_NOMBRE +", "+
					USUARIOS_COLUMN_NOMBRE_USUARIO +");";
	
    private static final String CONCEPTOS_TABLE_CREATE = 
    		"CREATE TABLE "+ CONCEPTOS_TABLE +" ("+ 
    				CONCEPTOS_COLUMN_ID +", "+ 
    				CONCEPTOS_COLUMN_USUARIO_ID +", "+ 
    				CONCEPTOS_COLUMN_NOMBRE +", "+ 
    				CONCEPTOS_COLUMN_FACTOR_ID +");";
    
    private static final String MOVIMIENTOS_TABLE_CREATE = 
    		"CREATE TABLE "+ MOVIMIENTOS_TABLE +" ("+
    				MOVIMIENTOS_COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "+
    				MOVIMIENTOS_COLUMN_CONCEPTO_ID +" INT, "+
    				MOVIMIENTOS_COLUMN_FECHA +" DATE, "+
    				MOVIMIENTOS_COLUMN_DETALLE +" VARCHAR(500), "+
    				MOVIMIENTOS_COLUMN_IMPORTE +" FLOAT);";
    
	public PorkyOpenHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(USUARIOS_TABLE_CREATE);
		db.execSQL(CONCEPTOS_TABLE_CREATE);
		db.execSQL(MOVIMIENTOS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
	{
	}
	
	public long insertConcepto(Concepto concepto)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(CONCEPTOS_COLUMN_ID, concepto.get_id());
		values.put(CONCEPTOS_COLUMN_USUARIO_ID, concepto.getUsuarioId());
		values.put(CONCEPTOS_COLUMN_NOMBRE, concepto.getNombre()); 
		values.put(CONCEPTOS_COLUMN_FACTOR_ID, concepto.getFactorId());
		
		long id = db.insert(CONCEPTOS_TABLE, null, values);
		db.close();
		
		concepto.set_id(id);
		
		return id;
	}
	
	private ContentValues valuesFromConceptoLine(String line, int usuarioId)
	{
		String[] valores = line.split(WS_FIELD_SEPARATOR.replaceAll("\\|", "\\\\|"));
		
		ContentValues values = new ContentValues();
		values.put(CONCEPTOS_COLUMN_ID, Integer.valueOf(valores[0]));
		values.put(CONCEPTOS_COLUMN_USUARIO_ID, usuarioId);
		values.put(CONCEPTOS_COLUMN_NOMBRE, valores[1]);
		values.put(CONCEPTOS_COLUMN_FACTOR_ID, 0);
		
		return values;		
	}
	
	public void insertConceptosWithProgress(List<String> lines, int usuarioId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		try
		{
			for (String line : lines)
			{
				db.insert(CONCEPTOS_TABLE, null, valuesFromConceptoLine(line, usuarioId));
				itemProccesed();
			}
			db.setTransactionSuccessful();
		}
		catch (Exception ex)
		{
			Log.e(PorkyOpenHelper.class.getName(), "Error insertando conceptos", ex);
		}
		finally
		{
			db.endTransaction();
			db.close();
		}		
	}
	
	protected void itemProccesed() 
	{
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public void addMovimiento(Date fecha, long conceptoId, String detalle, float importe)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ContentValues values = new ContentValues();
		values.put(MOVIMIENTOS_COLUMN_CONCEPTO_ID, conceptoId);
		values.put(MOVIMIENTOS_COLUMN_DETALLE, detalle);
		values.put(MOVIMIENTOS_COLUMN_FECHA, sdf.format(fecha)); 
		values.put(MOVIMIENTOS_COLUMN_IMPORTE, importe);
		
		db.insert(MOVIMIENTOS_TABLE, null, values);
		db.close();		
	}
	
	public Concepto findConceptoById(long _id)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select "+ CONCEPTOS_COLUMN_ID +", "+ CONCEPTOS_COLUMN_NOMBRE +" from "+ CONCEPTOS_TABLE +" where "+ CONCEPTOS_COLUMN_ID +" = "+ _id, new String[]{});

		if (c.moveToFirst())
		{
			return new Concepto(_id, -1, c.getString(1), -1);
		}
		else
		{
			return null;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public Movimiento findMovimientoById(long _id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(MOVIMIENTOS_TABLE, 
				new String[]{MOVIMIENTOS_COLUMN_ID, MOVIMIENTOS_COLUMN_CONCEPTO_ID, MOVIMIENTOS_COLUMN_FECHA, 
				MOVIMIENTOS_COLUMN_DETALLE, MOVIMIENTOS_COLUMN_IMPORTE}, 
				MOVIMIENTOS_COLUMN_ID +" = ? ", new String[]{String.valueOf(_id)}, null,  null,  null);
		
		if (c.moveToFirst())
		{
			long conceptoId = c.getLong(1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			try 
			{
				Date fecha = sdf.parse(c.getString(2));
				String detalle = c.getString(3);
				float importe = c.getFloat(4);
				
				return new Movimiento(_id, conceptoId, fecha, detalle, importe);
			} 
			catch (ParseException e) 
			{
				Log.e(TAG, "Error al parsear fecha", e);
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	public long[] getMovimientosIds()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor c = db.query(MOVIMIENTOS_TABLE, new String[]{MOVIMIENTOS_COLUMN_ID}, null, null, null, null, MOVIMIENTOS_COLUMN_ID);
		long[] ids = new long[c.getCount()];
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			ids[c.getPosition()] = c.getLong(0);
		}
		
		c.close();
		db.close();
		
		return ids;
	}
	
	public Cursor findConceptosByNombre(String nombre)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] columns = new String[]{CONCEPTOS_COLUMN_ID, CONCEPTOS_COLUMN_NOMBRE};
		String[] params = new String[]{"%"+ nombre +"%"};
		
		return db.query(CONCEPTOS_TABLE, columns, CONCEPTOS_COLUMN_NOMBRE + " like ? ", params, null, null, CONCEPTOS_COLUMN_NOMBRE);
	}
	
	public boolean hasMovimientos()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		SQLiteStatement stmt = db.compileStatement("SELECT COUNT(*) FROM "+ MOVIMIENTOS_TABLE);
		
		boolean hasMovimientos = false;
		
		try
		{
			hasMovimientos = stmt.simpleQueryForLong() > 0;
		}
		catch (SQLiteDoneException ex)
		{
			
		}
		finally
		{
			stmt.close();
			db.close();
		}
		
		return hasMovimientos;
	}
	
	public int deleteAllConceptos()
	{
		SQLiteDatabase db = getWritableDatabase();
		int rows = db.delete(CONCEPTOS_TABLE, "", new String[]{});
		db.close();
		
		return rows;
	}
	
	public boolean hasConceptos(long usuarioId)
	{
		SQLiteDatabase db = this.getReadableDatabase();		
		SQLiteStatement stmt = db.compileStatement("SELECT count(*) FROM "+ CONCEPTOS_TABLE +" WHERE usuario_id = ?");
		stmt.bindLong(1, usuarioId);
		
		boolean hasConceptos = false;
		
		try
		{
			hasConceptos = stmt.simpleQueryForLong() > 0;
		}
		catch (SQLiteDoneException ex)
		{
			
		}
		finally
		{
			stmt.close();
			db.close();
		}

		return hasConceptos;
	}
	
	public boolean deleteMovimiento(long _id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rows = db.delete(MOVIMIENTOS_TABLE, MOVIMIENTOS_COLUMN_ID+" = ? ", new String[]{String.valueOf(_id)});
		db.close();
		
		return rows == 1;
	}

}
