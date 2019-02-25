package com.example.perci.myapplication;

import java.util.Calendar;

public class MonthDay {
    public int getByMonthDay(int m) {
        if (m == 1 || m==3 || m==5 || m==7 || m==8 || m==10 || m==12) {
            return 31;
        } else if (m == 2){
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);

            if ((year%4 ==0 && year%100 != 0) || year%400 == 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    }
}
