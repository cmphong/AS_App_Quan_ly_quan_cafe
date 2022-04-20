package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterCheckout;
import com.c_m_p.poscoffeeshop.Adapter.AdapterTable;
import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.MyUtils.Money;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Activity_CheckOut extends AppCompatActivity implements AdapterCheckout.IClickButton {
    public static String TABLE_CHECKOUT = "TABLE_CHECKOUT",
                         TABLE_OBJ      = "TABLE_OBJ";

    List<Ordered> mList;
    AdapterCheckout adapter;
    RecyclerView rv_checkout_item;

    TextView tv_title, tv_sub_total, tv_tax, tv_other_charge, tv_amount;
    FrameLayout btn_add_new;
    Button btn_cancel, btn_pay;
    Table table;
    DecimalFormat df;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        initialView();

        df = CommonData.setCurrencyByLanguageApp(this);
        mList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            table = (Table) bundle.getSerializable(AdapterTable.TABLE_ORDERED); // add new

            mList = table.getItems();
        }

        tv_title.setText(table.getName());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_checkout_item.setLayoutManager(linearLayoutManager);

        adapter = new AdapterCheckout(this, this);
        adapter.setData(mList);
        rv_checkout_item.setAdapter(adapter);

        setAmount();

        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_CheckOut.this, Activity_Order.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(TABLE_CHECKOUT, table);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_CheckOut.this, Activity_Payment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(TABLE_OBJ, table);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClickButtonMinus(int position) {
        Ordered ordered = mList.get(position);
        ordered.setQuantity(ordered.getQuantity() - 1);
        adapter.setData(mList);
        setAmount();

        updateFbRefTable(ordered, false);
    }

    @Override
    public void onClickButtonPlus(int position) {
        Ordered ordered = mList.get(position);
        ordered.setQuantity(ordered.getQuantity() + 1);
        adapter.setData(mList);
        setAmount();

        updateFbRefTable(ordered, true);
    }

    private void updateFbRefTable(Ordered ordered, boolean isIncrease) {
        Logdln("Table id: " +table.getId()+ "\n"+
                        "name: " +table.getName()+ "\n"+
                        "orderingTotal: " +table.getOrderingTotal()+ "\n"+
                        "checkin_time: " +table.getCheckin_time()+ "\n"+
                        "isOrdering: " +table.getIsOrdering()+ "\n"+
                        "items: " +table.getItems().size()
                , 127);




//        List<Ordered> listOrdering = new ArrayList<>();
//        for (Drink drink : listDrinkSelected){
//            listOrdering.add(new Ordered(drink.getId(), drink.getName(), Float.parseFloat(drink.getPrice()), drink.getOrdering()));
//            mList <============
//        }

        String tableId = table.getId();
////        table.setIsOrdering(true);
//        table.setOrderingTotal(Activity_Order.getOrderingTotal());
//        table.setCheckin_time(String.valueOf(System.currentTimeMillis()));
//        table.setItems(mList);

        for(Ordered item : table.getItems()){
            if(item.getId().equals(ordered.getId())){
                item.setQuantity(ordered.getQuantity());
            }
        }
        if(isIncrease) {
            table.setOrderingTotal(table.getOrderingTotal() + 1);
        }else{
            table.setOrderingTotal(table.getOrderingTotal() - 1);
        }
//
        CommonData.fbRef_Table.child(tableId).updateChildren(table.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast("Update successful!");
                Logdln("Update successful!", 79);
            }
        });
    }



    private void setAmount(){
        float sumSubTotal = sumSubTotal();
        float tax = sumSubTotal*0.1f;
        float otherCharge = 0f;
        float amount = sumSubTotal + tax + otherCharge;
        tv_sub_total.setText(df.format(sumSubTotal));
        tv_tax.setText(df.format(tax)); //  10%
        tv_other_charge.setText(df.format(otherCharge));
        tv_amount.setText(df.format(amount));
    }

    private float sumSubTotal() {
        float result = 0f;
        for(Ordered orderedObj : mList){
            result += orderedObj.getTotal();
        }
        return result;
    }


    private void initialView() {
        tv_title         = findViewById(R.id.tv_title);
        btn_add_new       = findViewById(R.id.fl_add_new);
        rv_checkout_item = findViewById(R.id.rv_checkout_item);
        tv_sub_total     = findViewById(R.id.tv_sub_total);
        tv_tax           = findViewById(R.id.tv_tax);
        tv_other_charge  = findViewById(R.id.tv_other_charge);
        tv_amount        = findViewById(R.id.tv_amount);
        btn_cancel       = findViewById(R.id.btn_cancel);
        btn_pay  = findViewById(R.id.btn_pay);
    }




    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_CheckOut.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_CheckOut.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_CheckOut.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_CheckOut.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}