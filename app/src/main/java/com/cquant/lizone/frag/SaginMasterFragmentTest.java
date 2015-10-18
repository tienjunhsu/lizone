package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cquant.lizone.R;
import com.cquant.lizone.tool.LogTool;

/**
 * Created by asus on 2015/10/18.
 */
public class SaginMasterFragmentTest extends  BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup  mRadioMoney;
    private FragmentManager fm;
    private FragmentTransaction tx;
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View root = inflater.inflate(R.layout.master_list_fragment, container, false);
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        mRadioMoney = (RadioGroup) root.findViewById(R.id.rg_money);
       fm = getFragmentManager();
        fragment1 = new SignMasterFragment();
        fragment2 = new ShortMasterFragment();
        fragment2 = new StabMasterFragment();
        tx = fm.beginTransaction();
        tx.replace(R.id.id_content,fragment1);
        tx.commit();

        mRadioMoney.setOnCheckedChangeListener(this);

        return root;
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int radioButtonId = group.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) group.findViewById(radioButtonId);
        String str = rb.getText().toString();
            switch(radioButtonId) {
                case R.id.money_one:{
                    LogTool.d("new select is :"+str);
                    fm = getFragmentManager();
                    tx = fm.beginTransaction();
                    tx.replace(R.id.id_content,fragment1);
                    tx.commit();
                    break;
                }
                case R.id.money_two:{
                    LogTool.d("new select is :"+str);
                    fm = getFragmentManager();
                    tx = fm.beginTransaction();
                    tx.replace(R.id.id_content,fragment2);
                    tx.commit();
                    break;
                }
                case R.id.money_three:{
                    LogTool.d("new select is :"+str);
                    fm = getFragmentManager();
                    tx = fm.beginTransaction();
                    tx.replace(R.id.id_content,fragment3);
                    tx.commit();
                    break;
                }
            }
    }
}
