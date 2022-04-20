package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.Model.MyThermalPrinter;
import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.Model.Receipt;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;

import com.c_m_p.poscoffeeshop.MyUtils.MyUtils;
import com.c_m_p.poscoffeeshop.MyUtils.PrintContent;
import com.emh.thermalprinter.connection.bluetooth.BluetoothConnection;
import com.emh.thermalprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* TODO
    [x] Update Table to Empty
    [x] Update Drink, include: ordered Field
 */
public class Activity_Payment extends AppCompatActivity implements View.OnClickListener {

    public static final int PERMISSION_BLUETOOTH_CODE = 111;

    TextView tv_pay, tv_tender, tv_change;
    Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9,
            btn_00, btn_dot, btn_clear, btn_OK;
    ImageButton btn_back, btn_delete;

    Table table;
    List<Drink> listDrinkOnFirebase;

    // DIALOG
    Dialog dialog;
    TextView tv_dialog_change;
    Button btn_dialog_print , btn_dialog_confirm, btn_dialog_confrimAndPrint;

    float sub_total     = 0f,
          payment_total = 0f;
    String strReceive = "0";
    boolean hasPoint = false;

    SharedPreferences sharedPreferences_print;
    List<MyThermalPrinter> listPrinter;
    DecimalFormat df;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sharedPreferences_print = getSharedPreferences(Fragment_Print_Management.PRINT_MANAGER, Context.MODE_PRIVATE);
        listPrinter = MyUtils.getListPrinterFromSharedPreferences(sharedPreferences_print);


