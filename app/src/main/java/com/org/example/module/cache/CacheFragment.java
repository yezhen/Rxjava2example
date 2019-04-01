package com.org.example.module.cache;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.org.example.BaseFragment;
import com.org.example.R;
import com.org.example.adapter.ItemListAdapter;
import com.org.example.model.ItemBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class CacheFragment extends BaseFragment {

    private long startingTime;

    @BindView(R.id.tv_load_time)
    TextView mTvLoadTime;

    @OnClick(R.id.btn_CleanMemoryAndDiskCache)
    void CleanMemoryAndDiskCache() {
        Data.getInstance().cleanMemoryAndDiskCache();
        mItems.clear();
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "内存缓存和磁盘缓存已清空", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_CleanMemoryCache)
    void CleanMemoryCache() {
        Data.getInstance().cleanMemoryCache();
        mItems.clear();
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "内存缓存已清空", Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.btn_load)
    void load() {
        mSwipeRefreshLayout.setRefreshing(true);
        startingTime = System.currentTimeMillis();
        unsubscribe();
        Data.getInstance().subscribeData(new Consumer<List<ItemBean>>() {
            @Override
            public void accept(List<ItemBean> itemBeans) throws Exception {
                mSwipeRefreshLayout.setRefreshing(false);
                int loadingTime = (int) (System.currentTimeMillis() - startingTime);
                mTvLoadTime.setText(
                        getString(R.string.loading_time_and_source,
                                loadingTime,
                                Data.getInstance().getDataSourceText()));
                mItems.clear();
                mItems.addAll(itemBeans);
                mAdapter.notifyDataSetChanged();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @BindView(R.id.recycleView)
    RecyclerView mRecycleView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<ItemBean> mItems;

    private ItemListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cache, container, false);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN, Color.RED);

        mItems = new ArrayList<>();
        mAdapter = new ItemListAdapter(mItems);

        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecycleView.setAdapter(mAdapter);
        return view;
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_cache;
    }

    @Override
    protected int getTileRes() {
        return R.string.title_cache;
    }
}
