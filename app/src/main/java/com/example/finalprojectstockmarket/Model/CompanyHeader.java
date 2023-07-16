package com.example.finalprojectstockmarket.Model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectstockmarket.R;

public class CompanyHeader extends RecyclerView.ViewHolder {

    public final TextView tvTitle;

    public CompanyHeader(@NonNull View view) {
        super(view);
        tvTitle = view.findViewById(R.id.tvTitle);
    }
}
