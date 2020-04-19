package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.databinding.ViewholderRidePointBinding;
import com.navetteclub.databinding.ViewpagerRidePointBinding;
import com.navetteclub.databinding.ViewpagerRidePointBindingImpl;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RidePointMapRecyclerViewAdapter extends RecyclerView.Adapter<RidePointMapRecyclerViewAdapter.ViewHolder>{

    private List<RidePointWithDatas> mItems;

    private OnClickItemListener<RidePointWithDatas> mListener;

    public RidePointMapRecyclerViewAdapter setOnClickListener(OnClickItemListener<RidePointWithDatas> listener){
        mListener = listener;
        return this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewpagerRidePointBinding itemBinding = ViewpagerRidePointBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onClick(v, position, holder.mItem);
            }
        });
        holder.mBinding.cancelButton.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
        holder.mBinding.callButtom.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
        holder.mBinding.actionButton.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<RidePointWithDatas> items){
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
                    RidePointWithDatas oldItem = mItems.get(oldItemPosition);
                    RidePointWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getRidePoint().getId() == newItem.getRidePoint().getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    RidePointWithDatas oldItem = mItems.get(oldItemPosition);
                    RidePointWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getRidePoint()!=null && oldItem.getRidePoint().equals(newItem.getRidePoint());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewpagerRidePointBinding mBinding;
        RidePointWithDatas mItem;

        ViewHolder(ViewpagerRidePointBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(RidePointWithDatas item){
            mItem = item;
            if(mItem!=null){
                RidePoint ridePoint = mItem.getRidePoint();
                if(ridePoint!=null){
                    mBinding.setRideType(ridePoint.getType());
                    mBinding.setRideStatus(ridePoint.getStatus());
                    if(RidePoint.STATUS_PING.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setText(R.string.button_next);
                        mBinding.actionButton.setVisibility(View.GONE);
                    }
                    if(RidePoint.STATUS_ACTIVE.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setText(R.string.button_go);
                        mBinding.actionButton.setVisibility(View.GONE);
                    }
                    if(RidePoint.STATUS_NEXT.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setText(R.string.button_active);
                        mBinding.actionButton.setVisibility(View.VISIBLE);
                    }
                    if(RidePoint.STATUS_ONLINE.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setVisibility(View.GONE);
                    }
                }
                if(mItem.getUser()!=null){
                    mBinding.setUser(mItem.getUser());
                    if(mItem.getUser().getImageUrl()!=null){
                        Picasso.get()
                                .load(Constants.getBaseUrl() + mItem.getUser().getImageUrl())
                                .placeholder(R.drawable.user_placeholder)
                                .error(R.drawable.user_placeholder)
                                .resize(100, 100)
                                .into(mBinding.avatarImageView);
                    }
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }
}

