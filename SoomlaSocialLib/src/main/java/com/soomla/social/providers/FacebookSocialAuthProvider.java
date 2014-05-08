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

package com.soomla.social.providers;

import android.util.Log;

import org.brickred.socialauth.provider.FacebookImpl;
import org.brickred.socialauth.util.MethodType;
import org.brickred.socialauth.util.OAuthConfig;
import org.brickred.socialauth.util.Response;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FacebookSocialAuthProvider implements ISocialProvider {

    public static final String TAG = "FacebookSocialAuthProvider";

    private static final String FB_GRAPH_LIKE_URL = "me/og.likes";

    private FacebookImpl mFacebook;

    public FacebookSocialAuthProvider(OAuthConfig providerConfig) throws Exception {
        mFacebook = new FacebookImpl(providerConfig);
    }

    @Override
    public Object getInternalProvider() {
        return mFacebook;
    }

    @Override
    public void updateStatus(String message) throws Exception {
        final Response response = mFacebook.updateStatus(message);
        Log.d(TAG, "updateStatusAsync, response:"+response);
    }

    public void uploadImage(String message, String fileName, InputStream inputStream) throws Exception {
        final Response response = mFacebook.uploadImage(message, fileName, inputStream);
        Log.d(TAG, "uploadImage, response:"+response);
    }

    private void like() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("object", "URL TO LIKE");
        final Response response = mFacebook.api(FB_GRAPH_LIKE_URL, MethodType.POST.toString(), params, null, null);
        Log.d(TAG, "like, response:"+response);
    }
}
