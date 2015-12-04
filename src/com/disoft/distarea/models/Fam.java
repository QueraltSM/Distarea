package com.disoft.distarea.models;

public class Fam
{
	int eid;
	String nombre, fecha, fid, sfid;
	public Fam(){}
	public Fam(String fid, String nombre, String fecha, int eid) 
		{this.fid=fid; this.nombre=nombre; this.fecha=fecha; this.eid=eid;}
	//Constructor Subfamilia
	public Fam(String sfid, String fid, String nombre, String fecha, int eid)
		{this.sfid=sfid; this.fid=fid; this.nombre=nombre; this.fecha=fecha; this.eid=eid;}
	
	public String getFid(){return fid;}
	public void setFid(String fid){this.fid = fid;}
	public String getNombre(){return nombre;}
	public void setNombre(String nombre){this.nombre = nombre;}
	public String getFecha(){return fecha;}
	public void setFecha(String fecha){this.fecha = fecha;}
	public int getEid(){return eid;}
	public void setEid(int eid){this.eid = eid;}
	public String getSfid(){return sfid;}
	public void setSfid(String sfid){this.sfid = sfid;}
}
