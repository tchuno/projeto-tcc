package net.gnfe.util.excel;

import net.gnfe.util.DummyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class ExcelWriter {

	private int startCol = 0;
	private ExcelFormat excelFormat;
	private Workbook workbook;
	private int currentCol;
	//private XSSFRow currentRow;
	private Row currentRow;
	private Sheet currentSheet;

	public void criarArquivo(String extensao) {
		if("xls".equals(extensao)) {
			workbook = new HSSFWorkbook();
		}
		else {
			XSSFWorkbook workbook2 = new XSSFWorkbook();
			workbook = new SXSSFWorkbook(workbook2, 500);
		}
	}

	public void abrirArquivo(File fileOrigem) throws InvalidFormatException, IOException {

		String name = fileOrigem.getName();
		String extensao = DummyUtils.getExtensao(name);

		if("xls".equals(extensao)) {
			FileInputStream fis = new FileInputStream(fileOrigem);
			workbook = new HSSFWorkbook(fis);
		}
		else {

			OPCPackage open = OPCPackage.open(fileOrigem);
			XSSFWorkbook workbook2 = new XSSFWorkbook(open);
			workbook = new SXSSFWorkbook(workbook2, 500);
		}
	}

	public void criaPlanilha(String nome) {
		currentSheet = workbook.createSheet(nome);
	}

	public void criaLinha(Sheet sheet, int i) {

		currentRow = sheet.createRow(i);
		currentCol = startCol;
	}

	public void criaLinha(int i) {
		currentRow = currentSheet.createRow(i);
		currentCol = startCol;
	}


	public void criaLinha(XSSFSheet sheet, int i) {

		currentRow = sheet.createRow(i);
		currentCol = startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public void setExcelFormat(ExcelFormat excelFormat) {
		this.excelFormat = excelFormat;
	}

	public ExcelFormat getExcelFormat() {
		return excelFormat;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public int escrever(Number num) {

		Cell cell = currentRow.createCell(currentCol++);
		CellStyle stringCS = excelFormat.getStringCS();
		cell.setCellStyle(stringCS);

		if(num != null) {
			cell.setCellValue(num.doubleValue());
		}

		return currentCol;
	}

	public int escrever(Number num, Integer width) {

		int col = currentCol++;
		Cell cell = currentRow.createCell(col);
		CellStyle stringCS = excelFormat.getStringCS();
		cell.setCellStyle(stringCS);

		if(num != null) {
			cell.setCellValue(num.doubleValue());
		}

		if(width != null) {
			currentSheet.setColumnWidth(col, width);
		}

		return currentCol;
	}

	public int escrever(String str) {

		if (StringUtils.isBlank(str)) {
			str = "";
		}

		Cell cell = currentRow.createCell(currentCol++);
		CellStyle stringCS = excelFormat.getStringCS();
		cell.setCellStyle(stringCS);

		cell.setCellValue(str);

		return currentCol;
	}

	public int escrever(String str, int indexColuna) {

		Cell cell = currentRow.createCell(indexColuna);

		if(str != null) {
			cell.setCellValue(str);
		}

		return currentCol;
	}

	public int escreverCabecalho(String str) {

		Cell cell = currentRow.createCell(currentCol++);
		CellStyle cabecalhoCS = excelFormat.getCabecalhoCS();
		cell.setCellStyle(cabecalhoCS);

		if(str != null) {
			cell.setCellValue(str);
		}

		return currentCol;
	}

	public int escreverDate(Date date) {

		Cell cell = currentRow.createCell(currentCol++);
		CellStyle dateTimeCS = excelFormat.getDateCS();
		cell.setCellStyle(dateTimeCS);

		if(date != null) {
			cell.setCellValue(date);
		}

		return currentCol;
	}

	public int escreverDateTime(Date dateTime) {

		Cell cell = currentRow.createCell(currentCol++);
		CellStyle dateTimeCS = excelFormat.getDateTimeCS();
		cell.setCellStyle(dateTimeCS);

		if(dateTime != null) {
			cell.setCellValue(dateTime);
		}

		return currentCol;
	}

	public int escreverTime(Date time) {

		Cell cell = currentRow.createCell(currentCol++);
		CellStyle dateTimeCS = excelFormat.getTimeCS();
		cell.setCellStyle(dateTimeCS);

		if(time != null) {
			cell.setCellValue(time);
		}

		return currentCol;
	}

	public Sheet selecionarPlanilha(String nome) {
		currentSheet = workbook.getSheet(nome);

		if (currentSheet == null) {
			currentSheet = workbook.createSheet(nome);
		}

		return currentSheet;
	}
}
