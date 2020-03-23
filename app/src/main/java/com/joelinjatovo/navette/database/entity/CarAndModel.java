package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

public class CarAndModel {

    @SerializedName("car_model")
    @Embedded
    private CarModel model;

    @SerializedName("car")
    @Relation(
            parentColumn = "id",
            entityColumn =  "car_model_id"
    )
    private Car car;

    public CarModel getModel() {
        return model;
    }

    public void setModel(CarModel model) {
        this.model = model;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
