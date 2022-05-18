package database;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

public class SqlMap {

    private HashMap<String,ArrayList<String>> map;
    private HashMap<String,String> info;

    public SqlMap() {

    }

    public SqlMap(ResultSet rs) {
        try {
            if (!rs.isBeforeFirst()) {
                DefaultTableModel model = new DefaultTableModel();
                model.setRowCount(0);
                map = null;
            } else {
                ResultSetMetaData metaData = rs.getMetaData();
                int numberOfColumns = metaData.getColumnCount();
                info = new HashMap<>();
                info.put("size", String.valueOf(numberOfColumns));
                String[] columnNames = new String[numberOfColumns];

                map = new HashMap<String, ArrayList<String>>();
                for (int column = 0; column < numberOfColumns; column++) {
                    columnNames[column] = metaData.getColumnLabel(column + 1);
                    map.put(columnNames[column], new ArrayList<String>());
                }

                while (rs.next()) {
                    //ArrayList newRow = new ArrayList();
                    for (int i = 1; i <= numberOfColumns; i++) {
                        map.get(columnNames[i-1]).add(rs.getString(i));
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,ArrayList<String>> getMap() {
        return map;
    }

}
