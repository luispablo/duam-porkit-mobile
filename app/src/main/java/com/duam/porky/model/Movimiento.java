package com.duam.porky.model;

import java.io.Serializable;
import java.util.Date;

public class Movimiento implements Serializable
{
	private static final long serialVersionUID = -6524443395123669225L;
	
	private long _id;
	private long conceptoId;
	private Date fecha;
	private String detalle;
	private float importe;
	private long webServiceId;

	public Movimiento()
	{
		
	}
	
	public Movimiento(long _id, long conceptoId, Date fecha, String detalle, float importe, long webServiceId)
	{
		this._id = _id;
		this.conceptoId = conceptoId;
		this.fecha = fecha;
		this.detalle = detalle;
		this.importe = importe;
		this.webServiceId = webServiceId;
	}
	
	public long get_id() 
	{
		return _id;
	}
	
	public void set_id(long _id) 
	{
		this._id = _id;
	}
	
	public long getConceptoId() 
	{
		return conceptoId;
	}
	
	public void setConceptoId(long conceptoId) 
	{
		this.conceptoId = conceptoId;
	}
	
	public Date getFecha() 
	{
		return fecha;
	}
	
	public void setFecha(Date fecha) 
	{
		this.fecha = fecha;
	}
	
	public String getDetalle() 
	{
		return detalle;
	}
	
	public void setDetalle(String detalle) 
	{
		this.detalle = detalle;
	}
	
	public float getImporte() 
	{
		return importe;
	}
	
	public void setImporte(float importe) 
	{
		this.importe = importe;
	}

	public long getWebServiceId() 
	{
		return webServiceId;
	}

	public void setWebServiceId(long webServiceId) 
	{
		this.webServiceId = webServiceId;
	}

}
