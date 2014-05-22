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

import org.brickred.socialauth.provider.TwitterImpl;
import org.brickred.socialauth.util.OAuthConfig;
import org.brickred.socialauth.util.Response;

public class TwitterSocialAuthProvider implements ISocialProvider {
    private TwitterImpl mTwitter;

    public TwitterSocialAuthProvider(OAuthConfig providerConfig) throws Exception {
        mTwitter = new TwitterImpl(providerConfig);
    }

    @Override
    public void updateStatus(String message) throws Exception {
        mTwitter.updateStatus(message);
    }
}
