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
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by oriargov on 5/14/14.
 */
public class FacebookExampleFragment extends FacebookEnabledFragment {

    private Button btnShareDlg;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fb_example_main, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);

        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile"));
        //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));

        btnShareDlg = (Button) view.findViewById(R.id.btnShareDlg);
        btnShareDlg.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onLogin() {
        super.onLogin();
        btnShareDlg.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLogout() {
        super.onLogout();
        btnShareDlg.setVisibility(View.GONE);
    }
}
