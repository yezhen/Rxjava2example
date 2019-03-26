package com.org.example.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GankResultBeauty {
    public boolean error;

    @SerializedName("results")
    public List<GankBeauty> beauties;
}
