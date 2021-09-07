package com.disoft.distarea.models;

public class ArtEst
{
	int aid, eid;
	float existencias;
	String afid, fid, sfid, activo;
	public ArtEst(){}
	public ArtEst(int aid, String afid, int eid, String fid, String activo) 
	{this.aid=aid; this.afid=afid; this.eid=eid; this.fid=fid; this.activo=activo;}
	//Constructor completo
	public ArtEst(int aid, String afid, int eid, String fid, String sfid, float existencias, String activo){ 
		this.aid=aid; this.afid=afid; this.eid=eid; this.fid=fid;
		this.sfid=sfid; this.existencias=existencias; this.activo=activo;}
	public int getAid(){return aid;}
	public void setAid(int aid){this.aid = aid;}
	public String getAfid(){return afid;}
	public int getEid(){return eid;}
	public void setEid(int eid){this.eid = eid;}
	public void setAfid(String afid){this.afid = afid;}
	public String getFid(){return fid;}
	public void setFid(String fid){this.fid = fid;}
	public String getSfid(){return sfid;}
	public void setSfid(String sfid){this.sfid = sfid;}
	public float getExistencias(){return existencias;}
	public void setExistencias(float existencias){this.existencias = existencias;}
	public String getActivo(){return activo;}
	public void setActivo(String activo){this.activo = activo;}
}
