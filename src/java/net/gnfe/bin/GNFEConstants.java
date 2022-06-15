package net.gnfe.bin;

import java.text.NumberFormat;
import java.util.Locale;


public interface GNFEConstants {

	Locale LOCALE_PT_BR = new Locale("pt", "BR");
	NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(LOCALE_PT_BR);

}
