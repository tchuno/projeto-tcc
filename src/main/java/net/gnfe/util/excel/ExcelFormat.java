package net.gnfe.util.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

@SuppressWarnings("deprecation")
public class ExcelFormat {

	private CellStyle currencyCS;
	private CellStyle percentCS;
	private CellStyle dateCS;
	private CellStyle dateTimeCS;
	private CellStyle timeCS;
	private CellStyle stringCS;
	private CellStyle cabecalhoCS;

	public ExcelFormat(Workbook workbook) {

		currencyCS = workbook.createCellStyle();
		currencyCS.setDataFormat((short)8);
		setBorder(currencyCS);

		percentCS = workbook.createCellStyle();
		DataFormat percentDataFormat = workbook.createDataFormat();
		short format = percentDataFormat.getFormat("0%");
		percentCS.setDataFormat(format);
		setBorder(percentCS);

		DataFormat dateTimeFormat = workbook.createDataFormat();
		short dateTimeFormatShort = dateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
		dateTimeCS = workbook.createCellStyle();
		dateTimeCS.setDataFormat(dateTimeFormatShort);
		setBorder(dateTimeCS);

		DataFormat timeFormat = workbook.createDataFormat();
		short timeFormatShort = timeFormat.getFormat("HH:mm");
		timeCS = workbook.createCellStyle();
		timeCS.setDataFormat(timeFormatShort);
		setBorder(timeCS);

		DataFormat dateFormat = workbook.createDataFormat();
		short dateFormatShort = dateFormat.getFormat("dd/MM/yyyy");
		dateCS = workbook.createCellStyle();
		dateCS.setDataFormat(dateFormatShort);
		setBorder(dateCS);

		stringCS = workbook.createCellStyle();
		setBorder(stringCS);

		cabecalhoCS = workbook.createCellStyle();
		Font font = workbook.createFont();
		setBorder(cabecalhoCS);
		setColor(cabecalhoCS, font);
	}

	private void setBorder(CellStyle style) {

		short border = CellStyle.BORDER_THIN;
		short borderColor = IndexedColors.BLACK.getIndex();
		style.setBorderBottom(border);
		style.setBottomBorderColor(borderColor);
		style.setBorderLeft(border);
		style.setLeftBorderColor(borderColor);
		style.setBorderRight(border);
		style.setRightBorderColor(borderColor);
		style.setBorderTop(border);
		style.setTopBorderColor(borderColor);
	}

	private void setColor(CellStyle style, Font font) {

		short backgroundColor = IndexedColors.LIGHT_BLUE.getIndex();
		style.setFillBackgroundColor(backgroundColor);
		style.setFillForegroundColor(backgroundColor);

		font.setBold(true);
		style.setFont(font);
	}

	public CellStyle getCurrencyCS() {
		return currencyCS;
	}

	public CellStyle getPercentCS() {
		return percentCS;
	}

	public CellStyle getDateCS() {
		return dateCS;
	}

	public CellStyle getDateTimeCS() {
		return dateTimeCS;
	}

	public CellStyle getStringCS() {
		return stringCS;
	}

	public CellStyle getTimeCS() {
		return timeCS;
	}

	public CellStyle getCabecalhoCS() {
		return cabecalhoCS;
	}
}