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

import android.os.Bundle;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.provider.FacebookImpl;
import org.brickred.socialauth.util.Response;

public class FacebookProvider implements ISocialProvider {

    private FacebookImpl mFacebook;

    public FacebookProvider() {
        SocialAuthAdapter socialAuthAdapter = new SocialAuthAdapter(new DialogListener() {
            @Override
            public void onComplete(Bundle bundle) {

            }

            @Override
            public void onError(SocialAuthError socialAuthError) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onBack() {

            }
        });

//        socialAuthAdapter.getCurrentProvider().api()
        try {
            FacebookImpl facebook = socialAuthAdapter.getCurrentProvider().getPlugin(FacebookImpl.class);
//            facebook.uploadImage();
            final Response response = facebook.updateStatus("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void share() {

    }

    public void updateStatus(String message) {

    }
}
