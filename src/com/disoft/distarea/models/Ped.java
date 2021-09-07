package com.disoft.distarea.models;

import com.disoft.distarea.extras.Common;

public class Ped
{
	int autoid, pid, aid, eid, estado, idclif; double cantidad, precio; String fecha, oferta, obs, afid, preciomanual; public Ped(){}
	public Ped(int autoid, int pid, int aid, int eid, String fecha, double cantidad, 
			double precio, String obs, int estado, String afid, String oferta, int idclif,
			String preciomanual) 
		{this.autoid=autoid; this.pid=pid; this.aid=aid; this.eid=eid; this.fecha=fecha; 
			this.cantidad=cantidad; this.precio=precio; this.obs=obs; this.estado=estado; 
			this.afid=afid; this.oferta=oferta; this.idclif=idclif; this.preciomanual=preciomanual;}
	public int getAutoid(){return autoid;}
	public void setAutoid(int autoid){this.autoid = autoid;}
	public int getPid(){return pid;}
	public void setPid(int pid){this.pid = pid;}
	public int getAid(){return aid;}
	public void setAid(int aid){this.aid = aid;}
	public int getEid(){return eid;}
	public void setEid(int eid){this.eid = eid;}
	public String getFecha(){return fecha;}
	public void setFecha(String fecha){this.fecha = fecha;}
	public double getCantidad(){return cantidad;}
	public void setCantidad(double cantidad){this.cantidad = Common.round(cantidad,2);}
	public double getPrecio(){return precio;}
	public void setPrecio(double precio){this.precio = Common.round(precio,2);}
	public String getObservacion(){return obs;}
	public void setObservacion(String obs){this.obs = obs;}
	//Estado 0 = Pendiente; 1 = Cerrado; 2 = Enviado; 3 = Procesado (Sin marca)
	//Estado -1 = Pendiente; -2 = Cerrado; -3 = Enviado; -4 = Procesado (Marcados)
	public int getEstado(){return estado;}
	public void setEstado(int estado){this.estado = estado;}
	public String getAfid(){return afid;}
	public void setAfid(String afid){this.afid = afid;}
	public String getOferta(){return oferta;}
	public void setOferta(String oferta){this.oferta = oferta;}
	public int getIdclif(){return idclif;}
	public void setIdclif(int idclif){this.idclif = idclif;}
	public String getPreciomanual(){return preciomanual;}
	public void setPreciomanual(String preciomanual){this.preciomanual = preciomanual;}
}
