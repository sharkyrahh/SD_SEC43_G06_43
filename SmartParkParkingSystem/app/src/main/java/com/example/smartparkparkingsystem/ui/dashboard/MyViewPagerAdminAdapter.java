package com.example.smartparkparkingsystem.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyViewPagerAdminAdapter extends FragmentStateAdapter
{
    public MyViewPagerAdminAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MyViewPagerAdminAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MyViewPagerAdminAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new EntryFragmentAdmin();
            case 1:
                return new ExitFragmentAdmin();
            default:
                return new EntryFragmentAdmin();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
