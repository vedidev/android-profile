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

package com.soomla.social.model;

import com.soomla.social.actions.ISocialAction;
import com.soomla.store.StoreInventory;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class GameReward {
    private Set<ISocialAction> mRequiredActions = new HashSet<ISocialAction>();
    private boolean mAwarded = false;

    // VirtualItem
    private String mRewardId;
    private int mAmount;

    public GameReward(ISocialAction requiredSocialAction, String mRewardId, int amount) {
        addRequiredSocialAction(requiredSocialAction);
        this.mRewardId = mRewardId;
        this.mAmount = amount;
    }

    public boolean award() throws VirtualItemNotFoundException {
        if(!mAwarded && requirementsMet()) {
            StoreInventory.giveVirtualItem(mRewardId, mAmount);
            mAwarded = true;
        }

        return mAwarded;
    }

    public void addRequiredSocialAction(ISocialAction requiredSocialAction) {
        mRequiredActions.add(requiredSocialAction);
    }

    public boolean requirementsMet() {
        for (ISocialAction requirement : mRequiredActions) {
            if(!requirement.wasDone())
                return false;
        }

        return true;
    }
}
