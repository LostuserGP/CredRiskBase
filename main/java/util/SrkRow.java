package util;

import java.time.LocalDate;

public class SrkRow implements Comparable<SrkRow> {
    private int id;
    private String name;
    private String rating;
    private int depLimit;
    private int dtoLimit;
    private int denLimit;
    private String comment;
    private int subsidiarySum;
    private int limitType;
    private String UKZ;
    private String Analyst;
    private LocalDate ratingDate;
    private String causes;
    private int ratingValue;

    public SrkRow(int id, String name, String rating, int depLimit, int dtoLimit,
                  int denLimit, String comment, int subsidiarySum, String ukz, String analyst,
                  LocalDate ratingDate, String causes, int ratingValue) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.depLimit = depLimit;
        this.dtoLimit = dtoLimit;
        this.denLimit = denLimit;
        this.comment = comment;
        this.subsidiarySum = subsidiarySum;
        this.UKZ = ukz;
        this.Analyst= analyst;
        this.ratingDate = ratingDate;
        this.causes = causes;
        this.ratingValue = ratingValue;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public LocalDate getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDate ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getCauses() {
        return causes;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public String getAnalyst() {
        return Analyst;
    }

    public void setAnalyst(String analyst) {
        Analyst = analyst;
    }

    public String getUKZ() {
        return UKZ;
    }

    public void setUKZ(String UKZ) {
        this.UKZ = UKZ;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getDepLimit() {
        return depLimit;
    }

    public void setDepLimit(int depLimit) {
        this.depLimit = depLimit;
    }

    public int getDtoLimit() {
        return dtoLimit;
    }

    public void setDtoLimit(int dtoLimit) {
        this.dtoLimit = dtoLimit;
    }

    public int getDenLimit() {
        return denLimit;
    }

    public void setDenLimit(int denLimit) {
        this.denLimit = denLimit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSubsidiarySum() {
        return subsidiarySum;
    }

    public void setSubsidiarySum(int subsidiarySum) {
        this.subsidiarySum = subsidiarySum;
    }

    @Override
    public int compareTo(SrkRow o) {
        if(getLimitType() == o.getLimitType())
            return name.compareTo(o.getName());
        else if(getLimitType() > o.getLimitType())
            return 1;
        else if(getLimitType() < o.getLimitType())
            return -1;
        else
            return 0;
    }
}
