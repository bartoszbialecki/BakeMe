package com.example.bakeme.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakeme.R;
import com.example.bakeme.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {
    // region VARIABLES
    private List<Step> mSteps;
    private OnItemClickListener mListener;
    private int mSelectedItemPosition = -1;
    private boolean mIsTablet;
    // endregion

    // region CONSTRUCTORS
    public RecipeStepsAdapter(boolean isTablet) {
        mIsTablet = isTablet;
        mSteps = new ArrayList<>();
    }
    // endregion

    // region LISTENERS
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // endregion

    // region GETTERS AND SETTERS
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    // endregion

    // region PUBLIC METHODS
    public void addSteps(List<Step> steps) {
        if (mSteps == null) {
            mSteps = new ArrayList<>();
        }

        mSteps.addAll(steps);
        notifyDataSetChanged();
    }

    public Step getStep(int position) {
        if (mSteps != null) {
            if (position < mSteps.size()) {
                return mSteps.get(position);
            }
        }

        return null;
    }

    public void setSelectedItem(int position) {
        if (mSteps != null && position < mSteps.size()) {
            if (mSelectedItemPosition > -1) {
                notifyItemChanged(mSelectedItemPosition);
            }

            mSelectedItemPosition = position;
            notifyItemChanged(position);
        }
    }
    // endregion

    // region ADAPTER METHODS
    @Override
    public RecipeStepsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_steps_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeStepsAdapter.ViewHolder holder, int position) {
        Step step = mSteps.get(position);

        String desc = !TextUtils.isEmpty(step.getShortDescription()) ? step.getShortDescription() : "";
        holder.stepDescriptionTextView.setText(desc);

        if (mIsTablet) {
            if (position == mSelectedItemPosition) {
                holder.recipeStepCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
                holder.stepDescriptionTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            } else {
                holder.recipeStepCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
                holder.stepDescriptionTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primaryText));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getLayoutPosition();

                if (mSelectedItemPosition > -1) {
                    notifyItemChanged(mSelectedItemPosition);
                }

                mSelectedItemPosition = pos;
                notifyItemChanged(mSelectedItemPosition);

                if (mListener != null) {
                    mListener.onItemClick(holder.itemView, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }
    // endregion

    // region VIEW HOLDER
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_step_card_view)
        CardView recipeStepCardView;
        @BindView(R.id.recipe_step_description_text_view)
        TextView stepDescriptionTextView;

        ViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
    // endregion
}
