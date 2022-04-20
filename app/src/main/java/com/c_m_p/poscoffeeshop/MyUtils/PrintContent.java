package com.c_m_p.poscoffeeshop.MyUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.async.AsyncBluetoothEscPosPrint;
import com.c_m_p.poscoffeeshop.async.AsyncEscPosPrinter;
import com.emh.thermalprinter.EscPosCharsetEncoding;
import com.emh.thermalprinter.EscPosPrinter;
import com.emh.thermalprinter.EscPosPrinterSize;
import com.emh.thermalprinter.connection.DeviceConnection;
import com.emh.thermalprinter.connection.bluetooth.BluetoothConnection;
import com.emh.thermalprinter.connection.tcp.TcpConnection;
import com.emh.thermalprinter.textparser.PrinterTextParserImg;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PrintContent {
    public static final int PERMISSION_BLUETOOTH_CODE = 111;


    // ==============================================================
    // ===== BLUETOOTH PRINTER ======================================
    // ==============================================================
    public static void printTestBLUETOOTH(Activity context, BluetoothConnection selectedDeivice, String paperSize) {
        String content = "[L] BLUETOOTH PRINTER\n" +
                            getBodyContentTest(paperSize);

        asyncPrintBLUETOOTH(context, content, selectedDeivice, paperSize);
    }

    public static void printReceiptBLUETOOTH(Activity context, BluetoothConnection selectDevice, String paperSize, Table table) {
        String content = getBodyContentReceipt(table, paperSize);

        asyncPrintBLUETOOTH(context, content, selectDevice, paperSize);
    }

    private static void asyncPrintBLUETOOTH(Activity context, String content, BluetoothConnection selectedDevice, String paperSize){
        new AsyncBluetoothEscPosPrint(context).execute(PrintContent.getAsyncEscPosPrinter(context, content, selectedDevice, paperSize));

    }

    @SuppressLint("SimpleDateFormat")
    public static AsyncEscPosPrinter getAsyncEscPosPrinter(Activity context, String content, DeviceConnection printerConnection, String paperSize) {
        float printerWidthMM = 0f;
        int printerNbrCharacterPerLine = 0;
        if(Integer.parseInt(paperSize) == 57) {
            printerWidthMM = 48f;
            printerNbrCharacterPerLine = 28;
        }else{
            printerWidthMM = 65f;
            printerNbrCharacterPerLine = 44;
        }

        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(
                printerConnection, 203, printerWidthMM, printerNbrCharacterPerLine);
        return printer.setTextToPrint(
                getHeaderContentReceipt(context, printer) +

                "[L]\n" +

                content
        );
    }


    // ==============================================================
    // ===== TCP/IP =================================================
    // ==============================================================
    public static void printTestTCP(Context context, String ipAddress, int portAddress, String paperSize){
        String strContent = "[L] LAN PRINTER\n" +
                            getBodyContentTest(paperSize);

        printTCP(context, strContent, ipAddress, portAddress, paperSize);
    }

    public static void printReceiptTCP(Context context, String ipAddress, int portAddress, String paperSize, Table table){
        String content = getBodyContentReceipt(table, paperSize);
        printTCP(context, content, ipAddress, portAddress, paperSize);
    }

    private static void printTCP(Context context, String content, String ipAddress, int portAddress, String paperSize) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // default: dpi = 203,
                    //          printerWidthMM = 65f,
                    //          printerNbrCharacterPerLine = 42
                    float printerWidthMM = 0f;
                    int printerNbrCharacterPerLine = 0;
                    if(Integer.parseInt(paperSize) == 57){
                        printerWidthMM = 48;
                        printerNbrCharacterPerLine = 28;
                    }else{
                        printerWidthMM = 65;
                        printerNbrCharacterPerLine = 44;
                    }
                    EscPosPrinter printer;
                    printer = new EscPosPrinter(
                                    new TcpConnection(ipAddress, portAddress),
                                    203,
                                    printerWidthMM,
                                    printerNbrCharacterPerLine,
                            new EscPosCharsetEncoding("windows-1252", 16));
                    printer.printFormattedText(
                            getHeaderContentReceipt(context, printer) +

                            "\n" +

                            content
                    );

//                    printer.disconnectPrinter();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getHeaderContentReceipt(Context context, EscPosPrinterSize printer){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                CommonData.STORE_PROFILE, Context.MODE_PRIVATE);
        String store_name = sharedPreferences.getString(CommonData.STORE_NAME, "");
        String phone = sharedPreferences.getString(CommonData.PHONE_NUMBER, "");
        String address = sharedPreferences.getString(CommonData.ADDRESS, "");

        Bitmap bitmap = CommonData.getImageLogo(context);
        Bitmap bitmapScale = Bitmap.createScaledBitmap(
                bitmap, 192, 192, true
        );

        SimpleDateFormat df = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        return "[C]<img>" +
//                PrinterTextParserImg.bitmapToHexadecimalString(
//                        printer, context.getResources().getDrawableForDensity(
//                                R.drawable.logo_default, DisplayMetrics.DENSITY_MEDIUM, null)) +

                PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmapScale) +
                "</img>\n" +
                "[L]\n" +
                "[C]<u><font size='big'>" +store_name+ "</font></u>\n" +
                "\n" +
                "[L] " +address+ "[L][L][L][L]\n" +
                "[L] So DT: " +phone+ "\n" +
                "\n" +
                "[C]<u><font size='big'>BILL</font></u>\n" +
                "[C]\n" +
                "[C]<u type='double'>" + df.format(new Date()) + "</u>\n" +
                "[C]\n"
                ;
    }

    private static String getBodyContentReceipt(Table table, String paperSize){
        DecimalFormat df = new DecimalFormat("#,##0.00");

        String strOrderedItem = "";
        float subTotal = 0f;

        List<Ordered> listOrdered = table.getItems();
        for(Ordered item : listOrdered){
            strOrderedItem +=
                            "[R] " +item.getName()+ "[R][R]\n"+
                            "[R] " +item.getQuantity()+ "[R]" +df.format(item.getPrice())+ "[R]" +df.format(item.getTotal())+ "\n";
            subTotal += item.getTotal();
        }


        int size = Integer.parseInt(paperSize);
        String line = size == 57 ? "[L]____________________________\n"  // 28 character (printerNbrCharactersPerLine)
                : "[L]____________________________________________\n"; // 44 character (printerNbrCharactersPerLine)

        String result =
            line +
            "[R] <b>Qty[R]Price[R]Amount</b>\n" +
            strOrderedItem +
            "\n"+
            line +
            "[L] [R]Sub Total[R]$" +df.format(subTotal)+ "\n" +
            "[L] [R]VAT[R]" +df.format(subTotal/10)+ "\n" +
            "[L] [R]<font size='tall'>Total[R]" +df.format(subTotal + (subTotal/10))+ "</b>\n" +
            "\n" +
            line +
            "\n" +
            "[C]<font size='tall'>  Thank you for visited</font>\n" +
            "\n"
//            "[C]    <barcode type='ean13' height='10'>831254784551</barcode>    \n" +
//            "\n" +
//            "[L]<qrcode>http://c_m_p.tk</qrcode>\n[L]\n[L]\n[L]\n"
            ;
        return result;
    }

    private static String getBodyContentTest(String paperSize){
        int size = Integer.parseInt(paperSize);
        String line = size == 57
                ? "[L]____________________________\n"  // 28 character (printerNbrCharactersPerLine)
                : "[L]____________________________________________\n"; // 44 character (printerNbrCharactersPerLine)

        return  line +
                "[L]COL 1[C]COL 2[C]COL 3[C]COL 4[R]COL 5\n" +
                "[C]COL 1[C]COL 2[C]COL 3[C]COL 4\n" +
                "[C]COL 1[C]COL 2[C]COL 3\n" +
                "[C]COL 1[C]COL 2\n" +
                "[C]COL 1\n" +
                "\n" +
                line +
                "\n" +
                "[L] It was worked It was worked \n" +
                "[L] It was worked It was worked \n" +
                "[L] It was worked It was worked \n" +
                "[L] It was worked It was worked \n" +
                "[L]\n";
    }





    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== PrintContent.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== PrintContent.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== PrintContent.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== PrintContent.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
