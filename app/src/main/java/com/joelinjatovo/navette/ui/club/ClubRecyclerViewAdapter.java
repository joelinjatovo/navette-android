package com.joelinjatovo.navette.ui.club;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.ui.club.ClubsFragment.OnListFragmentInteractionListener;

import java.util.List;

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder> {

    private List<ClubAndPoint> mItems;

    private final OnListFragmentInteractionListener mListener;

    public ClubRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_club, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        //holder.mClubImageView.setImageDrawable(mItems.get(position).getClub().getName());
        holder.mNameTextView.setText(mItems.get(position).getClub().getName());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(v, holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<ClubAndPoint> items){
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
                    ClubAndPoint oldItem = mItems.get(oldItemPosition);
                    ClubAndPoint newItem = mItems.get(newItemPosition);
                    return oldItem.getClub().getId() == newItem.getClub().getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ClubAndPoint oldItem = mItems.get(oldItemPosition);
                    ClubAndPoint newItem = mItems.get(newItemPosition);
                    return oldItem.getClub().getName()!=null && oldItem.getClub().getName().equals(newItem.getClub().getName());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mClubImageView;
        final TextView mNameTextView;
        ClubAndPoint mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mClubImageView = view.findViewById(R.id.clubImageView);
            mNameTextView = view.findViewById(R.id.nameTextView);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }
    }
}

