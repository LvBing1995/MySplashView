package com.githun.lvbing.mysplashview;

import android.os.Parcel;
import android.os.Parcelable;

public class Advert implements Parcelable{


    /**
     * linkurl : http://www.cztv.com
     * imageurl : http://i04.cztv.com/me/2016-01/06/2ff4d79aaf428976756f6058b8dea94b.jpg
     * duration : 4
     */

    private String linkurl;
    private String imageurl;
    private int duration;
    private int is_superscript = 1;//0：不显示倒计时，1：显示倒计时
    public Advert() {
    }

    protected Advert(Parcel in) {
        linkurl = in.readString();
        imageurl = in.readString();
        duration = in.readInt();
        is_superscript = in.readInt();
    }

    public static final Creator<Advert> CREATOR = new Creator<Advert>() {
        @Override
        public Advert createFromParcel(Parcel in) {
            return new Advert(in);
        }

        @Override
        public Advert[] newArray(int size) {
            return new Advert[size];
        }
    };

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIs_superscript() {
        return is_superscript;
    }

    public void setIs_superscript(int is_superscript) {
        this.is_superscript = is_superscript;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(linkurl);
        dest.writeString(imageurl);
        dest.writeInt(duration);
        dest.writeInt(is_superscript);
    }
}
