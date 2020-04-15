package com.navetteclub.api.models;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;

public class OrderRequest {
    @SerializedName("place")
    public Integer place;

    @SerializedName("preordered")
    public Boolean preordered;

    @SerializedName("privatized")
    public Boolean privatized;

    @SerializedName("distance_value")
    public long distanceValue;

    @SerializedName("distance")
    public String distance;

    @SerializedName("delay_value")
    public long delayValue;

    @SerializedName("delay")
    public String delay;

    @SerializedName("direction")
    public String direction;

    @SerializedName("car")
    public Long car;

    @SerializedName("origin")
    public Point origin;

    @SerializedName("retours")
    public Point retours;

    public OrderRequest(OrderWithDatas orderWithDatas){
        if(orderWithDatas.getOrder()!=null){
            place = orderWithDatas.getOrder().getPlace();
            preordered = orderWithDatas.getOrder().getPreordered();
            privatized = orderWithDatas.getOrder().getPrivatized();
            distance = orderWithDatas.getOrder().getDistance();
            distanceValue = orderWithDatas.getOrder().getDistanceValue();
            delay = orderWithDatas.getOrder().getDelay();
            delayValue = orderWithDatas.getOrder().getDelayValue();
            direction = orderWithDatas.getOrder().getDirection();
        }

        if(orderWithDatas.getCar()!=null){
            car = orderWithDatas.getCar().getId();
        }

        origin = orderWithDatas.getOrigin();
        retours = orderWithDatas.getRetours();
    }
}
