package com.c_m_p.poscoffeeshop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.c_m_p.poscoffeeshop.Adapter.AdapterCategory;
import com.c_m_p.poscoffeeshop.Adapter.AdapterDrink;
import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.MyUtils.AutoFitGridLayoutManager;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// TODO: 2021-11-22
//  [ ] Handle rotate device
//  [x] not update after edit drink obj

public class Fragment_Category extends Fragment {

    public static String ACTION_EDIT        = "EDIT",
                         CURRENT_CATEGORY   = "CURRENT_CATEGORY",
                         CURRENT_DRINK      = "CURRENT_DRINK",
                         LIST_CATEGORY      = "LIST_CATEGORY";

    RecyclerView rv_category, rv_drink;
    List<Category> mListCategory;
    List<Drink> mListDrink;
    List<Drink> listDrinkTmp;
    Category currentCategory;
    Drink currentDrink;

    AdapterCategory adapterCategory;
    AdapterDrink adapterDrink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        rv_category = v.findViewById(R.id.rv_category);
        rv_drink = v.findViewById(R.id.rv_category_detail);

        initialList();

        getListCategoryFromFirebase();
        getListDrinkFromFirebase();

    // ===== START - RECYCLERVIEW CATEGROY =========================================
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), RecyclerView.VERTICAL, false
        );
        rv_category.setLayoutManager(linearLayoutManager);
        adapterCategory = new AdapterCategory(getContext(), new AdapterCategory.IOnCategeroyItem() {
            @Override
            public void onClickItemListener(Category category) {
                currentCategory = category;
                showItemDrink();
            }

            @Override
            public void onLongClickItemListener(Category category) {
                currentCategory = category;
            }
        });
        adapterCategory.setData(mListCategory);
        rv_category.setAdapter(adapterCategory);
    // ===== END - RECYCLERVIEW CATEGROY =========================================

    // ===== START - RECYCLERVIEW GRID DRINK =====================================
//        rv_drink.setLayoutManager(gridLayoutManager);
//        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            rv_drink.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        }
//        else{
//            rv_drink.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        }
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getContext(), 350);
        rv_drink.setLayoutManager(layoutManager);
        adapterDrink = new AdapterDrink(getActivity(), new AdapterDrink.IOnDrinkItem() {
            @Override
            public void onClickItemListener(Drink drink) {
                // Pending......
                // ............
            }

            @Override
            public void onLongClickItemListener(Drink drink) {
                currentDrink = drink;
            }
        });
        adapterDrink.setData(mListDrink);
        rv_drink.setAdapter(adapterDrink);

    // ===== END - RECYCLERVIEW GRID DRINK =====================================

        return v;
    }

    @Override
    public boolean onContextItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case 11:
                Intent intent11 = new Intent(getActivity(), Activity_Catagory_Add_New.class);
                Bundle bundle11 = new Bundle();
                bundle11.putString("action", ACTION_EDIT);
                bundle11.putSerializable(CURRENT_CATEGORY, currentCategory);
                intent11.putExtras(bundle11);
                startActivity(intent11);
                break;
            case 12:
                showDialogConfirm("category");
                break;
            case 21:
                Intent intent21 = new Intent(getActivity(), Activity_Drinks_Add_New.class);
                Bundle bundle21 = new Bundle();
                bundle21.putString("action", ACTION_EDIT);
                bundle21.putSerializable(CURRENT_DRINK, currentDrink);
