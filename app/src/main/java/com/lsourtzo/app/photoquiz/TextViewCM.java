package com.lsourtzo.app.photoquiz;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewCM extends TextView {
    public TextViewCM(Context context) {
        super(context);
        setFont();
    }
    public TextViewCM(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public TextViewCM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/ACTypoGrotesk.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
