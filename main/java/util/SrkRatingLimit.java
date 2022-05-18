package util;

public class SrkRatingLimit {
    private int Id;
    private String Name;
    private int depLimit;
    private int dtoLimit;
    private int denLimit;

    public SrkRatingLimit(int id, String name, int depLimit, int dtoLimit, int denLimit) {
        Id = id;
        Name = name;
        this.depLimit = depLimit;
        this.dtoLimit = dtoLimit;
        this.denLimit = denLimit;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
}
