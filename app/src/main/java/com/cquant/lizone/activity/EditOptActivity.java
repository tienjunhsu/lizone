package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cquant.lizone.R;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.StrTool;

/**
 * Created by asus on 2015/11/17.
 */
public class EditOptActivity extends BaseActivity {

    private static final String TAG = "EditOptActivity";

    private Toolbar toolbar;
    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_opt_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        initToolBar();
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("添加自选");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class MarketAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return 3;//hsu
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int size = 0;
            /*try {
                size = mGroup.get(ex_label[groupPosition]).size();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return size;
        }

        @Override
        public Object getGroup(int groupPosition) {
            //return mGroup.get(ex_label[groupPosition]);
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            //测试一下没有数据的时候会不会出现空指针异常
            //return mGroup.get(ex_label[groupPosition]).get(childPosition);
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_parent_item,parent,false);
            }
            //TextView tv = (TextView) convertView.findViewById(R.id.ex_name);
            //tv.setText(ex_name[groupPosition]);
            //listView.expandGroup(groupPosition);//hsu
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_child_item,parent,false);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do something
                    LogTool.d(".......MarketFragment:setOnClickListener");
                    //startKpointActivity(item);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
