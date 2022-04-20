package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.IconObj;
import com.c_m_p.poscoffeeshop.R;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterSelectImageBackground extends RecyclerView.Adapter<AdapterSelectImageBackground.SelectImageViewHolder>{

    private List<IconObj> listImage;
    private Context context;

    IOnClickItem mIOnClickItem;
    public interface IOnClickItem{
        void onClickItemListener(IconObj iconObj);
    }

    public AdapterSelectImageBackground(Context context, IOnClickItem mIOnClickItem) {
        this.context = context;
        this.mIOnClickItem = mIOnClickItem;
    }

    public void setData(List<IconObj> listImage){
        this.listImage = listImage;
    }

    @NonNull
    @NotNull
    @Override
    public SelectImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_image_background,
                parent,
                false
        );
        return new SelectImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectImageViewHolder holder, int position) {
        IconObj imageObj = listImage.get(position);

        holder.iv_background.setImageResource(
                imageObj.getImageId()
        );

        holder.iv_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOnClickItem.onClickItemListener(imageObj);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listImage != null){
            return listImage.size();
        }
        return 0;
    }

    class SelectImageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ln_item;
        ShapeableImageView iv_background;

        public SelectImageViewHolder(@NonNull @NotNull View v) {
            super(v);

            ln_item = v.findViewById(R.id.ln_item);
            iv_background = v.findViewById(R.id.siv_background);

        }
    }
}
