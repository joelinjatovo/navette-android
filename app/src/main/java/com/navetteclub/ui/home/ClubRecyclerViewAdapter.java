package com.navetteclub.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.database.entity.Club;
import com.navetteclub.databinding.ViewholderClubHomeBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder>{

    private List<Club> mItems;

    private final OnClickItemListener<Club> mListener;

    public ClubRecyclerViewAdapter(OnClickItemListener<Club> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderClubHomeBinding itemBinding = ViewholderClubHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

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
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<Club> items){
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
                    Club oldItem = mItems.get(oldItemPosition);
                    Club newItem = items.get(newItemPosition);
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Club oldItem = mItems.get(oldItemPosition);
                    Club newItem = items.get(newItemPosition);
                    return oldItem!=null && oldItem.equals(newItem);
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewholderClubHomeBinding mBinding;
        Club mItem;

        ViewHolder(ViewholderClubHomeBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(Club item){
            mItem = item;
            if(item!=null) {
                mBinding.setClub(item);
                    if(item.getImageUrl()!=null) {
                        Picasso.get()
                                .load(BuildConfig.BASE_URL + mItem.getImageUrl())
                                .placeholder(R.drawable.image_placeholder)
                                .error(R.drawable.image_placeholder)
                                .resize(270, 135)
                                .into(mBinding.imageView);
                }
            }
        }
    }
}

