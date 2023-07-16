package com.example.finalprojectstockmarket.Section;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectstockmarket.Model.CompanyHeader;
import com.example.finalprojectstockmarket.Model.CompanyItem;
import com.example.finalprojectstockmarket.Interface.ItemMoveCall;
import com.example.finalprojectstockmarket.Model.Company;
import com.example.finalprojectstockmarket.R;

import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class PortfolioSection extends Section implements ItemMoveCall.ItemTouchHelperContract{
    private final String title;
    private final List<Company> list;
    private final PortfolioSection.ClickListener clickListener;
    private final Context context;

    public PortfolioSection(@NonNull final String title, @NonNull final List<Company> list, Context context, @NonNull final PortfolioSection.ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_company)
                .headerResourceId(R.layout.portfolio_sec_header)
                .build());

        this.title = title;
        this.list = list;
        this.clickListener = clickListener;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new CompanyItem(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CompanyItem itemHolder = (CompanyItem) holder;

        final Company company = list.get(position);

        itemHolder.ticker.setText(company.ticker);
        itemHolder.imgItem.setImageResource(company.arrow);
        itemHolder.change.setText(String.valueOf(company.change));
        itemHolder.change.setTextColor(company.changeColor);
        itemHolder.shares_or_name.setText(company.shares + " shares");
        itemHolder.last.setText(company.last);

        if(!company.name.equals("nul")){
            itemHolder.ticker.setText(company.ticker);
            itemHolder.imgItem.setImageResource(company.arrow);
            itemHolder.change.setText(String.valueOf(company.change));
            itemHolder.change.setTextColor(company.changeColor);
            itemHolder.shares_or_name.setText(company.name_or_shares);
            itemHolder.last.setText(company.last);
        }else {
            itemHolder.ticker.setText(company.ticker);
            itemHolder.btnGoTo.setVisibility(View.GONE);
            itemHolder.shares_or_name.setText(company.shares);
            itemHolder.change.setText("");
            itemHolder.last.setText("");
        }

        if(company.name.equals("nul")){
            itemHolder.btnGoTo.setVisibility(View.GONE);
        }

        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(company, itemHolder.getAdapterPosition())
        );

        itemHolder.btnGoTo.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(company, itemHolder.getAdapterPosition())
        );
    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {

        return new CompanyHeader(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final CompanyHeader headerHolder = (CompanyHeader) holder;

        headerHolder.tvTitle.setText(title);
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

    @Override
    public void onRowSelected(CompanyItem myViewHolder) {
        myViewHolder.rootView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(CompanyItem myViewHolder) {
        myViewHolder.rootView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.grey, null));
    }


    public interface ClickListener {

        void onItemRootViewClicked(Company company, final int itemAdapterPosition);
    }
}
