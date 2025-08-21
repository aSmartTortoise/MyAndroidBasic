package com.wyj.view.animation.like.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 链接bean
 * @author xiaoman
 */
data class LinkBean(
    var linkTitle: String? = null,
    var linkUrl: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(linkTitle)
        dest.writeString(linkUrl)
    }

    companion object CREATOR : Parcelable.Creator<LinkBean> {
        override fun createFromParcel(parcel: Parcel): LinkBean {
            return LinkBean(parcel)
        }

        override fun newArray(size: Int): Array<LinkBean?> {
            return arrayOfNulls(size)
        }
    }

}