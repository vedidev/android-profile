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

package com.soomla.social.users;

import com.soomla.social.IAuthProviderAggregator;
import com.soomla.social.data.UserProfileStorage;
import com.soomla.social.events.FacebookProfileEvent;
import com.soomla.social.events.SocialAuthProfileEvent;
import com.soomla.store.BusProvider;
import com.squareup.otto.Subscribe;

/**
 * Created by oriargov on 5/24/14.
 */
public class SoomlaUserManager {

    private static final String TAG = "SoomlaUserManager";

    public void init() {
        BusProvider.getInstance().register(this);
    }

    public void dispose() {
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe public void onFacebookProfileEvent(FacebookProfileEvent fbProfileEvent) {
        UserProfile userProfile = new UserProfile(
                IAuthProviderAggregator.FACEBOOK,
                fbProfileEvent.User.getId());

        userProfile.setRawJson(fbProfileEvent.User.getInnerJSONObject().toString());

        // todo: need special permission ("email")
        userProfile.setEmail(fbProfileEvent.User.getProperty("email").toString());

        userProfile.setAvatarLink(fbProfileEvent.getProfileImageUrl());
        userProfile.setFirstName(fbProfileEvent.User.getFirstName());
        userProfile.setLastName(fbProfileEvent.User.getLastName());

        storeSocialProfile(userProfile);
    }

    @Subscribe public void onSocialAuthProfileEvent(SocialAuthProfileEvent saProfileEvent) {
        UserProfile userProfile = new UserProfile(
                IAuthProviderAggregator.FACEBOOK,
                saProfileEvent.User.getValidatedId());

        userProfile.setRawJson(saProfileEvent.User.toString());

        userProfile.setEmail(saProfileEvent.User.getEmail());
        userProfile.setAvatarLink(saProfileEvent.User.getProfileImageURL());
        userProfile.setFirstName(saProfileEvent.User.getFirstName());
        userProfile.setLastName(saProfileEvent.User.getLastName());

        storeSocialProfile(userProfile);
    }

    private void storeSocialProfile(UserProfile userProfile) {
        UserProfileStorage.save(userProfile);
    }

//    public SocialProfile getSocialProfileById(String profileId) {
//        String jsonProfile = StorageManager.getKeyValueStorage().getValue(DB_KEY_PREFIX + profileId);
//        return SocialProfile.fromJson(jsonProfile);
//    }

//    public void getSocialProfiles() {
//        // todo: way to get stuff by prefix?
//        // todo: query encrypted stuff?
//        StorageManager
//                .getKeyValueStorage()
//                .getNonEncryptedQueryValues("");
//    }
}
