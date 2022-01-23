package com.jie.databinding.model;

import androidx.databinding.ObservableField;

public class Image {
    private ObservableField<String> mUrl;
    public Image(String url) {
        mUrl = new ObservableField<>(url);
    }

    public ObservableField<String> getUrl() {
        return mUrl;
    }

    public void setUrl(ObservableField<String> url) {
        mUrl = url;
    }
}
