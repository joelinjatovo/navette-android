package com.navetteclub.ui.club;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.database.entity.Club;
import com.navetteclub.ui.OnClickItemListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder>  implements Filterable {

    private List<Club> mItems;

    private List<Club> mOriginItems;

    private final OnClickItemListener<Club> mListener;

    public ClubRecyclerViewAdapter(OnClickItemListener<Club> listener) {
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
        holder.mNameTextView.setText(mItems.get(position).getName());
        new Picasso.Builder(holder.mClubImageView.getContext())
                .build()
                .load(BuildConfig.BASE_URL + mItems.get(position).getImageUrl())
                .resize(200,200).into(holder.mClubImageView);

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

    public void setItems(List<Club> items){
        if (mItems == null) {
            mItems = items;
            mOriginItems = items;
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
                    return oldItem.getName()!=null && oldItem.getName().equals(newItem.getName());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    void filter(String query) {
        getFilter().filter(query);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Club> results = new ArrayList<>();

                if (mOriginItems == null)
                    mOriginItems = new ArrayList<>(mItems);

                if (constraint != null && constraint.length() > 0) {
                    if (mOriginItems != null && mOriginItems.size() > 0) {
                        for (final Club cd : mOriginItems) {
                            if (cd.getName().toLowerCase()
                                    .contains(constraint.toString().toLowerCase()))
                                results.add(cd);
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();//newly Aded by ZA
                } else {
                    oReturn.values = mOriginItems;
                    oReturn.count = mOriginItems.size();//newly added by ZA
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(final CharSequence constraint,
                                          FilterResults results) {
                ArrayList<Club> itemList = new ArrayList<>((ArrayList<Club>) results.values);
                ///Collections.sort(itemList);
                setItems(itemList);
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mClubImageView;
        final TextView mNameTextView;
        Club mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mClubImageView = view.findViewById(R.id.clubImageView);
            mNameTextView = view.findViewById(R.id.clubNameTextView);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }
    }
}

