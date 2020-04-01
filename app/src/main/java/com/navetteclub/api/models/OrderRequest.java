package com.navetteclub.api.models;

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
        }

        if(orderWithDatas.getCar()!=null){
            car = orderWithDatas.getCar().getId();
        }

        if(orderWithDatas.getPoints()!=null){
            if(orderWithDatas.getPoints().size()>0){
                origin = orderWithDatas.getPoints().get(0);
            }
            if(orderWithDatas.getPoints().size()>2){
                retours = orderWithDatas.getPoints().get(2);
            }
        }
    }
}
