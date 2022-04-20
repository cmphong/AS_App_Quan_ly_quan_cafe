package com.c_m_p.poscoffeeshop.MyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUUID {
    public static String generator(){
        Date date = new Date();
        long millisecond = date.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return df.format(millisecond);
    }
}
