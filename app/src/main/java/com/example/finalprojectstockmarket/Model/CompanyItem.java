package com.example.finalprojectstockmarket.Model;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectstockmarket.R;

public class CompanyItem extends RecyclerView.ViewHolder {

    public final View rootView;
    public final ImageView imgItem;
    public final TextView ticker;
    public final TextView shares_or_name;
    public final TextView change;
    public final TextView last;
    public final Button btnGoTo;
    public final View dividerLine;


    public CompanyItem(@NonNull View view) {
        super(view);

        rootView = view;
        imgItem = view.findViewById(R.id.img_change_arrow);
        ticker = view.findViewById(R.id.company_ticker);
        shares_or_name = view.findViewById(R.id.company_name_or_shares);
        change = view.findViewById(R.id.company_price_change);
        last = view.findViewById(R.id.company_price);
        btnGoTo = view.findViewById(R.id.btn_goTo);
        dividerLine = view.findViewById(R.id.divider_line);
    }
}
