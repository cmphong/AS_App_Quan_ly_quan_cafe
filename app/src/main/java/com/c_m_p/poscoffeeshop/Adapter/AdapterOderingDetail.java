package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.R;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterOderingDetail extends RecyclerView.Adapter<AdapterOderingDetail.OrderingDetailViewHolder> {

    List<Drink> listDrink;
    Context context;

    public void setData(List<Drink> listDrink){
        this.listDrink = listDrink;
        notifyDataSetChanged();
    }

    public AdapterOderingDetail(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public OrderingDetailViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_ordering_detail, parent, false
        );
        return new OrderingDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderingDetailViewHolder holder, int position) {
        Drink drink = listDrink.get(position);

        holder.siv_image.setImageResource(context.getResources().getIdentifier(
                drink.getImage(), "drawable", context.getPackageName()
        ));
        holder.tv_name.setText(drink.getName());
        holder.tv_count.setText("x" +drink.getOrdering());
        holder.tv_price.setText("$" +drink.getAmount());

    }

    @Override
    public int getItemCount() {
        if(listDrink != null){
            return listDrink.size();
        }
        return 0;
    }

    class OrderingDetailViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView siv_image;
        TextView tv_name, tv_count, tv_price;

        public OrderingDetailViewHolder(@NonNull @NotNull View v) {
            super(v);

            siv_image = v.findViewById(R.id.siv_ordering_detail_item_image);
            tv_name = v.findViewById(R.id.tv_ordering_detail_item_name);
            tv_count = v.findViewById(R.id.tv_ordering_detail_item_count);
            tv_price = v.findViewById(R.id.tv_ordering_detail_item_price);

        }
    }
}
