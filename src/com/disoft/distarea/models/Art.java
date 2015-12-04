package com.disoft.distarea.models;

public class Art
{
	int aid;
	float pvp1, pvp2, pvp3, pvp4, pvp5, pvp6;
	String articulo, cbarras, tipo;
	public Art(){}
	public Art(int aid, String articulo, String cbarras, String tipo) 
		{this.aid=aid; this.articulo=articulo; this.cbarras=cbarras; this.tipo=tipo;}
	//Constructroll
	public Art(int aid, String articulo, String cbarras, String tipo, 
			float pvp1, float pvp2, float pvp3, float pvp4, float pvp5, float pvp6) 
	{this.aid=aid; this.articulo=articulo; this.cbarras=cbarras; this.tipo=tipo;
	this.pvp1=pvp1; this.pvp2=pvp2; this.pvp3=pvp3; this.pvp4=pvp4; this.pvp5=pvp5; this.pvp6=pvp6;}
	//
	public int getAid(){return aid;}
	public void setAid(int aid){this.aid = aid;}
	public String getArticulo(){return articulo;}
	public void setArticulo(String articulo){this.articulo = articulo;}
	public String getCbarras(){return cbarras;}
	public void setCbarras(String cbarras){this.cbarras = cbarras;}
	public String getTipo(){return tipo;}
	public void setTipo(String tipo){this.tipo = tipo;}
	public float getPvp1(){return pvp1;}
	public void setPvp1(float pvp){this.pvp1 = pvp;}
	public float getPvp2(){return pvp2;}
	public void setPvp2(float pvp){this.pvp2 = pvp;}
	public float getPvp3(){return pvp3;}
	public void setPvp3(float pvp){this.pvp3 = pvp;}
	public float getPvp4(){return pvp4;}
	public void setPvp4(float pvp){this.pvp4 = pvp;}
	public float getPvp5(){return pvp5;}
	public void setPvp5(float pvp){this.pvp5 = pvp;}
	public float getPvp6(){return pvp6;}
	public void setPvp6(float pvp){this.pvp6 = pvp;}
}
