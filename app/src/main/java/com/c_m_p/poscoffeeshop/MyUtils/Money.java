package com.c_m_p.poscoffeeshop.MyUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Money {


    public static String EndsWith00(float n){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(n);
    }
}
