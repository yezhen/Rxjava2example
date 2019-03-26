package com.org.example.utils;

import com.org.example.model.GankBeauty;
import com.org.example.model.GankResultBeauty;
import com.org.example.model.ItemBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Function;

public class GankBeautyResult2ItemMapper implements Function<GankResultBeauty, List<ItemBean>> {
    private static GankBeautyResult2ItemMapper instance;

    private GankBeautyResult2ItemMapper() {}

    public static GankBeautyResult2ItemMapper getInstance() {
        if (instance == null) {
            synchronized (GankBeautyResult2ItemMapper.class) {
                if (instance == null) {
                    instance = new GankBeautyResult2ItemMapper();
                }
            }
        }
        return instance;
    }

    @Override
    public List<ItemBean> apply(GankResultBeauty gankResultBeauty) throws Exception {
        List<GankBeauty> beauties = gankResultBeauty.beauties;
        List<ItemBean> items = new ArrayList<>(beauties.size());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        for (GankBeauty gankBeauty :beauties) {
            ItemBean bean = new ItemBean();
            try {
                Date date = inputFormat.parse(gankBeauty.createdAt);
                bean.description = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                bean.description = "unknow date";
            }
            bean.image_url = gankBeauty.url;
            items.add(bean);
        }

        return items;
    }
}
