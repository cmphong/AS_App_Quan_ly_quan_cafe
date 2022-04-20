package com.c_m_p.poscoffeeshop.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterViewPagerOrder extends FragmentStateAdapter {

    List<Fragment> listFragment;

    public AdapterViewPagerOrder(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setData(List<Fragment> listFragment){
        this.listFragment = listFragment;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getItemCount() {
        return listFragment.size();
    }
}
