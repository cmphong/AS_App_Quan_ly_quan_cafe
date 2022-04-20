package com.c_m_p.poscoffeeshop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterTable;
import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Table extends Fragment {

    public static String TABLE_OBJ = "TABLE_OBJ",
                         ACTION_NAME = "ACTION_NAME",
                         EDIT = "EDIT";

    List<Table> listTable;
    SpinKitView sp_loading;
    RecyclerView rv_store_status;
    int nColumn = 2;
    AdapterTable adpater;
    Table currentTable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_table, container, false);

        sp_loading      = v.findViewById(R.id.sp_loading);
        rv_store_status = v.findViewById(R.id.rv_store_status);
        rv_store_status.setVisibility(View.GONE);

        getList();

        adpater = new AdapterTable(getActivity(), new AdapterTable.IOnTableItem() {
            @Override
            public void setOnLongClickTableItemListener(Table table) {
                currentTable = table;
            }
        });
        adpater.setData(listTable);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), nColumn, RecyclerView.VERTICAL, false);
        rv_store_status.setLayoutManager(gridLayoutManager);

        rv_store_status.setAdapter(adpater);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sp_loading.setVisibility(View.GONE);
            }
        }, 8000);


        return v;
    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {

        if (item.getItemId() == R.id.context_menu_category_edit) {
            Intent intent = new Intent(getContext(), Activity_Table_Add_New.class);
            Bundle bundle = new Bundle();
            bundle.putString(ACTION_NAME, EDIT);
            bundle.putSerializable(TABLE_OBJ, currentTable);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.context_menu_category_delete){
            if(!currentTable.getIsOrdering()) {
                showDialogConfirm();
            }else{
                showDialogWarning();
            }
        }

        return super.onContextItemSelected(item);
    }

    private void showDialogWarning() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.Alert);
        alert.setMessage(R.string.Please_choose_empty_table);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    private void showDialogConfirm() {
        AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
        alertDelete.setTitle(R.string.Delete_Confirm);
        alertDelete.setMessage(R.string.Are_you_sure);
        alertDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    deleteTable();
            }
        });
        alertDelete.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDelete.show();

    }

    private void deleteTable() {
        String id = currentTable.getId();
        listTable.remove(currentTable);
        CommonData.fbRef_Table.child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast(String.valueOf(R.string.Delete_Successful));
            }
        });
        adpater.notifyDataSetChanged();

    }

    private void getList() {
        listTable = new ArrayList<>();
        if(CommonData.fbRef_Table != null) {
            CommonData.fbRef_Table.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Table table = snapshot.getValue(Table.class);
                    listTable.add(table);
                    adpater.notifyDataSetChanged();

                    sp_loading.setVisibility(View.GONE);
                    rv_store_status.setVisibility(View.VISIBLE);

                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Table table = snapshot.getValue(Table.class);
                    for(int i = 0 ; i < listTable.size() ; i++){
                        if(table.getId().equals(listTable.get(i).getId())){
                            listTable.set(i, table);
                        }
                    }
                    adpater.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }else{
            Logdln("CommonData.fbRef_Table is null", 91);
        }

    }


    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Table.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Table.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Table.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Table.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
    }
}