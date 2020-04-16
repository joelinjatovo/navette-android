package com.navetteclub.ui.order;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.api.models.google.Step;
import com.navetteclub.databinding.ViewholderOrderBinding;
import com.navetteclub.databinding.ViewholderStepBinding;
import com.navetteclub.models.Timeline;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Log;

import java.util.List;

public class StepRecyclerViewAdapter
        extends RecyclerView.Adapter<StepRecyclerViewAdapter.ViewHolder> {

    private List<Step> mItems;

    private final OnClickItemListener<Step> mListener;

    public StepRecyclerViewAdapter(OnClickItemListener<Step> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderStepBinding itemBinding = ViewholderStepBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<Step> items){
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
                    Step oldItem = mItems.get(oldItemPosition);
                    Step newItem = items.get(newItemPosition);
                    return oldItem.getHtmlInstructions() == newItem.getHtmlInstructions();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Step oldItem = mItems.get(oldItemPosition);
                    Step newItem = items.get(newItemPosition);
                    return oldItem.getHtmlInstructions()!=null && oldItem.getHtmlInstructions().equals(newItem.getHtmlInstructions());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewholderStepBinding mBinding;
        private Step mItem;

        ViewHolder(ViewholderStepBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(Step step){
            this.mItem = step;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding.title.setText(Html.fromHtml(mItem.getHtmlInstructions(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mBinding.title.setText(Html.fromHtml(mItem.getHtmlInstructions()));
            }

            String distance = mItem.getDistance().getText();
            String time = mItem.getDuration().getText();
            mBinding.subtitle.setText(String.format("Distance:%s, Duration:%s", distance, time));
        }

    }
}

