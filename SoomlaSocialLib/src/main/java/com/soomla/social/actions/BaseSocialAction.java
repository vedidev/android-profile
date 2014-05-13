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

import com.soomla.social.model.GameReward;
import com.soomla.store.exceptions.VirtualItemNotFoundException;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseSocialAction implements ISocialAction {

    private String mName;
    private String mProviderName;

    @Override
    public String getName() { return mName; }

    @Override
    public String getProviderName() { return mProviderName; }

    private boolean mWasDone = false;
    @Override
    public boolean wasDone() { return mWasDone; }
    @Override
    public void setDone() {
        mWasDone = true;
        final Set<GameReward> gameRewards = getGameRewards();
        for(GameReward gameReward : gameRewards) {
            try {
                gameReward.award();
            } catch (VirtualItemNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Set<GameReward> mGameRewards = new HashSet<GameReward>();

    protected BaseSocialAction(String name, String providerName) {
        this.mName = name;
        this.mProviderName = providerName;
    }

//    public abstract void execute();

    @Override
    public Set<GameReward> getGameRewards() {
        return mGameRewards;
    }

    @Override
    public boolean addGameReward(GameReward gameReward) {
        return mGameRewards.add(gameReward);
    }
}
