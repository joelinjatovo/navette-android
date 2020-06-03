package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.databinding.ViewholderRidePointBinding;
import com.navetteclub.ui.OnClickItemListener;

import java.util.List;

public class RidePointRecyclerViewAdapter extends RecyclerView.Adapter<RidePointRecyclerViewAdapter.ViewHolder>{

    private List<RidePointWithDatas> mItems;

    private final OnClickItemListener<RidePointWithDatas> mListener;

    public RidePointRecyclerViewAdapter(OnClickItemListener<RidePointWithDatas> listener) {
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
                    return oldItem.getRidePoint()!=null &&
                            oldItem.getRidePoint().getId() != null &&
                            oldItem.getRidePoint().getId().equals(newItem.getRidePoint().getId());
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
        final ViewholderRidePointBinding mBinding;
        RidePointWithDatas mItem;

        ViewHolder(ViewholderRidePointBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(RidePointWithDatas item){
            mItem = item;
            if(mItem!=null){
                if(mItem.getRidePoint()!=null){
                    mBinding.setRideType(mItem.getRidePoint().getType());
                    mBinding.setRideStatus(mItem.getRidePoint().getStatus());
                    mBinding.setDuration(mItem.getRidePoint().getDuration());

                    if(mItem.getPoint()!=null){
                        mBinding.setPoint(mItem.getPoint().getName());
                    }

                    if(mItem.getRidePoint().getStatus()!=null){
                        mBinding.setIsActive(RidePoint.STATUS_ACTIVE.equals(mItem.getRidePoint().getStatus()));
                        mBinding.setIsNext(RidePoint.STATUS_NEXT.equals(mItem.getRidePoint().getStatus()));
                        mBinding.setIsOnline(RidePoint.STATUS_ONLINE.equals(mItem.getRidePoint().getStatus()));
                        mBinding.setIsCompleted(RidePoint.STATUS_COMPLETED.equals(mItem.getRidePoint().getStatus()));
                        mBinding.setIsCanceled(RidePoint.STATUS_CANCELED.equals(mItem.getRidePoint().getStatus()));
                        if (RidePoint.STATUS_PING.equals(mItem.getRidePoint().getStatus())) {
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

