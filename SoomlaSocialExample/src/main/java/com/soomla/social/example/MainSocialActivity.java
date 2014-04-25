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

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soomla.social.SoomlaSocialCenter;
import com.soomla.social.events.SocialLoginEvent;
import com.squareup.otto.Subscribe;

import com.soomla.store.BusProvider;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;


public class MainSocialActivity extends ActionBarActivity {

    private static final String TAG = "MainSocialActivity";

    private Button mBtnUpdate;
    private EditText mEdtStatus;

    private SoomlaSocialCenter soomlaSocialCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_social);

        soomlaSocialCenter = new SoomlaSocialCenter();
        soomlaSocialCenter.addSocialProvider(SocialAuthAdapter.Provider.FACEBOOK, R.drawable.facebook);

        mBtnUpdate = (Button) findViewById(R.id.update);
        soomlaSocialCenter.getSocialAuthAdapter().enable(mBtnUpdate);
    }

    @Subscribe public void onSocialLoginEvent(SocialLoginEvent socialLoginEvent) {
        // Variable to receive message status
        Log.d(TAG, "Authentication Successful");

        // Get name of provider after authentication
        final String providerName = socialLoginEvent.getBundle().getString(SocialAuthAdapter.PROVIDER);
        Log.d(TAG, "Provider Name = " + providerName);
        Toast.makeText(this, providerName + " connected", Toast.LENGTH_SHORT).show();

        mEdtStatus = (EditText) findViewById(R.id.editTxt);

        // Please avoid sending duplicate message. Social Media Providers
        // block duplicate messages.

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Call updateStatus to share message via oAuth providers
                soomlaSocialCenter.getSocialAuthAdapter().updateStatus(mEdtStatus.getText().toString(), new MessageListener(), false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_social, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // todo: refactor this to lib
    // To get status of message after authentication
    private final class MessageListener implements SocialAuthListener<Integer> {
        @Override
        public void onExecute(String provider, Integer t) {
            Integer status = t;
            if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
                Toast.makeText(MainSocialActivity.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainSocialActivity.this, "Message not posted on " + provider, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SocialAuthError e) {

        }
    }
}