//                bundle21.putSerializable(LIST_CATEGORY, (Serializable) mListCategory);
                intent21.putExtras(bundle21);
                startActivity(intent21);
                break;
            case 22:
                showDialogConfirm("drink");
                break;
        }
        return true;
    }

    private void showDialogConfirm(String name) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(getString(R.string.Delete_Confirm));
        alertDialog.setMessage(getString(R.string.Are_you_sure));
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(name.equals("category")) {
                    deleteCategory();
                }
                if(name.equals("drink")){
                    deleteDrink();
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();

    }

    private void deleteCategory() {
        String id = currentCategory.getId();
        CommonData.fbRef_Category.child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast(getContext(), getString(R.string.Delete_Successful));
            }
        });

    }

    private void deleteDrink() {
        String id = currentDrink.getId();
        CommonData.fbRef_Drink.child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                showToast(getContext(), getString(R.string.Delete_Successful));
            }
        });

    }

    // HANDLE ROTATE DEVICE ========================================
    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // YOUR HANDLE HERE
            Logd("is PORTRAIT");
        }
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // YOUR HANDLE HERE
            Logd("is LANDSCAPE");
        }

    }

    private void showItemDrink() {
        if(mListDrink == null || currentCategory == null){
            return;
        }

        List<Drink> tmp = new ArrayList<>();
        if (currentCategory.getId().equals("all")){
            tmp = mListDrink;
        } else
        if (currentCategory.getId().equals("popular")){
            if(mListDrink.size() > 10){
                tmp = getPopular(10, mListDrink);
            }else
            if(mListDrink.size() > 5){
                tmp = getPopular(5, mListDrink);
            } else
            if(mListDrink.size() > 3){
                tmp = getPopular(3, mListDrink);
            } else
            if (mListDrink.size() > 0) {
                tmp = getPopular(1, mListDrink);
            }
        } else {
            for (int i = 0; i < mListDrink.size(); i++) {
                if (currentCategory.getName().equals(mListDrink.get(i).getGroup())) {
                    tmp.add(mListDrink.get(i));
                }
            }
        }

        adapterDrink.setData(tmp);

    }

    private List<Drink> getPopular(int number, List<Drink> listDrink){
        // number = 3, 5, 10
        List<Drink> tmp = new ArrayList<>();
        if(listDrink != null || listDrink.size() > 0) {
            tmp.addAll(listDrink);
            List<Drink> result = new ArrayList<>();

            Collections.sort(tmp, new Comparator<Drink>() {
                @Override
                public int compare(Drink d1, Drink d2) {
                    return d2.getOrdered() - d1.getOrdered();
                }
            });

            for (int i = 0; i < number; i++) {
                result.add(tmp.get(i));
            }

            return result;
        }else{
            return null;
        }
    }

    private void getListCategoryFromFirebase() {
        CommonData.fbRef_Category.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Logd("onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Category category = dataSnapshot.getValue(Category.class);
                mListCategory.add(2, category);
                adapterCategory.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Logd("onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
//                Comment newComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();

                // ...
                Category category = dataSnapshot.getValue(Category.class);
                String id = category.getId();
                for (int i = 0 ; i < mListCategory.size() ; i++){
                    if(id.equals(mListCategory.get(i).getId())){
                        mListCategory.set(i, category);
                    }
                }
                adapterCategory.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

//                Logdln("onChildRemoved:" + dataSnapshot.getKey(), 195);
//                String commentKey = dataSnapshot.getKey();

                mListCategory.remove(currentCategory);
                mListCategory.get(1).setSelected(true);
                mListCategory.get(0).setSelected(false);
                adapterCategory.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Logd("onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Logd("postComments:onCancelled\n" +databaseError.toException());
//                showToast(getContext(), "Failed to load comments.");
            }
        });

    }

    private void getListDrinkFromFirebase() {
        CommonData.fbRef_Drink.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Drink drink = snapshot.getValue(Drink.class);
                mListDrink.add(0,drink);
                showItemDrink();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String id = snapshot.getKey();
                Drink drink = snapshot.getValue(Drink.class);
                for(int i = 0 ; i < mListDrink.size() ; i++){
                    if(id.equals(mListDrink.get(i).getId())){
                        mListDrink.set(i, drink);
                        showItemDrink();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                String id = snapshot.getKey();
                for(int i = 0 ; i < mListDrink.size() ; i++){
                    if(mListDrink.get(i).getId().equals(id)){
                        mListDrink.remove(i);
                        showItemDrink();
//                        adapterDrink.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//                Logd("onChildMoved()");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//                Logd("onCancelled()");
            }
        });
    }

    private void initialList() {
        mListCategory = new ArrayList<>();
        Category catPopular = new Category("popular", getString(R.string.Popular), "ic_popular");
        Category catAll = new Category("all", getString(R.string.All), "ic_all");

        catAll.setSelected(true);

        currentCategory = catAll;

        mListCategory.add(catPopular);
        mListCategory.add(catAll);

        mListDrink = new ArrayList<>();
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Category.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Category.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Category.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Category.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

}