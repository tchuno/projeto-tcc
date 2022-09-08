package net.gnfe.bin;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public interface GNFEConstants {

	Locale LOCALE_PT_BR = new Locale("pt", "BR");
	NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(LOCALE_PT_BR);
	List<String> IMAGEM_EXTENSOES = Arrays.asList("jpg", "jpeg", "png", "gif");

}
