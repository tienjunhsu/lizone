package com.cquant.lizone.frag;

import android.support.v4.app.Fragment;

//import com.cquant.lizone.LizoneApp;
//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by PC on 2015/8/24.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        //RefWatcher refWatcher = LizoneApp.getRefWatcher(getActivity());
        //refWatcher.watch(this);
    }
}
