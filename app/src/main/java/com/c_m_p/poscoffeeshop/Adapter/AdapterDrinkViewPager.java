package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.R;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterDrinkViewPager extends RecyclerView.Adapter<AdapterDrinkViewPager.DrinkViewHolder> {
    int orderingTotal = 0;

    List<Drink> listDrink;
    Context context;

    public void setData(List<Drink> listDrink){
        this.listDrink = listDrink;
        notifyDataSetChanged();
    }
    public void setOrderingTotal(int orderingTotal){
        this.orderingTotal = orderingTotal;
    }

    public IOnclickItem mIOnclickItem;
    public interface IOnclickItem{
        void updateOrderingCounterItem(Drink drink, boolean isIncreasement);
        void updateOrderingTotalToolbar(int orderingTotal);
    }


    public AdapterDrinkViewPager(Context context, IOnclickItem iOnclickItem) {
        this.context = context;
        this.mIOnclickItem = iOnclickItem;
    }

    @NonNull
    @NotNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_drink,
                parent,
                false
        );

        return new DrinkViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull DrinkViewHolder holder, int position) {

        Drink drink = listDrink.get(position);
        float price = Float.parseFloat(drink.getPrice());

        DecimalFormat df = CommonData.setCurrencyByLanguageApp(context);
        holder.siv_background.setImageResource(
                context.getResources().getIdentifier(
                        drink.getImage(), "drawable", context.getPackageName()
                )
        );
        holder.tv_drink_name.setText(CommonData.capitalize(drink.getName()));
        holder.tv_drink_price.setText(df.format(price));
        holder.tv_selected_count.setText(String.valueOf(drink.getOrdering()));
        if(drink.getOrdering() > 0){
            holder.fl_minus.setVisibility(View.VISIBLE);
        }else{
            holder.fl_minus.setVisibility(View.GONE);
        }

        holder.rl_item_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderingTotal++;
                mIOnclickItem.updateOrderingCounterItem(drink, true);
                mIOnclickItem.updateOrderingTotalToolbar(orderingTotal);
            }
        });

        holder.fl_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderingTotal > 0){
                    orderingTotal--;
                    mIOnclickItem.updateOrderingCounterItem(drink, false);
                    mIOnclickItem.updateOrderingTotalToolbar(orderingTotal);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listDrink != null){
            return listDrink.size();
        }
        return 0;
    }

    class DrinkViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_item_drink;
        ShapeableImageView siv_background;
        TextView tv_drink_name, tv_drink_price, tv_selected_count;
        FrameLayout fl_minus;

        public DrinkViewHolder(@NonNull @NotNull View v) {
            super(v);
            rl_item_drink = v.findViewById(R.id.rl_item_drink);
            siv_background = v.findViewById(R.id.siv_background);
            tv_drink_name = v.findViewById(R.id.tv_drink_name);
            tv_drink_price = v.findViewById(R.id.tv_drink_price);
            tv_selected_count = v.findViewById(R.id.tv_selected_count);
            fl_minus = v.findViewById(R.id.fl_minus);
        }
    }





    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== AdapterDrinkOrder.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== AdapterDrinkOrder.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== AdapterDrinkOrder.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== AdapterDrinkOrder.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
