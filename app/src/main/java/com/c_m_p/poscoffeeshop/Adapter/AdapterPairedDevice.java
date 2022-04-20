package com.c_m_p.poscoffeeshop.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Model.PairedDevice;
import com.c_m_p.poscoffeeshop.MyUtils.PrintContent;
import com.c_m_p.poscoffeeshop.R;
import com.emh.thermalprinter.connection.bluetooth.BluetoothConnection;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/* TODO
    [ ] remove BluetoothConnection variable
 */
public class AdapterPairedDevice
        extends RecyclerView.Adapter<AdapterPairedDevice.PairedDeviceViewHolder>
        implements ActivityCompat.OnRequestPermissionsResultCallback{

    List<PairedDevice> mList;
    PairedDevice pairedDeviceGlobal; // usage in OnRequestPermissionResult()
    BluetoothConnection[] arrBluetoothDevice;
    Activity activity;

    IOnPairedDevcieItem iOnPairedDevcieItem;



    public interface IOnPairedDevcieItem{
        void setOnClickItemListener(BluetoothConnection bluetoothConnection);
        void setOnClickTestButtonListener(BluetoothConnection device);
    }

    public void setData(List<PairedDevice> pairedDeviceList, BluetoothConnection[] arrBluetoothDevice){
        mList = pairedDeviceList;
        this.arrBluetoothDevice = arrBluetoothDevice;
    }

    public AdapterPairedDevice(Activity activity, IOnPairedDevcieItem iOnPairedDevcieItem) {
        this.activity = activity;
        this.iOnPairedDevcieItem = iOnPairedDevcieItem;
    }

    @NonNull
    @NotNull
    @Override
    public PairedDeviceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_paired_device, parent, false
        );

        return new PairedDeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PairedDeviceViewHolder holder, int position) {
        PairedDevice pairedDevice = mList.get(position);

        holder.tv_name.setText(pairedDevice.getName());
        holder.tv_mac_address.setText(pairedDevice.getMacAddress());
        if(pairedDevice.isSelect()){
            holder.iv_is_select.setVisibility(View.VISIBLE);
            holder.btn_paired_device_test.setVisibility(View.VISIBLE);
        }else{
            holder.iv_is_select.setVisibility(View.GONE);
            holder.btn_paired_device_test.setVisibility(View.GONE);
        }
        holder.rl_printer_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (PairedDevice item : mList){
                    if(item.isSelect() == true){
                        item.setSelect(false);
                    }
                }
                pairedDeviceGlobal = pairedDevice; // usage for method OnRequestPermissionResult()
                pairedDevice.setSelect(!pairedDevice.isSelect());

                for(BluetoothConnection device : arrBluetoothDevice){
                    if(pairedDevice.getMacAddress().equals(device.getDevice().getAddress())){
                        iOnPairedDevcieItem.setOnClickItemListener(device);
                    }
                }
                notifyDataSetChanged();
            }
        });

        holder.btn_paired_device_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BluetoothConnection device : arrBluetoothDevice){
                    if(pairedDevice.getMacAddress().equals(device.getDevice().getAddress())){
//                        PrintContent.printTestBLUETOOTH(activity, device);
                        iOnPairedDevcieItem.setOnClickTestButtonListener(device);
                    }
                }
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

    class PairedDeviceViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_printer_item;
        TextView tv_name, tv_mac_address;
        ImageView iv_is_select;
        Button btn_paired_device_test;

        public PairedDeviceViewHolder(@NonNull @NotNull View v) {
            super(v);

            rl_printer_item = v.findViewById(R.id.rl_printer_item);
            tv_name         = v.findViewById(R.id.tv_device_name);
            tv_mac_address  = v.findViewById(R.id.tv_macAddress);
            iv_is_select    = v.findViewById(R.id.iv_is_select);
            btn_paired_device_test = v.findViewById(R.id.btn_paired_device_test);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode == PrintContent.PERMISSION_BLUETOOTH_CODE){
                if(pairedDeviceGlobal != null) {
                    for (BluetoothConnection item : arrBluetoothDevice){
                        if(pairedDeviceGlobal.getMacAddress().equals(item.getDevice().getAddress())){
//                            PrintContent.printTestBLUETOOTH(activity, item);
                            iOnPairedDevcieItem.setOnClickTestButtonListener(item);
                        }
                    }
                }
            }
        }
    }
}
