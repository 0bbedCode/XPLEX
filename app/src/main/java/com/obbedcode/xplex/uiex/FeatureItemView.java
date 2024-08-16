package com.obbedcode.xplex.uiex;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.obbedcode.xplex.R;

public class FeatureItemView extends LinearLayout  {
    //implements View.OnClickListener, View.OnLongClickListener
    //Was LinearLayout
    private Context mContext;
    private CardView mCardView;
    private ImageView mIconView;
    private TextView mTextView;
    private MaterialCardView mInvisibleCardView;

    public FeatureItemView(Context context) {
        super(context);
        init(context, null);
    }

    public FeatureItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FeatureItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //set style here to set button color
        this.mContext = context;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);


        //setCardElevation(0);
        //setRippleColorResource(R.color.ripple_color);
        //setClickable(true);
        //setFocusable(true);


        LayoutInflater.from(context).inflate(R.layout.feature_item_view, this, true);
        mInvisibleCardView = findViewById(R.id.invisibleCardView);
        mCardView = findViewById(R.id.cardView);
        mIconView = findViewById(R.id.iconView);
        mTextView = findViewById(R.id.textView);

        // Set up ripple effect
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        mInvisibleCardView.setForeground(context.getDrawable(outValue.resourceId));
        mInvisibleCardView.setClickable(true);
        mInvisibleCardView.setFocusable(true);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FeatureItemView);
            setIcon(a.getDrawable(R.styleable.FeatureItemView_icon));
            setText(a.getString(R.styleable.FeatureItemView_text));
            //setCardBackgroundColor(a.getColor(R.attr.butt));
            //setCardBackgroundColor(a.getColor(R.styleable.FeatureItemView_cardBackgroundColor, Color.WHITE));
            setTextColor(a.getColor(R.styleable.FeatureItemView_textColor, Color.BLACK));
            a.recycle();
        }

        setCardSize(50, 50);//64 ?
        setIconSize(24, 24);//54 ?
        setTextSize(12);

        //Setting tint fucks it up ? but we reverted all changes ?
        //mIconView.setImageTintMode(PorterDuff.Mode.SRC_IN);
        //mIconView.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);

    }

    // Add these methods to FeatureItemView
    @Override
    public void setOnClickListener(OnClickListener l) {
        mInvisibleCardView.setOnClickListener(l);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mInvisibleCardView.setOnLongClickListener(l);
    }

    public void setIcon(Drawable icon) {
        mIconView.setImageDrawable(icon);
    }

    //We can plug Theme ID ?
    public void setIcon(int resId) { mIconView.setImageDrawable(mContext.getResources().getDrawable(resId)); }

    public void setText(String text) { mTextView.setText(text); }

    public void setCardBackgroundColor(int color) {
        mCardView.setCardBackgroundColor(color);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setIconTint(int color) {
        mIconView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setCardCornerRadius(float radius) {
        mCardView.setRadius(radius);
    }

    public void setCardSize(int width, int height) {
        //ViewGroup.LayoutParams params = cardView.getLayoutParams();
        //params.width = width;
        //params.height = height;
        //cardView.setLayoutParams(params);

        ViewGroup.LayoutParams params = mCardView.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        mCardView.setLayoutParams(params);
    }

    public void setIconSize(int width, int height) {
        //ViewGroup.LayoutParams params = iconView.getLayoutParams();
        //params.width = width;
        //params.height = height;
        //iconView.setLayoutParams(params);
        ViewGroup.LayoutParams params = mIconView.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        mIconView.setLayoutParams(params);


        // Update invisibleCardView size
        ViewGroup.LayoutParams invisibleParams = mInvisibleCardView.getLayoutParams();
        invisibleParams.width = params.width + 230;
        invisibleParams.height = params.height + 230;
        mInvisibleCardView.setLayoutParams(invisibleParams);

    }

    public void setTextSize(float size) {
        mTextView.setTextSize(size);
    }
}