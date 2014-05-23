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
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.soomla.social.IContextProvider;
import com.soomla.social.ISocialProvider;
import com.soomla.social.actions.UpdateStatusAction;
import com.soomla.social.actions.UpdateStoryAction;

import java.io.UnsupportedEncodingException;

/**
 * Created by oriargov on 5/22/14.
 */
public class FacebookSDKProvider implements ISocialProvider {

    public static final String TAG = "FacebookSDKProvider";

    private IContextProvider mCtxProvider;

    public FacebookSDKProvider(IContextProvider ctxProvider) {
        mCtxProvider = ctxProvider;
    }

    @Override
    public void login() {
        if (mCtxProvider.getActivity() != null) {
            login(mCtxProvider.getActivity());
        }
        else if (mCtxProvider.getFragment() != null) {
            login(mCtxProvider.getFragment());
        }
        else {
            Log.w(TAG, "login: no valid context found");
        }
    }

    public void login(Activity activity) {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(activity).setCallback(statusCallback));
        } else {
            Session.openActiveSession(activity, true, statusCallback);
        }
    }

    public void login(Fragment fragment) {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(fragment).setCallback(statusCallback));
        } else {
            Session.openActiveSession(fragment.getActivity(), true, statusCallback);
        }
    }

    public boolean isLoggedIn(Context context) {
        Session session = Session.getActiveSession();
        if (session == null) {
            // try to restore from cache
            session = Session.openActiveSessionFromCache(context);
        }

        return session != null && session.isOpened();
    }

    @Override
    public void logout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    public boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }


    @Override
    public void updateStatusAsync(UpdateStatusAction updateStatusAction) {

    }

    @Override
    public void updateStoryAsync(UpdateStoryAction updateStoryAction) throws UnsupportedEncodingException {

    }

    @Override
    public void getProfileAsync() {

    }

    @Override
    public void getContactsAsync() {

    }

    protected void onLogin() {}
    protected void onSessionStateChanged(Session session, SessionState state, Exception exception) {}
    protected void onLogout() {}

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
