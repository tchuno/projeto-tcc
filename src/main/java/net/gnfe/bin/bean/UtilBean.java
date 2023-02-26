package net.gnfe.bin.bean;

import net.gnfe.bin.GNFEConstants;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
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
import java.util.Set;

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
		return "#"+parametroService.getValorCache(P.COR_FONTE_TITULO_BARRA);
	}

	public String cccCorMenu() {
		return "#"+parametroService.getValorCache(P.COR_MENU);
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

	public BigDecimal totalGeral(Set<OrcamentoProduto> orcamentoProduto) {
		return DummyUtils.totalGeral(orcamentoProduto);
	}

	public String getCpfCnpj(String cpf) {
		return DummyUtils.getCpfCnpj(cpf);
	}

	public String getExtensao(String extensao) {
		return DummyUtils.getExtensao(extensao);
	}

	public String getSemImagem() {
		return GNFEConstants.SEM_IMAGEM_BASE64;
	}
}
