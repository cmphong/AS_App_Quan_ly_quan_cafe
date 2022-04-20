package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterCategoryViewPager extends RecyclerView.Adapter<AdapterCategoryViewPager.CategoryViewHolder> {

    private List<Category> mList;
    private Context context;

    private IOnCategeroyItem iOnCategeroyItem;
    public interface IOnCategeroyItem {
        void onClickItemListener(Category category);
    }

    public void setData(List<Category> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }

    public AdapterCategoryViewPager(Context context, IOnCategeroyItem iOnCategeroyItem){
        this.context = context;
        this.iOnCategeroyItem = iOnCategeroyItem;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_category, parent, false
        );
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryViewHolder holder, int position) {
        Category categoryCurrent = mList.get(position);
        holder.tv_name.setText(categoryCurrent.getName());
        holder.iv_icon.setImageResource(
                context.getResources().getIdentifier(
                        categoryCurrent.getIcon(),
                        "drawable",
                        context.getPackageName()
                ));

        if(categoryCurrent.isSelected()) {
            holder.wrapper_ic.setBackgroundResource(R.drawable.background_r100_color_primary);
            holder.iv_icon.setColorFilter(ContextCompat.getColor(context, R.color.white));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.primary));

        }else{
            holder.wrapper_ic.setBackground(null);
            holder.iv_icon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.item_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatusItem(categoryCurrent);
                iOnCategeroyItem.onClickItemListener(categoryCurrent);
                notifyDataSetChanged();
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

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_category;
        ImageView iv_icon;
        TextView tv_name;
        LinearLayout wrapper_ic;

        public CategoryViewHolder(@NonNull @NotNull View v) {
            super(v);
            item_category = v.findViewById(R.id.item_category);
            iv_icon = v.findViewById(R.id.iv_category_icon);
            tv_name = v.findViewById(R.id.tv_category_name);
            wrapper_ic = v.findViewById(R.id.wrapper_icon_category);
        }
    }

    private void changeStatusItem(Category categoryCurrent) {
        for(Category category : mList){
            if(category.isSelected()){
                category.setSelected(false);
            }
        }
        categoryCurrent.setSelected(true);
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== AdapterCategory.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== AdapterCategory.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== AdapterCategory.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== AdapterCategory.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
