package com.example.finalprojectstockmarket.Interface;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectstockmarket.Model.CompanyItem;

public abstract class ItemMoveCall extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCall(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof CompanyItem) {
                CompanyItem myViewHolder =
                        (CompanyItem) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }


    public interface ItemTouchHelperContract {


        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(CompanyItem myViewHolder);


        void onRowClear(CompanyItem myViewHolder);
    }

}