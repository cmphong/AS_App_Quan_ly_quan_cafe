package com.c_m_p.poscoffeeshop.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.MyThermalPrinter;
import com.c_m_p.poscoffeeshop.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterPrint extends RecyclerView.Adapter<AdapterPrint.PrintViewHolder> {

    private List<MyThermalPrinter> listPrinter;
    Activity context;

    private IOnPrintItem mIOnPrintItem;
    public interface IOnPrintItem{
        void setOnCLickItemListener(MyThermalPrinter currentPrinter, int position);
    }

    public void setData(List<MyThermalPrinter> listPrinter){
        this.listPrinter = listPrinter;
        notifyDataSetChanged();
    }

    public AdapterPrint(Activity context, IOnPrintItem mIOnPrintItem) {
        this.context = context;
        this.mIOnPrintItem = mIOnPrintItem;
    }

    @NonNull
    @NotNull
    @Override
    public PrintViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_printer, parent, false
        );

        return new PrintViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PrintViewHolder holder, int position) {
        MyThermalPrinter myPrinter = (MyThermalPrinter) listPrinter.get(position);
        holder.tv_print_name.setText(myPrinter.getName());
        if(position == 0){
            holder.tv_default.setVisibility(View.VISIBLE);
        }else{
            holder.tv_default.setVisibility(View.GONE);
        }
        holder.tv_method.setText(myPrinter.getMethod());
        holder.tv_paper_size.setText(
                myPrinter.getPaperSize().equals("80")
                ? "80mm"
                : "57/58mm"
        );

        holder.ln_printer_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOnPrintItem.setOnCLickItemListener(myPrinter, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listPrinter != null){
            return listPrinter.size();
        }
        return 0;
    }


    class PrintViewHolder extends RecyclerView.ViewHolder {

        TextView tv_print_name, tv_method, tv_paper_size, tv_default;
        LinearLayout ln_printer_item;

        public PrintViewHolder(@NonNull @NotNull View v) {
            super(v);

            ln_printer_item = v.findViewById(R.id.rl_printer_item);
            tv_print_name   = v.findViewById(R.id.tv_print_name);
            tv_default      = v.findViewById(R.id.tv_default);
            tv_method       = v.findViewById(R.id.tv_method);
            tv_paper_size   = v.findViewById(R.id.tv_paper_size);

        }
    }
}
