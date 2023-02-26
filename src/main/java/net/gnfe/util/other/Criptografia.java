package net.gnfe.util.other;

import net.gnfe.util.DummyUtils;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import static net.gnfe.util.DummyUtils.systrace;

public class Criptografia {

	public static String FINAL = "%#$";
	private static final Encoder urlBase64Encoder = Base64.getUrlEncoder();
	private static final Decoder urlBase64Decoder = Base64.getUrlDecoder();

	public static final Chave GNFE = new Chave("Ls5dwRBNfeLTHdHE", "L6XF8Hk5HeeFnhzW");

	public static class Chave {

		private Cipher encripta;
		private Cipher decripta;

		public Chave(String key, String iv) {
			try {
				SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

				encripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
				encripta.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes("UTF-8")));

				decripta = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
				decripta.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes("UTF-8")));
			}
			catch (Exception e) {
				e.printStackTrace();
				new RuntimeException(e);
			}
		}
	}

	public static String encrypt(Chave chave, String textopuro) {
		try {
			byte[] doFinal = chave.encripta.doFinal(textopuro.getBytes("UTF-8"));
			String encripted = encode(doFinal);
			return encripted + FINAL;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(Chave chave, String textoencriptado) {

		if(StringUtils.isBlank(textoencriptado)) {
			return textoencriptado;
		}

		try {
			String aux = textoencriptado.replaceAll("%#\\$$", "");
			byte[] decodeBase64 = decode(aux);
			byte[] doFinal = chave.decripta.doFinal(decodeBase64);
			String decripted = new String(doFinal, "UTF-8");
			return decripted;
		}
		catch (javax.crypto.IllegalBlockSizeException e) {
			systrace(DummyUtils.getExceptionMessage(e));
			return textoencriptado;
		}
		catch (javax.crypto.BadPaddingException e) {
			systrace(DummyUtils.getExceptionMessage(e));
			return textoencriptado;
		}
		catch (java.lang.IllegalArgumentException e) {
			systrace(DummyUtils.getExceptionMessage(e));
			return textoencriptado;
		}
		catch (Exception e) {
			systrace(DummyUtils.getExceptionMessage(e));
			throw new RuntimeException(e);
		}
	}

	public static String encryptIfNot(Chave chave, String str) {
		if(StringUtils.isBlank(str)) {
			return str;
		}
		if(str.endsWith(FINAL)) {
			return str;
		}
		return encrypt(chave, str);
	}

	private static String encode(byte[] bytes) {
		String encripted = urlBase64Encoder.encodeToString(bytes);
		return encripted;
	}

	private static byte[] decode(String aux) {
		byte[] decodeBase64 = urlBase64Decoder.decode(aux);
		return decodeBase64;
	}

	public static void main(String[] args) {

		String encrypt = Criptografia.encrypt(Criptografia.GNFE, "Teste@123");
		String decript = Criptografia.decrypt(Criptografia.GNFE, "GeVtY9mh58vdXYo6MdhayQ==%#$");
		System.out.println(encrypt + " - " + decript);
	}
}
