package GuarantiesTool;

import database.Database;

public class BankLimit {
    private String name;
    private int id;
    private String rating;
    private String limit;
    private String usage;

    public BankLimit(int id) {
        this.id = id;

        try {
            Database db = Database.getInstance();
            this.name = db.getCountryNameById(id);
            //int ratingID = db.getRatingIdForGuaranteeReport(id);
            //this.rating = db.getRatingValueByID(ratingID);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
