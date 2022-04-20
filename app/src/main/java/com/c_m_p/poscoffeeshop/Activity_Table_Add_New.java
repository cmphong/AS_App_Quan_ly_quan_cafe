package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Model.Table;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Activity_Table_Add_New extends AppCompatActivity implements View.OnClickListener {

    TextView tv_warning;
    EditText edt_name;
    Button btn_cancel, btn_add;

    String actionName = "ADD_NEW";

    Table currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_add_new);

        tv_warning = findViewById(R.id.tv_warning);
        edt_name = findViewById(R.id.edt_table_add_name);
        btn_add = findViewById(R.id.btn_add_table);
        btn_cancel = findViewById(R.id.btn_cancel_table);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            actionName = bundle.getString(Fragment_Table.ACTION_NAME);
            currentTable = (Table) bundle.getSerializable(Fragment_Table.TABLE_OBJ);
            edt_name.setText(currentTable.getName());

        }

        btn_add.setText(actionName.equals("EDIT")
                        ? getString(R.string.Save)
                        : getString(R.string.Add)
        );

        btn_add.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void updateTableItemOnFirebase(Table currentTable) {
        String id = currentTable.getId();
        String name = edt_name.getText().toString().trim();

        CommonData.fbRef_Table.child(id).child("name").setValue(name, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast(getString(R.string.Update_Successful));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_table:
                tv_warning.setVisibility(View.GONE);
                if(validationForm()) {
                    if (actionName.equals("EDIT")) {
                        updateTableItemOnFirebase(currentTable);

                    } else if (actionName.equals("ADD_NEW")) {
                        String name = edt_name.getText().toString().trim();
                        if (name == null || name.length() == 0) {
                            showToast(getString(R.string.Name_is_required_try_again));
                            break;
                        }
                        addTable(name);
                        btn_cancel.setText(getString(R.string.Back));
                    }
                }else{
                    tv_warning.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_cancel_table:
                finish();
                break;
        }
    }

    private void addTable(String name) {
        String id_random = CommonData.fbRef_Table.child("id_table").push().getKey();
        Map<String, Object> itemTableMap = new HashMap<>();
        itemTableMap.put("id", id_random);
        itemTableMap.put("name", name);

        CommonData.fbRef_Table.child(id_random).updateChildren(itemTableMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast(getString(R.string.Add_Successful));
                Logdln("Add Successful!!", 68);
            }
        });
    }

    private boolean validationForm(){
        String name = edt_name.getText().toString().trim();
        return !TextUtils.isEmpty(name);
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Table_Add_New.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Table_Add_New.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Table_Add_New.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Table_Add_New.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast( String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}