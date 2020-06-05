package com.ensias.iwimapp;

import android.content.Context;
import android.graphics.Typeface;

public class FontAwesome {
    public static final String ROOT = "",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";
    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}