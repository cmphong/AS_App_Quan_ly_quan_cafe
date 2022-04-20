package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterSelectCategory extends RecyclerView.Adapter<AdapterSelectCategory.SelectCategoryViewHolder>{

    private List<Category> listCategory;
    private Context context;

    IOnClickItem mIOnClickItem;
    public interface IOnClickItem{
        void onClickItemListener(Category category);
    }

    public void setData(List<Category> listCategory){
        Category category = new Category();
        List<Category> listTmp = new ArrayList<>(listCategory);
        listTmp.add(0, category);
        this.listCategory = listTmp;
        notifyDataSetChanged();


    }

    public AdapterSelectCategory(Context context, IOnClickItem mIOnClickItem) {
        this.context = context;
        this.mIOnClickItem = mIOnClickItem;
    }

    @NonNull
    @NotNull
    @Override
    public SelectCategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SelectCategoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectCategoryViewHolder holder, int position) {
        if(position == 0){
            holder.ll_first_item.setVisibility(View.VISIBLE);
            holder.ll_category_item.setVisibility(View.GONE);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "2222222", Toast.LENGTH_SHORT).show();
                    mIOnClickItem.onClickItemListener(null);
                }
            });
        }else {
            Category category = listCategory.get(position);
            holder.ll_first_item.setVisibility(View.GONE);
            holder.ll_category_item.setVisibility(View.VISIBLE);

            int imageId = context.getResources().getIdentifier(
                    category.getIcon(), "drawable", context.getPackageName()
            );
            holder.iv_icon.setImageResource(imageId);
            String name = category.getName();
            holder.tv_name.setText(name);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIOnClickItem.onClickItemListener(category);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(listCategory != null){
            return listCategory.size();
        }
        return 0;
    }

    class SelectCategoryViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout item;
        LinearLayout ll_first_item, ll_category_item;
        ImageView iv_icon;
        TextView tv_name;

        public SelectCategoryViewHolder(@NonNull @NotNull View v) {
            super(v);
            item    = v.findViewById(R.id.item_category);
            ll_first_item = v.findViewById(R.id.ll_first_item);
            ll_category_item = v.findViewById(R.id.ll_category_item);
            iv_icon = v.findViewById(R.id.iv_category_icon);
            tv_name = v.findViewById(R.id.tv_category_name);
        }
    }
}
