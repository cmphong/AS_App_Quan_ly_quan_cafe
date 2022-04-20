package com.c_m_p.poscoffeeshop;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c_m_p.poscoffeeshop.Adapter.AdapterCategoryViewPager;
import com.c_m_p.poscoffeeshop.Adapter.AdapterDrinkViewPager;
import com.c_m_p.poscoffeeshop.Interface.Middle_ActivityOrder_and_FragmentCategoryViewPager;
import com.c_m_p.poscoffeeshop.Interface.IListFilter;
import com.c_m_p.poscoffeeshop.Model.Category;
import com.c_m_p.poscoffeeshop.Model.Drink;
import com.c_m_p.poscoffeeshop.Model.Ordered;
import com.c_m_p.poscoffeeshop.MyUtils.AutoFitGridLayoutManager;
import com.c_m_p.poscoffeeshop.MyUtils.CommonData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// TODO: 2021-11-22
//  [ ] Handle rotate device
//  [x] Handle onLongClick

public class Fragment_Category_ViewPager extends Fragment {

    Middle_ActivityOrder_and_FragmentCategoryViewPager interfaceMiddle;
    IListFilter iListFilter;

    RecyclerView rv_category, rv_drink;
    List<Category> mListCategory;
    List<Drink> mListDrink;
    List<Drink> listFilterDrink;
    List<Ordered> listOrdered;
    Category currentCategory;

    AdapterCategoryViewPager adapterCategory;
    AdapterDrinkViewPager adapterDrinkOrder;

    int orderingTotal = 0;

    public void setIListFilter (IListFilter iListFilter){
        this.iListFilter = iListFilter;
    }

    public void setListOrdered(List<Ordered> listOrdered){
        this.listOrdered = listOrdered;
//        Logdln("listOrdered: " +listOrdered.size(), 63);
    }

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
        adapterCategory = new AdapterCategoryViewPager(getContext(), new AdapterCategoryViewPager.IOnCategeroyItem() {
            @Override
            public void onClickItemListener(Category category) {
                currentCategory = category;
                showItemDrink();
            }
        });
        adapterCategory.setData(mListCategory);
        rv_category.setAdapter(adapterCategory);
    // ===== END - RECYCLERVIEW CATEGROY ===========================================

    // ===== START - RECYCLERVIEW GRID DRINK ===========================================
//        rv_drink.setLayoutManager(gridLayoutManager);
//        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            rv_drink.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        }
//        else{
//            rv_drink.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        }
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getContext(), 350);
        rv_drink.setLayoutManager(layoutManager);

        interfaceMiddle = (Middle_ActivityOrder_and_FragmentCategoryViewPager) getActivity();

        adapterDrinkOrder = new AdapterDrinkViewPager(getContext(), new AdapterDrinkViewPager.IOnclickItem() {
            @Override
            public void updateOrderingCounterItem(Drink drink, boolean isIncreasement) {
                int index = mListDrink.indexOf(drink);
                int ordering = mListDrink.get(index).getOrdering();
                if(isIncreasement){
                    mListDrink.get(index).setOrdering(ordering + 1);
                }else{
                    if(ordering > 0){
                        mListDrink.get(index).setOrdering(ordering - 1);
                    }
                }

                // pass List filtered TO Fragment_Order_Detail_ViewPager
                listFilterDrink = filterItemSelected(false);
                iListFilter.setListItemSelected(listFilterDrink);

                adapterDrinkOrder.notifyDataSetChanged();
//                adapterDrinkOrder.setData(mListDrink);
            }
            @Override
            public void updateOrderingTotalToolbar(int orderingTotal) {
                interfaceMiddle.updateWaitersCount(orderingTotal);
            }
        });
        adapterDrinkOrder.setData(mListDrink);
        adapterDrinkOrder.setOrderingTotal(orderingTotal);
        rv_drink.setAdapter(adapterDrinkOrder);
    // ===== END - RECYCLERVIEW GRID DRINK ===========================================

        return v;
    }

    public void setOrderingTotal(int orderingTotal){
        this.orderingTotal = orderingTotal;
    }


