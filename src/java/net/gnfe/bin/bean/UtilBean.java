package net.gnfe.bin.bean;

import net.gnfe.bin.GNFEConstants;
import net.gnfe.bin.domain.service.ParametroService;
import net.gnfe.bin.domain.service.ParametroService.P;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@ManagedBean
@ViewScoped
public class UtilBean extends AbstractBean {

	@Autowired private ParametroService parametroService;

	protected void initBean() { }

	public <T> T getValue(String enumClassName, String value) {
		return DummyUtils.getEnumValue(enumClassName, value);
	}

	public <T> T[] getValues(String enumClassName) {
		return DummyUtils.getEnumValues(enumClassName);
	}

	@SuppressWarnings("rawtypes")
	public String[] getValuesStr(String enumClassName) {
		Object[] enumValues = DummyUtils.getEnumValues(enumClassName);
		String[] arrayStr = new String[enumValues.length];
		for (int i = 0; i < enumValues.length; i++) {
			arrayStr[i] = ((Enum) enumValues[i]).name();
		}
		return arrayStr;
	}

	public Map<String, String> getCustomizacao() {
		return parametroService.getCustomizacao();
	}

	public String cccCorFontTituloBarra() {
		return "#"+parametroService.getValor(P.COR_FONTE_TITULO_BARRA);
	}

	public String cccCorMenu() {
		return "#"+parametroService.getValor(P.COR_MENU);
	}

	public String getDateStr(Date data) {

		if(data == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", GNFEConstants.LOCALE_PT_BR);

		String format = sdf.format(data);
		return format;
	}

	public String getDateTimeStr(Date data) {

		if(data == null) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", GNFEConstants.LOCALE_PT_BR);
		String format = sdf.format(data);
		return format;
	}

	@Override
	public void logoff() {
		super.logoff();
	}

	public String getDataHoraAtual() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", GNFEConstants.LOCALE_PT_BR);
		return sdf.format(new Date());	
	}

	public String getDateTimeStr2(Date data) {
		return DummyUtils.formatDateTime2(data);
	}

	public String getThreadName() {
		return Thread.currentThread().getName();
	}

	public String formatNumber(BigDecimal bd) {

		if(bd == null) {
			return null;
		}

		Locale ptBr = new Locale("pt", "BR");
		String format = NumberFormat.getCurrencyInstance(ptBr).format(bd);
		return format;
	}

	public String diasAtraso (Date data) {
		if(data == null){
			return null;
		}

		Date dataAtual = new Date();
		int numeroDias = (int) ((dataAtual.getTime() - data.getTime()) / (1000 * 60 * 60 * 24));
		int i = numeroDias - 30;
		return numeroDias + " - 30 = " + i ;
	}

	public String jurosAplicado (BigDecimal bd, Date data) {
		if(bd == null || data == null) {
			return null;
		}

		Date dataAtual = new Date();
		int numeroDias = (int) ((dataAtual.getTime() - data.getTime()) / (1000 * 60 * 60 * 24));
		int i = numeroDias / 30;
		Integer porcentagemJuros = 0;
		BigDecimal juros = new BigDecimal(0);
		if(i >= 1) {
			i--;
			porcentagemJuros = 20;
			porcentagemJuros = porcentagemJuros + i;
			String s = porcentagemJuros.toString();
			juros = new BigDecimal(s);
			juros = juros.setScale(0, BigDecimal.ROUND_HALF_EVEN);
		}

		return juros.toString() + "%";
	}

	public String formatNumberJuros(BigDecimal bd, Date data) {

		if(bd == null || data == null) {
			return null;
		}

		Date dataAtual = new Date();
		int numeroDias = (int) ((dataAtual.getTime() - data.getTime()) / (1000 * 60 * 60 * 24));
		int i = numeroDias / 30;
		Integer porcentagemJuros = 0;
		if(i >= 1) {
			i--;
			porcentagemJuros = 20;
			porcentagemJuros = porcentagemJuros + i;
			String s = porcentagemJuros.toString();
			s = "0." + s;
			BigDecimal juros = new BigDecimal(s);
			juros = bd.multiply(juros);
			bd = bd.add(juros).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}

		Locale ptBr = new Locale("pt", "BR");
		String format = NumberFormat.getCurrencyInstance(ptBr).format(bd);
		return format;
	}

}
