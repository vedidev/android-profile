/*
 * Copyright (C) 2012 Soomla Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.soomla.social;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.soomla.social.actions.ISocialAction;
import com.soomla.social.events.SocialLoginEvent;
import com.soomla.social.model.GameReward;
import com.soomla.social.providers.ISocialProvider;
import com.soomla.store.BusProvider;

import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SoomlaSocialCenter implements ISocialCenter {

    private static final String TAG = "SoomlaSocialCenter";

    private SocialAuthAdapter mSocialAdapter;

    private Set<ISocialAction> mRegisteredSocialActions = new HashSet<ISocialAction>();

    public SoomlaSocialCenter() {
        mSocialAdapter = new SocialAuthAdapter(new ResponseListener());
    }

    // todo: temp wip
    public SocialAuthAdapter getSocialAuthAdapter() {
        return mSocialAdapter;
    }

    public void addSocialProvider(SocialAuthAdapter.Provider provider, int providerIconResId) {
        mSocialAdapter.addProvider(provider, providerIconResId);
    }

    public /*ISocialProvider*/AuthProvider getSocialProvider() throws UnsupportedOperationException {
        return mSocialAdapter.getCurrentProvider();
    }

    @Override
    public boolean registerSocialAction(ISocialAction action) {
        return mRegisteredSocialActions.add(action);
    }

    @Override
    public boolean unregisterSocialAction(ISocialAction action) {
        return mRegisteredSocialActions.remove(action);
    }

    private final class ResponseListener implements DialogListener {
        @Override
        public void onComplete(Bundle bundle) {
            BusProvider.getInstance().post(new SocialLoginEvent(bundle));
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            socialAuthError.printStackTrace();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onBack() {

        }
    }
}
