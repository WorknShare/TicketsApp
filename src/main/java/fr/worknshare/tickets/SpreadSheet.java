package fr.worknshare.tickets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class for creating excel files from Object arrays.
 * @author Jérémy LAMBERT
 *
 */
public final class SpreadSheet {

	private String file;
	private String title;
	private Object[][] data;
	private String[] header;
	
	private int rowNum;
	private int totalColNum;

	private XSSFCellStyle headerCellStyle;
	private XSSFCellStyle dateCellStyle;
	
	public SpreadSheet(String file, String title, Object[][] data, String[] header) {
		this.file = file;
		this.title = title;
		this.data = data;
		this.header = header;
	}

	public SpreadSheet(String file, String title, Object[][] data) {
		this.file = file;
		this.title = title;
		this.data = data;
	}

	public SpreadSheet(String file, String title) {
		this.file = file;
		this.title = title;
	}

	public SpreadSheet(String file) {
		this.file = file;
	}


	public final String getFile() {
		return file;
	}

	public final void setFile(String file) {
		this.file = file;
	}

	public final Object[][] getData() {
		return data;
	}

	public final void setData(Object[][] data) {
		this.data = data;
	}

	public final String[] getHeader() {
		return header;
	}

	public final void setHeader(String[] header) {
		this.header = header;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}
	
	private void fillData(XSSFWorkbook workbook, XSSFSheet sheet) {
		for (Object[] obj : data) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : obj) {
				Cell cell = row.createCell(colNum++);
				
				if(field != null) {
					if (field instanceof String) {
						cell.setCellValue((String) field);
					} else if (field instanceof Integer) {
						cell.setCellValue((Integer) field);
					} else if(field instanceof Double) {
						cell.setCellValue((Double) field);
					} else if(field instanceof Boolean) {
						cell.setCellValue((Boolean) field);
					} else if(field instanceof Date) {
						initDateCellStyle(workbook);
						cell.setCellValue((Date) field);
						cell.setCellStyle(dateCellStyle);
					} else if(field instanceof Byte) {
						cell.setCellValue((Byte) field);
					}
				}
			}
			if(colNum > totalColNum) totalColNum = colNum;
		}
	}
	
	private void fillHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		int colNum = 0;
		initHeaderCellStyle(workbook);
		Row row = sheet.createRow(rowNum++);
		for(String head : header) {
			Cell cell = row.createCell(colNum++);
			cell.setCellValue(head);
			cell.setCellStyle(headerCellStyle);
		}
	}

	/**
	 * Saves the spreadsheet.
	 * @return true if the file has been successfully created and filled.
	 */
	public boolean save() {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(title == null ? "DEFAULT_TITLE" : title);

		rowNum = 0;
		totalColNum = 0;

		if(header != null && header.length > 0)
			fillHeader(workbook, sheet);        

		if(data != null)
			fillData(workbook, sheet);

		//Make columns fit the width of their content
		for(int i = 0 ; i < totalColNum ; i++)
			sheet.autoSizeColumn(i);
		
		
		//Write the file
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Unable to write Excel file.", e);
			return false;
		}


		return true;
	}
	
	private void initDateCellStyle(XSSFWorkbook workbook) {
		if(dateCellStyle == null) {
			CreationHelper createHelper = workbook.getCreationHelper();
			dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));
		}
	}

	private void initHeaderCellStyle(XSSFWorkbook workbook) {
		headerCellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setColor(new XSSFColor(new java.awt.Color(255, 255, 255)));
		
		headerCellStyle.setFont(font);
		headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(90, 90, 90)));
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
	}

}
