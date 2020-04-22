package com.navetteclub.ui.order;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.navetteclub.R;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarRecyclerViewAdapter extends RecyclerView.Adapter<CarRecyclerViewAdapter.ViewHolder> {

    private List<CarAndModel> mItems;

    private final OnClickItemListener<CarAndModel> mListener;

    private int selected;

    public CarRecyclerViewAdapter(OnClickItemListener<CarAndModel> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onClick(v, position, holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<CarAndModel> items){
        if (mItems == null) {
            mItems = items;
            notifyItemRangeInserted(0, mItems.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mItems.size();
                }

                @Override
                public int getNewListSize() {
                    return items.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    CarAndModel oldItem = mItems.get(oldItemPosition);
                    CarAndModel newItem = mItems.get(newItemPosition);
                    return oldItem != null
                            && oldItem.getCar() != null
                            && newItem != null
                            && newItem.getCar() != null
                            && oldItem.getCar().getId()!=null
                            && oldItem.getCar().getId().equals(newItem.getCar().getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CarAndModel oldItem = mItems.get(oldItemPosition);
                    CarAndModel newItem = mItems.get(newItemPosition);
                    return oldItem != null
                            && oldItem.getCar() != null
                            && newItem != null
                            && newItem.getCar() != null
                            && oldItem.getCar().getName()!=null
                            && oldItem.getCar().getName().equals(newItem.getCar().getName())
                            && oldItem.isSelected() == newItem.isSelected();
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public List<CarAndModel> getItems() {
        return this.mItems;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        int old = this.selected;
        this.selected = selected;
        if(old<getItemCount()) {
            mItems.get(old).setSelected(false);
            notifyItemChanged(old);
        }
        if(selected<getItemCount()) {
            mItems.get(selected).setSelected(true);
            notifyItemChanged(selected);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView mView;
        final ImageView mCarImageView;
        final TextView mNameTextView;
        final TextView mDescTextView;
        CarAndModel mItem;

        ViewHolder(View view) {
            super(view);
            mView = (MaterialCardView) view;
            mCarImageView = view.findViewById(R.id.carImageView);
            mNameTextView = view.findViewById(R.id.nameTextView);
            mDescTextView = view.findViewById(R.id.descTextView);
        }

        void setItem(CarAndModel item){
            mItem = item;
            if(mItem!=null){
                if(mItem.isSelected())
                    mView.setStrokeColor(mView.getContext().getResources().getColor(R.color.colorAccent));
                else
                    mView.setStrokeColor(mView.getContext().getResources().getColor(R.color.colorIcon));

                if(mItem.getCar()!=null){
                    mNameTextView.setText(mItem.getCar().getName());
                    mDescTextView.setText(String.valueOf(mItem.getCar().getPlace()));
                    if(mItem.getCar().getImageUrl()!=null){
                        /*
                        Picasso.get()
                                .load(Constants.getBaseUrl() + mItem.getCar().getImageUrl())
                                .placeholder(R.drawable.car_placeholder)
                                .error(R.drawable.car_placeholder)
                                .resize(360,180)
                                .into(mCarImageView);
                         */
                    }
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }
    }
}

