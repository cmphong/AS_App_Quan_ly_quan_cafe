package com.c_m_p.poscoffeeshop.MyUtils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.c_m_p.poscoffeeshop.Model.MyThermalPrinter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class MyUtils {

    public static String LIST_PRINTER = "LIST_PRINTER";

    public static String convertToTime(String strMilli){
        Float second = Float.parseFloat(strMilli); // TimeZone: UTC

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//        df.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return df.format(second);
    }

    public static List<MyThermalPrinter> getListPrinterFromSharedPreferences(SharedPreferences sharedPreferences_print) {
        // Hiện tất cả máy in bao gồm Bluetooth & LAN
        String jsonListPrint = sharedPreferences_print.getString(LIST_PRINTER, null);
        if(jsonListPrint != null){
            Type collectionType = new TypeToken<List<MyThermalPrinter>>(){}.getType();
            return new Gson().fromJson(jsonListPrint, collectionType);
        }else{
            return new ArrayList<>();
        }
    }
}
