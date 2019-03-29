package com.org.example.module.cache;

import android.support.annotation.IntDef;

import com.org.example.App;
import com.org.example.R;
import com.org.example.model.ItemBean;
import com.org.example.network.NetWork;
import com.org.example.utils.GankBeautyResult2ItemMapper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class Data {
    private static Data instance;

    private static final int DATA_SOURCE_MEMORY = 1;
    private static final int DATA_SOURCE_DISK = 2;
    private static final int DATA_SOURCE_NETWORK = 3;

    @IntDef({DATA_SOURCE_MEMORY, DATA_SOURCE_DISK, DATA_SOURCE_NETWORK})
    @interface DataSource {

    }

    BehaviorSubject<List<ItemBean>> cache;

    private int dataSource;

    private Data() {
    }

    public static  Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    private void setDataSource(@DataSource int dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSourceText() {
        int dataSourceTextRes;
        switch (dataSource) {
            case DATA_SOURCE_MEMORY:
                dataSourceTextRes = R.string.data_source_memory;
                break;
            case DATA_SOURCE_DISK:
                dataSourceTextRes = R.string.data_source_disk;
                break;
            case DATA_SOURCE_NETWORK:
                dataSourceTextRes = R.string.data_source_network;
                break;
            default:
                dataSourceTextRes = R.string.data_source_network;
                break;
        }
        return App.getInstance().getString(dataSourceTextRes);
    }

    public void loadFromNetWork() {
        NetWork.getGankApi().getBeauties(100, 1)
                .subscribeOn(Schedulers.io())
                .map(GankBeautyResult2ItemMapper.getInstance())
                .doOnNext(new Consumer<List<ItemBean>>() {
                    @Override
                    public void accept(List<ItemBean> itemBeans) throws Exception {
                        DataBase.getInstance().writeItems(itemBeans);
                    }
                })
                .subscribe(new Consumer<List<ItemBean>>() {
                    @Override
                    public void accept(List<ItemBean> itemBeans) throws Exception {
                        cache.onNext(itemBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        cache.onError(throwable);
                    }
                });

    }

    public Disposable subscribeData(Consumer<List<ItemBean>> onNext, Consumer<Throwable> onError) {
        if (cache == null) {
            cache = BehaviorSubject.create();
            Observable.create(new ObservableOnSubscribe<List<ItemBean>>() {
                @Override
                public void subscribe(ObservableEmitter<List<ItemBean>> e) throws Exception {
                    List<ItemBean> itemBeans = DataBase.getInstance().readItems();
                    if (itemBeans == null) {
                        setDataSource(DATA_SOURCE_NETWORK);
                        loadFromNetWork();
                    } else {
                        setDataSource(DATA_SOURCE_DISK);
                        e.onNext(itemBeans);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe(cache);
        } else {
            setDataSource(DATA_SOURCE_MEMORY);
        }
        return cache.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                cache = null;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public void cleanMemoryCache() {
        cache = null;
    }

    public void cleanMemoryAndDiskCache() {
        cleanMemoryCache();
        DataBase.getInstance().delete();
    }

}
