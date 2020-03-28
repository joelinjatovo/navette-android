package com.joelinjatovo.navette.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.joelinjatovo.navette.R;

public class AlertView extends LinearLayout {
    private Drawable mIcon;
    private String mTitle;
    private String mSubtitle;
    private Boolean mShowButton;
    private String mButtonLabel;

    private ImageView imageView;
    private TextView titleView;
    private TextView subtitleView;
    private Button button;

    public AlertView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AlertView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }

        View view = mInflater.inflate(R.layout.view_alert, this, true);

        imageView = view.findViewById(R.id.icon);
        titleView = view.findViewById(R.id.title);
        subtitleView = view.findViewById(R.id.subtitle);
        button = view.findViewById(R.id.button);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView, defStyle, 0);

        if (a.hasValue(R.styleable.CustomView_mIcon)) {
            mIcon = a.getDrawable(R.styleable.CustomView_mIcon);
            if (mIcon != null) {
                imageView.setImageDrawable(mIcon);
                mIcon.setCallback(this);
            }
        }

        mTitle = a.getString(R.styleable.CustomView_mTitle);
        if (mTitle != null) {
            titleView.setText(mTitle);
        }

        mSubtitle = a.getString(R.styleable.CustomView_mSubtitle);
        if (mSubtitle != null) {
            subtitleView.setText(mSubtitle);
        }

        mShowButton = a.getBoolean(R.styleable.CustomView_mShowButton, false);
        if (mShowButton) {
            button.setVisibility(View.VISIBLE);
        }else{
            button.setVisibility(View.GONE);
        }

        mButtonLabel = a.getString(R.styleable.CustomView_mButtonLabel);
        if (mButtonLabel!=null) {
            button.setText(mButtonLabel);
        }

        a.recycle();

    }

    public AlertView setIcon(@DrawableRes Integer res) {
        imageView.setImageResource(res);
        return this;
    }

    public AlertView setTitle(@StringRes Integer res) {
        titleView.setText(res);
        return this;
    }

    public AlertView setSubtitle(@StringRes Integer res) {
        subtitleView.setText(res);
        return this;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public TextView getSubtitleView() {
        return subtitleView;
    }

    public Button getButton() {
        return button;
    }
}
