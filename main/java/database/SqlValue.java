package database;

public class SqlValue {
    private String name;
    private String type;
    private String value;

    public SqlValue(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public SqlValue(String name, String value) {
        this.name = name;
        this.type = "string";
        this.value = value;
    }

    public SqlValue(String name, int value) {
        this.name = name;
        this.type = "number";
        this.value = String.valueOf(value);
    }

    public SqlValue(String name, double value) {
        this.name = name;
        this.type = "number";
        this.value = String.valueOf(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueSQL() {
        if (this.type.equals("number")) {
            return value;
        } else {
            return "'" + value + "'";
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
