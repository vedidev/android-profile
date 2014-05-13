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

/**
 * Created by oriargov on 5/8/14.
 */
public class UpdateStoryAction extends BaseSocialAction {

    private static final String ACTION_NAME = "UpdateStory";

    private String mName;
    private String mCaption;
    private String mMsg;
    private String mDesc;
    private String mLink;
    private String mPictureLink;

    public UpdateStoryAction(String providerName,
                             String name, String caption,
                             String msg, String desc,
                             String link, String pictureLink) {
        super(ACTION_NAME, providerName);
        this.mName = name;
        this.mCaption = caption;
        this.mMsg = msg;
        this.mDesc = desc;
        this.mLink = link;
        this.mPictureLink = pictureLink;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        this.mCaption = caption;
    }

    public String getMessage() {
        return mMsg;
    }

    public void setMessage(String msg) {
        this.mMsg = msg;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        this.mLink = link;
    }

    public String getPictureLink() {
        return mPictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.mPictureLink = pictureLink;
    }
}
