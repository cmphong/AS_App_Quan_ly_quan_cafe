package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterTable;
import com.c_m_p.poscoffeeshop.Adapter.AdapterViewPagerOrder;
import com.c_m_p.poscoffeeshop.Interface.IListFilter;
import com.c_m_p.poscoffeeshop.Interface.Middle_ActivityOrder_and_FragmentCategoryViewPager;
import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.Model.Table;

import java.util.ArrayList;
import java.util.List;

public class Activity_Order extends AppCompatActivity implements Middle_ActivityOrder_and_FragmentCategoryViewPager {

    RelativeLayout ln_red_circle;
    TextView tv_waiters_count;
    ViewPager2 vp2;

    Table table;

    Fragment_Category_ViewPager fragment1;
    Fragment_Order_Detail_ViewPager fragment2;

    public static int orderingTotal = 0;
    List<Ordered> listOrderedItems;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setActionBar();

        // Handle data from ActivityCheckout
        bundle = getIntent().getExtras();
        if(bundle != null){
            Table table_checkout = (Table) bundle.getSerializable(Activity_CheckOut.TABLE_CHECKOUT);
            Table table_ordered  = (Table) bundle.getSerializable(AdapterTable.TABLE_ORDERING);
            if(table_checkout != null) {
                table = table_checkout;
                if (table.getIsOrdering()) {
                    listOrderedItems = table.getItems();
                    orderingTotal = table.getOrderingTotal();
                }
            }

            if(table_ordered != null){
                table = table_ordered;
                orderingTotal = 0;
            }
        }

        fragment1 = new Fragment_Category_ViewPager();
        fragment2 = new Fragment_Order_Detail_ViewPager();
        IListFilter iListFilterInstance = fragment2.getIListFilterInstance();
        fragment1.setIListFilter(iListFilterInstance); // pass list to fragment2
        fragment1.setListOrdered(listOrderedItems);
        fragment1.setOrderingTotal(orderingTotal);

        fragment2.setTableData(table);
        fragment2.setOrderingTotal(orderingTotal); // for Firebase

        List<Fragment> listFragment = new ArrayList<>();
        listFragment.add(fragment1);
        listFragment.add(fragment2);

        vp2 = findViewById(R.id.vp_order);
        AdapterViewPagerOrder adapter = new AdapterViewPagerOrder(this);
        adapter.setData(listFragment);
        vp2.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_ok:
                fragment2.orderConfirm(Activity_Order.this);
//                finish();
                break;

            case R.id.menu_waiters: // usage callback, it's working fine
                switch (vp2.getCurrentItem()){
                    case 0:
                        vp2.setCurrentItem(1);
                        break;
                    case 1:
                        vp2.setCurrentItem(0);
                        break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem menuItem = menu.findItem(R.id.menu_waiters);
        View rootView = menuItem.getActionView();

        ln_red_circle = rootView.findViewById(R.id.rl_alert_red_circle);
        tv_waiters_count = rootView.findViewById(R.id.tv_waiters_count);

        // Handle data from ActivityCheckout
        if(listOrderedItems != null){
            tv_waiters_count.setText(String.valueOf(orderingTotal));
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_drink, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void updateWaitersCount(int orderingTotal) {
        this.orderingTotal = orderingTotal;
        tv_waiters_count.setText(String.valueOf(orderingTotal));
    }

    private void setActionBar(){
        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar = findViewById(R.id.toolbar_choose_drink);
        setSupportActionBar(myChildToolbar);

        // SHOW ARROW BUTTON BACK TO PARENT ACTIVITY (Activity_Dashboard)
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_left); // set Icon up
        }
    }

    public static int getOrderingTotal(){
        return orderingTotal;
    }




    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Order.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Order.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Order.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Order.java - line: " + n + " ==============================\n" + str);
    }
    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}