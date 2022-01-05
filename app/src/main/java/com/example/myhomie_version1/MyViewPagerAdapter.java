package com.example.myhomie_version1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myhomie_version1.fragment.Co2Fragment;
import com.example.myhomie_version1.fragment.HumidFragment;
import com.example.myhomie_version1.fragment.TempFragment;

import org.jetbrains.annotations.NotNull;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new TempFragment();
            case 1:
                return new HumidFragment();
            case 2:
                return new Co2Fragment();
            default:
                return new TempFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
