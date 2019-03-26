package com.org.example.module.elementary;

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
import android.widget.RadioButton;
import android.widget.Toast;

import com.org.example.BaseFragment;
import com.org.example.R;
import com.org.example.adapter.ElementaryAdapter;
import com.org.example.model.ElementaryBean;
import com.org.example.network.NetWork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ElementaryFragment extends BaseFragment {
    @BindView(R.id.recycleView)
    RecyclerView mRvView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    private List<ElementaryBean> mEleBeans;
    private ElementaryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elementary, container, false);
        ButterKnife.bind(this, view);
        mEleBeans = new ArrayList<>();
        mRvView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new ElementaryAdapter(mEleBeans);
        mRvView.setAdapter(mAdapter);
        mRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        mRefreshLayout.setEnabled(false);
        return view;
    }

    @OnCheckedChanged({R.id.searchRb1, R.id.searchRb2, R.id.searchRb3, R.id.searchRb4})
    void onTabChanged(RadioButton radioButton, boolean checked) {
        if (checked) {
            unsubscribe();
            mEleBeans.clear();
            mRefreshLayout.setRefreshing(true);
            //todo 访问网络
            search(radioButton.getText().toString());

        }
    }

    private void search(String key) {
        disposable = NetWork.getElementaryApi()
                .search(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ElementaryBean>>() {
                    @Override
                    public void accept(List<ElementaryBean> elementaryBeans) throws Exception {
                        mRefreshLayout.setRefreshing(false);
                        mEleBeans.addAll(elementaryBeans);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
