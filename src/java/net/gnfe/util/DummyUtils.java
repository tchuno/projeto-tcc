package net.gnfe.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import net.gnfe.bin.GNFEConstants;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.util.ddd.Entity;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.rest.jackson.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;

import java.io.*;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.gnfe.bin.GNFEConstants.LOCALE_PT_BR;

public abstract class DummyUtils {

	public static final String SYSPREFIX = "[gnfe] ";
	private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
	private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

	private static final NumberFormat MINUTO_NF = NumberFormat.getNumberInstance(LOCALE_PT_BR);
	private static final NumberFormat SEGUNDO_NF = NumberFormat.getNumberInstance(LOCALE_PT_BR);
	private static final NumberFormat KILOBYTE_NF = NumberFormat.getNumberInstance(LOCALE_PT_BR);
	private static final NumberFormat MEGABYTES_NF = NumberFormat.getNumberInstance(LOCALE_PT_BR);
	private static final NumberFormat INTEGER_NF = NumberFormat.getNumberInstance(LOCALE_PT_BR);
	private static final NumberFormat NF = NumberFormat.getInstance();
	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
	private static final String DATE_TIME_FORMAT_2 = "dd/MM/yyyy HH:mm:ss";
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	static {

		MINUTO_NF.setMinimumFractionDigits(2);
		MINUTO_NF.setMaximumFractionDigits(2);

		SEGUNDO_NF.setMinimumIntegerDigits(2);
		SEGUNDO_NF.setMaximumFractionDigits(0);

		KILOBYTE_NF.setMaximumFractionDigits(0);

		MEGABYTES_NF.setMaximumFractionDigits(2);
	}

	public static String getStackTrace(Exception e) {
		return getStackTrace(e, 10000);
	}

	public static String getStackTrace(Exception e, int limit) {
		String stackTrace = ExceptionUtils.getStackTrace(e);
		if (stackTrace.length() > limit) {
			stackTrace = stackTrace.substring(0, limit);
		}
		return stackTrace;
	}

	public static String getExtensao(String fileName) {

		fileName = StringUtils.trim(fileName);
		if(StringUtils.isBlank(fileName)) {
			return null;
		}

		int lastIndexOf = fileName.lastIndexOf('.');
		String extensao = fileName.substring(lastIndexOf + 1, fileName.length());
		extensao = StringUtils.lowerCase(extensao);
		return extensao;
	}

	public static String capitalize(String str) {

		if(StringUtils.isBlank(str)) {
			return str;
		}

		str = WordUtils.capitalize(str.toLowerCase());
		str = str.replace("A ", "a ");
		str = str.replace("E ", "e ");
		str = str.replace("Cpf ", "CPF ");
		str = str.replace("Cnpj ", "CNPJ ");
		str = str.replace("Da ", "da ");
		str = str.replace("De ", "de ");
		str = str.replace("Do ", "do ");
		str = str.replace("Uf ", "UF ");

		return str;
	}

	public static String getCpf(Object obj) {

		String cpf = getCpfCnpjDesformatado(obj);
		if(StringUtils.isBlank(cpf)) {
			return null;
		}
		String cpf2 = StringUtils.leftPad(cpf, 11, "0");

		String cpf3 = "";
		cpf3 += cpf2.substring(0, 3) + ".";
		cpf3 += cpf2.substring(3, 6) + ".";
		cpf3 += cpf2.substring(6, 9) + "-";
		cpf3 += cpf2.substring(9, 11);

		return cpf3;
	}

	public static String getCpfCnpj(Object obj) {

		String str = getCpfCnpjDesformatado(obj);
		if(StringUtils.isBlank(str)) {
			return null;
		}

		if(str.length() > 11) {
			return getCnpj(obj);
		} else {
			return getCpf(obj);
		}
	}

