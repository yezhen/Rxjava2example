package com.org.example.module.tokenAnvanced;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TokenAdvancedFragment extends BaseFragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_result)
    TextView mTvResult;

    private boolean tokenUpdated = false;

    private final FakeToken cachedFakeToken = new FakeToken(true);

    @OnClick(R.id.btn_request)
    void requestToken() {
        tokenUpdated = true;
        mSwipeRefreshLayout.setRefreshing(true);
        final FakeApi fakeApi = NetWork.getFakeApi();
        unsubscribe();
        disposable = Observable.just(1)
                .flatMap(new Function<Object, Observable<FakeThing>>() {
                    @Override
                    public Observable<FakeThing> apply(Object o) throws Exception {
                        Log.d("life", "apply....");
                        return cachedFakeToken.Token == null
                                ? Observable.<FakeThing>error(new NullPointerException("Token is null!"))
                                : fakeApi.getFakeData(cachedFakeToken);
                    }
                })
                .retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
                        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof IllegalArgumentException
                                        || throwable instanceof NullPointerException) {
                                    return fakeApi.getFakeToken("fake_auth_code")
                                            .doOnNext(new Consumer<FakeToken>() {
                                                @Override
                                                public void accept(FakeToken token) throws Exception {
                                                    tokenUpdated = true;
                                                    cachedFakeToken.expired = token.expired;
                                                    cachedFakeToken.Token = token.Token;
                                                }
                                            });
                                }
                                return Observable.error(throwable);
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FakeThing>() {
                    @Override
                    public void accept(FakeThing fakeData) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        String token = cachedFakeToken.Token;
                        if (tokenUpdated) {
                            token += "(已经更新)";
                        }
                        mTvResult.setText(getString(R.string.got_token_and_data, token, fakeData.id, fakeData.name));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @OnClick(R.id.btn_delete_token)
    void invalidateToken() {
        cachedFakeToken.expired = true;
        Toast.makeText(getActivity(), "token 已经销毁", Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token_advanced, container, false);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW);
        return view;

    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_token_advanced;
    }

    @Override
    protected int getTileRes() {
        return R.string.title_token_advanced;
    }
}
