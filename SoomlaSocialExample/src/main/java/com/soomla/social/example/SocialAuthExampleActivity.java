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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soomla.blueprint.rewards.Reward;
import com.soomla.social.ISocialCenter;
import com.soomla.social.SoomlaSocialAuthCenter;
import com.soomla.social.actions.ISocialAction;
import com.soomla.social.actions.UpdateStatusAction;
import com.soomla.social.actions.UpdateStoryAction;
import com.soomla.social.events.SocialActionPerformedEvent;
import com.soomla.social.events.SocialAuthProfileEvent;
import com.soomla.social.events.SocialLoginEvent;
import com.soomla.social.example.util.ImageUtils;
import com.soomla.social.model.SocialVirtualItemReward;
import com.soomla.store.BusProvider;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.brickred.socialauth.android.SocialAuthAdapter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class SocialAuthExampleActivity extends ActionBarActivity {

    private static final String TAG = "MainSocialActivity";

    private Button mBtnShare;

    private ViewGroup mProfileBar;
    private ImageView mProfileAvatar;
    private TextView mProfileName;

    private ViewGroup mPnlStatusUpdate;
    private Button mBtnUpdateStatus;
    private EditText mEdtStatus;

    private ViewGroup mPnlStoryUpdate;
    private Button mBtnUpdateStory;
    private EditText mEdtStory;

//    private SoomlaSocialAuthCenter soomlaSocialAuthCenter;
    private ISocialCenter soomlaSocialAuthCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socialauth_example_main);

        soomlaSocialAuthCenter = new SoomlaSocialAuthCenter();
        soomlaSocialAuthCenter.addSocialProvider(ISocialCenter.FACEBOOK, R.drawable.facebook);

        mProfileBar = (ViewGroup) findViewById(R.id.profile_bar);
        mProfileAvatar = (ImageView) findViewById(R.id.prof_avatar);
        mProfileName = (TextView) findViewById(R.id.prof_name);

        mPnlStatusUpdate = (ViewGroup) findViewById(R.id.pnlStatusUpdate);
        mEdtStatus = (EditText) findViewById(R.id.edtStatusText);
        mBtnUpdateStatus = (Button) findViewById(R.id.btnStatusUpdate);
        mBtnUpdateStatus.setEnabled(false);
        mBtnUpdateStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String message = mEdtStatus.getText().toString();

                // create social action
                UpdateStatusAction updateStatusAction = new UpdateStatusAction(
                        ISocialCenter.FACEBOOK, message, false);

                // optionally attach rewards to it
                Reward noAdsReward = new SocialVirtualItemReward("Update Status for Ad-free", "no_ads", 1);
                updateStatusAction.getRewards().add(noAdsReward);

                // perform social action
                soomlaSocialAuthCenter.updateStatusAsync(updateStatusAction);
            }
        });

        mPnlStoryUpdate = (ViewGroup) findViewById(R.id.pnlStoryUpdate);
        mEdtStory = (EditText) findViewById(R.id.edtStoryText);
        mBtnUpdateStory = (Button) findViewById(R.id.btnStoryUpdate);
        mBtnUpdateStory.setEnabled(false);
        mBtnUpdateStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = mEdtStory.getText().toString();
                // another example
                UpdateStoryAction updateStoryAction = new UpdateStoryAction(
                        ISocialCenter.FACEBOOK,
                        message, "name", "caption", "description",
                        "http://soom.la",
                        "https://s3.amazonaws.com/soomla_images/website/img/500_background.png");

                // optionally attach rewards to it
                Reward muffinsReward = new SocialVirtualItemReward("Update Story for muffins", "muffins_50", 1);
                updateStoryAction.getRewards().add(muffinsReward);

                try {
                    soomlaSocialAuthCenter.updateStoryAsync(updateStoryAction);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        mBtnShare = (Button) findViewById(R.id.btnShare);
        soomlaSocialAuthCenter.registerShareButton(mBtnShare);
    }

    @Subscribe public void onSocialLoginEvent(SocialLoginEvent socialLoginEvent) {
        // Variable to receive message status
        Log.d(TAG, "Authentication Successful");

        // Get name of provider after authentication
        final String providerName = socialLoginEvent.result.getString(SocialAuthAdapter.PROVIDER);
        Log.d(TAG, "Provider Name = " + providerName);
        Toast.makeText(this, providerName + " connected", Toast.LENGTH_SHORT).show();

        // Please avoid sending duplicate message. Social Media Providers
        // block duplicate messages.

        soomlaSocialAuthCenter.getProfileAsync();

        updateUIOnLogin(providerName);
    }

    @Subscribe public void onSocialProfileEvent(SocialAuthProfileEvent profileEvent) {
        showView(mProfileBar, true);

        new ImageUtils.DownloadImageTask(mProfileAvatar).execute(profileEvent.profile.getProfileImageURL());
        mProfileName.setText(profileEvent.profile.getFullName());
    }

    @Subscribe public void onSocialActionPerformedEvent(
            SocialActionPerformedEvent socialActionPerformedEvent) {
        final ISocialAction socialAction = socialActionPerformedEvent.socialAction;
        final String msg = socialAction.getName() + " on " +
                socialAction.getProviderName() + " performed successfully";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void updateUIOnLogin(final String providerName) {
        mBtnShare.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(android.R.drawable.ic_lock_power_off),
                null);

        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soomlaSocialAuthCenter.logout(mBtnShare.getContext(), providerName);
                updateUIOnLogout();

                // re-enable share button login
                soomlaSocialAuthCenter.registerShareButton(mBtnShare);
            }
        });

        showView(mPnlStatusUpdate, true);
        showView(mPnlStoryUpdate, true);

        mBtnUpdateStatus.setEnabled(true);
        mBtnUpdateStory.setEnabled(true);
    }

    private void updateUIOnLogout() {

        mBtnUpdateStatus.setEnabled(false);
        mBtnUpdateStory.setEnabled(false);

        showView(mProfileBar, false);
        showView(mPnlStatusUpdate, false);
        showView(mPnlStoryUpdate, false);

        mProfileAvatar.setImageBitmap(null);
        mProfileName.setText("");

        mBtnShare.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(android.R.drawable.ic_menu_share),
                null);
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
}
