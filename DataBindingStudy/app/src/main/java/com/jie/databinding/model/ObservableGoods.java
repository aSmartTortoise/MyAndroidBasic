package com.jie.databinding.model;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;

public class ObservableGoods {
    private ObservableField<String> mName;
    private ObservableField<String> mDetails;
    private ObservableFloat mPrice;

    public ObservableGoods(String name, String details, float price) {
        mName = new ObservableField<>(name);
        mDetails = new ObservableField<>(details);
        mPrice = new ObservableFloat(price);
    }

    public ObservableField<String> getName() {
        return mName;
    }

    public void setName(String name) {
        mName.set(name);
    }

    public ObservableField<String> getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails.set(details);
    }

    public ObservableFloat getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice.set(price);
    }
}
