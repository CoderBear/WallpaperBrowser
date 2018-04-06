package com.udemy.sbsapps.wallpaperbrowser.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.udemy.sbsapps.wallpaperbrowser.R;

/**
 * Created by michaelmallamo on 6/04/2018.
 */

public class Functions {

    public static void changeMainFragment(FragmentActivity fragmentActivity, Fragment fragment){
        fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }
}
