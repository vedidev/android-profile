
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

import android.content.Context;
import android.widget.Button;

import com.soomla.social.actions.ISocialAction;
import com.soomla.social.actions.UpdateStatusAction;
import com.soomla.social.actions.UpdateStoryAction;

import java.io.UnsupportedEncodingException;

public interface ISocialCenter {

    public static final String FACEBOOK = "Facebook";

    /**
     * register supported provider
     * @param providerName
     * @param providerIconResId
     */
    void addSocialProvider(String providerName, int providerIconResId);

    void signOut(Context context, String providerName);

    // todo: this is probably temp shortcut
    void registerShareButton(Button btnShare);

    // todo: not sure these will be here or on ISocialProvider(s)
    void updateStatusAsync(UpdateStatusAction updateStatusAction);
    // todo: this one in particular seems very FB oriented
    void updateStoryAsync(UpdateStoryAction updateStoryAction) throws UnsupportedEncodingException;

    /**
     * will fire SocialAuthProfileEvent when ready
     */
    void getProfileAsync();
    /**
     * will fire SocialAuthContactsEvent when ready
     */
    void getContactsAsync();

    /**
     *
     * @param action to add
     * @return whether this action exists already
     */
    boolean registerSocialAction(ISocialAction action);

    /**
     *
     * @param action to remove
     * @return whether this actions was present for removal
     */
    boolean unregisterSocialAction(ISocialAction action);
}
