package util;

public class SrkIndividualLimit {
    private int Id;
    private String Department;
    private int counterpartyLimit;
    private String comment;
    private String garantMinRating;
    private int garantLimit;
    private int counterpartyId;

    public SrkIndividualLimit(int id, String department, int counterpartyLimit, String comment, String garantMinRating, int garantLimit, int counterpartyId) {
        Id = id;
        Department = department;
        this.counterpartyLimit = counterpartyLimit;
        this.comment = comment;
        this.garantMinRating = garantMinRating;
        this.garantLimit = garantLimit;
        this.counterpartyId = counterpartyId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public int getCounterpartyLimit() {
        return counterpartyLimit;
    }

    public void setCounterpartyLimit(int counterpartyLimit) {
        this.counterpartyLimit = counterpartyLimit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGarantMinRating() {
        return garantMinRating;
    }

    public void setGarantMinRating(String garantMinRating) {
        this.garantMinRating = garantMinRating;
    }

    public int getGarantLimit() {
        return garantLimit;
    }

    public void setGarantLimit(int garantLimit) {
        this.garantLimit = garantLimit;
    }

    public int getCounterpartyId() {
        return counterpartyId;
    }

    public void setCounterpartyId(int counterpartyId) {
        this.counterpartyId = counterpartyId;
    }
}
