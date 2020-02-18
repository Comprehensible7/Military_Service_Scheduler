package com.teamproject.aaaaan_2.dday;


//-------------년월일 날짜 출력값 계산파일

public class CalenderSet {
    private int day = 0;

    public int monthSet(int month, int year) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                this.day = 31;
                break;
            case 2:
                if (year % 4 == 0 && year % 100 != 0) {
                    this.day = 29;
                    break;
                }
                this.day = 28;
                break;
            //break;
            case 4:
            case 6:
            case 9:
            case 11:
                this.day = 30;
                break;
        }
        return this.day;
    }

    public boolean calenderCheck(int year, int month, int day, int setYear, int setMonth, int setDay) {
        if (year < setYear) {
            return false;
        }
        if (year != setYear) {
            return true;
        }
        if (month < setMonth) {
            return false;
        }
        if (month != setMonth || day >= setDay) {
            return true;
        }
        return false;
    }

    public int[] outMonthCheck(int outYear, int outMonth) {
        int[] date = new int[2];
        if (outMonth > 12) {
            outMonth -= 12;
            outYear++;
        }
        date[0] = outYear;
        date[1] = outMonth;
        return date;
    }

    public int[] outPutDateByName(int year, int month, int day, int armyMonth, boolean decrease) {
        int armyDay;
        int outYear = year + 1;
        int[] outDate = new int[3];
        DDayTool dDayTool = new DDayTool();
        boolean standardCheck = calenderCheck(year, month, day, 2017, 1, 2);
        if (decrease && standardCheck) {
            int decreaseDay;
            if (armyMonth == 11) {
                decreaseDay = (int) (((((double) dDayTool.FirstDay(2017, 1, 2, year, month, day)) / 1246.0d) * 60.0d));
            } else {
                decreaseDay = (int) (((((double) dDayTool.FirstDay(2017, 1, 2, year, month, day)) / 1246.0d) * 90.0d));
            }
            while (decreaseDay > 30) {
                decreaseDay -= 30;
                armyMonth--;
            }
            armyDay = 30 - decreaseDay;
        } else {
            armyDay = 30;
        }
        int[] date = outMonthCheck(outYear, month + armyMonth);
        outYear = date[0];
        int outMonth = date[1];
        int outDay = day + armyDay;
        int checkDay = monthSet(outMonth, outYear);
        while (outDay > checkDay) {
            outDay -= checkDay;
            date = outMonthCheck(outYear, outMonth + 1);
            outYear = date[0];
            outMonth = date[1];
            checkDay = monthSet(outMonth, outYear);
        }
        outDate[0] = outYear;
        outDate[1] = outMonth;
        outDate[2] = outDay;
        return outDate;
    }

    public int[] outPutDate(int year, int month, int day, String armyName, boolean decrease) {

        // 육군은 default
        switch (armyName) {
            case "공군/공익":
                return outPutDateByName(year, month, day, 11, decrease);
            case "해군":
                return outPutDateByName(year, month, day, 10, decrease);
            default:
                return outPutDateByName(year, month, day, 8, decrease);
        }

    }
}
