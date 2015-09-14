package com.cquant.lizone.frag;

import android.os.Bundle;

import com.cquant.lizone.util.Utils;

/**
 * Created by asus on 2015/9/13.
 */
public class MonthMasterFragment extends MasterListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        url = Utils.BASE_URL+"Faxian_list/aid/3/";
        super.onCreate(savedInstanceState);
    }
}
