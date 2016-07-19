package com.banki.main.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.banki.ahgora.R;

public class SettingsFragment extends PreferenceFragment  implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String[] mAutoSummaryFields = { "pis", "empresa", "loginTarget",
                                                  "jornadaTrabalho", "avisoFinalIntervalo"};
    private final int mEntryCount = mAutoSummaryFields.length;
    private Preference[] mPreferenceEntries;

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPreferenceEntries = new Preference[mEntryCount];
        for (int i = 0; i < mEntryCount; i++) {
            mPreferenceEntries[i] = getPreferenceScreen().findPreference(mAutoSummaryFields[i]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < mEntryCount; i++) {
            updateSummary(mAutoSummaryFields[i]);
        }
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(key);
    }

    private void updateSummary(String key) {
        for (int i = 0; i < mEntryCount; i++) {
            Preference entry = mPreferenceEntries[i];
            if (key.equals(mAutoSummaryFields[i])) {
                String value = getText(entry);
                entry.setSummary(value);
                break;
            }
        }
    }

    private String getText(Preference entry) {
        String value = "";
        if (entry instanceof EditTextPreference)
            value = ((EditTextPreference)entry).getText();
        else if (entry instanceof ListPreference)
            value = ((ListPreference)entry).getEntry().toString();

        return value;
    }
}
