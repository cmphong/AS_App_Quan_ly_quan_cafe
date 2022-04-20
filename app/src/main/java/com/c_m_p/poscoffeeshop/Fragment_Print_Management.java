package com.c_m_p.poscoffeeshop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterPairedDevice;
import com.c_m_p.poscoffeeshop.Adapter.AdapterPrint;
import com.c_m_p.poscoffeeshop.Model.MyThermalPrinter;
import com.c_m_p.poscoffeeshop.Model.PairedDevice;
import com.c_m_p.poscoffeeshop.MyUtils.MyUUID;
import com.c_m_p.poscoffeeshop.MyUtils.MyUtils;
import com.c_m_p.poscoffeeshop.MyUtils.PrintContent;
import com.emh.thermalprinter.connection.bluetooth.BluetoothConnection;
import com.emh.thermalprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/** TODO<br>
 *   [x] Save ipaddress to SharePreferences<br>
 *   [x] Clean BluetoothConnection object
 */
public class Fragment_Print_Management extends Fragment implements View.OnClickListener {

    public static String PRINT_MANAGER = "PRINT_MANAGER",
                         BLUETOOTH = "BLUETOOTH",
                         NETWORK = "NETWORK",
                         LAN = "LAN",
                         LIST_PRINTER = "LIST_PRINTER",
                         IP_ADDRESS = "IP_ADDRESS",
                         PORT_ADDRESS = "PORT_ADDRESS",
                         BLUETOOTH_ADDRESS = "BLUETOOTH_ADDRESS",
                         PAPER_SIZE = "PAPER_SIZE",
                         PRINT_NAME = "PRINT_NAME";

    private String paperSize = "57";
    private List<MyThermalPrinter> mListPrinter;

    private TextView tv_notYetPrinter;
    private RecyclerView rv_list_printer_created;
    private Button btn_show;

    private Button btn_ADD_test_print, btn_ADD_save_tcp, btn_ADD_save_bluetooth;
    private MaterialButton btn_ADD_bluetooth, btn_ADD_network;
    private RadioButton rb_ADD_2inch, rb_ADD_3inch;
    private RelativeLayout ADD_method_BLUETOOTH, ADD_method_TCP_IP;
    private TextInputEditText tiet_ADD_tcp_print_name, tiet_ADD_ip_address,
                              tiet_ADD_bluetooth_print_name;

    private ImageView iv_EDIT_ic_method;
    private TextView tv_EDIT_title_set_default,
                     tv_EDIT_default,
                     tv_EDIT_method_name,
                     tv_EDIT_title_address,
                     tv_EDIT_address;
    private RadioButton rb_EDIT_2inch, rb_EDIT_3inch;
    private TextInputEditText tiet_EDIT_print_name;
    private Button btn_EDIT_delete, btn_EDIT_save;
    private SwitchMaterial sw_EDIT_default;

    private RecyclerView rv_list_paired_devices;
    private AdapterPrint adapter;

    private String currentMethod = BLUETOOTH;

    private BottomSheetDialog bottomSheetDialog_addPrinter,
                              bottomSheetDialog_editPrinter;
    private SharedPreferences sharedPreferences_print;

    private boolean hasChangePrinter = false;

