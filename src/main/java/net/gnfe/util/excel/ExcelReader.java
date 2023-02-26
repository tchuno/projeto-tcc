package net.gnfe.util.excel;


import net.gnfe.util.DummyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcelReader {

	private static final SimpleDateFormat SDF1 = new SimpleDateFormat("dd/MM/yyyy");

	private Workbook workbook = null;
	private List<LinhaVO> linas = new ArrayList<>();

	public void open(File file) throws IOException {

		try {
			String name = file.getName();
			String extensao = DummyUtils.getExtensao(name);
			FileInputStream fis = new FileInputStream(file);
			if("xls".equals(extensao)) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}
		}
		catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void open(String nomeArquivo, InputStream inputstream) {

		try {
			String extensao = DummyUtils.getExtensao(nomeArquivo);
			if("xls".equals(extensao)) {
				workbook = new HSSFWorkbook(inputstream);
			} else {
				workbook = new XSSFWorkbook(inputstream);
			}
		}
		catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void close() {

		if(workbook != null) {
			try {
				workbook.close();
			}
			catch (IOException e) {
				throw new RuntimeException();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String getString(Row row, int col) {

		Cell cell = row.getCell(col);
		if(cell == null) {
			return null;
		}

		int cellType = cell.getCellType();
		if(cellType == Cell.CELL_TYPE_STRING) {
			String value = cell.getStringCellValue();
			value = StringUtils.trim(value);
			return value;
		}
		else if(cellType == Cell.CELL_TYPE_NUMERIC) {

			CellStyle cellStyle = cell.getCellStyle();

			if (cellStyle == null) {
				String value = cell.getStringCellValue();
				return value;
			}

			String dataFormatString = cellStyle.getDataFormatString();
			if(dataFormatString != null) {
				
				if (dataFormatString.equals("General")) {
					String value = cell.getStringCellValue();
					return value;
				}

				try {
					DataFormatter dataFormatter = new DataFormatter(new Locale("pt-BR"));
					String value = dataFormatter.formatCellValue(cell);
					value = StringUtils.trim(value);
					return value;
				}
				catch (AbstractMethodError e) {
					String value = cell.getStringCellValue();
					String[] split = value.split("[/]");
					if (split.length == 3) {
						return split[1] + "/" + split[0] + "/" + split[2];
					}
					return null;
				}
			}
		}

		try {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			String value = cell.getStringCellValue();
			value = StringUtils.trim(value);
			return value;
		}
		catch(Exception e) {
			String value = cell.getStringCellValue();
			return value;
		}
	}

	@SuppressWarnings("deprecation")
	public String getStringFromData(Row row, String nomeCampo, int col, int rowNum) {

		Cell cell = row.getCell(col);
		if(cell == null) {
			return null;
		}

		int cellType = cell.getCellType();
		if(Cell.CELL_TYPE_STRING == cellType) {

			String value = cell.getStringCellValue();
			try {
				SDF1.parse(value);
			}
			catch (ParseException e) {
				throw new RuntimeException(e);
			}

			value = StringUtils.trim(value);
			return value;
		}

		Date date = cell.getDateCellValue();
		if(date == null) {
			return null;
		}

		return SDF1.format(date);
	}

	public void addVO(LinhaVO vo) {
		linas.add(vo);
	}

	public List<? extends LinhaVO> getLinas() {
		return linas;
	}

	public static abstract class LinhaVO {

		private int rowNum;

		public int getRowNum() {
			return rowNum;
		}

		public void setRowNum(int rowNum) {
			this.rowNum = rowNum;
		}
	}
}
