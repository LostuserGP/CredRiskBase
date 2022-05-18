package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ParserSQL {
    private String fileName;
    private HashMap<String, SqlValue> values;
    private final String path = "/database/QuerySQL/";

    private ParserSQL() {}

    private ParserSQL(String fileName, HashMap<String, SqlValue>values) {
        this.fileName = fileName;
        this.values = values;
    }

    private ParserSQL(String fileName, String name, String value) {
        this.fileName = fileName;
        HashMap<String, SqlValue> temp = new HashMap<String, SqlValue>();
        SqlValue sqlValue = new SqlValue(name, value);
        temp.put(sqlValue.getName(), sqlValue);
        this.values = temp;
    }

    private ParserSQL(String fileName, String name, int value) {
        this.fileName = fileName;
        HashMap<String, SqlValue> temp = new HashMap<String, SqlValue>();
        SqlValue sqlValue = new SqlValue(name, value);
        temp.put(sqlValue.getName(), sqlValue);
        this.values = temp;
    }

    public static String parse(String fileName, String name, String value) {
        return (new ParserSQL(fileName, name, value)).parse();
    }

    public static String parse(String fileName, String name, int value) {
        return (new ParserSQL(fileName, name, value)).parse();
    }

    public static String parse(String fileName, HashMap<String, SqlValue>values) {
        return (new ParserSQL(fileName, values)).parse();
    }

    private String readBuffered() throws IOException {
        BufferedReader txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path + fileName)));
        String line;
        String answer = "";
        while((line = txtReader.readLine()) != null){
            answer = answer + " " + line;
        }
        txtReader.close();
        answer = answer.replaceAll("\\s+", " ");
        return answer;
    }

    public static String read(String fileName) throws IOException {
        ParserSQL parser = new ParserSQL();
        parser.fileName = fileName;
        return parser.readBuffered();
    }

    private String parse() {
        String result = "";
        try {
            String sqlQuery = readBuffered();
            sqlQuery = sqlQuery.replaceAll("\r\n", " ");
            String sqlArray[] = sqlQuery.split(" ");
            for (int i = 0; i < sqlArray.length; i++) {
                if (sqlArray[i].toUpperCase().equals("SET")) {
                    String name = (sqlArray[i+1]);
                    result = result + " SET " + name + " = " +  values.get(name).getValueSQL() + ";";
                    i = i + 3;
                } else {
                    if (i > 0) {
                        result = result + " " + sqlArray[i];
                    } else {
                        result = sqlArray[i];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
