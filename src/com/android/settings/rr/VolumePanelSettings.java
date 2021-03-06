/*Copyright (C) 2015 The ResurrectionRemix Project
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/
package com.android.settings.rr;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.Context;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.rr.utils.RRUtils;
import com.android.settings.rr.Preferences.*;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import lineageos.preference.LineageSecureSettingSwitchPreference;
@SearchIndexable
public class VolumePanelSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String KEY_NOTIF = "audio_panel_view_notification";
    private static final String KEY_STYLE = "systemui_plugin_volume";
    private static final String KEY_LEFT = "volume_panel_on_left";
    private static final String UI = "rr_ui";
    private static final String ITEMS = "items";
    private static final String EXTRAITEMS = "extra_items";
    private static final String RINGER = "ringer_button";

    private SystemSettingSwitchPreference mNotif;
    private SystemSettingSwitchPreference mMedia;
    private SystemSettingListPreference mStyle;
    private PreferenceCategory mUI;
    private PreferenceCategory mItems;
    private PreferenceCategory mExtra;
    private PreferenceCategory mRinger;
    private LineageSecureSettingSwitchPreference mLeft;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.RESURRECTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rr_volume_panel);
        ContentResolver resolver = getActivity().getContentResolver();
        mUI = (PreferenceCategory) findPreference(UI);
        mItems = (PreferenceCategory) findPreference(ITEMS);
        mExtra = (PreferenceCategory) findPreference(EXTRAITEMS);
        mNotif = (SystemSettingSwitchPreference) findPreference(KEY_NOTIF);
        mLeft = (LineageSecureSettingSwitchPreference) findPreference(KEY_LEFT);
        boolean show = Settings.Secure.getInt(resolver,
                Settings.Secure.VOLUME_LINK_NOTIFICATION, 1) == 1;
        mStyle = (SystemSettingListPreference) findPreference(KEY_STYLE);
        mStyle.setOnPreferenceChangeListener(this);
        if (show) {
            mNotif.setEnabled(false);
            mNotif.setSummary(R.string.vol_link_enabled_summary);
        } else {
            mNotif.setEnabled(true);
            mNotif.setSummary(R.string.audio_panel_view_ringer_summary);
        }

        int anim = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RR_CONFIG_ANIM, 0);
        String style = Settings.System.getString(resolver,
                Settings.System.SYSTEMUI_PLUGIN_VOLUME);
        updatePrefs(style);
        try {
            if (anim == 0) {
                removePreference("animation");
            } else if (anim == 1) {
                removePreference("preview");
            } else if (anim == 2) {
                removePreference("animation");
                removePreference("preview");
            }
        } catch (Exception e) {}
    }
    
    public void updatePrefs(String style) {
        if (style == null) style = "com.android.systemui.volume";
        if (!style.equals("com.android.systemui.volume")) {
            if (style.equals("co.potatoproject.plugin.volume.oreo")) {
                mLeft.setVisible(false);
                mExtra.setVisible(true);
            } else  if (style.equals("co.potatoproject.plugin.volume.compact")){
                mLeft.setVisible(true);
                mExtra.setVisible(true);
            } else  if (style.equals("co.potatoproject.plugin.volume.tiled")){
                mLeft.setVisible(true);
                mExtra.setVisible(false);
            } else  if (style.equals("co.potatoproject.plugin.volume.aosp")){
                mLeft.setVisible(true);
                mExtra.setVisible(true);
            }
            mItems.setVisible(false);
            mUI.setVisible(false);
            mFooterPreferenceMixin.createFooterPreference().setTitle(R.string.volume_panel_warning);
        } else {
            mUI.setVisible(true);
            mItems.setVisible(true);
            mExtra.setVisible(true);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
         if (preference == mStyle) {
             String style = (String) objValue;
             updatePrefs(style);
             return true;
        } 
        return false;
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
                ArrayList<SearchIndexableResource> result =
                    new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.rr_volume_panel;
                    result.add(sir);
                    return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                List<String> keys = super.getNonIndexableKeys(context);
                return keys;
            }
        };
}
