package com.benben.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.benben.R;
import com.benben.ui.view.XListView;
import com.benben.ui.view.XListView.IXListViewListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ����������� ListActivity, ����2.xϵͳ�ļ���
 * 
 * @author gqiu
 * 
 */
public abstract class BaseListFragment extends Fragment implements IXListViewListener , OnItemClickListener
{
    protected XListView mListView;
    protected List<Object> mItems;
    protected LinearLayout headerLayout; // ���layout������view,��������������
    protected TextView tv_no_data;
    private int mTotalPages = 0;// ��ҳ��
    private int mPageIndex = 0;// ��ǰҳ��
    protected boolean isRefresh = false;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<Object>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.list_content_simple, null);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        mListView = (XListView) view.findViewById(R.id.list);
        headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);
        addLayoutHeaderView();
        setAdapter();
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        return view;
    }


    protected void addLayoutHeaderView()
    {

    }
    protected abstract void setAdapter();

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        if (arg2 < 1)
        {
            return;
        }
        onRealItemClick(arg0, arg1, arg2 - 1, arg3);
    }

    /**
     * ������д�˷���
     * 
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    protected void onRealItemClick(AdapterView<?> arg0, View arg1, int realPosition, long arg3)
    {

    }


    protected void dataLoadFinish()
    {
        onLoad();
    }

    protected void setTotalPages(int totalPage)
    {
        mTotalPages = totalPage;
    }

    protected List<Object> getItems()
    {
        return mItems;
    }

    protected int getPageIndex()
    {
        return mPageIndex;
    }

    /**
     * ���ݼ������֮�����
     */
    protected void onLoad()
    {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy��MM��dd��  HH:mm");
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(sformat.format(new Date()));

        if (mPageIndex == mTotalPages - 1 || mTotalPages <= 1)
            mListView.setPullLoadEnable(false);
        else
            mListView.setPullLoadEnable(true);
    }

    /**
     * �б�����ˢ�µ��õķ���
     */
    public void onRefresh()
    {
        tv_no_data.setVisibility(View.GONE);
        mListView.setPullLoadEnable(false);
        mPageIndex = 0;
        isRefresh = true;
    }

    /**
     * �б������������
     */
    public void onLoadMore()
    {
        mPageIndex++;
        if (mPageIndex >= mTotalPages)
            return;

    }
}
