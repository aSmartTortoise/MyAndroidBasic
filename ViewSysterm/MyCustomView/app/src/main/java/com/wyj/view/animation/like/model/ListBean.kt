package com.wyj.view.animation.like.model

import android.os.Parcel
import android.os.Parcelable

data class ListBean(
    val desc: String,
    var hasLike: Boolean,
    var likeNumber: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(desc)
        dest.writeInt(if (hasLike) 1 else 0)
        dest.writeInt(likeNumber)
    }

    companion object CREATOR : Parcelable.Creator<ListBean> {
        override fun createFromParcel(parcel: Parcel): ListBean {
            return ListBean(parcel)
        }

        override fun newArray(size: Int): Array<ListBean?> {
            return arrayOfNulls(size)
        }
    }
}