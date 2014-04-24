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

import java.util.Set;

public interface ISocialAction {

    /**
     * link a game reward to be given when the social action is preformed
     * @param gameReward - to be awarded
     * @return whether this reward was already attached
     */
    boolean addGameReward(GameReward gameReward);

    /**
     * get current attached game rewards to this social action
     * @return - awards
     */
    Set<GameReward> getGameRewards();
}
