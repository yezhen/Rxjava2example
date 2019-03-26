package com.org.example.module.map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.org.example.BaseFragment;
import com.org.example.R;
import com.org.example.adapter.ItemListAdapter;
import com.org.example.model.ItemBean;
import com.org.example.network.NetWork;
import com.org.example.utils.GankBeautyResult2ItemMapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MapFragment extends BaseFragment {

    private int page;

    @BindView(R.id.tv_page)
    TextView mTvPage;

    @BindView(R.id.btn_pre)
    Button mBtnPre;

    @BindView(R.id.btn_next)
    Button mBtnNext;

    @BindView(R.id.recycleView)
    RecyclerView mRvView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ItemListAdapter mAdapter;

    private List<ItemBean> mItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mItems = new ArrayList<>();
        mAdapter = new ItemListAdapter(mItems);
        mRvView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRvView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW);
        mSwipeRefreshLayout.setEnabled(false);
        return view;
    }

    @OnClick(R.id.btn_pre)
    void onPrePage() {
        loadPage(--page);
        if (page == 1) {
            mBtnPre.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_next)
    void onNextPage() {
        loadPage(++page);
        if (page == 1) {
            mBtnPre.setEnabled(true);
        }
    }


    private void loadPage(int page) {
        mItems.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        NetWork.getGankApi().getBeauties(10, page)
                .map(GankBeautyResult2ItemMapper.getInstance())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ItemBean>>() {
                    @Override
                    public void accept(List<ItemBean> itemBeans) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mItems.addAll(itemBeans);
                        mTvPage.setText(getString(R.string.page_with_number, MapFragment.this.page));
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "加载数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
