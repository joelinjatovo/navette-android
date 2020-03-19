package com.joelinjatovo.navette.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(
    tableName = "clubs"
)
public class Order {
    @PrimaryKey
    private Long id;

    @SerializedName("id")
    @ColumnInfo(name = "rid")
    private String rid; // Remote id

    @SerializedName("place")
    @ColumnInfo(name = "place")
    private Integer place;

    @SerializedName("amount")
    @ColumnInfo(name = "amount")
    private Double amount;

    @SerializedName("subtotal")
    @ColumnInfo(name = "subtotal")
    private Double subtotal;

    @SerializedName("total")
    @ColumnInfo(name = "total")
    private Double total;

    @SerializedName("vat")
    @ColumnInfo(name = "vat")
    private Double vat;

    @SerializedName("currency")
    @ColumnInfo(name = "currency")
    private String currency;

    @SerializedName("privatized")
    @ColumnInfo(name = "privatized")
    private Boolean privatized;

    @SerializedName("preordered")
    @ColumnInfo(name = "preordered")
    private Boolean preordered;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getPrivatized() {
        return privatized;
    }

    public void setPrivatized(Boolean privatized) {
        this.privatized = privatized;
    }

    public Boolean getPreordered() {
        return preordered;
    }

    public void setPreordered(Boolean preordered) {
        this.preordered = preordered;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
