package com.org.example.module.zip;

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
import android.widget.Toast;

import com.org.example.BaseFragment;
import com.org.example.R;
import com.org.example.adapter.ItemListAdapter;
import com.org.example.model.ElementaryBean;
import com.org.example.model.ItemBean;
import com.org.example.network.NetWork;
import com.org.example.utils.GankBeautyResult2ItemMapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ZipFragment extends BaseFragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView mRecycleView;

    @OnClick(R.id.btn_load)
    void onLoad() {
        mSwipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        disposable = Observable.zip(NetWork.getGankApi().getBeauties(300, 1).map(GankBeautyResult2ItemMapper.getInstance()),
                NetWork.getElementaryApi().search("可爱"),
                new BiFunction<List<ItemBean>, List<ElementaryBean>, List<ItemBean>>() {
                    @Override
                    public List<ItemBean> apply(List<ItemBean> itemBeans, List<ElementaryBean> elementaryBeans) throws Exception {
                        List<ItemBean> items = new ArrayList<>();
                        for (int i = 0; i < itemBeans.size() / 2 && i < elementaryBeans.size(); i++) {
                            items.add(itemBeans.get(i * 2));
                            items.add(itemBeans.get(i * 2 + 1));

                            ElementaryBean elementaryBean = elementaryBeans.get(i);
                            ItemBean itemBean = new ItemBean();
                            itemBean.description = elementaryBean.description;
                            itemBean.image_url = elementaryBean.image_url;
                            items.add(itemBean);
                        }
                        return items;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ItemBean>>() {
                    @Override
                    public void accept(List<ItemBean> itemBeans) throws Exception {
                        mItems.clear();
                        mSwipeRefreshLayout.setRefreshing(false);
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

    private ItemListAdapter mAdapter;

    private List<ItemBean> mItems;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zip, container, false);
        ButterKnife.bind(this, view);

        mItems = new ArrayList<>();
        mAdapter = new ItemListAdapter(mItems);
        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecycleView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW);

        return view;
    }
}
