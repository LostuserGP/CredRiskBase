package util;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import database.Statrs;
import gui.DebugGUI;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public class Helper {
	public static boolean debugmode = false;

    public static Font outputFont = new Font("Times New Roman", 0, 12);

	public static void showDone() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JOptionPane.showMessageDialog(new JFrame(), "Done!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void showMsg(final String msg) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JOptionPane.showMessageDialog(new JFrame(), msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static DatePicker createDatePicker() {
		// Create the localized date picker and label.
		Locale locale = new Locale("ru");
		DatePickerSettings settings = new DatePickerSettings(locale);
		// Set a minimum size for the localized date pickers, to improve the look of the demo.
//		settings.setSizeTextFieldMinimumWidth(125);
//		settings.setSizeTextFieldMinimumWidthDefaultOverride(true);
		DatePicker localizedDatePicker = new DatePicker(settings);
		localizedDatePicker.setDateToToday();
		//panel.panel4.add(localizedDatePicker, getConstraints(1, (rowMarker * rowMultiplier), 1));
		//panel.addLabel(panel.panel4, 1, (rowMarker * rowMultiplier), labelText);
		return localizedDatePicker;
	}

	public static String createSQLDate(String date) {
		
		try {
			String[] split = date.split("\\.");
			int year = Integer.parseInt(split[2]);
			int month = Integer.parseInt(split[1]);
			int day = Integer.parseInt(split[0]);
			String sqlDate = year + "-" + (month < 10 ? "0" + month : month + "") + "-" +  (day < 10 ? "0" + day : day + "");
			return sqlDate;
		} catch (Exception e) {
			return "n/a";
		}
	}
	
	public static DefaultTableModel buildTableModelOld(Vector<Vector<String>> data, String[] columnNamesArray) {
		
		// names of columns

	    Vector<String> columnNames = new Vector<String>(Arrays.asList(columnNamesArray));
	    DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
	    	 @Override
	    	    public boolean isCellEditable(int row, int column) {
	    	       //all cells false
	    	       return false;
	    	    }
	    };

	    return tableModel;

	}

	public static DefaultTableModel buildTableModel(ArrayList<ArrayList<String>> data, String[] columnNamesArray) {

		// names of columns

		//ArrayList<String> columnNames = new ArrayList<String>(Arrays.asList(columnNamesArray));
		String[][] dataArray = new String[data.size()][data.get(0).size()];
		Object object;
		//NumberFormat formatterDouble = new DecimalFormat("#,###.00");
        NumberFormat formatterDouble = new DecimalFormat("#,###");
		NumberFormat formatterInt = new DecimalFormat("#,###");

		for (int i = 0; i < dataArray.length; i++) {
			for (int j = 0; j < dataArray[0].length; j++) {
				object = data.get(i).get(j);
				dataArray[i][j] = (object == null) ? "" : (NumberUtils.isCreatable(object.toString()) ?
						(object instanceof Integer) ? formatterInt.format(Integer.parseInt(object.toString())) : formatterDouble.format(Double.parseDouble(object.toString()))
						: object.toString());
			}
		}

		return new DefaultTableModel(dataArray, columnNamesArray) {
			@Override
			public boolean isCellEditable(int row, int column) {
				//all cells false
				return false;
			}
		};

	}

	public static TableModel buildTableModel(Statrs rs) {
		return buildTableModel(rs, false);
	}

	public static TableModel buildTableModel(Statrs srs, boolean hasNumber) {
		try {
			ResultSet rs = srs.getRs();
			if (!rs.isBeforeFirst()) {
				DefaultTableModel model = new DefaultTableModel();
				model.setRowCount(0);
				return model;
			}
			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			String[] columnNames = new String[numberOfColumns];

			// Get the column names
			for (int column = 0; column < numberOfColumns; column++) {
				columnNames[column] = metaData.getColumnLabel(column + 1);
			}

			// Get all rows.
			ArrayList<ArrayList> rows = new ArrayList();

			while (rs.next()) {
				ArrayList newRow = new ArrayList();
				for (int i = 1; i <= numberOfColumns; i++) {
					newRow.add(rs.getObject(i));
				}
				rows.add(newRow);
			}
			srs.close();

			Object[][] dataArray = new String[rows.size()][rows.get(0).size()];

			Object object;
			NumberFormat formatterDouble = new DecimalFormat("#,###.00");
			NumberFormat formatterInt = new DecimalFormat("#,###");

			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < dataArray[0].length; j++) {
					object = rows.get(i).get(j);
					if (hasNumber) {
						dataArray[i][j] = (object == null) ? "" :
								(NumberUtils.isCreatable(object.toString()) ?
								(object instanceof Integer) ?
										formatterInt.format(Integer.parseInt(object.toString())) :
										formatterDouble.format(Double.parseDouble(object.toString())) :
										object.toString());
					} else {
						dataArray[i][j] = (object == null) ? "" : object.toString();
					}
				}
			}
			return new DefaultTableModel(dataArray, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					//all cells false
					return false;
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TableModel buildTableModel2(ResultSet rs) {
		try {
			if (!rs.isBeforeFirst()) {
				DefaultTableModel model = new DefaultTableModel();
				model.setRowCount(0);
				return model;
			}
			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			String[] columnNames = new String[numberOfColumns];

			// Get the column names
			for (int column = 0; column < numberOfColumns; column++) {
				columnNames[column] = metaData.getColumnLabel(column + 1);
			}

			// Get all rows.
			ArrayList<ArrayList> rows = new ArrayList();

			while (rs.next()) {
				ArrayList newRow = new ArrayList();
				for (int i = 1; i <= numberOfColumns; i++) {
					if (metaData.getColumnType(i) == 3) {
						newRow.add((double) rs.getDouble(i));
					} else {
						newRow.add(rs.getString(i));
					}
				}
				rows.add(newRow);
			}

			Object[][] dataArray = new Object[rows.size()][rows.get(0).size()];
			Object object;

			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < dataArray[0].length; j++) {
					object = rows.get(i).get(j);
					dataArray[i][j] = (object == null) ? "" : object;
				}
			}
			return new DefaultTableModel(dataArray, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					//all cells false
					return false;
				}

			};
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String capitalized(String string, int i) {
		if (string.length() < i) return "";
		String before = string.substring(0, i);
		String itSelf = string.substring(i,i + 1).toUpperCase();
		String after = string.substring(i + 1, string.length());
		return before + itSelf + after;
	}
	
	public static String[] getStringsCapitalized(String string) {
		String[] strings = new String[string.length()];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = capitalized(string, i);
		}
		return strings;
	}
	
	public static DefaultTableModel buildTableModelOld(ResultSet rs)
	        throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }

	    return new DefaultTableModel(data, columnNames) {
	    	 @Override
	    	    public boolean isCellEditable(int row, int column) {
	    	       //all cells false
	    	       return false;
	    	    }
	    };

	}
	
	public static void exportTable(JTable table, File file) {
      
	//	TableModel model = table.getModel();
		
        XSSFWorkbook wb = new XSSFWorkbook();

        Sheet sheet = wb.createSheet("export");
        Row firtstRow = sheet.createRow(0);
        Row row;
        Cell cell;
        	
        for (int i = 0; i < table.getColumnCount(); i++) {
        	 cell = firtstRow.createCell(i);
        	 cell.setCellValue(table.getColumnName(i));
        }

		try {

			for (int i = 1; i < table.getRowCount() + 1; i++) {
				row = sheet.createRow(i);
				for (int j = 0; j < table.getColumnCount(); j++) {
					cell = row.createCell(j);
					Object value = table.getValueAt(i - 1, j);
					if (value != null) cell.setCellValue(value.toString());
				}
			}
   
			FileOutputStream fileOut = new FileOutputStream(file);
			wb.write(fileOut);
			fileOut.close();
			JOptionPane.showMessageDialog(null, "Done!");
			
		} catch (Exception e1) {
			Object[] options = { "Ok", "Copy to Clipboard" };
			int n = JOptionPane.showOptionDialog(new JFrame(),
					ExceptionUtils.getStackTrace(e1),
					"Exception message", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE, null, // do not use a
														// custom
														// Icon
					options, // the titles of buttons
					options[0]);
			Helper.debug("error");

			Clipboard clpbrd = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			if (n == 1)
				clpbrd.setContents(new StringSelection(
						ExceptionUtils.getStackTrace(e1)), null);
			e1.printStackTrace();
		}
        
    }
	
	public static TableRowSorter updateTableOld2(JTable table, ResultSet rs) throws SQLException {
		
		ResultSetMetaData metaData = rs.getMetaData();

		int columnCount = metaData.getColumnCount();
		String[] columnNames = new String[columnCount];
	    
		for (int column = 0; column < columnCount; column++) {
	        columnNames[column] = metaData.getColumnName(column + 1);
	    }
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex + 1));
	        }
	        data.add(vector);
	    }
	    
	    String[][] dataArray = new String[data.size()][data.get(0).size()];
	    Object object;
	    NumberFormat formatterDouble = new DecimalFormat("#,###.00");
	    NumberFormat formatterInt = new DecimalFormat("#,###");

	    
	    for (int i = 0; i < dataArray.length; i++) {
	    	for (int j = 0; j < dataArray[0].length; j++) {
	    		object = data.get(i).get(j);
				dataArray[i][j] = (object == null) ? "" : (NumberUtils.isCreatable(object.toString()) ?
						(object instanceof Integer) ? formatterInt.format(Integer.parseInt(object.toString())) : formatterDouble.format(Double.parseDouble(object.toString())) 
						: object.toString());
			}
		}
	    	    
		table.setModel(new DefaultTableModel(dataArray, columnNames));
		
		TableRowSorter<TableModel> sorter =
	             new TableRowSorter<TableModel>(table.getModel());
	    table.setRowSorter(sorter);
	    
	    
	    return sorter;

	}

	public static class MyTableCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int сolumn) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, сolumn);
			setHorizontalAlignment(SwingConstants.RIGHT);
			return this;
		}
	}

	public static TableRowSorter updateTableSRK(JTable table) throws SQLException {
		try {
			SrkForExcel forExcel = new SrkForExcel();
			List<SrkRow> list = forExcel.getSrkList();
			if (!(list.size() > 0)) {
				DefaultTableModel model = new DefaultTableModel();
				model.setRowCount(0);
				table.setModel(model);
				return null;
			}

			int numberOfColumns = 14;
			String[] columnNames = new String[numberOfColumns];

			columnNames[0] = "N";
			columnNames[1] = "Контрагент";
			columnNames[2] = "ВКР";
			columnNames[3] = "Дата";
			columnNames[4] = "ДЭГ";
			columnNames[5] = "ДТО";
			columnNames[6] = "ДЭН";
			columnNames[7] = "Иные решения";
			columnNames[8] = "Суммарный лимит";
			columnNames[9] = "УКЗ";
			columnNames[10] = "Ответственный";
			columnNames[11] = "Основание";
			columnNames[12] = "Тип лимита";
			columnNames[13] = "Вес рейтинга";

			// Get all rows.

			String[][] dataArray = new String[list.size()][numberOfColumns];
			for (int i = 0; i < dataArray.length; i++) {
				dataArray[i][0] = String.valueOf(list.get(i).getId());
				dataArray[i][1] = list.get(i).getName();
				dataArray[i][2] = list.get(i).getRating();
				dataArray[i][3] = list.get(i).getRatingDate().toString();
				if (list.get(i).getLimitType() == 3) {
					dataArray[i][4] = "-";
					dataArray[i][5] = "-";
					dataArray[i][6] = "-";
				} else {
					dataArray[i][4] = String.valueOf(list.get(i).getDepLimit());
					dataArray[i][5] = String.valueOf(list.get(i).getDtoLimit());
					dataArray[i][6] = String.valueOf(list.get(i).getDenLimit());
				}
				dataArray[i][7] = list.get(i).getComment();
				dataArray[i][8] = String.valueOf(list.get(i).getSubsidiarySum());
				dataArray[i][9] = list.get(i).getUKZ();
				dataArray[i][10] = list.get(i).getAnalyst();
				dataArray[i][11] = list.get(i).getCauses();
				dataArray[i][12] = String.valueOf(list.get(i).getLimitType());
				dataArray[i][13] = String.valueOf(list.get(i).getRatingValue());
			}

			TableModel tm =  new DefaultTableModel(dataArray, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					//all cells false
					return false;
				}

			};
			table.setModel(tm);
//			for (int i = 0; i < numberOfColumns; i++) {
//				if (metaData.getColumnType(i+1) <= 5) {
//					table.getColumnModel().getColumn(i).setCellRenderer(new MyTableCellRenderer());
//				}
//			}
			TableRowSorter<TableModel> sorter =
					new TableRowSorter<TableModel>(table.getModel());
			table.setRowSorter(sorter);

			return sorter;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TableRowSorter updateTable(JTable table, Statrs statrs) throws SQLException {
		try {
			ResultSet rs = statrs.getRs();
			if (!rs.isBeforeFirst()) {
				DefaultTableModel model = new DefaultTableModel();
				model.setRowCount(0);
				table.setModel(model);
				return null;
			}

			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			String[] columnNames = new String[numberOfColumns];

			// Get the column names
			for (int column = 0; column < numberOfColumns; column++) {
				columnNames[column] = metaData.getColumnLabel(column + 1);
			}

			// Get all rows.
			ArrayList<ArrayList> rows = new ArrayList();

			while (rs.next()) {
				ArrayList newRow = new ArrayList();
				for (int i = 1; i <= numberOfColumns; i++) {
					newRow.add(rs.getObject(i));
				}
				rows.add(newRow);
			}

			String[][] dataArray = new String[rows.size()][rows.get(0).size()];
			Object object;
			NumberFormat formatterDouble = new DecimalFormat("#,###.00");
			NumberFormat formatterInt = new DecimalFormat("#,###");

			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < dataArray[0].length; j++) {
					object = rows.get(i).get(j);
					if (j == 0 || columnNames[j].equals("ID") || columnNames[j].equals("Duns")  || columnNames[j].equals("DUNS")) {
						dataArray[i][j] = (object == null) ? "" : (object.toString());
					} else {
						dataArray[i][j] = (object == null) ? "" : (NumberUtils.isCreatable(object.toString()) ?
								(object instanceof Integer) ? formatterInt.format(Integer.parseInt(object.toString())) : formatterDouble.format(Double.parseDouble(object.toString()))
								: object.toString());
					}
				}
			}

			TableModel tm =  new DefaultTableModel(dataArray, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					//all cells false
					return false;
				}

			};
			table.setModel(tm);
			for (int i = 0; i < numberOfColumns; i++) {
				if (metaData.getColumnType(i+1) <= 5) {
					table.getColumnModel().getColumn(i).setCellRenderer(new MyTableCellRenderer());
				}
			}
			TableRowSorter<TableModel> sorter =
					new TableRowSorter<TableModel>(table.getModel());
			table.setRowSorter(sorter);

			statrs.close();

			return sorter;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TableRowSorter updateTable(JTable table, ResultSet rs) throws SQLException {
		try {
			if (!rs.isBeforeFirst()) {
				DefaultTableModel model = new DefaultTableModel();
				model.setRowCount(0);
				table.setModel(model);
				return null;
			}

			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			String[] columnNames = new String[numberOfColumns];

			// Get the column names
			for (int column = 0; column < numberOfColumns; column++) {
				columnNames[column] = metaData.getColumnLabel(column + 1);
			}

			// Get all rows.
			ArrayList<ArrayList> rows = new ArrayList();

			while (rs.next()) {
				ArrayList newRow = new ArrayList();
				for (int i = 1; i <= numberOfColumns; i++) {
					newRow.add(rs.getObject(i));
				}
				rows.add(newRow);
			}

			String[][] dataArray = new String[rows.size()][rows.get(0).size()];
			Object object;
			NumberFormat formatterDouble = new DecimalFormat("#,###.00");
			NumberFormat formatterInt = new DecimalFormat("#,###");

			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < dataArray[0].length; j++) {
					object = rows.get(i).get(j);
					dataArray[i][j] = (object == null) ? "" : (NumberUtils.isCreatable(object.toString()) ?
							(object instanceof Integer) ? formatterInt.format(Integer.parseInt(object.toString())) : formatterDouble.format(Double.parseDouble(object.toString()))
							: object.toString());
				}
			}

			TableModel tm =  new DefaultTableModel(dataArray, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					//all cells false
					return false;
				}

			};
			table.setModel(tm);
			for (int i = 0; i < numberOfColumns; i++) {
				if (metaData.getColumnType(i+1) <= 5) {
					table.getColumnModel().getColumn(i).setCellRenderer(new MyTableCellRenderer());
				}
			}
			TableRowSorter<TableModel> sorter =
					new TableRowSorter<TableModel>(table.getModel());
			table.setRowSorter(sorter);

			rs.close();

			return sorter;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TableRowSorter updateTableOld(JTable table, Statrs srs) throws SQLException {

//		table.setModel(buildTableModel(rs, true));
		table.setModel(buildTableModel(srs));

//		ResultSetMetaData metaData = rs.getMetaData();
//		int numberOfColumns = metaData.getColumnCount();
//		for (int column = 0; column < numberOfColumns; column++) {
//			if (metaData.getColumnType(column+1) == 3) {
//				System.out.println("column: " + column);
//				TableColumn tc = table.getColumnModel().getColumn(column);
//				TableCellRenderer tcr = tc.getCellRenderer();
//				tc.setCellRenderer(new MyTableCellRenderer());
//			}
//		}

		srs.close();

		TableRowSorter<TableModel> sorter =
				new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);

		return sorter;
	}

	public static TableRowSorter updateTable(JTable table, ArrayList<ArrayList<String>> data, String[] columnNames) throws SQLException {

		table.setModel(buildTableModel(data, columnNames));

		TableRowSorter<TableModel> sorter =
				new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);

		return sorter;
	}

	public static JTable loadTableOld(JTable table, ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		int columnCount = metaData.getColumnCount();
		String[] columnNames = new String[columnCount];

		for (int column = 0; column < columnCount; column++) {
			columnNames[column] = metaData.getColumnName(column + 1);
		}

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex + 1));
			}
			data.add(vector);
		}

		String[][] dataArray = new String[data.size()][data.get(0).size()];
		Object object;
		NumberFormat formatterDouble = new DecimalFormat("#,###.00");
		NumberFormat formatterInt = new DecimalFormat("#,###");


		for (int i = 0; i < dataArray.length; i++) {
			for (int j = 0; j < dataArray[0].length; j++) {
				object = data.get(i).get(j);
				dataArray[i][j] = (object == null) ? "" : (NumberUtils.isCreatable(object.toString()) ?
						(object instanceof Integer) ? formatterInt.format(Integer.parseInt(object.toString())) : formatterDouble.format(Double.parseDouble(object.toString()))
						: object.toString());
			}

		}

		table.setModel(new DefaultTableModel(dataArray, columnNames));

		TableRowSorter<TableModel> sorter =
				new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);


		return table;

	}

	public static int getSPRatingValueByString(String ratingString) {
		
		if (ratingString.contains("AAA"))
			return 1;
		else if (ratingString.contains("AA+"))
			return 2;
		else if (ratingString.contains("AA-"))
			return 4;
		else if (ratingString.contains("AA"))
			return 3;
		else if (ratingString.contains("A+"))
			return 5;
		else if (ratingString.contains("A-"))
			return 7;
		else if (ratingString.contains("A") && !ratingString.contains("N"))
			return 6;
		else if (ratingString.contains("BBB+"))
			return 8;
		else if (ratingString.contains("BBB-"))
			return 10;
		else if (ratingString.contains("BBB"))
			return 9;
		else if (ratingString.contains("BB+"))
			return 11;
		else if (ratingString.contains("BB-"))
			return 13;
		else if (ratingString.contains("BB"))
			return 12;
		else if (ratingString.contains("B+"))
			return 14;
		else if (ratingString.contains("B-"))
			return 16;
		else if (ratingString.contains("B"))
			return 15;
		else if (ratingString.contains("CCC+"))
			return 17;
		else if (ratingString.contains("CCC-"))
			return 19;
		else if (ratingString.contains("CCC"))
			return 18;
		else if (ratingString.contains("CC"))
			return 20;
		else if (ratingString.equals("C"))
			return 21;
		else if (ratingString.equals("D"))
			return 22;
		
		return -1;
		
	}
	
	public static int getMoodysRatingValueByString(String ratingString) {
		
		if (ratingString.contains("Aaa"))
			return 1;
		else if (ratingString.contains("Aa1"))
			return 2;
		else if (ratingString.contains("Aa3"))
			return 4;
		else if (ratingString.contains("Aa2"))
			return 3;
		else if (ratingString.contains("A1"))
			return 5;
		else if (ratingString.contains("A3"))
			return 7;
		else if (ratingString.contains("A2"))
			return 6;
		else if (ratingString.contains("Baa1"))
			return 8;
		else if (ratingString.contains("Baa3"))
			return 10;
		else if (ratingString.contains("Baa2"))
			return 9;
		else if (ratingString.contains("Ba1"))
			return 11;
		else if (ratingString.contains("Ba3"))
			return 13;
		else if (ratingString.contains("Ba2"))
			return 12;
		else if (ratingString.contains("B1"))
			return 14;
		else if (ratingString.contains("B3"))
			return 16;
		else if (ratingString.contains("B2"))
			return 15;
		else if (ratingString.contains("Caa1"))
			return 17;
		else if (ratingString.contains("Caa3"))
			return 19;
		else if (ratingString.contains("Caa2"))
			return 18;
		else if (ratingString.contains("Ca"))
			return 20;
		else if (ratingString.equals("C"))
			return 21;
		else if (ratingString.equals("D"))
			return 22;
		
		return -1;
		
		
	}

	public static String getRatingValueByID(int newRatingID) {
	
	if (newRatingID == 1) return "AAA";
	if (newRatingID == 2) return "AA+";
	if (newRatingID == 3) return "AA";
	if (newRatingID == 4) return "AA-";
	if (newRatingID == 5) return "A+";
	if (newRatingID == 6) return "A";
	if (newRatingID == 7) return "A-";
	if (newRatingID == 8) return "BBB+";
	if (newRatingID == 9) return "BBB";
	if (newRatingID == 10) return "BBB-";
	if (newRatingID == 11) return "BB+";
	if (newRatingID == 12) return "BB";
	if (newRatingID == 13) return "BB-";
	if (newRatingID == 14) return "B+";
	if (newRatingID == 15) return "B";
	if (newRatingID == 16) return "B-";
	if (newRatingID == 17) return "CCC+";
	if (newRatingID == 18) return "CCC";
	if (newRatingID == 19) return "CCC-";
	if (newRatingID == 20) return "CC";
	if (newRatingID == 21) return "C";
	if (newRatingID == 22) return "D";

	return "n/a";
}

	public static void debug(String str) {
		if (debugmode) {
			String className = getMethodName()[0];
			String methodName = getMethodName()[1];
			String debugText = "(" + className + ":" + methodName + "): \n" + str;
			DebugGUI.getInstance(debugText);
		}
	}

    public static void debug(String str, String ... args) {
        if (debugmode) {
            String className = getMethodName()[0];
            String methodName = getMethodName()[1];
            String debugText = "(" + className + ":" + methodName + "): \n" + str;
            for (String tmp : args) {
                debugText = debugText + "\nWith parametr: " + tmp;
            }
            DebugGUI.getInstance(debugText);
        }
    }

	public static String[] getMethodName() {
		Throwable t = new Throwable();
		StackTraceElement trace[] = t.getStackTrace();
		String answer[] = new String[2];
		if (trace.length > 2)
		{
			StackTraceElement element = trace[2];
			answer[0] = element.getClassName();
			answer[1] = element.getMethodName();
			if (answer[1].equals("runSelect") || answer[1].equals("runUpdate")) {
				element = trace[3];
				answer[0] = element.getClassName();
				answer[1] = element.getMethodName();
			}
			return answer;
		}
		else {
			answer[0] = "noClass";
			answer[1] = "noMethod";
			return answer;
		}
	}

	public static long makeMoney(String value) {
		if (value == null) return 0;
		if (value.equals("-") || value.equals("")) return 0;
		int iPoint = value.lastIndexOf(".");
		int iComma = value.lastIndexOf(",");
		//System.out.print("iPoint=" + iPoint + "; iComma=" + iComma + " ==> " + value + " --> ");
		value = value.trim();
		if (iPoint > iComma) {
			value = value.replaceAll(",", "");
		} else if (iPoint < iComma){
			value = value.replaceAll("\\." , "");
			value = value.replaceAll(",", "\\.");
		}
		//System.out.println(value);
		long answer;
		try {
			System.out.println("value before parse: " + value);
			Double tmp = Double.parseDouble(value) * 100;
			System.out.println("value after parse: " + tmp);
			answer = Math.round(tmp);
			System.out.println("value after int: " + answer);
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can not parse to float string " + value);
			return 0;
		}
	}

	public static Float makeFloat(String value) {
		if (value == null) return 0f;
		if (value.equals("-")) return 0f;
		int iPoint = value.lastIndexOf(".");
		int iComma = value.lastIndexOf(",");
		//System.out.print("iPoint=" + iPoint + "; iComma=" + iComma + " ==> " + value + " --> ");
		value = value.trim();
		if (iPoint > iComma) {
			value = value.replaceAll(",", "");
		} else {
			value = value.replaceAll("\\." , "");
			value = value.replaceAll(",", "\\.");
		}
		//System.out.println(value);
		float answer;
		try {
			answer = Float.parseFloat(value);
			return answer;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can not parse to float string " + value);
			return 0f;
		}
	}

	public static String[] extractValuesFromTable(JTable table) {
		int selectedRow = table.getSelectedRow();
		TableModel model = table.getModel();
		String[] args = new String[model.getColumnCount()];
		for (int i = 0; i < model.getColumnCount(); i++) {
			args[i] = "" + model.getValueAt(selectedRow, i);
		}
		return args;
	}

	public static NumberFormat getNumberFormat() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf;
    }
}

