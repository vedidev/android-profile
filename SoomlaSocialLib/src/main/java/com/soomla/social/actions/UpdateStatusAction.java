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
public class UpdateStatusAction extends BaseSocialAction {

    private static final String TAG = "UpdateStatusAction";

    private static final String ACTION_NAME = "UpdateStatus";

    private String mMessage;
    private boolean mShare;

    public UpdateStatusAction(String providerName, String msg, boolean share) {
        super(ACTION_NAME, providerName);
        this.mMessage = msg;
        this.mShare = share;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public boolean isShare() {
        return mShare;
    }

    public void setShare(boolean mShare) {
        this.mShare = mShare;
    }

//    @Override
//    public void execute(SocialAuthAdapter socialAuthAdapter) {
//        socialAuthAdapter.updateStatusAsync(
//                                mMessage,
//                                new MessageListener(), false);
//    }
//
//    private final class MessageListener implements SocialAuthListener<Integer> {
//        @Override
//        public void onExecute(String provider, Integer t) {
//            Integer status = t;
//            if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
//                Log.d(TAG, "Message posted on " + provider);
//            else
//                Log.w(TAG, "Message not posted on " + provider);
//        }
//
//        @Override
//        public void onError(SocialAuthError e) {
//            Log.w(TAG, e.getMessage(), e);
//
//        }
//    }
}
