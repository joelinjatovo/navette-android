package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;

@Entity(
    tableName = "orders"
)
public class Order {

    public static final String STATUS_PING = "ping";

    public static final String STATUS_ON_HOLD = "on-hold";

    public static final String STATUS_PROCESSING = "processing";

    public static final String STATUS_OK = "ok";

    public static final String STATUS_ACTIVE = "active";

    public static final String STATUS_CANCELED = "canceled";

    public static final String STATUS_COMPLETED = "completed";

    public static final String PAYMENT_TYPE_CASH = "cash";

    public static final String PAYMENT_TYPE_STRIPE = "stripe";

    public static final String PAYMENT_TYPE_PAYPAL = "paypal";

    public static final String PAYMENT_TYPE_APPLE_PAY = "apple_pay";

    public static final String TYPE_GO = "go";

    public static final String TYPE_BACK = "back";

    public static final String TYPE_GO_BACK = "go-back";

    @PrimaryKey
    private Long id;

    @SerializedName("rid")
    @ColumnInfo(name = "rid")
    private String rid; // Remote id

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private String type;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("place")
    @ColumnInfo(name = "place")
    private int place;

    @SerializedName("distance")
    @ColumnInfo(name = "distance")
    private Long distance;

    @SerializedName("payment_type")
    @ColumnInfo(name = "payment_type")
    private String paymentType;

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
    private Boolean privatized = false;

    @SerializedName("preordered")
    @ColumnInfo(name = "preordered")
    private Boolean preordered = false;

    @ColumnInfo(name = "user_id")
    private Long userId;

    @ColumnInfo(name = "club_id")
    private Long clubId;

    @ColumnInfo(name = "car_id")
    private Long carId;

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

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public Double getAmount() {
        return amount;
    }

    public String getAmountStr() {
        Double amount = getAmount();
        if(amount==null){
            amount = 0.00;
        }

        String currency = getCurrency();
        NumberFormat format = NumberFormat.getCurrencyInstance();
        //format.setMaximumFractionDigits(2);
        //format.setMinimumFractionDigits(2);
        format.setCurrency(Currency.getInstance(currency));
        return format.format(amount);
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

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
