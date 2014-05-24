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

package com.soomla.social.actions;

import com.soomla.social.ISocialProviderFactory;

import java.util.Map;

/**
 * Created by oriargov on 5/8/14.
 */
public class CustomSocialAction extends BaseSocialAction {

    public CustomSocialAction(String providerName,
                              String name,
                              String url,
                              String methodType,
                              Map<String, String> params,
                              Map<String, String> headers,
                              String body) {
        super(providerName, name, ISocialProviderFactory.SOOMLA_SOC_PREFIX+name);
        this.mUrl = url;
        this.mMethodType = methodType;
        this.mParams = params;
        this.mHeaders = headers;
        this.mBody = body;
    }


    /** Setters and Getters **/

    public String getUrl() {
        return mUrl;
    }

    public String getMethodType() {
        return mMethodType;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public String getBody() {
        return mBody;
    }


    /** Private Members **/

    private String mUrl;
    private String mMethodType;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;
    private String mBody;
}
