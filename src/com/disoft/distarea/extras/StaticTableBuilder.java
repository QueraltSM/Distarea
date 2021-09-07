package com.disoft.distarea.extras;

public class StaticTableBuilder implements TableBuilder {

	@Override
	public Table build() {
		Table table = new Table();
		table.addKey("EMPRESA");
		table.addKey("RECARGO");
		table.addKey("FACTURA");
		table.addKey("TOTAL");
		table.addKey("NIF");
		table.addKey("FECHA");
		table.addKey("IVA 0");
		table.addKey("IVA 4");
		table.addKey("IVA 10");
		table.addKey("IVA 21");
		table.addKey("IVA 99");
		table.addKey("IGIC 0");
		table.addKey("IGIC 3");
		table.addKey("IGIC 7");
		table.addKey("IGIC 9");
		table.addKey("IGIC 13 CON 5");
		table.addKey("IGIC 20");
		table.addKey("IGIC 99");
		table.addKey("RETENCION");
		table.addKey("IMPLICITO");
		table.addKey("AVISO");
		table.addKey("IMPORTE PAGO");
		table.addKey("IMPORTE CONTRAPARTIDA");
		table.addKey("IMPORTE COBRO");
		table.addKey("PAGADO");
		table.addKey("COBRADO");
		table.addKey("CUENTA PAGO");
		table.addKey("CUENTA COBRO");
		table.addKey("EFECTIVA");
		table.addKey("CUENTA CONTRAPARTIDA");
		table.addKey("MODO");
		table.addKey("DESCRIPCION");
		
		table.addValueToKey("AVISO","AVISO");
		
		table.addValueToKey("COBRADO","COBRADO");
		
		table.addValueToKey("CUENTA PAGO","CUENTA PAGO");
		
		table.addValueToKey("CUENTA COBRO","CUENTA COBRO");
		
		table.addValueToKey("CUENTA CONTRAPARTIDA","CUENTA CONTRAPARTIDA");
		
		table.addValueToKey("EMPRESA","EMPRESA");
		table.addValueToKey("EMPRESA","EMPLESA");
		table.addValueToKey("EMPRESA","EMPRESAS");
		table.addValueToKey("EMPRESA","ESPECIA");
		table.addValueToKey("EMPRESA","IMPRESA");
		table.addValueToKey("EMPRESA","EMPIEZA");
		table.addValueToKey("EMPRESA","PRESA");
		
		table.addValueToKey("PAGADO","PAGADO");		
		
		table.addValueToKey("RECARGO","RECARGO");
		table.addValueToKey("RECARGO","RICARDO");
		table.addValueToKey("RECARGO","RETARDO");
		table.addValueToKey("RECARGO","CARGO");
		
		table.addValueToKey("RETENCION","RETENCION");
		table.addValueToKey("RETENCION","ATENCION");
		table.addValueToKey("RETENCION","RETENTION");
		table.addValueToKey("RETENCION","RETENCIÓN");
		table.addValueToKey("RETENCION","REDENCIÓN");
		
		table.addValueToKey("FACTURA","FACTURA");
		table.addValueToKey("FACTURA","FRACTURA");
		table.addValueToKey("FACTURA","FACTURAS");
		table.addValueToKey("FACTURA","RUPTURA");
		
		table.addValueToKey("TOTAL","TOTAL");
		table.addValueToKey("TOTAL","TOTA");
		table.addValueToKey("TOTAL","TAL");
		table.addValueToKey("TOTAL","HOSTAL");
		table.addValueToKey("TOTAL","NOTA AL");
		table.addValueToKey("TOTAL","PRECIO");
		table.addValueToKey("TOTAL","PRESIO");
		table.addValueToKey("TOTAL","CANTIDAD");		
		
		table.addValueToKey("NIF","NIF");
		table.addValueToKey("NIF","NIS");
		table.addValueToKey("NIF","ANIS");				
		
		table.addValueToKey("FECHA","FECHA");	
		table.addValueToKey("FECHA","FELLA");
		table.addValueToKey("FECHA","FEYA");
		table.addValueToKey("FECHA","FECHAS");
		table.addValueToKey("FECHA","FEIA");	
		
		table.addValueToKey("IVA 0","IVA CERO");
		table.addValueToKey("IVA 0","IVA 0");
		table.addValueToKey("IVA 0","IVA A SER");
		table.addValueToKey("IVA 0","IVA ACERO");
		table.addValueToKey("IVA 0","IVA A SER O");
		table.addValueToKey("IVA 0","IVA AL CERO");
		table.addValueToKey("IVA 0","IVA AL SERO");
		table.addValueToKey("IVA 0","IVA AL ZERO");
		table.addValueToKey("IVA 0","IVA AL 0");
		table.addValueToKey("IVA 0","IBA A SER");
		table.addValueToKey("IVA 0","IBA ACERO");
		table.addValueToKey("IVA 0","IBA A SER O");
		table.addValueToKey("IVA 0","IBA AL CERO");
		table.addValueToKey("IVA 0","IBA AL SERO");
		table.addValueToKey("IVA 0","IBA AL ZERO");
		table.addValueToKey("IVA 0","IBA AL 0");		
		table.addValueToKey("IVA 0","IVA ZERO");
		table.addValueToKey("IVA 0","IVA SERO");
		table.addValueToKey("IVA 0","IBA CERO");
		table.addValueToKey("IVA 0","IBA SERO");
		table.addValueToKey("IVA 0","IBA ZERO");
		table.addValueToKey("IVA 0","IBA 0");
		
		
		table.addValueToKey("IVA 4","IVA CUATRO");
		table.addValueToKey("IVA 4","IVA 4");
		table.addValueToKey("IVA 4","IBA CUATRO");
		table.addValueToKey("IVA 4","IBA 4");
		
		table.addValueToKey("IVA 10","IVA DIEZ");
		table.addValueToKey("IVA 10","IVA 10");
		table.addValueToKey("IVA 10","IBA DIEZ");
		table.addValueToKey("IVA 10","IBA 10");
		
		table.addValueToKey("IVA 21","IVA 21");
		table.addValueToKey("IVA 21","IVA VEINTIUNO");
		table.addValueToKey("IVA 21","IBA 21");
		table.addValueToKey("IVA 21","IBA VEINTIUNO");
		
		table.addValueToKey("IVA 99","IVA 99");
		table.addValueToKey("IVA 99","IBA 99");
		table.addValueToKey("IVA 99","IVA NOVENTA Y NUEVE");
		table.addValueToKey("IVA 99","IBA NOVENTA Y NUEVE");
		
		table.addValueToKey("IGIC 0","IGIC CERO");
		table.addValueToKey("IGIC 0","IGIC 0");
		table.addValueToKey("IGIC 0","IGIC A SER");
		table.addValueToKey("IGIC 0","IGIC ACERO");
		table.addValueToKey("IGIC 0","IGIC A SER O");
		table.addValueToKey("IGIC 0","IGIC AL CERO");
		table.addValueToKey("IGIC 0","IGIC AL SERO");
		table.addValueToKey("IGIC 0","IGIC AL ZERO");
		table.addValueToKey("IGIC 0","IGIC AL 0");	
		table.addValueToKey("IGIC 0","IGIC ZERO");
		table.addValueToKey("IGIC 0","IGIC SERO");
		table.addValueToKey("IGIC 0","ICIQ CERO");
		table.addValueToKey("IGIC 0","ICIQ 0");
		table.addValueToKey("IGIC 0","ICIQ A SER");
		table.addValueToKey("IGIC 0","ICIQ ACERO");
		table.addValueToKey("IGIC 0","ICIQ A SER O");
		table.addValueToKey("IGIC 0","ICIQ AL CERO");
		table.addValueToKey("IGIC 0","ICIQ AL SERO");
		table.addValueToKey("IGIC 0","ICIQ AL ZERO");
		table.addValueToKey("IGIC 0","ICIQ AL 0");	
		table.addValueToKey("IGIC 0","ICIQ ZERO");
		table.addValueToKey("IGIC 0","ICIQ SERO");
		table.addValueToKey("IGIC 0","IGIC CERO");
		table.addValueToKey("IGIC 0","BASE 0");
		table.addValueToKey("IGIC 0","BASE A SER");
		table.addValueToKey("IGIC 0","BASE ACERO");
		table.addValueToKey("IGIC 0","BASE A SER O");
		table.addValueToKey("IGIC 0","BASE AL CERO");
		table.addValueToKey("IGIC 0","BASE AL SERO");
		table.addValueToKey("IGIC 0","BASE AL ZERO");
		table.addValueToKey("IGIC 0","BASE AL 0");	
		table.addValueToKey("IGIC 0","BASE ZERO");
		table.addValueToKey("IGIC 0","BASE SERO");
		

		table.addValueToKey("IGIC 3","IGIC TRES");
		table.addValueToKey("IGIC 3","IGIC 3");
		table.addValueToKey("IGIC 3","ICIQ TRES");
		table.addValueToKey("IGIC 3","ICIQ 3");
		table.addValueToKey("IGIC 3","BASE TRES");
		table.addValueToKey("IGIC 3","BASE 3");

		table.addValueToKey("IGIC 7","IGIC SIETE");
		table.addValueToKey("IGIC 7","IGIC 7");
		table.addValueToKey("IGIC 7","ICIQ SIETE");
		table.addValueToKey("IGIC 7","ICIQ 7");
		table.addValueToKey("IGIC 7","BASE SIETE");
		table.addValueToKey("IGIC 7","BASE 7");

		table.addValueToKey("IGIC 9","IGIC 9");
		table.addValueToKey("IGIC 9","ICIQ 9");
		table.addValueToKey("IGIC 9","BASE 9");
		table.addValueToKey("IGIC 9","IGIC NUEVE");
		table.addValueToKey("IGIC 9","ICIQ NUEVE");
		table.addValueToKey("IGIC 9","BASE NUEVE");
		
		table.addValueToKey("IGIC 13 CON 5","IGIC 13");
		table.addValueToKey("IGIC 13 CON 5","IGIC TRECE");
		table.addValueToKey("IGIC 13 CON 5","ICIQ 13");
		table.addValueToKey("IGIC 13 CON 5","ICIQ TRECE");
		table.addValueToKey("IGIC 13 CON 5","BASE 13");
		table.addValueToKey("IGIC 13 CON 5","BASE TRECE");
		
		table.addValueToKey("IGIC 99","IGIC 99");
		table.addValueToKey("IGIC 99","IGIC NOVENTA Y NUEVE");
		table.addValueToKey("IGIC 99","ICIQ 99");
		table.addValueToKey("IGIC 99","ICIQ NOVENTA Y NUEVE");
		table.addValueToKey("IGIC 99","BASE 99");
		table.addValueToKey("IGIC 99","BASE NOVENTA Y NUEVE");
		
		table.addValueToKey("IMPORTE COBRO","IMPORTE COBRO");
		table.addValueToKey("IMPORTE PAGO","IMPORTE PAGO");
		table.addValueToKey("IMPORTE CONTRAPARTIDA","IMPORTE CONTRAPARTIDA");
		
		table.addValueToKey("IMPLICITO","IMPLICITO");
		table.addValueToKey("IMPLICITO","IMPLÍCITO");
		
		table.addValueToKey("EFECTIVA","EFECTIVA");
		
		table.addValueToKey("MODO","MODO");
		table.addValueToKey("DESCRIPCION","DESCRIPCION");
		table.addValueToKey("DESCRIPCION","DESCRIPCIÓN");
		
		return table;
	}

}
