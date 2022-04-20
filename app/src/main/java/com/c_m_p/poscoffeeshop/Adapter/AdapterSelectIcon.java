package com.c_m_p.poscoffeeshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.IconObj;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.c_m_p.poscoffeeshop.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterSelectIcon extends RecyclerView.Adapter<AdapterSelectIcon.IconViewHolder> {



    private List<IconObj> listIcon;
    private Context context;

    IOnClickItem mIOnClickItem;
    public interface IOnClickItem{
        void onClickItemListener(IconObj iconObj);
    }

    public void setData(List<IconObj> listIcon){
        this.listIcon = listIcon;
        notifyDataSetChanged();
    }

    public AdapterSelectIcon(Context context, IOnClickItem mIOnClickItem) {
        this.context = context;
        this.mIOnClickItem = mIOnClickItem;
    }

    @NonNull
    @NotNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new IconViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IconViewHolder holder, int position) {
        IconObj iconObj = listIcon.get(position);
        holder.iv_icon.setImageResource(iconObj.getImageId());
        String name = iconObj.getName().substring(7); // ic_cat_abcxyz.png, ic_cat_abc_xyz.png
        int resId = context.getResources().getIdentifier(
                name, "string", context.getPackageName()
        );
        holder.tv_name.setText(CommonData.capitalize(context.getString(resId)));
        holder.wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOnClickItem.onClickItemListener(iconObj);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listIcon != null){
            return listIcon.size();
        }
        return 0;
    }

    class IconViewHolder extends RecyclerView.ViewHolder {
        LinearLayout wrapper;
        ImageView iv_icon;
        TextView tv_name;

        public IconViewHolder(@NonNull @NotNull View v) {
            super(v);
//            wrapper = v.findViewById(R.id.wrapper_icon_category);
//            iv_icon = v.findViewById(R.id.iv_category_icon);
//            tv_name = v.findViewById(R.id.tv_category_name);
            wrapper = v.findViewById(R.id.wrapper_icon_category);
            iv_icon = v.findViewById(R.id.iv_category_icon);
            tv_name = v.findViewById(R.id.tv_category_name);
        }
    }
}