	private static String getCnpj(Object obj) {

		String cnpj = getCpfCnpjDesformatado(obj);
		if(StringUtils.isBlank(cnpj)) {
			return null;
		}
		String cnpj2 = StringUtils.leftPad(cnpj, 14, "0");

		String cnpj3 = "";
		cnpj3 += cnpj2.substring(0, 2) + ".";
		cnpj3 += cnpj2.substring(2, 5) + ".";
		cnpj3 += cnpj2.substring(5, 8) + "/";
		cnpj3 += cnpj2.substring(8, 12) + "-";
		cnpj3 += cnpj2.substring(12, 14);

		return cnpj3;
	}

	public static String getCpfCnpjDesformatado(Object obj) {

		if(obj == null) {
			return null;
		}

		String cpfCnpj = obj.toString();
		if (StringUtils.isBlank(cpfCnpj)) {
			return null;
		}

		String cpfCnpj2 = cpfCnpj;
		cpfCnpj2 = cpfCnpj2.replace(".", "");
		cpfCnpj2 = cpfCnpj2.replace("-", "");
		cpfCnpj2 = cpfCnpj2.replace("/", "");

		return cpfCnpj2;
	}

	public static void sysout(Object msg) {
		System.out.println(SYSPREFIX + msg);
	}

	public static void systrace(Object msg) {
		System.out.println(SYSPREFIX + getCurrentMethodName(3) + " > " + msg);
	}

	public static void syserr(Object msg) {
		System.err.println(SYSPREFIX + msg);
	}

	public static boolean isCpfCnpjValido(String cpfCnpj) {

		String str = getCpfCnpjDesformatado(cpfCnpj);

		if(str.length() > 11) {

			//cnpj
			return isCnpjValido(str);
		}
		else {

			//cpf
			return isCpfValido(str);
		}
	}