//    private void updateMListDrink() {
//        for (Drink drink : mListDrink){
//            for (Ordered item : listOrdered){
//                if (drink.getId().equals(item.getId())){
//                    drink.setOrdering(item.getQuantity());
//                }
//            }
//        }
//    }


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



    private List<Drink> filterItemSelected(boolean hasListOrdered) {
        List<Drink> result = new ArrayList<>();
        if(!hasListOrdered) {
            for (int i = 0; i < mListDrink.size(); i++) {
                if (mListDrink.get(i).getOrdering() > 0) {
                    result.add(mListDrink.get(i));
                }
            }
        }else {
            for (Drink drink : mListDrink) {
                for (Ordered item : listOrdered) {
                    if (drink.getId().equals(item.getId())) {
                        drink.setOrdering(item.getQuantity());
                        result.add(drink);
                    }
                }
            }
        }
        return result;
    }

    private void showItemDrink_1() {
        if(mListDrink == null || currentCategory == null){
            return;
        }

        List<Drink> tmp = new ArrayList<>();

        if (currentCategory.getId().equals("all")){
            tmp = mListDrink;
        }else
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

            if (listOrdered == null) { // CREATE NEW ORDER
                for (Drink drink : mListDrink) {
                    if (drink.getGroup() != null && drink.getGroup().equals(currentCategory.getName())) {
                        tmp.add(drink);
                    }
                }
            } else {
                for (Drink drink : mListDrink) { // PUSH ORDER
                    for (Ordered item : listOrdered) {
                        if (item.getId().equals(drink.getId())) {
                            drink.setOrdering(item.getQuantity());
                        }
                    }
                    if(drink.getGroup() != null) {
                        if (drink.getGroup().equals(currentCategory.getName())) {
                            tmp.add(drink);
                        }
                    }
                }
                // pass List ordered TO Fragment_Order_Detail_ViewPager
                listFilterDrink = filterItemSelected(true);
                iListFilter.setListItemSelected(listFilterDrink);
            }
        }

        adapterDrinkOrder.setData(tmp);
    }

    private void showItemDrink() {
        if(mListDrink == null || currentCategory == null){
            return;
        }

        List<Drink> tmp = new ArrayList<>();

        if (listOrdered == null) { // CREATE NEW ORDER
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
            }else if (currentCategory.getId().equals("all")){
                tmp = mListDrink;
            }else {
                for (Drink drink : mListDrink) {
                    if (drink.getGroup() != null && drink.getGroup().equals(currentCategory.getName())) {
                        tmp.add(drink);
                    }
                }
            }
        } else { // CHANGE ORDER
            for (Drink drink : mListDrink) {
                for (Ordered item : listOrdered) {
                    if (item.getId().equals(drink.getId())) {
                        drink.setOrdering(item.getQuantity());
                    }
                }
            }

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
            }else if (currentCategory.getId().equals("all")){
                tmp = mListDrink;
            }else{
                for (Drink drink : mListDrink) {
                    if (drink.getGroup() != null && drink.getGroup().equals(currentCategory.getName())) {
                        tmp.add(drink); // FILLTER BY CATEGORY
                    }
                }
            }

            // pass List ordered TO Fragment_Order_Detail_ViewPager
            listFilterDrink = filterItemSelected(true);
            iListFilter.setListItemSelected(listFilterDrink);

        }


        adapterDrinkOrder.setData(tmp);
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
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Logd("onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
//                String commentKey = dataSnapshot.getKey();

                // ...
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
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//                Logd("onChildChanged()");
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
//                Logd("onChildRemoved()");
                String id = snapshot.getKey();
                for(int i = 0 ; i < mListDrink.size() ; i++){
                    if(mListDrink.get(i).getId().equals(id)){
                        mListDrink.remove(i);
                        showItemDrink();
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

        catPopular.setSelected(true);

        currentCategory = catPopular;

        mListCategory.add(catPopular);
        mListCategory.add(catAll);

        mListDrink = new ArrayList<>();
    }



    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== Fragment_Category_ViewPager.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== Fragment_Category_ViewPager.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== Fragment_Category_ViewPager.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== Fragment_Category_ViewPager.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast(Context context, String str ){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

}