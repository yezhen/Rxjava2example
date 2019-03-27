package com.org.example.module.token;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.org.example.BaseFragment;
import com.org.example.R;
import com.org.example.model.FakeThing;
import com.org.example.model.FakeToken;
import com.org.example.network.FakeApi;
import com.org.example.network.NetWork;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TokenFragment extends BaseFragment {
    @OnClick(R.id.btn_load)
    void onLoad() {
        mSwipeRefreshLayout.setRefreshing(true);
        final FakeApi fakeApi = NetWork.getFakeApi();
        unsubscribe();
        disposable = fakeApi.getFakeToken("fake_auth_token")
                .flatMap(new Function<FakeToken, ObservableSource<FakeThing>>() {
                    @Override
                    public ObservableSource<FakeThing> apply(FakeToken token) throws Exception {
                        return fakeApi.getFakeData(token);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FakeThing>() {
                    @Override
                    public void accept(FakeThing fakeThing) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mTvResult.setText(getString(R.string.got_data,fakeThing.id,fakeThing.name));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token, container, false);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN, Color.RED);
        return view;
    }
}
