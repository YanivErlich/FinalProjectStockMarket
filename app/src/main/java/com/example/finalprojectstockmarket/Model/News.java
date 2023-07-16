package com.example.finalprojectstockmarket.Model;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.Instant;
import java.util.Date;

public class News {


    public final String src;
    public final String img;
    public final String lastUpdated;
    public final String title;
    public final String url;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public News (String src, String img, String title, String timestamp, String url){

        // this is need since new android accepts only https calls
        String urlHttps = img.replaceFirst("(?i)^http://", "https://");

        this.src = src;
        this.img = urlHttps;
        this.title = title;
        this.url = url;

        Date currentDate = new Date();
        Instant instant = Instant.parse(timestamp);
        long diff = currentDate.getTime() - instant.toEpochMilli();
        long days = (diff / (60*60*24*1000));
        String lastAgoTxt = " days ago";

        if (days <= 0){
            days = (diff / (60*60*1000));

            lastAgoTxt = "hours ago";

            if (days <= 0){
                days = (diff / (60*1000));
                lastAgoTxt = "minutes ago";
            }
        }

        this.lastUpdated = days + lastAgoTxt;

    }
}
