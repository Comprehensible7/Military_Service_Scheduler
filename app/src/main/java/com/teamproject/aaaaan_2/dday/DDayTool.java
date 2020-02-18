package com.teamproject.aaaaan_2.dday;

//------------전역일 디데이 계산파일

public class DDayTool {
    //진행도 계산
    public double Check(int dDay, int firstDay) {
        return 100.0d * (((double) (firstDay - dDay)) / ((double) firstDay));
    }

    public int FirstDay(int year1, int month1, int day1, int year2, int month2, int day2) {
        int dDay = 0;
        CalenderSet c = new CalenderSet();

        if (!c.calenderCheck(year2, month2, day2, year1, month1, day1)) {
            return 0;
        }

        int monthOfDay = c.monthSet(month1, year1);

        while (day1 != day2) {
            if (day1 > monthOfDay) {
                day1 -= monthOfDay;
                int[] dateTemp = c.outMonthCheck(year1, month1 + 1);
                year1 = dateTemp[0];
                month1 = dateTemp[1];
            } else {
                day1++;
                dDay++;
            }
        }

        while (month1 != month2) {
            if (month1 > 12) {
                month1 -= 12;
                year1++;
            } else {
                dDay += c.monthSet(month1, year1);
                month1++;
            }
        }

        while (year1 != year2) {
            if (year1 % 4 != 0 || year1 % 100 == 0) {
                dDay += 364;
            } else {
                dDay += 365;
            }
            year1++;
        }

        return dDay;
    }
}

