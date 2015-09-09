package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.cquant.lizone.R;
import com.cquant.lizone.frag.AccountOverviewFragment;
import com.cquant.lizone.frag.AccountPositionFragment;
import com.cquant.lizone.frag.AccountRecordFragment;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountActivity extends BaseActivity {

    private static final String[] CONTENT = new String[] { "概览", "持仓","记录" };

    private Toolbar toolbar;

    private ArrayList<Fragment> listFragment;

    private AccountOverviewFragment mOverviewFrag;
    private AccountPositionFragment mPositionFrag;
    private AccountRecordFragment mRecordFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        initToolBar();
        listFragment = new ArrayList<Fragment>();
        mOverviewFrag = new AccountOverviewFragment();
        mPositionFrag = new AccountPositionFragment();
        mRecordFrag = new AccountRecordFragment();

        listFragment.add(mOverviewFrag);
        listFragment.add(mPositionFrag);
        listFragment.add(mRecordFrag);

        FragmentPagerAdapter adapter = new AccountAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.my_account);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_verify:
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.open_firm_account_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    class AccountAdapter extends FragmentPagerAdapter {
        public AccountAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position ];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
