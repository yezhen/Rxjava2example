package com.org.example.network;

import com.org.example.model.ElementaryBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 基础
 */
public interface ElementaryApi {

    @GET("search")
    Observable<List<ElementaryBean>> search(@Query("q") String query);


}
