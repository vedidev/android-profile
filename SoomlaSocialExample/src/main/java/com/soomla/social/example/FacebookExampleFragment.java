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

package com.soomla.social.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.soomla.social.example.util.ImageUtils;

import java.util.Arrays;

/**
 * Created by oriargov on 5/14/14.
 */
public class FacebookExampleFragment extends FacebookEnabledFragment {

    private Button mBtnShareDlg;

    private ViewGroup mProfileBar;
    private ImageView mProfileAvatar;
    private TextView mProfileName;

    private ProfilePictureView mProfilePicView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fb_example_main, container, false);

        mProfileBar = (ViewGroup) view.findViewById(R.id.profile_bar);
        mProfileAvatar = (ImageView) view.findViewById(R.id.prof_avatar);
        mProfileName = (TextView) view.findViewById(R.id.prof_name);

        mProfilePicView = (ProfilePictureView) view.findViewById(R.id.fb_profile_picview);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile"));
        //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));

        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                showView(mProfileBar, true);

                final String userFullName = user.getFirstName() + " " + user.getLastName();
                mProfileName.setText(userFullName);

                // 1. custom UI+fetch
                if(mProfileAvatar != null) {
                    String imageUrl = "http://graph.facebook.com/" + user.getId() + "/picture";
                    new ImageUtils.DownloadImageTask(mProfileAvatar).execute(imageUrl);
                }
                else { // 2. FB UI
                    if(mProfilePicView != null) {
                        mProfilePicView.setProfileId(user.getId());
                    }
                }

//                new ImageUtils.DownloadImageTask(mProfileAvatar).execute(profileEvent.profile.getProfileImageURL());
//                mProfileName.setText(profileEvent.profile.getFullName());
            }
        });

        mBtnShareDlg = (Button) view.findViewById(R.id.btnShareDlg);
        mBtnShareDlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FacebookDialog.canPresentShareDialog(getActivity().getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    // Publish the post using the Share Dialog
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                            .setFragment(FacebookExampleFragment.this)
                                    // null link is status update
                            .setLink("https://developers.facebook.com/android")
                            .build();
                    uiHelper.trackPendingDialogCall(shareDialog.present());

                } else {
                    // Fallback. For example, publish the post using the Feed Dialog
                    publishFeedDialog();
                }
            }
        });

        return view;
    }

    private void showView(final View view, boolean show) {
        final Animation animation = show ?
                AnimationUtils.makeInAnimation(view.getContext(), true) :
                AnimationUtils.makeOutAnimation(view.getContext(), true);
        animation.setFillAfter(true);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    @Override
    protected void onLogin() {
        super.onLogin();
        mBtnShareDlg.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLogout() {
        super.onLogout();
        mBtnShareDlg.setVisibility(View.GONE);
    }
}
