package com.example.barry.firebasepushnotificationdemo.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.barry.firebasepushnotificationdemo.NotificationFragment;
import com.example.barry.firebasepushnotificationdemo.ProfileFragment;
import com.example.barry.firebasepushnotificationdemo.UsersFragment;

//1. have this extend PagerViewAdapter
public class PagerViewAdapter extends FragmentPagerAdapter {
    //2. alt+click > implement the methods to fix error
    //3. alt+click > create the super() to fix error

    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //return null;
        //5. add a switch to handle clicks
        switch (position) {
            case 0:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;

            case 1:
                UsersFragment usersFragment = new UsersFragment();
                return usersFragment;

            case 2:
                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;

                default:
                    return null;
        }

        //return null;
    }

    @Override
    public int getCount() {
        return 3; //4. return the 3 fragments
    }
}
