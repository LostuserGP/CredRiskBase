package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Statrs {

    private ResultSet rs;
    private Statement sta;
    private PreparedStatement pst;

    public Statrs(Statement sta, ResultSet rs) {
        this.sta = sta;
        this.rs = rs;
    }

    public Statrs(PreparedStatement pst, ResultSet rs) {
        this.pst = pst;
        this.rs = rs;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void close(){
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (sta != null && !sta.isClosed()) {
                sta.close();
            }
            if (pst != null && !pst.isClosed()) {
                pst.close();
            }
        } catch (SQLException e) {
            System.out.println("Can't close ResultSet or Statement");
            e.printStackTrace();

        }
    }

}
