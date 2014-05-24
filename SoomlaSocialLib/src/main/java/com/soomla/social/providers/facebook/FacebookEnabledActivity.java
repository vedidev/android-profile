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

package com.soomla.social.providers.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.soomla.social.IContextProvider;

/**
 * Created by oriargov on 5/22/14.
 */
public class FacebookEnabledActivity extends FragmentActivity {

    private static final String TAG = "FacebookEnabledActivity";

    private final String PENDING_ACTION_BUNDLE_KEY = "com.soomla.social.facebook:PendingAction";

    private FacebookSDKProvider mFacebookSDKProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = this;
        mFacebookSDKProvider = new FacebookSDKProvider(new IContextProvider() {
            @Override
            public Activity getActivity() {
                return activity;
            }

            @Override
            public Context getContext() {
                return activity;
            }
        });

        mFacebookSDKProvider.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
//            pendingAction = PendingAction.valueOf(name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFacebookSDKProvider.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFacebookSDKProvider.onSaveInstanceState(outState);

//        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookSDKProvider.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFacebookSDKProvider.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFacebookSDKProvider.onDestroy();
    }

    protected void onLogin() {}
    protected void onSessionStateChanged(Session session, SessionState state, Exception exception) {}
    protected void onLogout() {}

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d(TAG, String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d(TAG, "Success!");
        }
    };

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                onLogin();
            } else if (state.isClosed()) {
                onLogout();
            }
            else {
                onSessionStateChanged(session, state, exception);
            }
        }
    };
}
