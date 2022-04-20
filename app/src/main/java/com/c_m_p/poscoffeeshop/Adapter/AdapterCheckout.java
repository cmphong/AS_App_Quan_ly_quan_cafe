package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Activity_Order;
import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class AdapterCheckout extends RecyclerView.Adapter<AdapterCheckout.CheckoutViewHolder> {

    Context context;

    public interface IClickButton{
        void onClickButtonMinus(int position);
        void onClickButtonPlus(int position);
    }

    private IClickButton iClickButton;
    private List<Ordered> mList;
//    private Table table;
//    private Context context;

    public void setData(List<Ordered> list){
        this.mList = list;
        notifyDataSetChanged();
    }
//    public void setTable (Table table){
//        this.table = table;
//    }

    public AdapterCheckout(Context context, IClickButton iClickButton) {
        this.context = context;
        this.iClickButton = iClickButton;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);

        return new CheckoutViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {

        Ordered ordered = mList.get(position);

        DecimalFormat df = CommonData.setCurrencyByLanguageApp(context);
        holder.tv_item_name.setText(ordered.getName());
        holder.tv_price.setText(df.format(ordered.getPrice()));
        holder.tv_quantities.setText(String.valueOf(ordered.getQuantity()));
        holder.tv_total.setText(df.format(ordered.getTotal()));


        holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ordered.getQuantity() >= 1) {
                    iClickButton.onClickButtonMinus(position);
                }
            }
        });

        holder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickButton.onClickButtonPlus(position);
            }
        });

        if(ordered.getQuantity() <= 1){
            holder.btn_decrease.setClickable(false);
            holder.btn_decrease.setImageResource(R.drawable.button_minus_grey);
        }else{
            holder.btn_decrease.setClickable(true);
            holder.btn_decrease.setImageResource(R.drawable.button_minus);
        }
    }

    @Override
    public int getItemCount() {
        if(mList != null){
            return mList.size();
        }
        return 0;
    }

    class CheckoutViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_item_name, tv_price, tv_quantities, tv_total;
        private ImageButton btn_decrease, btn_increase;

        public CheckoutViewHolder(@NonNull View v) {
            super(v);

            tv_item_name = v.findViewById(R.id.tv_item_name);
            tv_price = v.findViewById(R.id.tv_price);
            tv_quantities = v.findViewById(R.id.tv_quantities);
            tv_total = v.findViewById(R.id.tv_total);
            btn_decrease = v.findViewById(R.id.btn_decrease);
            btn_increase = v.findViewById(R.id.btn_increase);
        }
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== AdapterCheckout.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== AdapterCheckout.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== AdapterCheckout.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== AdapterCheckout.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
