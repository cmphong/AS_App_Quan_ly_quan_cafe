package com.c_m_p.poscoffeeshop.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Activity_CheckOut;
import com.c_m_p.poscoffeeshop.Activity_Order;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.MyUtils;
import com.c_m_p.poscoffeeshop.R;

import java.util.List;

public class AdapterTable extends RecyclerView.Adapter<AdapterTable.TableViewHolder>{
    public static String TABLE_ORDERED = "TABLE_ORDERED",
                         TABLE_ORDERING = "TABLE_ORDERING";

    List<Table> mList;
    Activity activity;

    IOnTableItem mIOnTableItem;
    public interface IOnTableItem{
        void setOnLongClickTableItemListener(Table table);
    }

    public void setData(List<Table> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }


    public AdapterTable(Activity activity, IOnTableItem iOnTableItem) {
        this.activity = activity;
        mIOnTableItem = iOnTableItem;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_table,
                parent,
                false
        );

        v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = activity.getMenuInflater();
                inflater.inflate(R.menu.context_menu_category, menu);
            }
        });

        return new TableViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterTable.TableViewHolder holder, int position) {
        String checkinTime = "",
                checkoutTime = "";

        Table table = mList.get(position);
        holder.tv_table_name.setText(table.getName());
        if(table.getCheckin_time() != null){
            checkinTime = MyUtils.convertToTime(table.getCheckin_time());
        }
        if(table.getCheckout_time() != null){
            checkoutTime = MyUtils.convertToTime(table.getCheckout_time());
        }

        if(table.getIsOrdering()){
            holder.tv_checkin_time.setText(checkinTime);

            holder.table_item.setBackgroundResource(R.drawable.background_r10_effect_color_primary);
            holder.tv_ordering_total.setText(
                    activity.getResources().getText(R.string.Ordered) + " " +
                    table.getOrderingTotal() + " " +
                    (table.getOrderingTotal() > 1
                            ? activity.getResources().getText(R.string.items)
                            : activity.getResources().getText(R.string.item))
            );
        }else{
            holder.table_item.setBackgroundResource(R.drawable.background_r10_effect_stroke_color_primary);
            holder.tv_ordering_total.setText(R.string.No_Order);
            holder.tv_checkin_time.setText(checkoutTime);
        }

        holder.table_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(table.getIsOrdering()) {
                    Intent intent = new Intent(activity, Activity_CheckOut.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(TABLE_ORDERED, table);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }else {
                    Intent intent = new Intent(activity, Activity_Order.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(TABLE_ORDERING, table);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            }
        });
        holder.table_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIOnTableItem.setOnLongClickTableItemListener(table);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mList != null){
            return mList.size();
        }
        return 0;
    }

    class TableViewHolder extends RecyclerView.ViewHolder{

        TextView tv_table_name,
                tv_checkin_time,
                tv_ordering_total;
        RelativeLayout table_item;

        public TableViewHolder(@NonNull View v) {
            super(v);

            tv_table_name       = v.findViewById(R.id.tv_table_name);
            tv_checkin_time     = v.findViewById(R.id.tv_checkin_time);
            tv_ordering_total   = v.findViewById(R.id.tv_ordering_total);
            table_item          = v.findViewById(R.id.table_item);

        }
    }





    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== AdapterTable.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== AdapterTable.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== AdapterTable.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== AdapterTable.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
