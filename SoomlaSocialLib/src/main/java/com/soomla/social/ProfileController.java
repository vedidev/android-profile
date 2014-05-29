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

package com.soomla.social;

import com.soomla.social.actions.ISocialAction;
import com.soomla.social.events.SocialActionStartedEvent;
import com.soomla.social.users.UserProfile;
import com.soomla.store.BusProvider;
import com.soomla.store.events.UnexpectedStoreErrorEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oriargov on 5/28/14.
 */
public class ProfileController implements IAuthProviderAggregator {

    /** singleton **/
    private static final ProfileController mInstance = new ProfileController();
    public static ProfileController getInstance() { return mInstance; }

    public void login(String providerId) {
        final IAuthProvider authProvider = mProviders.get(providerId);
        if(authProvider == null) {
            BusProvider.getInstance().post(
                    new UnexpectedStoreErrorEvent("provider not found:"+providerId));
                    return;
        }

        BusProvider.getInstance().post(new SocialActionStartedEvent(ISocialAction.Action.Login));
        authProvider.login();
    }

    public void updateUserProfile(UserProfile userProfile, boolean isDefault) {
        if (isDefault) {
            setDefaultUserProfile(userProfile);
        }
        mProfiles.put(userProfile.getProviderId(), userProfile);
    }

    public UserProfile getUserProfile(String providerId) {
        return mProfiles.get(providerId);
    }

    public UserProfile getDefaultUserProfile() {
        return mDefaultUserProfile;
    }
    public void setDefaultUserProfile(UserProfile userProfile) {
        mDefaultUserProfile = userProfile;
    }

    /** Private Members **/
    private Map<String, IAuthProviderAggregator> mProviderAggregators =
            new HashMap<String, IAuthProviderAggregator>();
    private Map<String, IAuthProvider> mProviders =
            new HashMap<String, IAuthProvider>();
    private Map<String, UserProfile> mProfiles =
            new HashMap<String, UserProfile>();

    private UserProfile mDefaultUserProfile;

}
