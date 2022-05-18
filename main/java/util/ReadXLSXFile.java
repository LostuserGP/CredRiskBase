package util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class ReadXLSXFile {
    private ArrayList<PivotData> cells;

    public Vector<Vector<String>> importExcelSheet(String fileName) {
        Vector<Vector<String>> cellVectorHolder = new Vector<>();
        try {
            Workbook workBook = WorkbookFactory.create(new FileInputStream(fileName));
            Sheet sheet = workBook.getSheetAt(0);
            Iterator rowIter = sheet.rowIterator();

            while (rowIter.hasNext()) {
                XSSFRow row = (XSSFRow) rowIter.next();
                Iterator cellIter = row.cellIterator();
                Vector<String> cellStoreVector = new Vector<>();

                while (cellIter.hasNext()) {
                    XSSFCell cell = (XSSFCell) cellIter.next();
//                    String cellTypeDesc = "";
                    String cellValue = "";
                    //Integer cellType = cell.getCellType();
                    CellType cType = cell.getCellType();

                    switch (cType) {
                        case NUMERIC:
//                            cellTypeDesc = "NUMERIC";
                            Double doubleValue = cell.getNumericCellValue();

                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                if (HSSFDateUtil.isValidExcelDate(doubleValue)) {
                                    Date date = HSSFDateUtil.getJavaDate(doubleValue);

                                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                    cellValue = df.format(date);
                                    System.err.println(cellValue);
                                }
                            } else {
                                Integer intValue = doubleValue.intValue();
                                cellValue = String.valueOf(intValue);
                            }

                            break;
                        case STRING:
//                            cellTypeDesc = "STRING";
                            cellValue = cell.getStringCellValue();
                            break;
                        case FORMULA:
//                            cellTypeDesc = "FORMULA";
                            cellValue = cell.getCellFormula();
                            break;
                        case BLANK:
//                            cellTypeDesc = "BLANK";
                            cellValue = "BLANK";
                            break;
                        case BOOLEAN:
//                            cellTypeDesc = "BOOLEAN";
                            boolean booleanValue = cell.getBooleanCellValue();
                            cellValue = "" + booleanValue;
                            break;
                        case ERROR:
//                            cellTypeDesc = "ERROR";
                            byte byteValue = cell.getErrorCellValue();
                            cellValue = "" + byteValue;
                            break;
                    }

                    cellStoreVector.addElement(cellValue);
                }
                cellVectorHolder.addElement(cellStoreVector);
            }
        } catch (Exception e) {
            Helper.debug(e.getMessage());
        }
        return cellVectorHolder;
    }

    public ArrayList<ArrayList<String>> importExcelSheet2(String fileName) {
        ArrayList<ArrayList<String>> cellArrayHolder = new ArrayList<>();
        try {
            Workbook workBook = WorkbookFactory.create(new FileInputStream(fileName));
            Sheet sheet = workBook.getSheetAt(0);
            Iterator rowIter = sheet.rowIterator();

            while (rowIter.hasNext()) {
                XSSFRow row = (XSSFRow) rowIter.next();
                Iterator cellIter = row.cellIterator();
                ArrayList<String> cellStoreArray = new ArrayList<>();

                while (cellIter.hasNext()) {
                    XSSFCell cell = (XSSFCell) cellIter.next();
//                    String cellTypeDesc = "";
                    String cellValue = "";
                    //Integer cellType = cell.getCellType();
                    CellType cType = cell.getCellType();

                    switch (cType) {
                        case NUMERIC:
//                            cellTypeDesc = "NUMERIC";
                            Double doubleValue = cell.getNumericCellValue();

                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                if (HSSFDateUtil.isValidExcelDate(doubleValue)) {
                                    Date date = HSSFDateUtil.getJavaDate(doubleValue);
//                                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
//                                    cellValue = df.format(date);
                                    LocalDate ldate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                                    cellValue = ldate.toString();
                                }
                            } else {
                                cellValue = doubleValue.toString();
                            }
                            break;
                        case STRING:
//                            cellTypeDesc = "STRING";
                            cellValue = cell.getStringCellValue();
                            break;
                        case FORMULA:
//                            cellTypeDesc = "FORMULA";
                            cellValue = cell.getCellFormula();
                            break;
                        case BLANK:
//                            cellTypeDesc = "BLANK";
                            cellValue = "";
                            break;
                        case BOOLEAN:
//                            cellTypeDesc = "BOOLEAN";
                            boolean booleanValue = cell.getBooleanCellValue();
                            cellValue = "" + booleanValue;
                            break;
                        case ERROR:
//                            cellTypeDesc = "ERROR";
                            byte byteValue = cell.getErrorCellValue();
                            cellValue = "" + byteValue;
                            break;
                    }
                    cellStoreArray.add(cellValue);
                }
                cellArrayHolder.add(cellStoreArray);
            }
        } catch (Exception e) {
            Helper.debug(e.getMessage());
        }
        return cellArrayHolder;
    }

}