package com.c_m_p.poscoffeeshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterOderingDetail;
import com.c_m_p.poscoffeeshop.Interface.IListFilter;
import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* TODO
    [ ] orderingTotal on Firebase = 0
 */
public class Fragment_Order_Detail_ViewPager extends Fragment implements IListFilter {

    IListFilter iListFilter = this;
    Table table;
    int orderingTotal = 0;

    TextView tv_table_name, tv_amount;
    Button btn_ok;
    RecyclerView rv_ordering_detail;
    AdapterOderingDetail adapterOderingDetail;
    List<Drink> listDrinkSelected;

    public IListFilter getIListFilterInstance(){
        return iListFilter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_order_detail, container, false);

        tv_table_name = v.findViewById(R.id.tv_ordering_table_name);
        tv_amount = v.findViewById(R.id.tv_ordering_detail_amount);
        btn_ok = v.findViewById(R.id.btn_ordering_detail_ok);
        rv_ordering_detail = v.findViewById(R.id.rv_ordering_detail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_ordering_detail.setLayoutManager(linearLayoutManager);

        tv_table_name.setText(table.getName());
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderConfirm(getActivity());
            }
        });

        return v;
    }

    public void orderConfirm(Activity activity) {
        updateFbRefTable(activity);
        // startActivity(new Intent(this, ActivityPrint.class));
    }

    private void updateFbRefTable(Activity activity) {
        List<Ordered> listOrdering = new ArrayList<>();
        for (Drink drink : listDrinkSelected){
            listOrdering.add(new Ordered(drink.getId(), drink.getName(), Float.parseFloat(drink.getPrice()), drink.getOrdering()));
        }

        String tableId = table.getId();
        table.setIsOrdering(true);
        table.setOrderingTotal(Activity_Order.getOrderingTotal());
        table.setCheckin_time(String.valueOf(System.currentTimeMillis()));
        table.setItems(listOrdering);

        CommonData.fbRef_Table.child(tableId).updateChildren(table.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {

                showToast(activity, activity.getString(R.string.Order_Successful));
                Logdln("Update successful!", 79);
                activity.finish();
            }
        });

    }


    @Override
    public void setListItemSelected(List<Drink> listDrinkFiltered) {
        listDrinkSelected = listDrinkFiltered;
    }


    @Override
    public void onResume() {
        super.onResume();
        Logd("onResume()");
        adapterOderingDetail = new AdapterOderingDetail(getContext());
        adapterOderingDetail.setData(listDrinkSelected);
        adapterOderingDetail.notifyDataSetChanged();

        rv_ordering_detail.setAdapter(adapterOderingDetail);
    }

    public void setTableData(Table table){
        this.table = table;
    }
    public void setOrderingTotal(int orderingTotal){
        this.orderingTotal = orderingTotal;
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Order_Detail_ViewPager.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Order_Detail_ViewPager.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Order_Detail_ViewPager.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Order_Detail_ViewPager.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}