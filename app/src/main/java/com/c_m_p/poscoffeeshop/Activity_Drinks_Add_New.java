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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterCategory;
import com.c_m_p.poscoffeeshop.Adapter.AdapterSelectCategory;
import com.c_m_p.poscoffeeshop.Adapter.AdapterSelectImageBackground;
import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.Model.IconObj;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** TODO<br>
 *   [x] Tối ưu lại hàm getCategoriesFromFirebase, vì sử dụng<br>
 *      addChildEventListener() quá dư thừa<br>
 *   [ ] Không hiện danh sách Category khi tạo mới
 *   [ ]
 */
public class Activity_Drinks_Add_New extends AppCompatActivity implements View.OnClickListener {

    EditText edt_name, edt_price, edt_unit, edt_note;
    Button btn_back, btn_add;
    LinearLayout ln_select_group;
    TextView tv_select_group, tv_warning;
    ImageView iv_select_group, iv_image;

    Dialog dialogSelectIcon, dialogSelectImage;

    AdapterCategory adapterCategory;
    List<Category> mListCategory;
    Category category_selected;
    String resImageName = "image_default";

    String actionName = "NEW";
    Drink currentDrink;
    boolean isSelectedInDialog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks_add_new);

        edt_name        = findViewById(R.id.edt_type_drink_name);
        edt_price       = findViewById(R.id.edt_type_drink_price);
        edt_unit        = findViewById(R.id.edt_type_drink_unit);
        edt_note        = findViewById(R.id.edt_type_drink_note);
        iv_image        = findViewById(R.id.iv_drink_image);
        ln_select_group = findViewById(R.id.ln_select_group);
        tv_select_group = findViewById(R.id.tv_select_group);
        tv_warning      = findViewById(R.id.tv_warning);
        iv_select_group = findViewById(R.id.iv_ic_category_selected);
        btn_back        = findViewById(R.id.btn_drink_add_back);
        btn_add         = findViewById(R.id.btn_drink_add_new);

        mListCategory = new ArrayList<>();
        mListCategory = CommonData.getListCategoryFromFirebase();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String action = bundle.getString("action");
            actionName = action;
            currentDrink = (Drink) bundle.getSerializable(Fragment_Category.CURRENT_DRINK);
//            mListCategory = (List<Category>) bundle.getSerializable(Fragment_Category.LIST_CATEGORY);

            edt_name.setText(currentDrink.getName());
            if(currentDrink.getGroup() != null) {
                tv_select_group.setText(currentDrink.getGroup());
                iv_select_group.setVisibility(View.VISIBLE);
                iv_select_group.setImageResource(getResources().getIdentifier(
                        getIconFromCategory(mListCategory, currentDrink),
                        "drawable",
                        getPackageName()
                ));
            }
            resImageName = currentDrink.getImage();
            iv_image.setImageResource(getResources().getIdentifier(
                    currentDrink.getImage(), "drawable", getPackageName()
            ));

            edt_price.setText(currentDrink.getPrice());
            edt_unit.setText(currentDrink.getUnit());
            edt_note.setText(currentDrink.getNote());
            btn_add.setText(getString(R.string.Update));
            btn_back.setText(getString(R.string.Cancel));

        }
