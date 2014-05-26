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

package com.soomla.social.providers.socialauth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.soomla.social.IContextProvider;
import com.soomla.social.ISocialProviderFactory;
import com.soomla.social.ISocialProvider;
import com.soomla.social.actions.CustomSocialAction;
import com.soomla.social.actions.ISocialAction;
import com.soomla.social.actions.UpdateStatusAction;
import com.soomla.social.actions.UpdateStoryAction;
import com.soomla.social.events.SocialActionFailedEvent;
import com.soomla.social.events.SocialActionPerformedEvent;
import com.soomla.social.events.SocialAuthContactsEvent;
import com.soomla.social.events.SocialAuthErrorEvent;
import com.soomla.social.events.SocialAuthProfileEvent;
import com.soomla.social.events.SocialLoginErrorEvent;
import com.soomla.social.events.SocialLoginEvent;
import com.soomla.store.BusProvider;

import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.util.Response;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoomlaSocialAuthProviderFactory implements ISocialProviderFactory, ISocialProvider {

    private static final String TAG = "SoomlaSocialCenter";

    private Map<String, SocialAuthAdapter.Provider> mProviderLookup =
            new HashMap<String, SocialAuthAdapter.Provider>();

    private SocialAuthAdapter mSocialAdapter;
    private String mCurrentProviderName;

    private IContextProvider mCtxProvider;

    public SoomlaSocialAuthProviderFactory() {
        mProviderLookup.put(ISocialProviderFactory.FACEBOOK, SocialAuthAdapter.Provider.FACEBOOK);
        mCurrentProviderName = ISocialProviderFactory.FACEBOOK;
        mSocialAdapter = new SocialAuthAdapter(new ResponseListener());
    }

    @Override
    public ISocialProvider setCurrentProvider(IContextProvider ctxProvider, String providerName) {
        mCtxProvider = ctxProvider;
        mCurrentProviderName = providerName;
        final SocialAuthAdapter.Provider provider = mProviderLookup.get(mCurrentProviderName);
        if (provider == null) {
            Log.w(TAG, "provider not supported:" + providerName);
            return null;
        }

        mSocialAdapter.authorize(ctxProvider.getContext(), provider);
        return this;
    }

    @Override
    public ISocialProvider getCurrentProvider() {
        return this;
    }

    @Override
    public String getProviderName() {
        return mCurrentProviderName;
    }

    @Override
    public void login() {
        mSocialAdapter.authorize(mCtxProvider.getContext(),
                mProviderLookup.get(mCurrentProviderName));
    }

    @Override
    public void logout() {
        mSocialAdapter.signOut(mCtxProvider.getContext(), mCurrentProviderName);
    }

    @Override
    public void updateStatusAsync(UpdateStatusAction updateStatusAction) {
        mSocialAdapter.updateStatus(updateStatusAction.getMessage(),
                new MessageListener(updateStatusAction), false);
    }

    @Override
    public void updateStoryAsync(UpdateStoryAction updateStoryAction) throws UnsupportedEncodingException {
        mSocialAdapter.updateStory(
                updateStoryAction.getMessage(),
                updateStoryAction.getName(),
                updateStoryAction.getCaption(),
                updateStoryAction.getDesc(),
                updateStoryAction.getLink(),
                updateStoryAction.getPictureLink(),
                new MessageListener(updateStoryAction));
    }

    public void customActionAsync(CustomSocialAction customSocialAction) throws Exception {
        final ApiTask apiTask = new ApiTask(
                customSocialAction.getUrl(),
                customSocialAction.getMethodType(),
                customSocialAction.getParams(),
                customSocialAction.getHeaders(),
                customSocialAction.getBody(),
                new MessageListener(customSocialAction));
        apiTask.execute();
//        mSocialAdapter.api(url, methodType, params, headers, body);
    }

    public AuthProvider getSocialProvider() throws UnsupportedOperationException {
        return mSocialAdapter.getCurrentProvider();
    }

    @Override
    public void getProfileAsync() {
        mSocialAdapter.getUserProfileAsync(new SocialAuthListener<Profile>() {
            @Override
            public void onExecute(String provider, Profile profile) {
                BusProvider.getInstance().post(new SocialAuthProfileEvent(profile));
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                BusProvider.getInstance().post(new SocialAuthErrorEvent(socialAuthError.getInnerException()));
            }
        });
    }

    @Override
    public void getContactsAsync() {
        mSocialAdapter.getContactListAsync(new SocialAuthListener<List<Contact>>() {
            @Override
            public void onExecute(String provider, List<Contact> contacts) {
                BusProvider.getInstance().post(new SocialAuthContactsEvent(contacts));
            }

            @Override
            public void onError(SocialAuthError socialAuthError) {
                BusProvider.getInstance().post(
                        new SocialAuthErrorEvent(socialAuthError.getInnerException()));
            }
        });
    }

    private String currentProviderId() {
        return mSocialAdapter.getCurrentProvider().getProviderId();
    }

    private final class ResponseListener implements DialogListener {
        @Override
        public void onComplete(Bundle bundle) {
            BusProvider.getInstance().post(new SocialLoginEvent(bundle));
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            socialAuthError.printStackTrace();
            BusProvider.getInstance().post(new SocialLoginErrorEvent(
                    socialAuthError.getInnerException()));
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onBack() {

        }
    }

    private class ApiTask extends AsyncTask<Void, Void, Integer> {

        private String url;
        private String methodType;
        private Map<String, String> params;
        private Map<String, String> headers;
        private String body;
        private SocialAuthListener<Integer> listener;

        private ApiTask(String url, String methodType,
                        Map<String, String> params,
                        Map<String, String> headers,
                        String body,
                        SocialAuthListener<Integer> listener) {
            this.url = url;
            this.methodType = methodType;
            this.params = params;
            this.headers = headers;
            this.body = body;
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Void... v) {
            // Call using API method of socialauth
            Response response = null;
            try {
                response = mSocialAdapter.getCurrentProvider().api(
                        url, methodType, params, headers,
                        body);
                Log.d("Status", String.valueOf(response.getStatus()));
                return response.getStatus();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError(new SocialAuthError("API call failed", e));
                return null;
            }

        }

        @Override
        protected void onPostExecute(Integer status) {
            listener.onExecute(currentProviderId(), status);
        }
    }

    private final class MessageListener implements SocialAuthListener<Integer> {
        private ISocialAction mSocialAction;

        private MessageListener(ISocialAction socialAction) {
            this.mSocialAction = socialAction;
        }

        @Override
        public void onExecute(String provider, Integer t) {
            Integer status = t;
            if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204) {
                mSocialAction.setCompleted(true);
                BusProvider.getInstance().post(new SocialActionPerformedEvent(mSocialAction));
            }
            else {
                BusProvider.getInstance().post(new SocialActionFailedEvent(mSocialAction));
            }
        }

        @Override
        public void onError(SocialAuthError e) {
            Log.w(TAG, e.getMessage(), e);
        }
    }
}
