package util;

import database.Database;
import database.Statrs;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SrkForExcel {
    private Database db;
    private ArrayList<SrkRatingLimit> srkRatingLimits;
    private ArrayList<SrkRow> SrkList;

    public SrkForExcel(){
        init();
        if (SrkList.size() > 0) {
            Collections.sort(SrkList);
        }
    }

    public List<SrkRow> getSrkList() {
        return SrkList;
    }

    public void init() {
        db = Database.getInstance();
        getSRKRatingLimit();
        SrkList = new ArrayList<SrkRow>();
        try {
            Statrs srs = db.getSrkForExcel();
            ResultSet rs = srs.getRs();
            while (rs.next()) {
                int id = rs.getInt("CounterpartyID");
                String name = rs.getString("Name");
                String rating = rs.getString("Rating");
                String ukz = rs.getString("UKZ");
                String analyst = rs.getString("Analyst");
                String causes = rs.getString("Causes");
                int ratingValue = rs.getInt("RatingValue");
                java.sql.Date sqlDate = java.sql.Date.valueOf(String.valueOf(rs.getDate("RatingDate")));
                LocalDate ratingDate = sqlDate.toLocalDate();
                ratingDate = ratingDate.plusMonths(18);
                int subsidiaryLimit = rs.getInt("SubsidiaryLimit");
                int depLimit = 0;
                int dtoLimit = 0;
                int denLimit = 0;
                int limitType = 3;
                boolean intraGroup = rs.getBoolean("intraGroup");
                if (!intraGroup) {
                    limitType = 2;
                    for (int i = 0; i < srkRatingLimits.size(); i++) {
                        SrkRatingLimit rl = srkRatingLimits.get(i);
                        if (rl.getName().equals(rating)) {
                            depLimit = rl.getDepLimit();
                            dtoLimit = rl.getDtoLimit();
                            denLimit = rl.getDenLimit();
                        }
                    }
                }
              SrkRow sr = new SrkRow(
                      id,
                      name,
                      rating,
                      depLimit,
                      dtoLimit,
                      denLimit,
                      "",
                      subsidiaryLimit,
                      ukz,
                      analyst,
                      ratingDate,
                      causes,
                      ratingValue
              );
                sr.setLimitType(limitType);
                sr = getIndividualLimit(sr, intraGroup);
                SrkList.add(sr);
            }
            srs.close();
        }
        catch (Exception e) {

        }
    }

    private void getSRKRatingLimit() {
        srkRatingLimits = new ArrayList<SrkRatingLimit>();
        try {
            Statrs srs = db.getSrkRatingLimits();
            ResultSet rs = srs.getRs();
            while (rs.next()) {
                SrkRatingLimit ratingLimit = new SrkRatingLimit(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getInt("depLimit"),
                        rs.getInt("dtoLimit"),
                        rs.getInt("denLimit")
                );
                srkRatingLimits.add(ratingLimit);
            }
        } catch (Exception e) {

        }
    }

    private SrkRow getIndividualLimit(SrkRow sr, boolean intraGroup) {
        try {
            Statrs srs = db.getIndividualLimitsByID(sr.getId());
            ResultSet rs = srs.getRs();
            String comment = "";
            while (rs.next()) {
                sr.setLimitType(1);
                String department = rs.getString("Department");
                int limit = rs.getInt("CounterpartyLimit");
                String garantMinRaiting = rs.getString("GarantMinRaiting");
                int garantLimit = rs.getInt("GarantLimit");
                comment += rs.getString("Comment");
                if (comment.length() > 0) {
                    comment += " \r\n";
                }
                if (department.equals("DEP")) {
                    sr.setDepLimit(limit);
                    comment += "Департамент: DEP \r\n";
                } else if (department.equals("DEN")) {
                    sr.setDenLimit(limit);
                    comment += "Департамент: DEN \r\n";
                } else if (department.equals("DTO")) {
                    sr.setDtoLimit(limit);
                    comment += "Департамент: DTO \r\n";
                }
                if (garantMinRaiting != null && garantMinRaiting.length() > 0) {
                    comment += "Минимальный кр. рейтинг гаранта: " + garantMinRaiting + " \r\n";
                }
                if (garantLimit > 0) {
                    comment += "Лимит на гаранта: " + garantLimit + " млн. Евро \r\n";
                }
                if (intraGroup) {
                    sr.setLimitType(3);
                    sr.setDepLimit(0);
                    sr.setDtoLimit(0);
                    sr.setDenLimit(0);
                }
            }
            sr.setComment(comment);
            return sr;
        } catch (Exception e) {

        }
        return sr;
    }
}