        initView();
        df = CommonData.setCurrencyByLanguageApp(getApplicationContext());


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            table = (Table) bundle.get(Activity_CheckOut.TABLE_OBJ);
            List<Ordered> orderedList = table.getItems();
            for(Ordered item : orderedList){
                sub_total += item.getTotal();
            }
            payment_total += sub_total + sub_total*10/100;
            tv_pay.setText(df.format(payment_total));

        }

        getDrinksFromFirebase();

    }

    private void getDrinksFromFirebase() {
        listDrinkOnFirebase = new ArrayList<>();
        List<Ordered> listOrdered = table.getItems();
        for(Ordered item : listOrdered){
            String idDrink = item.getId();
            CommonData.fbRef_Drink.child(idDrink).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        Drink drink = task.getResult().getValue(Drink.class);
                        listDrinkOnFirebase.add(drink);
                    }
                }
            });
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ib_back: finish(); break;

            case R.id.btn_0: showAmountReceive("0"); break;
            case R.id.btn_1: showAmountReceive("1"); break;
            case R.id.btn_2: showAmountReceive("2"); break;
            case R.id.btn_3: showAmountReceive("3"); break;
            case R.id.btn_4: showAmountReceive("4"); break;
            case R.id.btn_5: showAmountReceive("5"); break;
            case R.id.btn_6: showAmountReceive("6"); break;
            case R.id.btn_7: showAmountReceive("7"); break;
            case R.id.btn_8: showAmountReceive("8"); break;
            case R.id.btn_9: showAmountReceive("9"); break;
            case R.id.btn_00: showAmountReceive("00"); break;

            case R.id.btn_clear:
                strReceive = "0";
                tv_tender.setText("0");
                showChange();
                hasPoint = false;
                break;
            case R.id.btn_delete:
                if(hasPoint){
                    int index = strReceive.indexOf(".");
                    if (strReceive.substring(index).length() <= 2){
                        strReceive = strReceive.substring(0, index);
                        hasPoint = false;
                    }else{
                        strReceive = strReceive.substring(0, strReceive.length() - 1);
                    }
                    tv_tender.setText(df.format(Double.parseDouble(strReceive)));
                    showChange();
                    break;
                }

                if(strReceive.length()>1) {
                    strReceive = strReceive.substring(0, strReceive.length() - 1);
                }else{
                    strReceive = "0";
                }
                tv_tender.setText(df.format(Double.parseDouble(strReceive)));
                showChange();
                break;

            case R.id.btn_dot_sign:
                if(!hasPoint){
                    tv_tender.setText(df.format(Double.parseDouble(strReceive)) + ".");
                    strReceive += ".";
                    hasPoint = true;
                }
                break;

            case R.id.btn_save:
                showConfirmDialog();
                break;

            // Handle onclick event on Dialog
            case R.id.dialog_btn_print:
                actionPrint();
                break;
            case R.id.dialog_btn_confirm:
                addReceiptOnFirebase();
                updateTable();
                updateDrink();
                break;
            case R.id.dialog_btn_confirm_and_print:
                addReceiptOnFirebase();
                updateTable();
                updateDrink();
                actionPrint();
                break;
        }
    }

    private void actionPrint(){
        if (listPrinter != null || listPrinter.size() > 0){

            MyThermalPrinter printer = listPrinter.get(0);
            if(printer.getMethod().equals("BLUETOOTH")){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH_CODE);
                } else {
                    BluetoothConnection[] arrBluetoothConnection = (new BluetoothPrintersConnections()).getList();
                    for (BluetoothConnection device : arrBluetoothConnection){
                        if(printer.getMacAddress().equals(device.getDevice().getAddress())){
                            String paperSize = printer.getPaperSize();
                            PrintContent.printReceiptBLUETOOTH(Activity_Payment.this, device, paperSize, table);
                        }
                    }

                }
            }
            if(printer.getMethod().equals("LAN")){
                String ip = listPrinter.get(0).getIpAddress();
                int port = listPrinter.get(0).getPortAddress();
                String paperSize = listPrinter.get(0).getPaperSize();
                PrintContent.printReceiptTCP(Activity_Payment.this, ip, port, paperSize, table);
            }
        }
    }

    private void updateDrink() {
        List<Ordered> listOrdered = table.getItems();
        for (Drink drink : listDrinkOnFirebase){
            if(drink != null) {
                for (Ordered item : listOrdered){
                    if (item.getId().equals(drink.getId())) {
                        drink.setOrdered(drink.getOrdered() + item.getQuantity());
                    }
                }

                CommonData.fbRef_Drink.child(drink.getId()).child("ordered").setValue(drink.getOrdered(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                        Logdln("Update Drink successful", 252);
                    }
                });
            }

        }
    }

    private void updateTable() {
        String idTable = table.getId();
        Map<String, Object> tableMap = new HashMap<>();
        tableMap.put("checkin_time", null);
        tableMap.put("isOrdering", false);
        tableMap.put("items", null);
        tableMap.put("orderingTotal", null);
        CommonData.fbRef_Table.child(idTable).updateChildren(tableMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                Logdln("Updated table: " +table.getName(), 177);
            }
        });
    }

    private void addReceiptOnFirebase() {
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd_hhmmss");
        String id = table.getId();
        String date_time = this.table.getCheckin_time();
        float payment_total = this.payment_total;
        String table_name = this.table.getName();
        List<Ordered> items = this.table.getItems();

        Receipt receipt = new Receipt(id, date_time, payment_total, table_name, items);
        Map<String, Object> receiptMap = receipt.toMap();
        CommonData.fbRef_Receipt.child(id).updateChildren(receiptMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
//                Logdln("error: " +error, 303);
                showToast(getString(R.string.Payment_successful));
                dialog.dismiss();
                finish();
            }
        });
    }

    private void showConfirmDialog() {
        dialog = new Dialog(this);
//        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment_confirm);

        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Mapping
        tv_dialog_change            = dialog.findViewById(R.id.dialog_tv_change);
        btn_dialog_print            = dialog.findViewById(R.id.dialog_btn_print);
        btn_dialog_confirm          = dialog.findViewById(R.id.dialog_btn_confirm);
        btn_dialog_confrimAndPrint  = dialog.findViewById(R.id.dialog_btn_confirm_and_print);

        double change = Double.parseDouble(strReceive) - payment_total;
        tv_dialog_change.setText(df.format(change));
        if(change < 0){tv_dialog_change.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));};
        if(change >= 0){tv_dialog_change.setTextColor(ContextCompat.getColor(this, R.color.black));};

        // onclick event Dialog
        btn_dialog_confirm.setOnClickListener(this);
        if(listPrinter != null && listPrinter.size() > 0){
            btn_dialog_print.setClickable(true);
            btn_dialog_print.setBackgroundResource(R.drawable.background_r10_effect_color_primary);
            btn_dialog_print.setOnClickListener(this);

            btn_dialog_confrimAndPrint.setClickable(true);
            btn_dialog_confrimAndPrint.setBackgroundResource(R.drawable.background_r10_effect_color_primary);
            btn_dialog_confrimAndPrint.setOnClickListener(this);
        }else{
            btn_dialog_print.setClickable(false);
            btn_dialog_print.setBackgroundResource(R.drawable.background_r10_color_grey500);

            btn_dialog_confrimAndPrint.setClickable(false);
            btn_dialog_confrimAndPrint.setBackgroundResource(R.drawable.background_r10_color_grey500);
        }

        dialog.show();

    }

    private void showChange() {
        double change = Double.parseDouble(strReceive) - payment_total;
        tv_change.setText(df.format(change));
        if(change < 0){tv_change.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));};
        if(change >= 0){tv_change.setTextColor(ContextCompat.getColor(this, R.color.white));};

    }

    private void showAmountReceive(String strNumber) {
        if (hasPoint){
            int index = strReceive.indexOf(".");
            if (strNumber.length() >= 2){ return; }
            if (strReceive.substring(index).length() >= 3){ return; }
        }

        if (tv_tender.getText().toString().trim().equals("0")) {
            strReceive = strNumber;
        } else {
            strReceive += strNumber;
        }

        DecimalFormat df = new DecimalFormat(",##0.##");
        tv_tender.setText(df.format(Double.parseDouble(strReceive)));
        showChange();
    }









    private void initView() {
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_00 = findViewById(R.id.btn_00);
        btn_dot = findViewById(R.id.btn_dot_sign);
        btn_delete = findViewById(R.id.btn_delete);
        btn_clear = findViewById(R.id.btn_clear);
        btn_OK = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.ib_back);

        tv_pay = findViewById(R.id.tv_pay);
        tv_tender = findViewById(R.id.tv_tender);
        tv_change = findViewById(R.id.tv_change);

        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_dot.setOnClickListener(this);
        btn_00.setOnClickListener(this);
        btn_OK.setOnClickListener(this);

        btn_back.setOnClickListener(this);
    }

    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Payment.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Payment.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Payment.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Payment.java - line: " + n + " ==============================\n" + str);
    }
    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}