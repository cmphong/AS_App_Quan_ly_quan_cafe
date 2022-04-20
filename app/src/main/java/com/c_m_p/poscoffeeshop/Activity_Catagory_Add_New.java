package com.c_m_p.poscoffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterSelectIcon;
import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.Model.IconObj;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Catagory_Add_New extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_add_icon;
    private Button btn_add, btn_cancel;
    private EditText edt_category_name;
    private TextView tv_warning;

    private String actionName = "NEW";
    private Dialog dialog;
    private String resImageName = "";

    Category currentCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory_add_new);

        tv_warning = findViewById(R.id.tv_warning);
        iv_add_icon = findViewById(R.id.iv_add_category_icon);
        btn_add = findViewById(R.id.btn_add_category);
        btn_cancel = findViewById(R.id.btn_cancel_category);
        edt_category_name = findViewById(R.id.edt_category_add_name);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            actionName = bundle.getString("action");
            if(actionName != null && actionName.equals(Fragment_Category.ACTION_EDIT)){
                currentCategory = (Category) bundle.getSerializable(Fragment_Category.CURRENT_CATEGORY);
                String icName = currentCategory.getIcon();
                iv_add_icon.setImageResource(getResources().getIdentifier(icName, "drawable", getPackageName()));
                edt_category_name.setText(currentCategory.getName());
                btn_add.setText("OK");

                resImageName = icName;
            }
        }else{
            actionName = "NEW";
            resImageName = getResources()
                    .getResourceName(R.drawable.ic_cat_drink)
                    .split("/")[1];
        }

        iv_add_icon.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_add_category_icon:
//                getIconCategoryFromFirebase();
                openDialogSelectIcon();
                break;
            case R.id.btn_add_category:
                tv_warning.setVisibility(View.GONE);
                if(validationForm()) {
                    if (actionName.equals("NEW")) {
                        addCategory();
                    } else {
                        editCategory();
                        finish();
                        break;
                    }
                    btn_cancel.setText(getString(R.string.Back));
                }else{
                    tv_warning.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_cancel_category:
                    finish();
                break;
        }
    }

    private void editCategory() {
        String ic_name = resImageName;
        String name = edt_category_name.getText().toString().trim();
        String id = currentCategory.getId();
        Map<String, Object> itemCategoryMap = new HashMap<>();
        itemCategoryMap.put("name", name);
        itemCategoryMap.put("icon", ic_name);
        CommonData.fbRef_Category.child(id).updateChildren(itemCategoryMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {

                showToast("Edit Successful!!");
                Logdln("Edit Successful!!", 115);
            }
        });
    }

    private void addCategory() {
        // id = ???
        String ic_name = resImageName;
        String name = edt_category_name.getText().toString().trim();
        String id_random = CommonData.fbRef_Category.child("id_category").push().getKey();
        Map<String, Object> itemCategoryMap = new HashMap<>();
        itemCategoryMap.put("id", id_random);
        itemCategoryMap.put("name", name);
        itemCategoryMap.put("icon", ic_name);
        CommonData.fbRef_Category.child(id_random).updateChildren(itemCategoryMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast("Add Successful!!");
                Logdln("Add Successful!!", 115);
            }
        });
    }

    private void openDialogSelectIcon() {

        List<IconObj> listDrawables = CommonData.getListIconInDrawable("ic_cat_");

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_group_category);

        // Set width & height
        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        // mapping
        RecyclerView recyclerView = dialog.findViewById(R.id.dialog_rv_select_icon);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);

        AdapterSelectIcon adapterSelectIcon = new AdapterSelectIcon(this, new AdapterSelectIcon.IOnClickItem() {
            @Override
            public void onClickItemListener(IconObj iconObj) {
                resImageName = iconObj.getName();
                iv_add_icon.setImageResource(iconObj.getImageId());
                dialog.dismiss();
            }
        });
        adapterSelectIcon.setData(listDrawables);

        recyclerView.setAdapter(adapterSelectIcon);

        dialog.show();
    }

    private boolean validationForm(){
        String name = edt_category_name.getText().toString().trim();
        return !TextUtils.isEmpty(name);
    }

    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Catagory_Add_New.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Catagory_Add_New.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Catagory_Add_New.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Catagory_Add_New.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}