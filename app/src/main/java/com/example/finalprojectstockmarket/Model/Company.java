package com.example.finalprojectstockmarket.Model;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

import com.example.finalprojectstockmarket.R;

public class Company {

    public String name;
    public String ticker;
    public String shares;
    public String last;
    public double change;
    public String name_or_shares;
    @DrawableRes
    public
    int arrow;
    @ColorInt
    public
    int changeColor;

    public Company(){
        this.name = "Loading..";
        this.shares = "loading..";
        this.last = "loading..";
        this.change = 0;
        this.name_or_shares = "0";
        this.arrow = 0;
    }

    public Company(String name, String ticker, String shares, String last, String prevClose,
                   String name_or_shares, Context context) {
        this.name = name;
        this.ticker = ticker;
        this.shares = shares;
        this.last = last;
        this.name_or_shares = name_or_shares;

    }

}