//        else{
//            mListCategory = CommonData.getListCategoryFromFirebase();
//        }

        ln_select_group.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_back.setOnClickListener(this);

    }

    private String getIconFromCategory(List<Category> mListCategory, Drink drink) {
        for (Category category : mListCategory){
            // check exist
            if(drink.getGroup() != null && drink.getGroup().equals(category.getName())){
                return category.getIcon();
            }
        }
        return "ic_cat_drink";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ln_select_group:
                openDialogSelectGroup();
                break;
            case R.id.iv_drink_image:
                openDialogSelectImage();
                break;
            case R.id.btn_drink_add_new:
                tv_warning.setVisibility(View.GONE);
                if(validateForm()) {
                    if (actionName.equals(Fragment_Category.ACTION_EDIT)) {
                        updateDrink();
                    } else {
                        addDrinkNew();
                    }
                }else{
                    tv_warning.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_drink_add_back:
                finish();
                break;
        }
    }

    private void addDrinkNew() {
        String id = CommonData.fbRef_Drink.child("Drink").push().getKey();
        String name = edt_name.getText().toString().trim();
        String price = edt_price.getText().toString();
        String unit = edt_unit.getText().toString().trim();
        String group = null;
        if(category_selected != null) {
            group = category_selected.getName();
        }
        String image = resImageName;
        String note = edt_note.getText().toString().trim();

        Drink drink = new Drink(id, name, image, price, note, group, unit);
        Map<String, Object> drinkMap = new HashMap<>();
        drinkMap = drink.toMap();

        CommonData.fbRef_Drink.child(id).updateChildren(drinkMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                if(category_selected != null) {
                    updateCategoryFirebase(drink);
                }
                showToast(getString(R.string.Add_Successful));
            }
        });
    }

    private void updateDrink(){
        String id = currentDrink.getId();
        String name = edt_name.getText().toString().trim();
        String price = edt_price.getText().toString();
        String unit = edt_unit.getText().toString().trim();
        String group = currentDrink.getGroup();
        // case selected category
        if(category_selected != null) {
            group = category_selected.getName();
        }
        // case selected null
        if(category_selected == null) {
            if(isSelectedInDialog){
                group = null;
            }
            // case non-select
        }

        String image = resImageName;
        String note = edt_note.getText().toString().trim();

        Drink drink = new Drink(id, name, image, price, note, group, unit);
        Map<String, Object> drinkMap = new HashMap<>();
        drinkMap = drink.toMap();

        CommonData.fbRef_Drink.child(id).updateChildren(drinkMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                if(category_selected != null) {
                    updateCategoryFirebase(drink);
                }
                showToast(getString(R.string.Update_Successful));
                finish();
            }
        });
    }

    private void updateCategoryFirebase(Drink drink) {
        Map<String, Object> items = new HashMap<>();
        items.put(drink.getId(), true);
        CommonData.fbRef_Category
            .child(category_selected.getId())
                .child("items").updateChildren(items);
    }

    private void openDialogSelectGroup() {

        dialogSelectIcon = new Dialog(this);
        dialogSelectIcon.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSelectIcon.setContentView(R.layout.dialog_select_group_category);

        // Set width & height
        Window window = dialogSelectIcon.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        // initView
        RecyclerView rv_selectIcon = dialogSelectIcon.findViewById(R.id.dialog_rv_select_icon);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        rv_selectIcon.setLayoutManager(gridLayoutManager);

        AdapterSelectCategory adapterSelectCategory = new AdapterSelectCategory(this, new AdapterSelectCategory.IOnClickItem() {
            @Override
            public void onClickItemListener(Category category) {
                category_selected = category;
                isSelectedInDialog = true;
                if(category != null) {
                    iv_select_group.setVisibility(View.VISIBLE);
                    iv_select_group.setImageResource(getResources().getIdentifier(
                            category.getIcon(), "drawable", getPackageName()
                    ));
                    tv_select_group.setText(CommonData.capitalize(category.getName()));
                }else{
                    iv_select_group.setVisibility(View.GONE);
                    tv_select_group.setText(getString(R.string.Select_one));
                }

                dialogSelectIcon.dismiss();
            }
        });
        adapterSelectCategory.setData(mListCategory);

        rv_selectIcon.setAdapter(adapterSelectCategory);

        dialogSelectIcon.show();
    }

    private void openDialogSelectImage(){
        List<IconObj> listDrawables = CommonData.getListIconInDrawable("image_");

        dialogSelectImage = new Dialog(this);
        dialogSelectImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSelectImage.setContentView(R.layout.dialog_select_image_category);

        Window window = dialogSelectImage.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        // initView
        RecyclerView rv_selectImage = dialogSelectImage.findViewById(R.id.dialog_rv_select_image);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rv_selectImage.setLayoutManager(gridLayoutManager);

        AdapterSelectImageBackground adapter = new AdapterSelectImageBackground(this, new AdapterSelectImageBackground.IOnClickItem() {
            @Override
            public void onClickItemListener(IconObj iconObj) {
                resImageName = iconObj.getName();
                iv_image.setImageResource(iconObj.getImageId());
                dialogSelectImage.dismiss();
            }
        });
        adapter.setData(listDrawables);
        rv_selectImage.setAdapter(adapter);

        dialogSelectImage.show();
    }

    private boolean validateForm(){
        String name = edt_name.getText().toString().trim();
        String price = edt_price.getText().toString().trim();
        String unit = edt_unit.getText().toString().trim();
        return !TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(price) &&
                !TextUtils.isEmpty(unit);
    }

    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Activity_Drinks_Add_New.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Activity_Drinks_Add_New.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Activity_Drinks_Add_New.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Activity_Drinks_Add_New.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(String str ){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}