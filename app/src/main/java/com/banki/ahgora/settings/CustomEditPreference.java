package com.banki.ahgora.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class CustomEditPreference extends EditTextPreference {

    private CharSequence originalSummary;

    public CustomEditPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        originalSummary = super.getSummary();
    }

    public CustomEditPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        originalSummary = super.getSummary();
    }

    public CustomEditPreference(Context context) {
        super(context);
        originalSummary = super.getSummary();
    }

    public void updateSummary() {
        String summary = originalSummary.toString();
        summary += " ";
        summary += getText();
        setSummary(summary);
    }
}