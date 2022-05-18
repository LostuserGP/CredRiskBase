package util;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static String addMonth(String date, int monthCount) {
        date = createDate(date, "", true);
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(4,6)) + monthCount;
        int day = Integer.parseInt(date.substring(6,8));
        String answer = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
        return answer;
    }

    public static String createSqlDate(LocalDate ldate) {
        String tmp = ldate.toString();
        tmp = tmp.replaceAll("-", "");
        return tmp;
    }

    public static String createDate(Date date, String divider) {
        return createDate(date, divider, true);
    }

    public static String createDate(Date date) {
        return createDate(date, "", true);
    }

    public static String createDate(Date date, String divider, boolean ieYearFirst) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String sqlDate = "";
        //String sDate = "" + year + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day);
        if (ieYearFirst) {
            sqlDate = year + divider + (month < 10 ? "0" + month : month + "") + divider + (day < 10 ? "0" + day : day + "");
        } else {
            sqlDate = (day < 10 ? "0" + day : day + "") + divider + (month < 10 ? "0" + month : month + "") + divider + year;
        }
        return sqlDate;
    }

    public static String createDate(String date, String divider) {
        return createDate(date, divider, true);
    }

    public static String createDate(String date) {
        return createDate(date, "", true);
    }

    public static String createDate(String date, String divider, boolean isYearFirst) {
        if (date == null || date.equals("null") || date.equals("")) return null;
        String[] split = date.split("\\D");
        String sqlDate = "";
        int year = 0;
        int month = 0;
        int day = 0;
        if (split.length == 3) {
            if (split[0].length() == 4) {
                int year0 = Integer.parseInt(split[0]);
                int month0 = Integer.parseInt(split[1]);
                int day0 = Integer.parseInt(split[2]);
                if (checkDate(year0, month0, day0)) {
                    year = year0;
                    month = month0;
                    day = day0;
                } else {
                    System.out.println("Date creation from string '" + date + "' failed");
                    return null;
                }
            } else if (split[2].length() == 4) {
                int year0 = Integer.parseInt(split[2]);
                int month0 = Integer.parseInt(split[1]);
                int day0 = Integer.parseInt(split[0]);
                if (checkDate(year0, month0, day0)) {
                    year = year0;
                    month = month0;
                    day = day0;
                } else {
                    System.out.println("Date creation from string '" + date + "' failed");
                    return null;
                }
            }
        } else if ((split.length == 1) && (split[0].length() == 8)) {
            int year1 = Integer.parseInt(split[0].substring(0,4));
            int month1 = Integer.parseInt(split[0].substring(4,6));
            int day1 = Integer.parseInt(split[0].substring(6,8));
            int year2 = Integer.parseInt(split[0].substring(4,8));
            int month2 = Integer.parseInt(split[0].substring(2,4));
            int day2 = Integer.parseInt(split[0].substring(0,2));
            if (checkDate(year1, month1, day1)) {
                year = year1;
                month = month1;
                day = day1;
            } else if (checkDate(year2, month2, day2)) {
                year = year2;
                month = month2;
                day = day2;;
            } else  if (checkDate(year1, day1, month1)) {
                year = year1;
                month = day1;
                day = month1;
            } else if (checkDate(year2, day2, month2)) {
                year = year2;
                month = day2;
                day = month2;
            } else {
                System.out.println("Date creatinon from string '" + date + "' failed");
                return null;
            }
        } else {
            System.out.println("Date creatinon from string '" + date + "' failed");
            return null;
        }

        if (isYearFirst) {
            sqlDate = year + divider + (month < 10 ? "0" + month : month + "") + divider + (day < 10 ? "0" + day : day + "");
        } else {
            sqlDate = (day < 10 ? "0" + day : day + "") + divider + (month < 10 ? "0" + month : month + "") + divider + year;
        }

        return sqlDate;
    }

    private static boolean checkDate(int year, int month, int day) {
        if (checkYear(year) && checkMonth(month) && checkDay(day)) return true;
        return false;
    }

    private static boolean checkYear(int year) {
        if (year >= 1990 && year <= 2100) return true;
        return false;
    }

    private static boolean checkDay(int day) {
        if (day >= 1 && day <= 31) return true;
        return false;
    }

    private static boolean checkMonth(int month) {
        if (month >= 1 && month <= 12) return true;
        return false;
    }

    private static String createSqlDate2(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String sqlDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        return sqlDate;
    }

    private static String createSqlDate2(String date) {
        if (date == null || date.equals("null")) return null;
        String[] split = date.split("\\.");
        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2]);
        String sqlDate = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
        return sqlDate;
    }

    private static java.sql.Date createSqlDate3(String date) {
        if (date == null || date.equals("null") || date.equals("")) return null;
        //String[] split = date.split("\\.");
        String[] split = date.split("\\D");
        int day = Integer.parseInt(split[2]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[0]);
        String sqlDate = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
        return java.sql.Date.valueOf(sqlDate);
    }

    private static String createSqlDate(String date) {
        if (date == null || date.equals("null") || date.equals("")) return null;
        System.out.println(date);
        //String[] split = date.split("\\.");
        String[] split = date.split("\\D");
        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2]);
        String sqlDate = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
        return sqlDate;
    }

    private static String createDateReport(Date date) {
        if (date == null) return "null";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String reportDate = (day < 10 ? "0" + day : day + "") + "." + (month < 10 ? "0" + month : month + "") + "." + year;
        return reportDate;
    }

    private static String createDateOld(String date) {
        if (date == null || date.equals("null")) return "null";
        String[] split = date.split("-.");
        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);
        String sqlDate = year + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "");
        return "'" + sqlDate + "'";
    }

}