    private BluetoothConnection selectedDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_print_management, container, false);

        sharedPreferences_print = getContext().getSharedPreferences(PRINT_MANAGER, Context.MODE_PRIVATE);
        mListPrinter = MyUtils.getListPrinterFromSharedPreferences(sharedPreferences_print);

        tv_notYetPrinter        = v.findViewById(R.id.tv_notYetPrinter);
        rv_list_printer_created = v.findViewById(R.id.rv_list_printer_created);
        btn_show                = v.findViewById(R.id.btn_show_bottomSheetDialog);

        if (mListPrinter == null || mListPrinter.size() == 0){
            tv_notYetPrinter.setVisibility(View.VISIBLE);
            rv_list_printer_created.setVisibility(View.GONE);
        }else{
            tv_notYetPrinter.setVisibility(View.GONE);
            rv_list_printer_created.setVisibility(View.VISIBLE);

            showListPrinter();
        }


        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog_addPrinter();
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        if (R.id.rb_ADD_2inch == v.getId()) {
            if (((RadioButton) v).isChecked()) {
                paperSize = "57";
            }
        }
        if (R.id.rb_ADD_3inch == v.getId()) {
            if (((RadioButton) v).isChecked()) {
                paperSize = "80";
            }
        }

        if (R.id.btn_ADD_bluetooth == v.getId()) {
            if (!currentMethod.equals(BLUETOOTH)) {
                currentMethod = BLUETOOTH;
            }
            setStatus();
        }

        if (R.id.btn_ADD_network == v.getId()) {
            if (!currentMethod.equals(NETWORK)) {
                currentMethod = NETWORK;
            }
            setStatus();
        }

        if (R.id.btn_ADD_test_print == v.getId()) {
            testLANPrint();
        }

        if (R.id.btn_ADD_save_tcp == v.getId()) {
            String id = MyUUID.generator();
            String name = tiet_ADD_tcp_print_name.getText().toString().trim();
            String ipAddress = tiet_ADD_ip_address.getText().toString().trim();
            if (!ipAddress.isEmpty()) {
                MyThermalPrinter myThermalPrinter = new MyThermalPrinter(
                        id, name, ipAddress, "", paperSize, LAN
                );
                mListPrinter.add(0, myThermalPrinter);

                setNewPrinterToSharedPreferences(mListPrinter);
            } else {
                showToast(getContext(), getString(R.string.Printer_IP_Address_is_required_try_again));
            }
        }

    }




    // =================================================================
    // ===== SHARED PREFERENCES ========================================
    // =================================================================
    private void setNewPrinterToSharedPreferences(List<MyThermalPrinter> listPrinter) {
        SharedPreferences.Editor editor = sharedPreferences_print.edit();
        editor.putString(LIST_PRINTER, new Gson().toJson(listPrinter));
        if(editor.commit()) {
            showToast(getContext(), getString(R.string.Set_Printer_Successful));
            rv_list_printer_created.setVisibility(View.VISIBLE);
            tv_notYetPrinter.setVisibility(View.GONE);
            showListPrinter();
            bottomSheetDialog_addPrinter.dismiss();
        }else{
            showToast(getContext(), getString(R.string.Save_not_success_try_agian));
        }
    }

    private void updateInfoPrinterToSharedPreferences(MyThermalPrinter currentPrinter, int position){
        SharedPreferences.Editor editor = sharedPreferences_print.edit();
        String id = currentPrinter.getId();
        String name = tiet_EDIT_print_name.getText().toString().trim();
        String paperSize = this.paperSize;

        String ipAddress = currentPrinter.getIpAddress();
        String macAdress = currentPrinter.getMacAddress();
        String method    = currentPrinter.getMethod();

        MyThermalPrinter myThermalPrinter = new MyThermalPrinter(
                id, name, ipAddress, macAdress, paperSize, method
        );

        if(sw_EDIT_default != null && sw_EDIT_default.isChecked()){
            mListPrinter.remove(position);
            mListPrinter.add(0, myThermalPrinter);
        }

        for (int i = 0; i < mListPrinter.size() ; i++){
            if(id.equals(mListPrinter.get(i).getId())){
                mListPrinter.set(i, myThermalPrinter);
            }
        }


        editor.putString(LIST_PRINTER, new Gson().toJson(mListPrinter));
        if(editor.commit()) {
            showToast(getContext(), getString(R.string.Update_Printer_Successful));
            rv_list_printer_created.setVisibility(View.VISIBLE);
            tv_notYetPrinter.setVisibility(View.GONE);
            showListPrinter();
            bottomSheetDialog_editPrinter.dismiss();
        }else{
            showToast(getContext(), getString(R.string.Save_not_success_try_agian));
        }
    }

    // =================================================================
    // ===== BOTTOM SHEET DIALOG =======================================
    // =================================================================
    private void showBottomSheetDialog_editPrinter(MyThermalPrinter currentPrinter, int position) {
        bottomSheetDialog_editPrinter = new BottomSheetDialog(
                getContext(), R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_bottom_sheet_edit_print,
                getActivity().findViewById(R.id.bottom_sheet_dialog_container)
        );
        bottomSheetDialog_editPrinter.setContentView(bottomSheetView);

        tv_EDIT_title_set_default = bottomSheetView.findViewById(R.id.tv_EDIT_title_set_default);
        tv_EDIT_default           = bottomSheetView.findViewById(R.id.tv_EDIT_default);
        sw_EDIT_default         = bottomSheetView.findViewById(R.id.sw_EDIT_default);
        iv_EDIT_ic_method       = bottomSheetView.findViewById(R.id.iv_EDIT_ic_method);
        tv_EDIT_method_name     = bottomSheetView.findViewById(R.id.tv_EDIT_method_name);
        tv_EDIT_title_address   = bottomSheetView.findViewById(R.id.tv_EDIT_title_address);
        tv_EDIT_address         = bottomSheetView.findViewById(R.id.tv_EDIT_address);
        rb_EDIT_2inch           = bottomSheetView.findViewById(R.id.rb_EDIT_2inch);
        rb_EDIT_3inch           = bottomSheetView.findViewById(R.id.rb_EDIT_3inch);
        tiet_EDIT_print_name    = bottomSheetView.findViewById(R.id.tiet_EDIT_print_name);
        btn_EDIT_delete         = bottomSheetView.findViewById(R.id.btn_EDIT_delete);
        btn_EDIT_save           = bottomSheetView.findViewById(R.id.btn_EDIT_save);


        handleDefaultPrinter(currentPrinter, position);

        tv_EDIT_method_name.setText(
                getString(R.string.via) + " "
                + currentPrinter.getMethod()
        );
        if(currentPrinter.getMethod().equals(LAN)){
            iv_EDIT_ic_method.setImageResource(R.drawable.ic_wifi);
            tv_EDIT_title_address.setText(getString(R.string.Ip_Address));
            tv_EDIT_address.setText(currentPrinter.getIpAddress());
        }else if(currentPrinter.getMethod().equals(BLUETOOTH)){
            iv_EDIT_ic_method.setImageResource(R.drawable.ic_bluetooth);
            tv_EDIT_title_address.setText(getString(R.string.MAC_Address));
            tv_EDIT_address.setText(currentPrinter.getMacAddress());
        }

        if(currentPrinter.getPaperSize().equals("57")){
            rb_EDIT_2inch.setChecked(true);
        }else if (currentPrinter.getPaperSize().equals("80")){
            rb_EDIT_3inch.setChecked(true);
        }

        if(currentPrinter.getPaperSize().length() != 0) {
            tiet_EDIT_print_name.setText(currentPrinter.getName());
        }

        rb_EDIT_2inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RadioButton) v).isChecked()) {
                    paperSize = "57";
                }
            }
        });
        rb_EDIT_3inch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RadioButton) v).isChecked()) {
                    paperSize = "80";
                }
            }
        });

        btn_EDIT_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkChange(currentPrinter)) {
                    updateInfoPrinterToSharedPreferences(currentPrinter, position);
                }else{
                    bottomSheetDialog_editPrinter.dismiss();
                }
            }
        });

        btn_EDIT_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(getString(R.string.Confirm));
                alertDialog.setMessage(getString(R.string.Are_you_sure));
                alertDialog.setPositiveButton(getString(R.string.Delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePrinter(currentPrinter);
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });

        bottomSheetDialog_editPrinter.show();

    }

    private void handleDefaultPrinter(MyThermalPrinter currentPrinter, int position) {
        if(position == 0){
            tv_EDIT_title_set_default.setTextColor(getResources().getColor(R.color.grey500, null));
            sw_EDIT_default.setVisibility(View.GONE);
            tv_EDIT_default.setVisibility(View.VISIBLE);
        }else{
            tv_EDIT_title_set_default.setTextColor(getResources().getColor(R.color.grey100, null));
            sw_EDIT_default.setVisibility(View.VISIBLE);
            tv_EDIT_default.setVisibility(View.GONE);
        }
    }

    private void showBottomSheetDialog_addPrinter() {
        bottomSheetDialog_addPrinter = new BottomSheetDialog(
                getContext(), R.style.BottomSheetDialogTheme
        );
        View bottomSheeetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.dialog_bottom_sheet_add_print,
                        getActivity().findViewById(R.id.bottom_sheet_dialog_container)
                );
        bottomSheetDialog_addPrinter.setContentView(bottomSheeetView);

        BottomSheetBehavior behavior = bottomSheetDialog_addPrinter.getBehavior();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        rb_ADD_2inch                    = bottomSheeetView.findViewById(R.id.rb_ADD_2inch);
        rb_ADD_3inch                    = bottomSheeetView.findViewById(R.id.rb_ADD_3inch);
        btn_ADD_bluetooth               = bottomSheeetView.findViewById(R.id.btn_ADD_bluetooth);
        btn_ADD_network                 = bottomSheeetView.findViewById(R.id.btn_ADD_network);
        btn_ADD_test_print              = bottomSheeetView.findViewById(R.id.btn_ADD_test_print);
        btn_ADD_save_tcp                = bottomSheeetView.findViewById(R.id.btn_ADD_save_tcp);
        btn_ADD_save_bluetooth          = bottomSheeetView.findViewById(R.id.btn_ADD_save_bluetooth);

        ADD_method_BLUETOOTH            = bottomSheeetView.findViewById(R.id.method_BLUETOOTH);
        ADD_method_TCP_IP               = bottomSheeetView.findViewById(R.id.method_TCP_IP);

        tiet_ADD_bluetooth_print_name   = bottomSheeetView.findViewById(R.id.tiet_ADD_bluetooth_print_name);
        tiet_ADD_tcp_print_name         = bottomSheeetView.findViewById(R.id.tiet_ADD_tcp_print_name);
        tiet_ADD_ip_address             = bottomSheeetView.findViewById(R.id.tiet_ADD_ip_address);

        rv_list_paired_devices          = bottomSheeetView.findViewById(R.id.rv_paired_devices);

        setStatus();
        showListPairedDevices();

        rb_ADD_2inch.setOnClickListener(Fragment_Print_Management.this);
        rb_ADD_3inch.setOnClickListener(Fragment_Print_Management.this);

        btn_ADD_bluetooth.setOnClickListener(Fragment_Print_Management.this);
        btn_ADD_network.setOnClickListener(Fragment_Print_Management.this);

        btn_ADD_test_print.setOnClickListener(Fragment_Print_Management.this);
        btn_ADD_save_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDevice != null) {
                    String id = MyUUID.generator();
                    String name = tiet_ADD_bluetooth_print_name.getText().toString().trim();
                    String macAddress = selectedDevice.getDevice().getAddress();

                    MyThermalPrinter myThermalPrinter = new MyThermalPrinter(
                            id,
                            name.length() == 0 ? selectedDevice.getDevice().getName() : name,
                            "",
                            macAddress,
                            paperSize,
                            BLUETOOTH
                    );
                    mListPrinter.add(0, myThermalPrinter);

                    setNewPrinterToSharedPreferences(mListPrinter);
                } else {
                    showToast(getContext(), getString(R.string.Please_choose_Bluetooth_device));
                }
            }
        });
        btn_ADD_save_tcp.setOnClickListener(Fragment_Print_Management.this);

        bottomSheetDialog_addPrinter.show();
    }

    private void showListPairedDevices() {
        BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        if(bluetoothDevicesList != null){
            List<PairedDevice> listPairedDevice = new ArrayList<>();
            for (BluetoothConnection device : bluetoothDevicesList) {
                PairedDevice pairedDevice = new PairedDevice(
                        device.getDevice().getName(),
                        device.getDevice().getAddress()
                );
                listPairedDevice.add(pairedDevice);
            }

            AdapterPairedDevice adapter = new AdapterPairedDevice(getActivity(), new AdapterPairedDevice.IOnPairedDevcieItem() {
                @Override
                public void setOnClickItemListener(BluetoothConnection bluetoothConnection) {
                    selectedDevice = bluetoothConnection;
                }

                @Override
                public void setOnClickTestButtonListener(BluetoothConnection device) {
                    PrintContent.printTestBLUETOOTH(getActivity(), device, paperSize);
                }
            });
            adapter.setData(listPairedDevice, bluetoothDevicesList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    getContext(), RecyclerView.VERTICAL, false
            );
            rv_list_paired_devices.setLayoutManager(linearLayoutManager);

            rv_list_paired_devices.setAdapter(adapter);
        }
    }

    private void showListPrinter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_list_printer_created.setLayoutManager(linearLayoutManager);
        adapter = new AdapterPrint(getActivity(), new AdapterPrint.IOnPrintItem() {
            @Override
            public void setOnCLickItemListener(MyThermalPrinter currentPrinter, int position) {
                showBottomSheetDialog_editPrinter(currentPrinter, position);
            }
        });
        adapter.setData(mListPrinter);
        rv_list_printer_created.setAdapter(adapter);
    }

    private boolean checkChange(MyThermalPrinter currentPrinter) {
        if (!currentPrinter.getPaperSize().equals(paperSize)
            || !currentPrinter.getName().equals(
                    tiet_EDIT_print_name.getText().toString().trim())
            || sw_EDIT_default.isChecked()
        ){
            return true;
        }
        return false;
    }

    private void deletePrinter(MyThermalPrinter currentPrinter) {
        SharedPreferences.Editor editor = sharedPreferences_print.edit();
        Logdln("size: " + mListPrinter.size(), 489);
        mListPrinter.remove(currentPrinter);
        Logdln("size: " + mListPrinter.size(), 491);
        editor.putString(LIST_PRINTER, new Gson().toJson(mListPrinter));
        if(editor.commit()) {
            showToast(getContext(), getString(R.string.Delete_Successful));
            adapter.notifyDataSetChanged();
            bottomSheetDialog_editPrinter.dismiss();
        }else{
            showToast(getContext(), getString(R.string.Save_not_success_try_agian));
        }
    }

    private void testLANPrint() {
        String ipAddress = tiet_ADD_ip_address.getText().toString().trim();
        PrintContent.printTestTCP(getContext(), ipAddress, 9100, paperSize);
    }

    private void setStatus() {
        switch (currentMethod){
            case "BLUETOOTH":
                btn_ADD_bluetooth.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                btn_ADD_bluetooth.setStrokeColorResource(R.color.primary);
                btn_ADD_bluetooth.setIconTintResource(R.color.primary);
                btn_ADD_bluetooth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green50));

                btn_ADD_network.setTextColor(ContextCompat.getColor(getContext(), R.color.grey500));
                btn_ADD_network.setStrokeColor(ColorStateList.valueOf(Color.argb(0, 0, 0, 0)));
                btn_ADD_network.setIconTintResource(R.color.grey500);
                btn_ADD_network.setBackgroundColor(Color.argb(0, 0,0, 0));

                ADD_method_BLUETOOTH.setVisibility(View.VISIBLE);
                ADD_method_TCP_IP.setVisibility(View.GONE);

                break;
            case "NETWORK":
                btn_ADD_network.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                btn_ADD_network.setStrokeColorResource(R.color.primary);
                btn_ADD_network.setIconTintResource(R.color.primary);
                btn_ADD_network.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green50));

                btn_ADD_bluetooth.setTextColor(ContextCompat.getColor(getContext(), R.color.grey500));
                btn_ADD_bluetooth.setStrokeColor(ColorStateList.valueOf(Color.argb(0, 0, 0, 0)));
                btn_ADD_bluetooth.setIconTintResource(R.color.grey500);
                btn_ADD_bluetooth.setBackgroundColor(Color.argb(0, 0,0, 0));

                ADD_method_TCP_IP.setVisibility(View.VISIBLE);
                ADD_method_BLUETOOTH.setVisibility(View.GONE);

                break;
        }
    }


    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Print_Manegement.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Print_Manegement.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Print_Manegement.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Print_Manegement.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}