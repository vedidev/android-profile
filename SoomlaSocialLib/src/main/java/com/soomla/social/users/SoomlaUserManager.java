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

import com.soomla.social.ISocialProviderFactory;
import com.soomla.social.events.FacebookProfileEvent;
import com.soomla.social.events.SocialAuthProfileEvent;
import com.soomla.social.events.SocialProfileEvent;
import com.soomla.store.BusProvider;
import com.soomla.store.data.StorageManager;
import com.squareup.otto.Subscribe;

/**
 * Created by oriargov on 5/24/14.
 */
public class SoomlaUserManager {

    private static final String TAG = "SoomlaUserManager";

    public static final String DB_KEY_PREFIX = "com.soomla.users.";

    public void init() {
        BusProvider.getInstance().register(this);
    }

    public void dispose() {
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe public void onFacebookProfileEvent(FacebookProfileEvent fbProfileEvent) {
        SocialProfile socialProfile = new SocialProfile(
                ISocialProviderFactory.FACEBOOK,
                fbProfileEvent.User.getId());

        socialProfile.setRawJson(fbProfileEvent.User.getInnerJSONObject().toString());

        // todo: need special permission ("email")
        socialProfile.setEmail(fbProfileEvent.User.getProperty("email").toString());

        socialProfile.setAvatarLink(fbProfileEvent.getProfileImageUrl());
        socialProfile.setFirstName(fbProfileEvent.User.getFirstName());
        socialProfile.setLastName(fbProfileEvent.User.getLastName());

        storeSocialProfile(socialProfile);
    }

    @Subscribe public void onSocialAuthProfileEvent(SocialAuthProfileEvent saProfileEvent) {
        SocialProfile socialProfile = new SocialProfile(
                ISocialProviderFactory.FACEBOOK,
                saProfileEvent.User.getValidatedId());

        socialProfile.setRawJson(saProfileEvent.User.toString());

        socialProfile.setEmail(saProfileEvent.User.getEmail());
        socialProfile.setAvatarLink(saProfileEvent.User.getProfileImageURL());
        socialProfile.setFirstName(saProfileEvent.User.getFirstName());
        socialProfile.setLastName(saProfileEvent.User.getLastName());

        storeSocialProfile(socialProfile);
    }

    private void storeSocialProfile(SocialProfile socialProfile) {
        StorageManager.getKeyValueStorage().setValue(DB_KEY_PREFIX +
                        socialProfile.getProfileId(), socialProfile.getRawJson());

        BusProvider.getInstance().post(new SocialProfileEvent(socialProfile));
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