	private static int calcularDigito(String str, int[] peso) {

		int soma = 0;
		for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
			digito = Integer.parseInt(str.substring(indice,indice+1));
			soma += digito*peso[peso.length-str.length()+indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

	public static boolean isCpfValido(String cpf) {

		if ((cpf==null) || (cpf.length()!=11)
		|| cpf.equals("11111111111")
				|| cpf.equals("22222222222")
				|| cpf.equals("33333333333")
				|| cpf.equals("44444444444")
				|| cpf.equals("55555555555")
				|| cpf.equals("66666666677")
				|| cpf.equals("77777777777")
				|| cpf.equals("88888888888")
				|| cpf.equals("99999999999")) {
			return false;
		}

		Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
		Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
		return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
	}

	public static boolean isCnpjValido(String cnpj) {

		if ((cnpj==null)||(cnpj.length()!=14)) {
			return false;
		}

		Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
		Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
		return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
	}

	public static String getClassName(Entity obj) {

		Class<?> clazz = obj.getClass();
		String className = clazz.getName();
		className = getClassName(className);
		return className;
	}

	public static String getClassName(String className) {

		if(className.contains("_$$_")) {
			int indexOf = className.indexOf("_$$_");
			className = className.substring(0, indexOf);
		}
		return className;
	}

	public static String formatCurrency(BigDecimal bd) {

		if (bd == null) {
			return null;
		}

		String format = NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(bd.doubleValue());
		format = format.substring(3);
		return format;
	}

	public static String toMegabytes(long size) {

		double mb = size / 1024d / 1024d;
		String mbStr = NF.format(mb);
		return mbStr;
	}

	public static String toKilobyte(long size) {

		double mb = size / 1024d;
		String mbStr = INTEGER_NF.format(mb);
		return mbStr;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getEnumValue(String enumClassName, String value) {

		Object[] values = getEnumValues(enumClassName);
		for (Object object : values) {

			if(String.valueOf(object).equals(value)) {
				return (T) object;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] getEnumValues(String enumClassName) {

		try {

			enumClassName = "net.gnfe.bin.domain.enumeration." + enumClassName;

			Class<?> clazz = Class.forName(enumClassName);

			if (!clazz.isEnum()) {
				throw new RuntimeException(enumClassName + " não é uma enum");
			}

			return (T[]) clazz.getEnumConstants();
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getServerName() {

		String server = System.getProperty("gnfe.servername");
		if(StringUtils.isNotBlank(server)) {
			return server;
		}

		try {
			File file = new File("/etc/hostname");
			if(file.exists()) {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				server = br.readLine();
				br.close();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		if(server == null) {
			server = "localhost";
		}

		return server;
	}

	public static String formatDateTime2(Date data) {
		return format(data, new SimpleDateFormat(DATE_TIME_FORMAT_2));
	}

	public static String formatDateTime(Date data) {
		return format(data, new SimpleDateFormat(DATE_TIME_FORMAT));
	}

	public static String formatDate(Date data) {
		return format(data, new SimpleDateFormat(DATE_FORMAT));
	}

	private static String format(Date data, SimpleDateFormat dateTimeFormat) {
		if(data == null) {
			return null;
		}
		return dateTimeFormat.format(data);
	}

	public static Date parseDateTime(String valor) {
		return parse(valor, new SimpleDateFormat(DATE_TIME_FORMAT));
	}

	public static Date parseDateTime2(String valor) {
		return parse(valor, new SimpleDateFormat(DATE_TIME_FORMAT_2));
	}

	private static Date parse(String valor, SimpleDateFormat dateTimeFormat3) {
		if(StringUtils.isBlank(valor)) {
			return null;
		}
		try {
			return dateTimeFormat3.parse(valor);
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toFileSize(long size) {
		double d = size / 1024d / 1024d;
		if((int) d > 0) {
			return toMegabytes(size) + " MB";
		}
		d = size / 1024d;
		if((int) d > 0) {
			return toKilobyte(size) + " KB";
		}
		return size + " B";
	}

	public static <I, O> O convertTypes(I valor, Class<O> resultType) {

		if(valor == null) {
			return null;
		}

		try {
			Constructor<O> constructor = resultType.getConstructor(String.class);
			String valorStr = valor.toString();
			if(StringUtils.isBlank(valorStr)) {
				return null;
			}
			O result = constructor.newInstance(valorStr);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static String getCurrentMethodName(int idx) {

		Thread ct = Thread.currentThread();
		StackTraceElement[] st = ct.getStackTrace();
		StackTraceElement ste = st[idx];
		String className = ste.getClassName();
		int lastIndexOf = className.lastIndexOf(".");
		int length = className.length();
		String simpleClassName = className.substring(lastIndexOf + 1,  length); 
		String mn = ste.getMethodName();

		return simpleClassName + "." + mn + "()";
	}

	public static String getExceptionMessage(Exception e) {

		String message = e.getMessage();
		String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);

		if(rootCauseMessage != null && !rootCauseMessage.equals(message)) {
			return message + " Caused by: " + rootCauseMessage;
		}

		return message;
	}

	public static String getExceptionMessage(Throwable e) {
		String message = e.getMessage();
		String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
		if(rootCauseMessage != null) {
			if(message == null || rootCauseMessage.endsWith(message)) {
				if(e instanceof MessageKeyException) {
					Object[] args = ((MessageKeyException) e).getArgs();
					rootCauseMessage += " " + (args != null ? Arrays.asList((Object[]) args) : "");
				}
				return rootCauseMessage;
			}
			else if(!rootCauseMessage.equals(message)) {
				return message + " Caused by: " + rootCauseMessage;
			}
		}
		return message;
	}

	public static void sleep(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static <T> T jsonToObject(String jsonAsString, Class<T> clazz) {
		if (StringUtils.isNotBlank(jsonAsString)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			T obj = mapper.readValue(jsonAsString, clazz);
			return obj;
		}
		return null;
	}

	public static File getFileDestino(File dirDestino, String fileName) {

		String extensao = DummyUtils.getExtensao(fileName);
		String nomeSemExtensao = fileName.substring(0, fileName.lastIndexOf("."));

		File file;
		int count = 0;
		do {

			String nome = count == 0 ? nomeSemExtensao : nomeSemExtensao + "(" + count + ")";

			String nomeCompleto = nome + "." + extensao;

			file = new File(dirDestino, nomeCompleto);

			count++;
		}
		while(file.exists());

		return file;
	}

	public static String substituirCaracteresEspeciais(String str) {

		if(StringUtils.isBlank(str)) {
			return str;
		}

		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String removerTracosPontosEspacoParentesesAspas(String str) {

		if(StringUtils.isBlank(str)) {
			return str;
		}

		str = str.replace("-", "");
		str = str.replace(".", "");
		str = str.replace(" ", "");
		str = str.replace("(", "");
		str = str.replace(")", "");

		return str;
	}

	public static String htmlToString(String html) {

		html = Jsoup.parse(html).wholeText();
		html = StringUtils.trim(html);
		html = html.replaceAll("\r", "");
		html = html.replaceAll("\n", "`%`");
		html = html.replaceAll("^[ \t]*`%`[ \t]*`%`", "");
		html = html.replaceAll("`%`[ \t]*`%`$", "");
		html = StringUtils.trim(html);
		html = html.replaceAll("`%`", "\n");
		html = html.replaceAll("\n[ \t]*", "\n");
		return html;
	}

	public static String gerarSenha() {
		int qtdeMaximaCaracteres = 8;
		String[] caracteres = {"0", "1", "b", "2", "4", "5", "6", "7", "8",
				"9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
				"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
				"x", "y", "z"};

		StringBuilder senha = new StringBuilder();

		for (int i = 0; i < qtdeMaximaCaracteres; i++) {
			int posicao = (int) (Math.random() * caracteres.length);
			senha.append(caracteres[posicao]);
		}
		return senha.toString();
	}

	public static File getFileFromResource(String path) {

		int lastIndexOfBarra = path.lastIndexOf("/");
		int lastIndexOfPonto = path.lastIndexOf(".");
		String nome = path.substring(lastIndexOfBarra + 1, lastIndexOfPonto);
		String extensao = path.substring(lastIndexOfPonto + 1, path.length());

		try {
			ClassLoader classLoader = DummyUtils.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(path);

			if (is == null) {
				throw new FileNotFoundException(path);
			}

			File tempFile = File.createTempFile(nome, "." + extensao);
			//tempFile.deleteOnExit();
			FileUtils.copyInputStreamToFile(is, tempFile);

			return tempFile;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static BigDecimal totalGeral(Set<OrcamentoProduto> orcamentoProdutos) {
		BigDecimal totalGeral = new BigDecimal(0);
		for(OrcamentoProduto orcamentoProduto : orcamentoProdutos) {
			Produto produto = orcamentoProduto.getProduto();
			if(produto == null) {
				continue;
			}
			BigDecimal valorUnidade = produto.getValorUnidade();
			Integer quantidade = orcamentoProduto.getQuantidade();
			valorUnidade = valorUnidade.multiply(new BigDecimal(quantidade));
			totalGeral = totalGeral.add(valorUnidade);
		}
		return totalGeral;
	}

	public static String gerarDigitosAleatorios(int digitos) {
		StringBuilder text = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < digitos; i++) {
			text.append(random.nextInt(10)); // gerar um número aleatório entre 0 e 9
		}
		return text.toString();
	}

	public static String formatarNumero(BigDecimal num, String pattern){

		if(num == null) {
			return null;
		}

		DecimalFormat formater = new DecimalFormat(pattern);
		String format = formater.format(num);
		format = format.replace(".", "");
		format = format.replace(",", ".");
		return format;
	}

	public static String getLogMemoria() {

		Runtime runtime = Runtime.getRuntime();
		long freeMemory = runtime.freeMemory();
		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();

		return "usada: " + ((totalMemory - freeMemory) / 1024 / 1024) + "MB maxMemory: " + (maxMemory / 1024 / 1024) + "MB freeMemory: " + (freeMemory / 1024 / 1024) + "MB totalMemory: " + (totalMemory / 1024 / 1024) + "MB";
	}

	public static String convertStringToBase64(String input) {

		if(StringUtils.isBlank(input)) {
			return null;
		}
		byte[] bytes = input.getBytes();

		Base64.Encoder encoder = Base64.getEncoder();
		String fileBase64 = encoder.encodeToString(bytes);

		return fileBase64;
	}
}