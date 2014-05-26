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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;

import com.soomla.social.util.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by oriargov on 5/14/14.
 */
public class FacebookExampleActivity extends FragmentActivity {

    public static final String TAG = "FacebookExampleActivity";

    private FacebookExampleFragment mFBFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DEV only! Add code to print out the key hash for FB app settings
//        Log.d(TAG, Utils.getHashKey(this));

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mFBFragment = new FacebookExampleFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mFBFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mFBFragment = (FacebookExampleFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }
}
