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

import android.content.Context;
import android.widget.Button;

import com.soomla.social.ISocialCenter;
import com.soomla.social.actions.UpdateStatusAction;
import com.soomla.social.actions.UpdateStoryAction;

import java.io.UnsupportedEncodingException;

/**
 * Created by oriargov on 5/22/14.
 */
public class NativeSDKCenter implements ISocialCenter {
    @Override
    public void addSocialProvider(String providerName, int providerIconResId) {
        
    }

    @Override
    public void login(Context context, String providerName) {

    }

    @Override
    public void logout(Context context, String providerName) {

    }

    @Override
    public void registerShareButton(Button btnShare) {

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
}
