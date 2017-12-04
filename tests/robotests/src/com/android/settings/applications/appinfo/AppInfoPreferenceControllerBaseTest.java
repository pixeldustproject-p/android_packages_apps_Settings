/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.applications.appinfo;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.pm.ApplicationInfo;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.TestConfig;
import com.android.settings.applications.AppInfoDashboardFragment;
import com.android.settings.notification.AppNotificationSettings;
import com.android.settings.testutils.SettingsRobolectricTestRunner;
import com.android.settingslib.applications.ApplicationsState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION_O)
public class AppInfoPreferenceControllerBaseTest {

    @Mock
    private SettingsActivity mActivity;
    @Mock
    private AppInfoDashboardFragment mFragment;
    @Mock
    private PreferenceScreen mScreen;
    @Mock
    private Preference mPreference;

    private TestPreferenceController mController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mController = new TestPreferenceController(mFragment);
        final String key = mController.getPreferenceKey();
        when(mScreen.findPreference(key)).thenReturn(mPreference);
        when(mPreference.getKey()).thenReturn(key);
        when(mFragment.getActivity()).thenReturn(mActivity);
    }

    @Test
    public void getAvailabilityStatus_shouldReturnAvailable() {
        assertThat(mController.getAvailabilityStatus()).isEqualTo(mController.AVAILABLE);
    }

    @Test
    public void refreshUi_shouldUpdatePreference() {
        mController.displayPreference(mScreen);
        mController.refreshUi();

        assertThat(mController.preferenceUpdated).isTrue();
    }

    @Test
    public void handlePreferenceTreeClick_shouldStartDetailFragmentClass() {
        final ApplicationsState.AppEntry appEntry = mock(ApplicationsState.AppEntry.class);
        appEntry.info = new ApplicationInfo();
        when(mFragment.getAppEntry()).thenReturn(appEntry);

        mController.handlePreferenceTreeClick(mPreference);

        verify(mActivity).startPreferencePanel(any(),
                eq(mController.getDetailFragmentClass().getName()), any(), anyInt(), any(), any(),
                anyInt());
    }

    private class TestPreferenceController extends AppInfoPreferenceControllerBase {

        private boolean preferenceUpdated;

        public TestPreferenceController(AppInfoDashboardFragment parent) {
            super(RuntimeEnvironment.application, parent, "TestKey");
        }

        @Override
        public void updateState(Preference preference) {
            preferenceUpdated = true;
        }

        @Override
        public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
            return AppNotificationSettings.class;
        }

    }

}