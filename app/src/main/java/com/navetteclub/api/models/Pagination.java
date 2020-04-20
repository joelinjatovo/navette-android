package com.navetteclub.api.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Pagination {
    @SerializedName("total")
    public Integer total;

    @SerializedName("count")
    public Integer count;

    @SerializedName("per_page")
    public Integer perPage;

    @SerializedName("current_page")
    public Integer currentPage;

    @SerializedName("last_page")
    public Integer lastPage;

    @NonNull
    public String toString(){
        return "Pagination["
                + " total=" + total
                + " count=" + count
                + " perPage=" + perPage
                + " currentPage=" + currentPage
                + " lastPage=" + lastPage
                + " isLastPage=" + isLastPage()
            + "]";
    }

    public boolean isLastPage(){
        return currentPage == lastPage;
    }
}
