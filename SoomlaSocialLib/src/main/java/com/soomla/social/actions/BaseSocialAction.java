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

import com.soomla.blueprint.challenges.ActionMission;
import com.soomla.blueprint.data.BPJSONConsts;
import com.soomla.blueprint.rewards.Reward;
import com.soomla.social.data.SOCJSONConsts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by oriargov on 5/14/14.
 */
public abstract class BaseSocialAction extends ActionMission implements ISocialAction {

    private String mProviderName;
    public String getProviderName() { return mProviderName; }

    /**
     * Constructor
     *
     * @param jsonObject see <code>ActionMission</code>
     * @throws JSONException
     */
    public BaseSocialAction(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        mProviderName = jsonObject.getString(SOCJSONConsts.SOC_SOCIAL_PROVIDERID);
    }

    protected BaseSocialAction(String providerName, String name, String missionId) {
        super(name, missionId);
        this.mProviderName = providerName;
    }

    protected BaseSocialAction(String providerName, String missionId, String name, List<Reward> rewards) {
        super(missionId, name, rewards);
        this.mProviderName = providerName;
    }
}
