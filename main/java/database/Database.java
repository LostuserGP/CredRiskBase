package database;

import GuarantiesTool.GuaranteeEntity;
import gui.DebugGUI;
import util.DateHelper;
import util.Helper;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Database {
	
	public Connection conn = null;
	private String user;
	private Logger log = Logger.getLogger(Database.class.getName());
	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH-mm-ss";
	private static File dir = new File("P:\\ООиКР\\Кредитные риски\\Отчет о величине кредитных рисков\\Новый шаблон Java");
	private boolean isTestServer;

	//public static final String

	private static Database ourInstance = new Database();

	public static Database getInstance() {
		return ourInstance;
	}

	public File getDir() {
		return dir;
	}

	private void findZone() {
		try {
            System.out.println("Trying connect to production server");
			String connectionUrl = "jdbc:jtds:sqlserver://DBS/RM_ACCOUNTS;USENTLMV2=true";
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			this.conn = DriverManager.getConnection(connectionUrl);//here put the new simple url.
			this.user = conn.getMetaData().getUserName();
			isTestServer = false;
            System.out.println("Connection to production server was established");
			DebugGUI.getInstance("Подключились к рабочему серверу");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Проблема с драйвером SQL");
			DebugGUI.getInstance("Проблема с драйвером SQL");
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			try {
                System.out.println("Trying connect to development server");
				String connectionUrl = "jdbc:jtds:sqlserver://SQL/RM_ACCOUNTS;USENTLMV2=true";
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
//				String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=RM_ACCOUNTS;integratedSecurity=true";
//				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				this.conn = DriverManager.getConnection(connectionUrl);//here put the new simple url.
				this.user = conn.getMetaData().getUserName();
				isTestServer = true;
				System.out.println("Connection to development server was established");
				DebugGUI.getInstance("Подключились к тестовому серверу");
				//e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean getZone() {
		return isTestServer;
	}

	private Database() {
		findZone();
		String surname = user.substring(10);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String date = sdf.format(Calendar.getInstance().getTime());
		String filename = "logs\\" + surname + " " + date   + ".log";
		File f = new File(filename);
		f.getParentFile().mkdirs();
		FileHandler fh;
		try {
			fh = new FileHandler(filename);
			log.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			log.info(user + " has logged in");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		System.out.println("try to close connection to database");
		try {
			if (this.conn != null && !this.conn.isClosed()) {
				this.conn.close();
				System.out.println("connection to database have been closed");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String createDate(Date date) {
//		Calendar cal = Calendar.getInstance();
//	    cal.setTime(date);
//	    int year = cal.get(Calendar.YEAR);
//	    int month = cal.get(Calendar.MONTH) + 1;
//	    int day = cal.get(Calendar.DAY_OF_MONTH);
//	    String sqlDate = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
	    String sqlDate = DateHelper.createDate(date);
		DebugGUI.getInstance("(createDate) Использована дата: " + sqlDate);
	    return "'" + sqlDate + "'";
	}
	
	private String createDate(String date) {
//		if (date.equals("null")) return "null";
//		String[] split = date.split("-");
//		int year = Integer.parseInt(split[0]);
//	    int month = Integer.parseInt(split[1]);
//	    int day = Integer.parseInt(split[2]);
//	    String sqlDate = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
	    String sqlDate = DateHelper.createDate(date);
		DebugGUI.getInstance("(createDate) Использована дата: " + sqlDate);
	    return "'" + sqlDate + "'";
	}
 
	private Statrs runSelect(String sql) throws SQLException {
        Helper.debug(sql);
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery(sql);
        return new Statrs(sta, rs);
	}
	
	private int runUpdate(String sql) throws Exception {
        Helper.debug(sql);
		System.out.println("Execute script: \n" + sql + "\n");
		Statement sta = conn.createStatement();
		int result = sta.executeUpdate(sql);
		sta.close();
		return result;
	}

	public Statrs getCommitteeResults() throws Exception {
		String script = ParserSQL.read("getCommitteeResults");
		PreparedStatement stmt = conn.prepareStatement(script);
		Helper.debug(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public Statrs getActiveRatings() throws Exception {
		String script = ParserSQL.read("getActiveRatings");
        Helper.debug(script);
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery(script);
		return new Statrs(sta, rs);
	}

	public Statrs getInternalRatings() throws Exception {
		String script = ParserSQL.read("getInternalRatings");
		PreparedStatement stmt = conn.prepareStatement(script);
        Helper.debug(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getSRK() throws Exception {
		String script = ParserSQL.read("getSRK");
		PreparedStatement stmt = conn.prepareStatement(script);
        Helper.debug(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getExternalRatings() throws Exception {
        String script = ParserSQL.read("getExternalRatings");
        PreparedStatement stmt = conn.prepareStatement(script);
        Helper.debug(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getFinancialStatements() throws Exception {
		String script = ParserSQL.read("getFinancialStatements");
        Helper.debug(script);
		PreparedStatement stmt = conn.prepareStatement(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public Statrs getCountryRatings() throws Exception {
        String script = ParserSQL.read("getCountryRatings");
        PreparedStatement stmt = conn.prepareStatement(script);
        Helper.debug(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public Statrs getGuarantees() throws Exception {
		String script = ParserSQL.read("getGuarantees");
		PreparedStatement stmt = conn.prepareStatement(script);
        Helper.debug(script);
        LocalDate ld = LocalDate.now();
        stmt.setString(1, ld.toString());
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public Statrs getWatchList() throws Exception {
		String script = ParserSQL.read("getWatchList");
        Helper.debug(script);
		PreparedStatement stmt = conn.prepareStatement(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getCountries() throws SQLException {
		String script = "SELECT Name FROM dbo.ctl_Countries";
		return runSelect(script);
	}

	public Statrs getSectors() throws Exception {
		String script = "SELECT SectorName FROM dbo.ctl_Sectors";
		return runSelect(script);
	}
	
	private int getRiskClassIDbyValue(String riskClass) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_RiskClasses WHERE Name = '" + riskClass + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	private int getRatingIDbyValue(String ratingValue) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_RatingValues WHERE Name = '" + ratingValue + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public int getSectorIDbyName(String sectorName) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_Sectors WHERE SectorName = '" + sectorName + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = 8; //in case of some problems returns Energy
		if (rs.next()) result =  rs.getInt("ID");
		srs.close();
		return result;
	}

	public int getCountryIDbyName(String countryName) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_Countries WHERE Name = '" + countryName + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}
	
	public int getCounterpartyIDbyName(String name) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_Counterparties WHERE Name = '" + name + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result =  rs.getInt("ID");
		srs.close();
		return result;
	}
	
	private String getCounterpartyNameByID(int id) throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Counterparties WHERE ID = '" + id + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		String answer = "";
		if (rs.next()) answer =  rs.getString("Name");
		srs.close();
		return answer;
	}
	
	public ArrayList<String> searchForCounterparties(String request) throws Exception {
		String script = "SELECT * FROM dbo.ctl_Counterparties WHERE Name LIKE '%" + request + "%'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		ArrayList<String> values = new ArrayList<String>();
		while (rs.next()) {
			values.add(rs.getString("Name"));
		}
		srs.close();
		return values;
	}

	public Statrs getInternalRatingsByID(int id) throws Exception {
		String script = ParserSQL.read("getInternalRatingsByID");
        Helper.debug(script, String.valueOf(id));
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getIndividualLimitsByID(int id) throws Exception {
		String script = ParserSQL.read("getIndividualLimits.sql");
		Helper.debug(script, String.valueOf(id));
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public Statrs getExternalRatingsByID(int id) throws Exception {
		String script = ParserSQL.read("getExternalRatingsByID");
        Helper.debug(script, String.valueOf(id));
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getExternalRatingsCountry(int countryId) throws Exception {
		String script = ParserSQL.read("getExternalRatingsCountry");
        Helper.debug(script, String.valueOf(countryId));
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, countryId);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	private int getFSIdByCounterpartyIdFSDateAndFSStandards(int counterpartyID,
			String fsDate, String fsStandards) throws Exception {

		String script = ParserSQL.read("getFSIdByCounterpartyIdFSDateAndFSStandards");
        Helper.debug(script, String.valueOf(counterpartyID), DateHelper.createDate(fsDate, ".", false), fsStandards);
		String sqlDate = DateHelper.createDate(fsDate, "-");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, counterpartyID);
		//stmt.setDate(2, DateHelper.createSqlDate3(fsDate));
		if (sqlDate == null) {
			stmt.setNull(2, java.sql.Types.DATE);
		} else {
			stmt.setDate(2, java.sql.Date.valueOf(sqlDate));
		}
		if (fsStandards == null) {
			stmt.setNull(3, Types.NVARCHAR);
		} else {
			stmt.setString(3, fsStandards);
		}
		ResultSet rs = stmt.executeQuery();
		int result = -1;
		if (rs.next()) result =  rs.getInt("ID");
		rs.close();
		stmt.close();
		return result;
	}

	public String getCountryNameById(int countryId) throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Countries WHERE Id = " + countryId;
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		String answer = "";
		if (rs.next()) answer = rs.getString("Name");
		srs.close();
		return answer;
	}

	public String getSectorNameById(int id) throws Exception {
		String script = "SELECT SectorName FROM dbo.ctl_Sectors WHERE ID = " + id;
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		String answer = "";
		if (rs.next()) answer = rs.getString("SectorName");
		srs.close();
		return answer;
	}

	public Statrs getCounterparties() throws Exception {
		String script = ParserSQL.read("getCounterparties");
		PreparedStatement pst = conn.prepareStatement(script);
		ResultSet rs = pst.executeQuery();
		return new Statrs(pst, rs);
	}

	public Statrs getRatingValues() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_RatingValues ORDER BY ID";
		return runSelect(script);
	}

	public Statrs getRiskClasses() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_RiskClasses";
		return runSelect(script);
	}
	
	public Statrs getFSbyCounterpartyId(int counterpartyID) throws Exception {
		String script = "SELECT ID, Date, Standards FROM dbo.ctl_FinancialStatements WHERE CounterpartyID = " + counterpartyID +
				" ORDER BY Date DESC";
		return runSelect(script);
	}

	public Statrs getStandsrds() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Standards";
		return runSelect(script);
	}
	
	public Statrs getRatingAgencies() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_RatingAgencies";
		return runSelect(script);
	}

	private int getRatingAgencyIDbyValue(String ratingAgency) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_RatingAgencies WHERE Name = '" + ratingAgency + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public Statrs getCommitteeStatuses() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_CommitteeStatuses";
		return runSelect(script);
	}

	public Statrs getCommitteeLimitations() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_CommitteeLimitations";
		return runSelect(script);
	}

	public Statrs getCounterpartyGroups() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_CounterpartyGroup";
		return runSelect(script);
	}

	private int getGroupIDByName(String group) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_CounterpartyGroup WHERE Name = '" + group + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public int getPortfolioIDbyName(String portfolio) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_Portfolios WHERE Name = '" + portfolio + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	private int getLimitationsIDByName(String limitations) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_CommitteeLimitations WHERE Name = '" + limitations + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	private int getStatusIDByName(String status) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_CommitteeStatuses WHERE Name = '" + status + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public Statrs getCurrencies() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Currencies";
		return runSelect(script);
	}

	public Statrs getGuaranteeTypes() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_GuaranteeTypes";
		return runSelect(script);
	}
	
	public Statrs getLatestExternalRatingByTicker(String ticker, String ratingAgency) throws Exception {
		String script = ParserSQL.read("getLatestExternalRatingByTicker");
		int ra = getRatingAgencyIDbyValue(ratingAgency);
		Helper.debug(script, ticker, ratingAgency);
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setString(1, ticker);
		stmt.setInt(2, ra);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public Statrs getLatestCountryRatingByTicker(String ticker, String ratingAgency) throws Exception {
        String script = ParserSQL.read("getLatestCountryRatingByTicker");
		int ra = getRatingAgencyIDbyValue(ratingAgency);
		Helper.debug(script, ticker, ratingAgency);
        PreparedStatement stmt = conn.prepareStatement(script);
        stmt.setString(1, ticker);
		stmt.setInt(2, ra);
		ResultSet rs = stmt.executeQuery();
        return new Statrs(stmt, rs);
	}

	private int getGuaranteeTypeIDbyName(String guaranteeType) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_GuaranteeTypes WHERE Name = '" + guaranteeType + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public int getCurrencyIDbyName(String currency) throws Exception {
		String script = "SELECT ID FROM dbo.ctl_Currencies WHERE Name = '" + currency + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public Statrs getPortfolios() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Portfolios";
		return runSelect(script);
	}

	public String getRatingValueByID(int id) throws Exception {
		String script = "SELECT TOP 1 Name FROM dbo.ctl_RatingValues WHERE ID = " + id;
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		String answer = "";
		if (rs.next()) answer =  rs.getString("Name");
		srs.close();
        return answer;
	}

	public int getRatingIdByValue(String value) throws Exception {
		String script = "SELECT TOP 1 ID FROM dbo.ctl_RatingValues WHERE Name = '" + value + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		int result = -1;
		if (rs.next()) result = rs.getInt("ID");
		srs.close();
		return result;
	}

	public ArrayList<String> getCounterpartiesByTicker(String ticker) throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Counterparties WHERE BloombergTicker = '" + ticker + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		ArrayList<String> toReturn = new ArrayList<String>();
		while (rs.next()) toReturn.add(rs.getString("Name"));
		srs.close();
		return toReturn;
	}
	
	public ArrayList<String> getCountriesByTicker(String ticker) throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Countries WHERE BloombergTicker = '" + ticker + "'";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		ArrayList<String> toReturn = new ArrayList<String>();
		while (rs.next()) toReturn.add(rs.getString("Name"));
		srs.close();
		return toReturn;
	}

	public Statrs getCommittees(int id) throws Exception {
		String script = ParserSQL.read("getCommittees");
		Helper.debug(script, String.valueOf(id));
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getSRKList() throws Exception {
		String script = ParserSQL.read("getSRKList");
		PreparedStatement stmt = conn.prepareStatement(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getSrkForExcel() throws Exception {
		String script = ParserSQL.read("getSrkForExcel.sql");
		PreparedStatement stmt = conn.prepareStatement(script);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public Statrs getGuarantees(int id) throws Exception {
		String script = "SELECT \r\n" + "  dbo.ctl_Guarantees.ID,\r\n" + "  ctl_Counterparties1.Name Guarant,\r\n" +
					"  dbo.ctl_Guarantees.GuaranteeNumber Number,\r\n" + "  dbo.ctl_Currencies.Name Currency,\r\n" +
					"  CAST(dbo.ctl_Guarantees.Amount as DECIMAL (15, 2)) Amount,\r\n" +
					"  dbo.ctl_GuaranteeTypes.Name Type,\r\n" + "  dbo.ctl_Guarantees.StartDate,\r\n" +
					"  dbo.ctl_Guarantees.EndDate\r\n" + "FROM\r\n" + "  dbo.ctl_Guarantees\r\n" +
					"  INNER JOIN dbo.ctl_Counterparties ON (dbo.ctl_Guarantees.CounterpartyID = dbo.ctl_Counterparties.ID)\r\n" +
					"  INNER JOIN dbo.ctl_Counterparties ctl_Counterparties1 ON (dbo.ctl_Guarantees.GuarantorID = ctl_Counterparties1.ID)\r\n" +
					"  LEFT OUTER JOIN dbo.ctl_Currencies ON (dbo.ctl_Guarantees.CurrencyID = dbo.ctl_Currencies.ID)\r\n" +
					"  INNER JOIN dbo.ctl_GuaranteeTypes ON (dbo.ctl_Guarantees.TypeID = dbo.ctl_GuaranteeTypes.ID)\r\n" +
					"WHERE\r\n" + "  dbo.ctl_Guarantees.CounterpartyID = " + id + "\r\n" + "ORDER BY \r\n" +
					"dbo.ctl_Guarantees.EndDate DESC";
		return runSelect(script);
	}

	public Statrs getGuarantyByID(int id) throws Exception {
		String script = ParserSQL.read("getGuarantyByID");
		Helper.debug(script, String.valueOf(id));
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}
	
	public int createCommittee(String status, String limitations,
			String group, LocalDate date, String counterparty, String comments) throws Exception {
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_Committees(\r\n" + 
				"  StatusID,\r\n" + 
				"  LimitationsID,\r\n" + 
				"  StartDate,\r\n" + 
				"  GroupID,\r\n" + 
				"  Comments,\r\n" + 
				"  CounterpartyID)\r\n" + 
				"VALUES(\r\n" + 
					getStatusIDByName(status) + " ,\r\n" +
					(limitations.equals("") ? "NULL" : getLimitationsIDByName(limitations)) + " ,\r\n" +
					"'" + date + "' ,\r\n" +
					getGroupIDByName(group) + " ,\r\n" +
					"'" + comments + "' ,\r\n" +
					"'" + getCounterpartyIDbyName(counterparty) + "') \r\n";  

		int result = runUpdate(script);
		
		log.info(user + " has created Committee:" +
		counterparty + ",\r\n" +
		status + ",\r\n" +
		limitations + ",\r\n" +
		group + ",\r\n" +
		date + "\r\n");
		
		return result;
	}
	
	public int createFS(String counterparty, LocalDate date, String standards, String comments) throws Exception {
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_FinancialStatements(\r\n" + 
				"  CounterpartyID,\r\n" + 
				"  Standards,\r\n" + 
				"  Date,\r\n" + 
				"  Comments) \r\n" + 
				"VALUES(\r\n" + 
					"'" + getCounterpartyIDbyName(counterparty) + "' ,\r\n" +
					"'" + standards  + "' ,\r\n" +
					"'" + date + "' ,\r\n" +
					(comments.equals("") ? null : "'" + comments + "'") + " )\r\n";

		int result = runUpdate(script);
		
		log.info(user + " has created FS:\r\n" + 
		counterparty + ",\r\n" +
		standards + ",\r\n" +
		date + ",\r\n" +
		comments + "\r\n");
		
		return result;
	}

	public int deleteRate(String from, String to, LocalDate reportDate, float rate) throws Exception {
		String script = "DELETE FROM \r\n" +
				"  dbo.exchange_rates\r\n" +
				"  WHERE dbo.exchange_rates.from_currency = '" + from + "'\r\n" +
				"  AND dbo.exchange_rates.to_currency = '" + to + "'\r\n" +
				"  AND dbo.exchange_rates.report_date = '" + reportDate.toString() + "'";

		int result = runUpdate(script);
		script = "DELETE FROM \r\n" +
				"  dbo.exchange_rates\r\n" +
				"  WHERE dbo.exchange_rates.from_currency = '" + to + "'\r\n" +
				"  AND dbo.exchange_rates.to_currency = '" + from + "'\r\n" +
				"  AND dbo.exchange_rates.report_date = '" + reportDate.toString() + "'";

		result = runUpdate(script);
		return result;
	}

	public int createRate(String from, String to, LocalDate reportDate, float rate) throws Exception {
		this.deleteRate(from, to, reportDate, rate);
		String script =
				"INSERT INTO\r\n" +
						"  dbo.exchange_rates(\r\n" +
						"  from_currency,\r\n" +
						"  to_currency,\r\n" +
						"  report_date,\r\n" +
						"  rate) \r\n" +
						"VALUES(\r\n" +
						"'" + from + "' ,\r\n" +
						"'" + to  + "' ,\r\n" +
						"'" + reportDate.toString() + "' ,\r\n" +
						"'" + rate + "') \r\n";
		int result = runUpdate(script);
		script =
				"INSERT INTO\r\n" +
						"  dbo.exchange_rates(\r\n" +
						"  from_currency,\r\n" +
						"  to_currency,\r\n" +
						"  report_date,\r\n" +
						"  rate) \r\n" +
						"VALUES(\r\n" +
						"'" + to + "' ,\r\n" +
						"'" + from  + "' ,\r\n" +
						"'" + reportDate.toString() + "' ,\r\n" +
						"'" + 1/rate + "') \r\n";
		result = runUpdate(script);

		log.info(user + " has created rate:\r\n" +
				from + ",\r\n" +
				to + ",\r\n" +
				reportDate + ",\r\n" +
				rate + "\r\n");

		return result;
	}

	public int createExternalRatingCounterparty(String ratingValue, String ratingAgency,
			LocalDate date, String counterparty) throws Exception {
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_ExternalRatingCounterparty(\r\n" + 
				"  RatingValueID,\r\n" + 
				"  RatingAgencyID,\r\n" + 
				"  StartDate,\r\n" + 
				"  CounterpartyID)\r\n" + 
				"VALUES(\r\n" + 
					getRatingIDbyValue(ratingValue) + " ,\r\n" +
					getRatingAgencyIDbyValue(ratingAgency) + " ,\r\n" +
					"'" + date.toString() + "' ,\r\n" +
					getCounterpartyIDbyName(counterparty) + ") \r\n";

		int result = runUpdate(script);
		
		log.info(user + " has created an external rating:\r\n" +
				counterparty + ",\r\n" +
				ratingValue + ",\r\n" +
				ratingAgency  + ",\r\n" +
				date.toString() + "\r\n");
		
		return result;
	}
	
	public int createCountryRating(String ratingValue, String ratingAgency,
			LocalDate date, String country) throws Exception {
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_RatingCountry(\r\n" + 
				"  RatingValueID,\r\n" + 
				"  RatingAgencyID,\r\n" + 
				"  StartDate,\r\n" + 
				"  CountryID)\r\n" + 
				"VALUES(\r\n" + 
					getRatingIDbyValue(ratingValue) + " ,\r\n" +
					getRatingAgencyIDbyValue(ratingAgency) + " ,\r\n" +
					"'" + date.toString() + "' ,\r\n" +
					getCountryIDbyName(country) + ") \r\n";  

		int result = runUpdate(script);
		
		log.info(user + " has created an external rating:\r\n" +
				country + ",\r\n" +
				ratingValue + ",\r\n" +
				ratingAgency  + ",\r\n" +
				date + "\r\n");
		
		return result;
		
	}
	
	public void createCounterparty(String name, String shortName, String bloombergTicker, int sectorID,
								  int portfolioID, int countryDomID, int countryRiskID, String isIntragroup,
								  String toBeMonitored, LocalDate date, String comments, String longterm, String etp, String efet, String gtc,
								  String isSRK, String Duns, int SubsidiaryLimit, String ukz, String Causes) throws Exception {
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_Counterparties(\r\n" + 
				"  Name,\r\n" + 
				"  ShortName,\r\n" + 
				"  BloombergTicker,\r\n" + 
				"  PortfolioID,\r\n" + 
				"  SectorID,\r\n" + 
				"  CountryOfDomicileID,\r\n" + 
				"  CountryOfRiskID,\r\n" + 
				"  intraGroup,\r\n" + 
				"  ToBeMonitored,\r\n" + 
				"  startDate,\r\n" +
						"  Longterm,\r\n" +
						"  Etp,\r\n" +
						"  Efet,\r\n" +
						"  Gtc,\r\n" +
						"  Duns,\r\n" +
						"  Causes,\r\n" +
						"  isSRK,\r\n" +
						"  SubsidiaryLimit,\r\n" +
						"  UKZ,\r\n" +
				"  Comments)\r\n" +
				"VALUES(\r\n" + 
					name + " ,\r\n" +
					shortName + " ,\r\n" +
					bloombergTicker + " ,\r\n" +
					portfolioID + " ,\r\n" +
					sectorID + " ,\r\n" +
						countryDomID + " ,\r\n" +
					countryRiskID + " ,\r\n" +  
					isIntragroup + " ,\r\n" +  
					toBeMonitored + " ,\r\n" +  
					"'" + date.toString() + "' ,\r\n" +
						longterm + " ,\r\n" +
						etp + " ,\r\n" +
						efet + " ,\r\n" +
						gtc + " ,\r\n" +
						Duns + " ,\r\n" +
						Causes + " ,\r\n" +
						isSRK + " ,\r\n" +
						SubsidiaryLimit + " ,\r\n" +
						ukz + " ,\r\n" +
						comments + " )";

		System.out.println(script);

		int result = runUpdate(script);
		//runScript(script);
		
		log.info(user + " has created a counterparty:\r\n" + 
				name + ",\r\n" +
				shortName + ",\r\n" +
				bloombergTicker + ",\r\n" +
				sectorID + ",\r\n" +
				countryDomID + ",\r\n" +
				countryRiskID + ",\r\n" +  
				isIntragroup + ",\r\n" +  
				date.toString() + ",\r\n" +
				longterm + " ,\r\n" +
				etp + " ,\r\n" +
				efet + " ,\r\n" +
				gtc + " ,\r\n" +
				Duns + " ,\r\n" +
				Causes + " ,\r\n" +
				isSRK + " ,\r\n" +
				SubsidiaryLimit + " ,\r\n" +
				comments + "\n");
	}
	
	public int createInternalRating(String ratingValue, String ratingValueWithoutCountry, String riskClass,
									boolean isConservative, String fs, LocalDate date, String counterparty,
									String analyst, String comments) throws Exception {
		
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_InternalRatingCounterparty(\r\n" + 
				"  RatingValueID,\r\n" + 
				"  RatingValueWithoutCountryID,\r\n" + 
				"  RiskClassID,\r\n" + 
				"  IsConservative,\r\n" + 
				"  FinancialStatementID,\r\n" + 
				"  StartDate,\r\n" + 
				"  CounterpartyID,\r\n" + 
				"  Comments,\r\n" + 
				"  Analyst)\r\n" + 
				"VALUES(\r\n" + 
					getRatingIDbyValue(ratingValue) + " ,\r\n" +
					(ratingValueWithoutCountry.equals("") ? null : getRatingIDbyValue(ratingValueWithoutCountry))  + " ,\r\n" +
					(riskClass.equals("") ? null : getRiskClassIDbyValue(riskClass)) + " ,\r\n" +
					(isConservative ? "'true'" : "'false'") + " ,\r\n" + 
					(fs.equals("") ? null : Integer.parseInt(fs.split("\\|")[0].replaceAll("\\s",""))) + " ,\r\n" +  
					"'" + date + "' ,\r\n" +
					getCounterpartyIDbyName(counterparty) + " ,\r\n" +  
					"'" +comments + "' ,\r\n" +  
					"'" +analyst + "') \r\n";  

		int result = runUpdate(script);
		
		log.info(user + " has created an internal rating:\r\n" +
				counterparty + ",\r\n" +
				ratingValue + ",\r\n" +
				ratingValueWithoutCountry  + ",\r\n" +
				riskClass + ",\r\n" +
				isConservative + ",\r\n" + 
				fs + ",\r\n" +  
				date + ",\r\n" +
				analyst + "\n");
		
		return result;
	}

	public int createGuarantee(String counterparty, String guarantor,
							   String guaranteeType, String currency, long l,
							   LocalDate fromDate, LocalDate toDate, String guaranteeNumber) throws Exception {
	    String script = "INSERT INTO dbo.ctl_Guarantees \r\n" + "(CounterpartyID,\r\n" + "GuarantorID,\r\n" +
                    "CurrencyID,\r\n" + "TypeID,\r\n" + "Amount,\r\n" + "StartDate,\r\n" + "EndDate,\r\n" +
                    "GuaranteeNumber)\r\n" + "VALUES \r\n" + "(" + getCounterpartyIDbyName(counterparty) + ",\r\n" +
                    getCounterpartyIDbyName(guarantor) + ",\r\n" + getCurrencyIDbyName(currency) + ",\r\n" +
                    getGuaranteeTypeIDbyName(guaranteeType) + ",\r\n" + l + ",\r\n'" + fromDate + "',\r\n'" +
                    toDate + "',\r\n" + "'" + guaranteeNumber + "')";

		int result = runUpdate(script);

		log.info(user + " has added Guarantee:" +
				counterparty + ",\r\n" +
				guarantor + ",\r\n" +
				guaranteeType + ",\r\n" +
				currency + ",\r\n" +
				l + ",\r\n" +
				fromDate + ",\r\n" +
				toDate + "\r\n");

		return result;
	}
	
	public int deleteCounterparty(int id) throws Exception {

		String script1 = "DELETE FROM ctl_InternalRatingCounterparty WHERE CounterpartyID = " + id;
		String script2 = "DELETE FROM ctl_ExternalRatingCounterparty WHERE CounterpartyID = " + id;
		String script3 = "DELETE FROM ctl_FinancialStatements WHERE CounterpartyID = " + id;
		String script4 = "DELETE FROM ctl_Guarantees WHERE CounterpartyID = " + id;
		String script5 = "DELETE FROM dbo.ctl_Committees WHERE CounterpartyID = " + id;
//		String script6 = "DELETE FROM ctl_GuaranteesLimits WHERE CounterpartyID = " + id;
		String script = "DELETE FROM dbo.ctl_Counterparties WHERE ID = " + id;

		runUpdate(script1);
		runUpdate(script2);
		runUpdate(script3);
		runUpdate(script4);
		runUpdate(script5);
//		runUpdate(script6);
		int result = runUpdate(script);

		log.info(user + " has deleted a counterparty:\r\n" + 
				getCounterpartyNameByID(id) + ",\r\n" +
				"ID = " + id);
				
		return result;
	}

	public int deleteInternalRating(int counterpartyID, String date, String ratingValue,
									String ratingValueWithoutCountry, String riskClass,
									String isConservative, String fsDate, String fsStandards,
									String analyst, String comments) throws Exception {
		
		int financialStatementID = getFSIdByCounterpartyIdFSDateAndFSStandards(counterpartyID, fsDate, fsStandards);
		int riskClassID = getRiskClassIDbyValue(riskClass);
        int RatingValueWithoutCountryID = getRatingIDbyValue(ratingValueWithoutCountry);
	
		String script = 
				"DELETE FROM\r\n" + 
				"  dbo.ctl_InternalRatingCounterparty\r\n" + 
				"WHERE\r\n" +
				"  RatingValueID = " + getRatingIDbyValue(ratingValue) + "\r\n" + 
				//"  AND RatingValueWithoutCountryID = " + getRatingIDbyValue(ratingValueWithoutCountry) + "\r\n" +
                "  AND RatingValueWithoutCountryID " + (RatingValueWithoutCountryID == -1 ? "IS NULL" : ("= " + RatingValueWithoutCountryID)) + "\r\n" +
				"  AND RiskClassID " + (riskClassID == -1 ? "IS NULL" : ("= " + riskClassID)) + "\r\n" +
				"  AND IsConservative = '" + isConservative + "'\r\n" + 
				"  AND FinancialStatementID " + (financialStatementID == -1 ? "IS NULL" : ("= " + financialStatementID)) + "\r\n" + 
				"  AND StartDate = " + createDate(date) + "\r\n" +
				"  AND CounterpartyID = " + counterpartyID + "\r\n" + 
				"  AND Comments "+ (comments.equals("null") ? "IS NULL" : ("= '" + comments + "'")) +
				"  AND Analyst "+ (analyst.equals("null") ? "IS NULL" : ("= '" + analyst + "'"));  

		int result = runUpdate(script);
		
		log.info(user + " has deleted an internal rating:\r\n" +
				getCounterpartyNameByID(counterpartyID) + ",\r\n" +
				ratingValue + ",\r\n" +
				riskClass + " ,\r\n" +
				isConservative + ",\r\n" + 
				fsDate + " | " + fsStandards + ",\r\n" +  
				createDate(date) + ",\r\n" +
				analyst + "\n");
		
		return result;
	}


	public int deleteExternalRating(int counterpartyID, String ratingAgency, String ratingValue,
			String date) throws Exception {
		
		String script = "DELETE FROM \r\n" + 
				"  dbo.ctl_ExternalRatingCounterparty\r\n" + 
				"  WHERE dbo.ctl_ExternalRatingCounterparty.CounterpartyID = " + counterpartyID + "\r\n" +
				"  AND dbo.ctl_ExternalRatingCounterparty.RatingAgencyID = " + getRatingAgencyIDbyValue(ratingAgency.replace("'", "''")) + "\r\n" +
				"  AND dbo.ctl_ExternalRatingCounterparty.RatingValueID = " + getRatingIDbyValue(ratingValue) + "\r\n" +
				"  AND dbo.ctl_ExternalRatingCounterparty.StartDate = " + createDate(date);

		int result = runUpdate(script);
		
		log.info(user + " has deleted an external rating:\r\n" +
				getCounterpartyNameByID(counterpartyID) + ",\r\n" +
				ratingValue + ",\r\n" +
				createDate(date) + "\n");
		
		return result;
		
	}


	public int deleteCommittee(int id) throws Exception {
		
		String script = "DELETE FROM \r\n" + 
				"  dbo.ctl_Committees\r\n" + 
				"  WHERE dbo.ctl_Committees.ID = " + id ;

		int result = runUpdate(script);
		
		log.info(user + " has deleted committee with ID = " + id + "\r\n");
		
		return result;
		
	}


	public int deleteFS(int id) throws Exception {
		String script = "DELETE FROM \r\n" + 
				"  dbo.ctl_FinancialStatements\r\n" + 
				"  WHERE dbo.ctl_FinancialStatements.ID = " + id ;

		int result = runUpdate(script);
		
		log.info(user + " has deleted FS with ID = " + id + "\r\n");
		
		return result;
		
	}


	public int deleteCountryRating(String country, String ratingAgency, String ratingValue,
			String date) throws Exception {
		String script = "DELETE FROM \r\n" + 
				"  dbo.ctl_RatingCountry\r\n" + 
				"  WHERE dbo.ctl_RatingCountry.CountryID = " + getCountryIDbyName(country) + "\r\n" +
				"  AND dbo.ctl_RatingCountry.RatingAgencyID = " + getRatingAgencyIDbyValue(ratingAgency.replace("'", "''")) + "\r\n" +
				"  AND dbo.ctl_RatingCountry.RatingValueID = " + getRatingIDbyValue(ratingValue) + "\r\n" +
				"  AND dbo.ctl_RatingCountry.StartDate = " + createDate(date);

		int result = runUpdate(script);
		
		log.info(user + " has deleted country rating:\r\n" +
				country + ",\r\n" +
				ratingValue + ",\r\n" +
				createDate(date) + "\n");
		
		return result;
		
	}


	public int deleteGuarantee(int id) throws Exception {
		
		String script = "DELETE FROM \r\n" + 
				"  dbo.ctl_Guarantees\r\n" + 
				"  WHERE dbo.ctl_Guarantees.ID = " + id ;

		int result = runUpdate(script);
		
		log.info(user + " has deleted Guarantee with ID = " + id + "\r\n");
		
		return result;
		
	}

	public int deleteIndividualLimit(int id) throws Exception {

		String script = "DELETE FROM \r\n" +
				"  dbo.ctl_IndividualLimits\r\n" +
				"  WHERE dbo.ctl_IndividualLimits.Id = " + id ;

		int result = runUpdate(script);

		log.info(user + " has deleted Individual Limit with Id = " + id + "\r\n");

		return result;

	}

	public Statrs getCounterpartyById(int id) throws Exception {
		String script = "SELECT\r\n" + 
				"counterparty.name as Name,\r\n" + 
				"counterparty.shortName as ShortName,\r\n" + 
				"counterpartyDonor.Name as Donor,\r\n" + 
				"countryOfDomicile.Name as CountryOfDomicile,\r\n" + 
				"countryOfRisk.Name as CountryOfRisk,\r\n" + 
				"Sector.SectorName as Sector,\r\n" + 
				"counterparty.BloombergTicker as BloombergTicker,\r\n" + 
				"portfolios.Name as Portfolio,\r\n" + 
				"counterparty.intraGroup as IntraGroup,\r\n" + 
				"counterparty.ToBeMonitored as ToBeMonitored,\r\n" +
				"counterparty.StartDate as StartDate,\r\n" +
				"counterparty.Comments as Comments,\r\n" +
				"counterparty.Longterm as Longterm,\r\n" +
				"counterparty.Etp as Etp,\r\n" +
				"counterparty.Efet as Efet,\r\n" +
				"counterparty.Gtc as Gtc,\r\n" +
				"counterparty.Duns as Duns,\r\n" +
				"counterparty.Causes as Causes,\r\n" +
				"counterparty.SubsidiaryLimit as SubsidiaryLimit,\r\n" +
				"counterparty.UKZ as UKZ,\r\n" +
				"counterparty.isSRK as isSRK\r\n" +
				"FROM\r\n" + 
				"dbo.ctl_Counterparties counterparty\r\n" + 
				"LEFT JOIN dbo.ctl_RatingDonor donor ON (donor.CounterpartyID = counterparty.id)\r\n" + 
				"LEFT JOIN dbo.ctl_Counterparties counterpartyDonor ON (counterpartyDonor.ID = donor.DonorID)\r\n" + 
				"LEFT JOIN dbo.ctl_Countries countryOfDomicile ON (counterparty.CountryOfDomicileID = countryOfDomicile.ID)\r\n" + 
				"LEFT JOIN dbo.ctl_Countries countryOfRisk ON (counterparty.CountryOfRiskID = countryOfRisk.ID)\r\n" + 
				"LEFT OUTER JOIN dbo.ctl_Portfolios portfolios ON (portfolios.ID = counterparty.PortfolioID)\r\n" +
				"LEFT OUTER JOIN dbo.ctl_Sectors sector ON (sector.ID = counterparty.SectorID)\r\n" + 
				"WHERE counterparty.ID = " + id;
		return runSelect(script);
	}

	public int updateCounterparty(int id, String name, String shortName, String bloombergTicker, 
			int sectorID, String portfolioID, int countyDomID, int countryRiskID,
			 String isIntragroup, String toBeMonitored, LocalDate date, String comments, String longterm, String etp, String efet, String gtc,
								  String isSRK, String Duns, int subsidiaryLimit, String ukz, String Causes) throws Exception {

		String script =
				"UPDATE dbo.ctl_Counterparties\r\n" +
		"  SET Name = " + name + ",\r\n" +
				"  ShortName = " + shortName + ",\r\n" +
				"  BloombergTicker = " + bloombergTicker + ",\r\n" +
				"  SectorID = " + sectorID + ",\r\n" +
				"  PortfolioID = " + portfolioID + ",\r\n" +
				"  CountryOfDomicileID = " + countyDomID + ",\r\n" +
				"  CountryOfRiskID = "  + countryRiskID + ",\r\n" +
				"  intraGroup = "  + isIntragroup + ",\r\n" +
				"  ToBeMonitored = "  + toBeMonitored + ",\r\n" +
				"  startDate = '"  + date.toString() + "',\r\n" +
						"  Longterm = "  + longterm + ",\r\n" +
						"  Etp = "  + etp + ",\r\n" +
						"  Efet = "  + efet + ",\r\n" +
						"  Gtc = "  + gtc + ",\r\n" +
						"  isSRK = "  + isSRK + ",\r\n" +
						"  Duns = "  + Duns + ",\r\n" +
						"  SubsidiaryLimit = "  + subsidiaryLimit + ",\r\n" +
						"  Causes = "  + Causes + ",\r\n" +
						"  UKZ = "  + ukz + ",\r\n" +
				"  Comments = "  + comments + "\r\n" +
				"WHERE ID = " + id;

		int result = runUpdate(script);
		
		log.info(user + " has created a counterparty:\r\n" + 
				name + ",\r\n" +
				shortName + ",\r\n" +
				bloombergTicker + ",\r\n" +
				sectorID + ",\r\n" + 
				countyDomID + ",\r\n" +  
				countryRiskID + ",\r\n" +  
				isIntragroup + ",\r\n" +  
				date.toString() + "',\r\n" +
				comments + "\n");
		
		return result;
		
	}

	public int updateGuarantee(int guaranteeID, String counterparty,
							   String guarantor, String guaranteeType, String currency,
							   long amount, LocalDate fromDate, LocalDate toDate, String guaranteeNumber) throws Exception {
		String script = "UPDATE dbo.ctl_Guarantees \r\n" +
                    "SET" + "  GuarantorID = " + getCounterpartyIDbyName(guarantor) + ",\r\n" + "  CurrencyID = " +
                    getCurrencyIDbyName(currency) + ",\r\n" + "  TypeID = " + getGuaranteeTypeIDbyName(guaranteeType) +
                    ",\r\n" + "  Amount = " + amount + ",\r\n" + "  StartDate = '" + fromDate + "',\r\n" +
                    "  EndDate = '" + toDate + "',\r\n" + "  GuaranteeNumber = '" + guaranteeNumber +
                    "'\r\n" + "WHERE ID = " + guaranteeID;

		int result = runUpdate(script);

		log.info(user + " has update Guarantee:" +
				counterparty + ",\r\n" +
				guarantor + ",\r\n" +
				guaranteeType + ",\r\n" +
				currency + ",\r\n" +
				amount + ",\r\n" +
				fromDate + ",\r\n" +
				toDate + "\r\n" +
				guaranteeNumber + "\r\n");

		return result;
	}
	
	public int createDonor(int counterparty, String donor) throws Exception {
		
		String script = 
				"INSERT INTO\r\n" + 
				"  dbo.ctl_RatingDonor(\r\n" + 
				"  CounterpartyID,\r\n" + 
				"  DonorID)\r\n" + 
				"VALUES(\r\n" + 
				"'" + counterparty + "',\r\n" +
				"'" + getCounterpartyIDbyName(donor) +"')";  

		int result = runUpdate(script);
		
		log.info(user + " has created Donor:" +
		counterparty + ",\r\n" +
		donor);
		
		return result;
	}

	public Statrs getTickers() throws Exception {
		String script = "SELECT BloombergTicker FROM dbo.ctl_Counterparties WHERE BloombergTicker IS NOT NULL";
		return runSelect(script);
	}

	public Statrs getCountryTickers() throws Exception {
		String script = "SELECT BloombergTicker, Name, ShortName FROM dbo.ctl_Countries WHERE BloombergTicker IS NOT NULL AND BloombergTicker <> ''";
		return runSelect(script);
	}

	public int removeAllDonors(int id) throws Exception {
		String script = "DELETE FROM \r\n" + 
				"  dbo.ctl_RatingDonor\r\n" + 
				"  WHERE dbo.ctl_RatingDonor.CounterpartyID = " + id;

		int result = runUpdate(script);
		
		log.info(user + " has deleted all donors for counterparty: " + id + ";");
			
		return result;
	}

	public ArrayList<String> getCounterpartyList() throws Exception {
		ArrayList<String> answer = new ArrayList<>();
		String script = "SELECT Name FROM dbo.ctl_Counterparties";
		Statrs srs = runSelect(script);
		ResultSet rs = srs.getRs();
		while (rs.next()) {
			answer.add(rs.getString("Name"));
		}
		srs.close();
		return answer;
	}

	public Statrs getCounterpartyRS() throws Exception {
		String script = "SELECT Name FROM dbo.ctl_Counterparties";
		return runSelect(script);
	}

    public Statrs getApprovedGuarantees() throws Exception {
		String script = ParserSQL.read("getApprovedGuarantees");
		return runSelect(script);
    }

	public Statrs getBanks() throws Exception {
		String script = ParserSQL.read("getBanks");
		return runSelect(script);
	}

	public Statrs getGuaranteeNames() throws Exception {
		String script = "SELECT GuaranteeName AS Name FROM dbo.ctl_GuaranteeNames";
		return runSelect(script);
	}

	public float getExchangeRate(String currency, LocalDate reportDate) {
		String script = "select rate from exchange_rates where report_date = '" +
				reportDate.toString() + "' and from_currency = '" +
				currency + "'";
		float result = 0;
		try {
			ResultSet rs = runSelect(script).getRs();
			rs.next();
			result = rs.getFloat(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int createApprovedGuarantee(String counterpartyName, String bankName, LocalDate agreeFirstDate,
									   LocalDate agreeEndDate, LocalDate guaranteeEndDate, long amount, String currency,
									   String guaranteeName) throws SQLException, IOException {
		String script = ParserSQL.read("createApprovedGuarantee");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setString(1, counterpartyName);
		stmt.setString(2, bankName);
		stmt.setString(3, agreeFirstDate.toString());
		stmt.setString(4, agreeEndDate.toString());
		stmt.setString(5, guaranteeEndDate.toString());
		stmt.setLong(6, amount);
		stmt.setString(7, currency);
		stmt.setString(8, guaranteeName);
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public int updateApprovedGuarantee(int id, String counterpartyName, String bankName, LocalDate agreeFirstDate,
									   LocalDate agreeEndDate, LocalDate guaranteeEndDate, long amount, String currency,
									   String guaranteeName) throws SQLException, IOException {
		String script = ParserSQL.read("updateApprovedGuarantee");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		stmt.setString(2, counterpartyName);
		stmt.setString(3, bankName);
		stmt.setString(4, agreeFirstDate.toString());
		stmt.setString(5, agreeEndDate.toString());
		stmt.setString(6, guaranteeEndDate.toString());
		stmt.setLong(7, amount);
		stmt.setString(8, currency);
		stmt.setString(9, guaranteeName);
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public Statrs getApprovedGuaranteeById(int id) throws IOException, SQLException {
		String script = ParserSQL.read("getApprovedGuaranteeById");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		return new Statrs(stmt, rs);
	}

	public int deleteApprovedGuarantee(int id) throws Exception {
		String script = ParserSQL.read("deleteApprovedGuarantee");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public int createRatingLimit(int groupNumber, int ratingID, int groupLimit, int bankLimit) throws SQLException, IOException {
		String script = ParserSQL.read("createRatingLimit");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, groupNumber);
		stmt.setInt(2, ratingID);
		stmt.setInt(3, groupLimit);
		stmt.setInt(4, bankLimit);
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public Statrs getRatingLimits() throws Exception {
		String script = ParserSQL.read("getRatingLimits");
		return runSelect(script);
	}

	public Statrs getSrkRatingLimits() throws Exception {
		String script = ParserSQL.read("getSrkRatingLimits.sql");
		return runSelect(script);
	}

	public Statrs getSrkRatingLimitById(int id) throws Exception {
		String script = "SELECT ID, Name, DepLimit, DtoLimit, DenLimit FROM ctl_RatingValues Where ID = "+ id;
		return runSelect(script);
	}

	public int updateSrkRatingLimit(int id, int depLimit, int denLimit, int dtoLimit) throws Exception {
		String script = ParserSQL.read("updateSrkRatingLimit.sql");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setInt(1, id);
		stmt.setInt(2, depLimit);
		stmt.setInt(3, denLimit);
		stmt.setInt(4, dtoLimit);
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public int[] addGuaranteesForReport(ArrayList<GuaranteeEntity> garList) throws Exception {
		String script = ParserSQL.read("addGuaranteesForReport");
		PreparedStatement stmt = conn.prepareStatement(script);
		for (GuaranteeEntity garEntity : garList) {
			int daughterID = garEntity.getDaughterCode();
			LocalDate reportDate = garEntity.getReportMonth();
			String bank = garEntity.getBank();
			String counterparty = garEntity.getCounterparty();
			String number = garEntity.getId();
			LocalDate startDate = garEntity.getStartGuaranteeDate();
			LocalDate finishDate = garEntity.getFinishGuaranteeDate();
			int currencyID = getCurrencyIDbyName(garEntity.getCurrency());
			long firstAmount = garEntity.getFirstSum();
			long StartMonthAmount = garEntity.getStartSum();
			long ChangeAmount = garEntity.getDifferencySum();
			long EndMonthAmount = garEntity.getFinishSum();
			long OperationAmount = garEntity.getOperationSum();
			String guaranteeType = garEntity.getGarType();
			String benificiryName = garEntity.getBeneficiary();
			int benificiaryCode = garEntity.getBeneficiaryCode();
			String approvalType = garEntity.getAppDocType();
			String approvalDate = garEntity.getAppDocDate();
			String approvalNumber = garEntity.getAppDocNumber();
			String note = garEntity.getNote();
			stmt.setInt(1, daughterID);
			stmt.setString(2, reportDate.toString());
			stmt.setString(3, bank);
			stmt.setString(4, counterparty);
			stmt.setString(5, number);
			stmt.setString(6, startDate.toString());
			stmt.setString(7, finishDate.toString());
			stmt.setInt(8, currencyID);
			stmt.setLong(9, firstAmount);
			stmt.setLong(10, StartMonthAmount);
			stmt.setLong(11, ChangeAmount);
			stmt.setLong(12, EndMonthAmount);
			stmt.setLong(13, OperationAmount);
			stmt.setString(14, guaranteeType);
			stmt.setString(15, benificiryName);
			stmt.setInt(16, benificiaryCode);
			stmt.setString(17, approvalType);
			stmt.setString(18, approvalDate);
			stmt.setString(19, approvalNumber);
			stmt.setString(20, note);
			stmt.addBatch();
		}
		int[] result = stmt.executeBatch();
		stmt.close();
		return result;
	}

	public int addIndividualLimit(String department, int counterpartyLimit, String comment, String garantMinRating, int garantLimit, int counterpartyId) throws Exception {
		String script = ParserSQL.read("createIndividualLimit.sql");
		PreparedStatement stmt = conn.prepareStatement(script);
		stmt.setString(1, department);
		stmt.setInt(2, counterpartyLimit);
		stmt.setString(3, comment);
		stmt.setString(4, garantMinRating);
		stmt.setInt(5, garantLimit);
		stmt.setInt(6, counterpartyId);
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public Statrs getGuaranteesReport() throws Exception {
		String script = ParserSQL.read("getGuaranteesReport");
		return runSelect(script);
	}

	public Statrs getBankLimitUsage() throws Exception {
		String script = ParserSQL.read("getBankLimitUsage");
		return runSelect(script);
	}

	public Statrs getContracts() throws Exception {
		String script = ParserSQL.read("getContracts");
		return runSelect(script);
	}

	public Statrs getGroupLimitUsage() throws Exception {
		String script = ParserSQL.read("getGroupLimitUsage");
		return runSelect(script);
	}

	public int removeOldGuaranteesReport(LocalDate date) throws Exception {
		String script = "DELETE FROM ctl_GuaranteesReports WHERE ReportDate < ?";
		PreparedStatement stmt = conn.prepareStatement(script);
		Helper.debug(script, date.toString());
		stmt.setString(1, date.toString());
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	public int removeCurrentGuaranteesReport(LocalDate date) throws Exception {
		String script = "DELETE FROM ctl_GuaranteesReports WHERE ReportDate = ?";
		PreparedStatement stmt = conn.prepareStatement(script);
		Helper.debug(script, date.toString());
		stmt.setString(1, date.toString());
		int result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

}

