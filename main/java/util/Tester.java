package util;

import database.Database;
import database.SqlMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tester {

    public static void main(String[] args) {
        test4();
    }

    private static void test1() {

        String id = "@id";
        String ratingAg = "@ratingAg";

        String script = "SELECT \r\n" +
                "  dbo.ctl_Guarantees.ID,\r\n" +
                "  ctl_Counterparties1.Name Guarant,\r\n" +
                "  dbo.ctl_Guarantees.GuaranteeNumber Number,\r\n" + //новый код
                "  dbo.ctl_Currencies.Name Currency,\r\n" +
                "  CAST(dbo.ctl_Guarantees.Amount as DECIMAL (15, 2)) Amount,\r\n" +
                "  dbo.ctl_GuaranteeTypes.Name Type,\r\n" +
                "  dbo.ctl_Guarantees.StartDate,\r\n" +
                "  dbo.ctl_Guarantees.EndDate\r\n" +
                "FROM\r\n" +
                "  dbo.ctl_Guarantees\r\n" +
                "  INNER JOIN dbo.ctl_Counterparties ON (dbo.ctl_Guarantees.CounterpartyID = dbo.ctl_Counterparties.ID)\r\n" +
                "  INNER JOIN dbo.ctl_Counterparties ctl_Counterparties1 ON (dbo.ctl_Guarantees.GuarantorID = ctl_Counterparties1.ID)\r\n" +
                "  LEFT OUTER JOIN dbo.ctl_Currencies ON (dbo.ctl_Guarantees.CurrencyID = dbo.ctl_Currencies.ID)\r\n" +
                "  INNER JOIN dbo.ctl_GuaranteeTypes ON (dbo.ctl_Guarantees.TypeID = dbo.ctl_GuaranteeTypes.ID)\r\n" +
                "WHERE\r\n" +
                "  dbo.ctl_Guarantees.ID = " + id + "\r\n"+
                "ORDER BY \r\n" +
                "dbo.ctl_Guarantees.EndDate DESC";
                System.out.println(script);
    }

    private static void test2() {
        String testdate;
        String date;

        testdate = "20180322";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "2018.03.22";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "2018-03-22";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "2018/03/22";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "22032018";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "22/03/2018";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "22.03.2018";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "22-03-2018";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "03222018";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);

        testdate = "20182203";
        date = DateHelper.createDate(testdate);
        System.out.println("результат для '" + testdate + "': " + date);
    }

    private static void test3() {
        Database db = Database.getInstance();
        String script = "SELECT * FROM dbo.ctl_Counterparties ORDER BY ID";

        System.out.println(script);

        Statement sta = null;
        try {
            sta = db.conn.createStatement();
            ResultSet rs = sta.executeQuery(script);

            SqlMap sqlmap = new SqlMap(rs);

            HashMap<String, ArrayList<String>> map = sqlmap.getMap();

            for (Map.Entry tmp : map.entrySet()) {
                System.out.println(tmp.getKey());
                ArrayList<String> x = (ArrayList<String>) tmp.getValue();
                for (String xvalue : x) {
                    System.out.print(xvalue + ";");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void test4() {

    }

}
