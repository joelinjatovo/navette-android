package com.navetteclub.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navetteclub.R;

public class MarkerStepView extends RelativeLayout {

    private TextView stepTextView;
    private TextView titleTextView;

    public MarkerStepView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MarkerStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MarkerStepView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }

        View view = mInflater.inflate(R.layout.view_marker_step, this, true);

        stepTextView = view.findViewById(R.id.step_text_view);
        titleTextView = view.findViewById(R.id.title_text_view);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MarkerStepView, 0, 0);

        String title = a.getString(R.styleable.MarkerStepView_marker_title);
        if (title != null) {
            titleTextView.setText(title);
        }

        String step = a.getString(R.styleable.MarkerStepView_marker_step);
        if (step != null) {
            stepTextView.setText(step);
        }

        a.recycle();

    }

    public TextView getTitleTextView(){
        return titleTextView;
    }

    public TextView getStepTextView(){
        return stepTextView;
    }
}
