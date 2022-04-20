package com.c_m_p.poscoffeeshop.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.R;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterDrink extends RecyclerView.Adapter<AdapterDrink.DrinkViewHolder> {
    int count = 0;

    List<Drink> listDrink;
    Activity context;

    private IOnDrinkItem iOnDrinkItem;
    public interface IOnDrinkItem {
        void onClickItemListener(Drink drink);
        void onLongClickItemListener(Drink drink);
    }

    public void setData(List<Drink> listDrink){
        this.listDrink = listDrink;
        notifyDataSetChanged();
    }

    public AdapterDrink(Activity context, IOnDrinkItem iOnDrinkItem) {
        this.context = context;
        this.iOnDrinkItem = iOnDrinkItem;
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
        DecimalFormat df = CommonData.setCurrencyByLanguageApp(context);
        Drink drink = listDrink.get(position);
        float price = Float.parseFloat(drink.getPrice());
        holder.siv_background.setImageResource(
                context.getResources().getIdentifier(
                        drink.getImage(), "drawable", context.getPackageName()
                )
        );
        holder.tv_drink_name.setText(CommonData.capitalize(drink.getName()));
        holder.tv_drink_price.setText(df.format(price));
        holder.tv_selected_count.setText(String.valueOf(drink.getOrdered()));


        holder.rl_item_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOnDrinkItem.onClickItemListener(drink);
            }
        });

        holder.rl_item_drink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                iOnDrinkItem.onLongClickItemListener(drink);
                return false;
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

        public DrinkViewHolder(@NonNull @NotNull View v) {
            super(v);
            rl_item_drink = v.findViewById(R.id.rl_item_drink);
            siv_background = v.findViewById(R.id.siv_background);
            tv_drink_name = v.findViewById(R.id.tv_drink_name);
            tv_drink_price = v.findViewById(R.id.tv_drink_price);
            tv_selected_count = v.findViewById(R.id.tv_selected_count);

            v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(0, 21, 0, context.getString(R.string.Edit));
                    menu.add(0, 22, 0, context.getString(R.string.Delete));
                }
            });
        }
    }
}
