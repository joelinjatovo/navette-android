package com.joelinjatovo.navette.api.models;

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
}
