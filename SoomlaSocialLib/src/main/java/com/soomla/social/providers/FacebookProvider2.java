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

import org.brickred.socialauth.provider.FacebookImpl;
import org.brickred.socialauth.util.MethodType;
import org.brickred.socialauth.util.OAuthConfig;

import java.util.HashMap;
import java.util.Map;

public class FacebookProvider2 extends FacebookImpl implements ISocialProvider {

    public static final String FB_GRAPH_LIKE_URL = "me/og.likes";

    public FacebookProvider2(OAuthConfig providerConfig) throws Exception {
        super(providerConfig);
    }

    public void like() {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("object", "URL TO LIKE");
            api(FB_GRAPH_LIKE_URL, MethodType.POST.toString(), params, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
