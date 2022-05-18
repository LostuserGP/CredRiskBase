package util;

import database.Database;
import database.Statrs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.*;

public class BloombergRatingsReader {

    private HashMap<String, Double> hashMap;
    private Set<String> issuers;
    private Set<String> dates;
    private String filename;
    private DefaultTableModel tableModel;

    public DefaultTableModel getTableModel() {
		return tableModel;
	}

    public HashMap<String, Double> getHashMap() {
        return hashMap;
    }

    public Set<String> getCounterparties() {
        return issuers;
    }

    public Set<String> getDates() {
        return dates;
    }

	public BloombergRatingsReader(String filename, boolean isCompanies, JProgressBar progressBar) throws Exception {

        this.hashMap = new HashMap<String, Double>();
        this.issuers = new HashSet<String>();
        this.dates = new HashSet<String>();
        this.filename = filename;
        
		parse(progressBar, isCompanies);
    }

    private ArrayList<PivotData> ExcelData() {
    	ArrayList<PivotData> tmp = new ArrayList<>();
		try {
			InputStream inputStream = new FileInputStream(filename);
			ReadXLSXFile readerNew = new ReadXLSXFile();
			Vector<Vector<String>> Cells = readerNew.importExcelSheet(filename);

			for (Vector<String> row : Cells) {
				if (!(row.get(0).equals("ticker") || row.get(0).equals("") || row.get(0) == null)) {
					PivotData pdtmp = new PivotData();
					pdtmp.setTicker(row.get(0));
					pdtmp.setName(row.get(1));
					pdtmp.setShort_name(row.get(2));
					pdtmp.setSp_rating(row.get(3));
					pdtmp.setMoody_rating(row.get(4));
					pdtmp.setFitch_rating(row.get(5));
					pdtmp.setSp_date(row.get(6));
					pdtmp.setMoody_date(row.get(7));
					pdtmp.setFitch_date(row.get(8));
					tmp.add(pdtmp);
				}
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tmp;
	}
    
    private void parse(JProgressBar progressBar, boolean isCompanies) throws Exception {
   	 
		progressBar.setString("");
		progressBar.setForeground(Color.GRAY);
		progressBar.setIndeterminate(true);

		ArrayList<PivotData> excelData = ExcelData();

		setHashMap(excelData, progressBar, isCompanies);

	}

	private Map<String, String> getCellMapping() {
		Map<String, String> cellMapping = new HashMap<String, String>();
        cellMapping.put("HEADER",
        		"ticker,name,short_name,sp_rating,moody_rating,fitch_rating,sp_date,moody_date,fitch_date");
        
        cellMapping.put("A", "ticker");
        cellMapping.put("B", "name");
        cellMapping.put("C", "short_name");
        cellMapping.put("D", "sp_rating");
        cellMapping.put("E", "moody_rating");
        cellMapping.put("F", "fitch_rating");
        cellMapping.put("G", "sp_date");
        cellMapping.put("H", "moody_date");
        cellMapping.put("I", "fitch_date");
       

		return cellMapping;

	}

    private void setHashMap(ArrayList<PivotData> date, JProgressBar progressBar, boolean isCompanies) throws Exception {

        PivotData pivotData;
        Database db = Database.getInstance();

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        String[] columnNames = new String[]{isCompanies ? "Counterparty" : "Country", "Bloomberg Name", "Rating agency", "Old rating", "Old date", "New rating", "Difference", "New date", "Date diff"};

        progressBar.setIndeterminate(false);
        progressBar.setMaximum(date.size());
        progressBar.setValue(0);

        for (int i = 0; i < date.size(); i++) {
            pivotData = date.get(i);
            String name = pivotData.getName();
            String ticker = pivotData.getTicker();
            ArrayList<String> entities;
            if (isCompanies)
                entities = db.getCounterpartiesByTicker(ticker);
            else
                entities = db.getCountriesByTicker(ticker);
            RatingSummary fitchSummary;
            RatingSummary spSummary;
            RatingSummary moodysSummary;
            String ratingAgency = "Fitch";
            Statrs srs;
            ResultSet rs;

            System.out.println(name);

            if (isCompanies)
                srs = db.getLatestExternalRatingByTicker(ticker, ratingAgency);
            else
                srs = db.getLatestCountryRatingByTicker(ticker, ratingAgency);

            rs = srs.getRs();
            if (rs.next()) {
                fitchSummary = new RatingSummary(ratingAgency, rs.getInt("RatingValueID"),
                        pivotData.getFitch_rating(), rs.getString("StartDate"),
                        Helper.createSQLDate(pivotData.getFitch_date()));
            } else
                fitchSummary = new RatingSummary(ratingAgency, -1, pivotData.getFitch_rating(),
                        "n/a", Helper.createSQLDate(pivotData.getFitch_date()));

            if (fitchSummary.toBeUpdated())
                for (String entity : entities) {
                    data.add(new ArrayList<String>(Arrays.asList(entity, name, ratingAgency, fitchSummary.getlastRating(), fitchSummary.getlastDate(), fitchSummary.getNewRating(), "" + fitchSummary.getDifference(), fitchSummary.getNewDate(), "" + fitchSummary.getDatesDiffereces())) );
                }


            ratingAgency = "S&P";
            if (isCompanies)
                srs = db.getLatestExternalRatingByTicker(ticker, ratingAgency);
            else
                srs = db.getLatestCountryRatingByTicker(ticker, ratingAgency);
            rs = srs.getRs();
            if (rs.next()) {
                spSummary = new RatingSummary(ratingAgency, rs.getInt("RatingValueID"), pivotData.getSp_rating(), rs.getString("StartDate"), Helper.createSQLDate(pivotData.getSp_date()));
            }
            else
                spSummary = new RatingSummary(ratingAgency, -1, pivotData.getSp_rating(), "n/a", Helper.createSQLDate(pivotData.getSp_date()));
            srs.close();

            if (spSummary.toBeUpdated())
                for (String entity : entities) {
                    data.add(new ArrayList<String>(Arrays.asList(entity, name, ratingAgency, spSummary.getlastRating(), spSummary.getlastDate(), spSummary.getNewRating(), "" + spSummary.getDifference(), spSummary.getNewDate(), "" + spSummary.getDatesDiffereces())) );
                }


            ratingAgency = "Moody''s";
            System.out.println(ratingAgency);
            Helper.debug(ratingAgency);
            if (isCompanies)
                srs = db.getLatestExternalRatingByTicker(ticker, ratingAgency);
            else
                srs = db.getLatestCountryRatingByTicker(ticker, ratingAgency);
            rs = srs.getRs();
            if (rs.next()) {
                moodysSummary = new RatingSummary(ratingAgency, rs.getInt("RatingValueID"), pivotData.getMoody_rating(), rs.getString("StartDate"), Helper.createSQLDate(pivotData.getMoody_date()));
            }
            else
                moodysSummary = new RatingSummary(ratingAgency, -1, pivotData.getMoody_rating(), "n/a", Helper.createSQLDate(pivotData.getMoody_date()));
            srs.close();

            if (moodysSummary.toBeUpdated())
                for (String entity : entities) {
                    data.add(new ArrayList<String>(Arrays.asList(entity, name, ratingAgency, moodysSummary.getlastRating(), moodysSummary.getlastDate(), moodysSummary.getNewRating(), "" + moodysSummary.getDifference(), moodysSummary.getNewDate(), "" + moodysSummary.getDatesDiffereces())) );
                }

            progressBar.setValue(progressBar.getValue() + 1);

        }

        //progressBar.setMaximum(n);

        tableModel = Helper.buildTableModel(data, columnNames);

    }

}