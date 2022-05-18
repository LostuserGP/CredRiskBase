package util;

public class RatingSummary {
	
	private String ratingAgency;
	private String lastRating;
	private String newRatingOriginalFormat;
	private String newRating;
	private int lastRatingID;
	private int newRatingID;
	private String lastDate;
	private String newDate;
	
	public int getDifference() {
		return  lastRatingID - newRatingID;
	}
	
	public int getDatesDiffereces() {
		if (lastDate.length() == newDate.length() && lastDate.length() == 10) {
			String[] splitLastDate = lastDate.split("-");
			String[] splitNewDate = newDate.split("-");
			return (Integer.parseInt(splitNewDate[0]) - Integer.parseInt(splitLastDate[0])) * 365 
					+ (Integer.parseInt(splitNewDate[1]) - Integer.parseInt(splitLastDate[1])) * 30 
					+ (Integer.parseInt(splitNewDate[2]) - Integer.parseInt(splitLastDate[2]));
		}
		return 0;
	}

	public RatingSummary(String ratingAgency, int lastRatingID,
			String newRatingOriginalFormat, String lastDate, String newDate) {
		super();
		this.ratingAgency = ratingAgency;
		this.lastRatingID = lastRatingID;
		this.newRatingOriginalFormat = newRatingOriginalFormat;
		this.lastDate = lastDate;
		this.newDate = newDate;
		this.newRatingID = ratingAgency.equals("Moody''s") ? Helper.getMoodysRatingValueByString(newRatingOriginalFormat) : Helper.getSPRatingValueByString(newRatingOriginalFormat);
		this.newRating = Helper.getRatingValueByID(newRatingID);
		this.lastRating = Helper.getRatingValueByID(lastRatingID);
	}

	public String getRatingAgency() {
		return ratingAgency;
	}

	public String getlastRating() {
		return lastRating;
	}

	public String getNewRatingOriginalFormat() {
		return newRatingOriginalFormat;
	}

	public String getNewRating() {
		return newRating;
	}

	public int getlastRatingID() {
		return lastRatingID;
	}

	public int getNewRatingID() {
		return newRatingID;
	}

	public String getlastDate() {
		return lastDate;
	}

	public String getNewDate() {
		return newDate;
	}

	public boolean toBeUpdated() {
        return newRatingID != -1 && (getDifference() != 0 || !lastDate.equals(newDate));
    }
	
	
}
