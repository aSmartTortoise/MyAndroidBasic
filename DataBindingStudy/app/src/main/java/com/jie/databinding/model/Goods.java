package com.jie.databinding.model;

import com.jie.databinding.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Goods extends BaseObservable {
    @Bindable
    public String mName;
    private String mDetails;
    private float mPrice;

    public Goods(String name, String details, float price) {
        mName = name;
        mDetails = details;
        mPrice = price;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
        notifyChange();
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }
}
