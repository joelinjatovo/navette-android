package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.databinding.ViewholderRidePointBinding;
import com.navetteclub.ui.OnClickItemListener;

import java.util.List;

public class RidePointRecyclerViewAdapter extends RecyclerView.Adapter<RidePointRecyclerViewAdapter.ViewHolder>{

    private List<RidePoint> mItems;

    private final OnClickItemListener<RidePoint> mListener;

    public RidePointRecyclerViewAdapter(OnClickItemListener<RidePoint> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderRidePointBinding itemBinding = ViewholderRidePointBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
        if(position==0){
            holder.mBinding.top1ImageView.setVisibility(View.INVISIBLE);
        }else{
            holder.mBinding.top1ImageView.setVisibility(View.VISIBLE);
        }

        if(position==getItemCount()-1){
            holder.mBinding.bottom1ImageView.setVisibility(View.INVISIBLE);
            holder.mBinding.bottom2ImageView.setVisibility(View.INVISIBLE);
            holder.mBinding.bottom3ImageView.setVisibility(View.INVISIBLE);
        }else{
            holder.mBinding.bottom1ImageView.setVisibility(View.VISIBLE);
            holder.mBinding.bottom2ImageView.setVisibility(View.VISIBLE);
            holder.mBinding.bottom3ImageView.setVisibility(View.VISIBLE);
        }

        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
        holder.mBinding.buttonCall.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
        holder.mBinding.buttonCancel.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<RidePoint> items){
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
                    RidePoint oldItem = mItems.get(oldItemPosition);
                    RidePoint newItem = items.get(newItemPosition);
                    return oldItem!=null &&
                            oldItem.getId() != null &&
                            oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    RidePoint oldItem = mItems.get(oldItemPosition);
                    RidePoint newItem = items.get(newItemPosition);
                    return oldItem!=null && oldItem.equals(newItem);
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewholderRidePointBinding mBinding;
        RidePoint mItem;

        ViewHolder(ViewholderRidePointBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(RidePoint item){
            mItem = item;
            if(mItem!=null){
                if(mItem!=null){
                    mBinding.setRideType(mItem.getType());
                    mBinding.setRideStatus(mItem.getStatus());
                    mBinding.setDuration(mItem.getDuration());

                    if(mItem.getPoint()!=null){
                        mBinding.setPoint(mItem.getPoint().getName());
                    }

                    if(mItem.getStatus()!=null){
                        mBinding.setIsActive(RidePoint.STATUS_ACTIVE.equals(mItem.getStatus()));
                        mBinding.setIsNext(RidePoint.STATUS_NEXT.equals(mItem.getStatus()));
                        mBinding.setIsOnline(RidePoint.STATUS_STARTED.equals(mItem.getStatus()));
                        mBinding.setIsCompleted(RidePoint.STATUS_COMPLETED.equals(mItem.getStatus()));
                        mBinding.setIsCanceled(RidePoint.STATUS_CANCELED.equals(mItem.getStatus()));
                        if (RidePoint.STATUS_PING.equals(mItem.getStatus())) {
                            mBinding.orderTextView.setVisibility(View.GONE);
                        } else {
                            mBinding.orderTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if(mItem.getUser()!=null){
                    mBinding.setUser(mItem.getUser());
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mBinding.rideTitleTextView.getText() + "'";
        }
    }
}

