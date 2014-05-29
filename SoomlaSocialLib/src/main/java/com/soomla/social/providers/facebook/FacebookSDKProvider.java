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

package com.soomla.social.providers.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.soomla.social.IAuthProviderAggregator;
import com.soomla.social.IContextProvider;
import com.soomla.social.ISocialProvider;
import com.soomla.social.actions.BaseSocialAction;
import com.soomla.social.actions.UpdateStatusAction;
import com.soomla.social.actions.UpdateStoryAction;
import com.soomla.social.events.FacebookContactsEvent;
import com.soomla.social.events.FacebookProfileEvent;
import com.soomla.social.events.SocialActionPerformedEvent;
import com.soomla.social.events.SocialLoginEvent;
import com.soomla.social.events.SocialLogoutEvent;
import com.soomla.social.util.Utils;
import com.soomla.store.BusProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by oriargov on 5/22/14.
 */
public class FacebookSDKProvider implements ISocialProvider {

    private static final String TAG = "FacebookSDKProvider";

    private static final String PUBLISH_ACTIONS = "publish_actions";
    /// List of additional write permissions being requested
    private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList(PUBLISH_ACTIONS);

    // Redirect URL for authentication errors requiring a user action
    private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");

    private static final String PENDING_ACTION_KEY = "pendingSocialAction";

    // Activity code to flag an incoming activity result is due
    // to a new permissions request
    private static final int REAUTH_ACTIVITY_CODE = 100;

    // Indicates an on-going reauthorization request
    private boolean pendingAnnounce;
    private UiLifecycleHelper uiHelper;

    private IContextProvider mCtxProvider;
    private BaseSocialAction mPendingSocialAction;

    public FacebookSDKProvider(IContextProvider ctxProvider) {
        mCtxProvider = ctxProvider;
    }

    public void onCreate(Bundle savedInstanceState) {
        uiHelper = new UiLifecycleHelper(mCtxProvider.getActivity(), statusCallback);
        uiHelper.onCreate(savedInstanceState);

        if (savedInstanceState.containsKey(PENDING_ACTION_KEY)) {
            final String pendingActionJsonStr = savedInstanceState.getString(PENDING_ACTION_KEY);
            if (pendingActionJsonStr != null) {
                try {
                    mPendingSocialAction = new UpdateStoryAction(new JSONObject(pendingActionJsonStr));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onDestroy() {
        uiHelper.onDestroy();
    }

    public void onResume() {
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        uiHelper.onSaveInstanceState(outState);
        if(mPendingSocialAction != null) {
            outState.putString(PENDING_ACTION_KEY, mPendingSocialAction.toJSONObject().toString());
        }
    }

    public void onPause() {
        uiHelper.onPause();
    }

    public void onStop() {
        uiHelper.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
        switch (requestCode) {
            case REAUTH_ACTIVITY_CODE:
                Session session = Session.getActiveSession();
                if (session != null) {
                    session.onActivityResult(
                            mCtxProvider.getActivity(), requestCode, resultCode, data);
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data,
                                 FacebookDialog.Callback facebookDialogCallback) {
        uiHelper.onActivityResult(requestCode, resultCode, data, facebookDialogCallback);
    }

    public void trackPendingDialogCall(FacebookDialog.PendingCall pendingCall) {
        uiHelper.trackPendingDialogCall(pendingCall);
    }

    public AppEventsLogger getAppEventsLogger() {
        return uiHelper.getAppEventsLogger();
    }

    @Override
    public String getProviderName() {
        return IAuthProviderAggregator.FACEBOOK;
    }

    @Override
    public void login() {
        if (mCtxProvider.getActivity() != null) {
            login(mCtxProvider.getActivity());
        }
//        else if (mCtxProvider.getFragment() != null) {
//            login(mCtxProvider.getFragment());
//        }
        else {
            Log.w(TAG, "login: no valid context found");
        }
    }

    public void login(Activity activity) {
        Session session = Session.getActiveSession();

        if(session == null){
            // try to restore from cache
            session = Session.openActiveSessionFromCache(activity);
        }

        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(activity)
                    .setPermissions(Arrays.asList("public_profile"))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(activity, true, statusCallback);
        }
    }

    public void login(Fragment fragment) {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(fragment).setCallback(statusCallback));
        } else {
            Session.openActiveSession(fragment.getActivity(), true, statusCallback);
        }
    }

    public boolean isLoggedIn(Context context) {
        Session session = Session.getActiveSession();
        if (session == null) {
            // try to restore from cache
            session = Session.openActiveSessionFromCache(context);
        }

        return session != null && session.isOpened();
    }

    @Override
    public void logout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    public void loginWithPublishPermissions() {
        Session session = Session.getActiveSession();
        final Activity activity = mCtxProvider.getActivity();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(activity)
                    .setPermissions(Arrays.asList(PUBLISH_ACTIONS))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(activity, true, statusCallback);
        }
    }

    public void loginWithCustomPermissions(List<String> permissions) {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            final Activity activity = mCtxProvider.getActivity();
            session.openForRead(new Session.OpenRequest(activity)
                    .setPermissions(permissions)
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(mCtxProvider.getActivity(), true, statusCallback);
        }
    }

    public void requestPublishPermissions(Session session) {
        Session.NewPermissionsRequest newPermissionsRequest = new Session
                .NewPermissionsRequest(mCtxProvider.getActivity(),
                Arrays.asList(PUBLISH_ACTIONS)).setRequestCode(REAUTH_ACTIVITY_CODE);
        session.requestNewPublishPermissions(newPermissionsRequest);
    }

    public void requestNewPermissions(Session session, List<String> permissions) {
        Session.NewPermissionsRequest newPermissionsRequest = new Session
                .NewPermissionsRequest(
                mCtxProvider.getActivity(), permissions).setRequestCode(REAUTH_ACTIVITY_CODE);
        session.requestNewPublishPermissions(newPermissionsRequest);
    }

    public boolean hasPublishPermissions() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains(PUBLISH_ACTIONS);
    }

    public boolean canPresentShareDialog(FacebookDialog.ShareDialogFeature shareFeature) {
        return FacebookDialog.canPresentShareDialog(mCtxProvider.getContext(), shareFeature);
    }

    public Session getSession() {
        return Session.getActiveSession();
    }

    protected void publishWithFBDialog(UpdateStoryAction updateStoryAction) {
        if (FacebookDialog.canPresentShareDialog(mCtxProvider.getContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            final Activity activity = mCtxProvider.getActivity();

            // Publish the post using the Share Dialog
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
                    activity)
                    .setName(updateStoryAction.getName())
                    .setCaption(updateStoryAction.getCaption())
                    .setDescription(updateStoryAction.getDesc())
                    .setPicture(updateStoryAction.getPictureLink())
                    .setLink(updateStoryAction.getLink())
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());

        } else {
            publishFeedDialog(updateStoryAction);
        }
    }

    public void publishFeedDialog(final UpdateStoryAction updateStoryAction) {
        Bundle params = new Bundle();
        params.putString("name", updateStoryAction.getName());
        params.putString("caption", updateStoryAction.getCaption());
        params.putString("description", updateStoryAction.getDesc());
        params.putString("link", updateStoryAction.getLink());
        params.putString("picture", updateStoryAction.getLink());
//        updateStoryAction.getMessage()

        final Activity activity = mCtxProvider.getActivity();
        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(activity,
                        getSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                Log.d(TAG, "Posted story, id: " + postId);
                                BusProvider.getInstance().post(
                                        new SocialActionPerformedEvent(updateStoryAction));
                            } else {
                                // User clicked the Cancel button
                                Log.d(TAG, "Publish cancelled");
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            Log.d(TAG, "Publish cancelled");
                        } else {
                            // Generic, ex: network error
                            Log.d(TAG, "Error posting story");
                        }
                    }
                })
                .build();
        feedDialog.show();
    }

    public void publish(final UpdateStoryAction updateStoryAction) {
        final Session session = getSession();

        if (session == null || !session.isOpened()) {
            return;
        }

        List<String> permissions = session.getPermissions();
        if (!permissions.containsAll(PUBLISH_PERMISSIONS)) {
            // Mark that we are currently waiting for confirmation of publish permissions
            pendingAnnounce = true;
//            session.addCallback(this);
            requestPublishPermissions(session);
            return;
        }

        Request.newStatusUpdateRequest(session,
                updateStoryAction.getMessage(),
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        BusProvider.getInstance().post(
                                new SocialActionPerformedEvent(updateStoryAction));
                    }
                })
                .executeAsync();
    }

    @Override
    public void updateStatusAsync(UpdateStatusAction updateStatusAction) {
        UpdateStoryAction updateStoryAction = new UpdateStoryAction(
                updateStatusAction.getProviderName(),
                updateStatusAction.getName(),
                null,
                updateStatusAction.getMessage()
                ,null,null,null);
        publishWithFBDialog(updateStoryAction);
    }

    @Override
    public void updateStoryAsync(UpdateStoryAction updateStoryAction) throws UnsupportedEncodingException {
        publishWithFBDialog(updateStoryAction);
    }

    @Override
    public void getProfileAsync() {
        Request.newMeRequest(getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                BusProvider.getInstance().post(new FacebookProfileEvent(user));
            }
        }).executeAsync();
    }

    @Override
    public void getContactsAsync() {
        Request.newMyFriendsRequest(getSession(), new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                BusProvider.getInstance().post(new FacebookContactsEvent(users));
            }
        }).executeAsync();
    }

    public void uploadImages(Collection<File> imageFiles, Collection<Bitmap> bitmaps) {
        if (FacebookDialog.canPresentShareDialog(mCtxProvider.getContext(),
                FacebookDialog.ShareDialogFeature.PHOTOS)) {
            // Publish the post using the Share Dialog
            FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(
                    mCtxProvider.getActivity())
                    .addPhotoFiles(imageFiles)
                    .addPhotos(bitmaps)
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());

        } else {
            Log.d(TAG, "cannot present: ShareDialogFeature.PHOTOS");
            // try to fall back on background requests
            List<Request> requests = new ArrayList<Request>();
            final Session session = getSession();
            final Request.Callback callback = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    // todo: fire event
//                    BusProvider.getInstance().post(new SocialActionPerformedEvent(uploadImageAction));
                }
            };

            for (File imgFile : imageFiles) {
                try {
                    final Request fileRequest = Request.newUploadPhotoRequest(
                            session, imgFile, callback);
                    requests.add(fileRequest);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            for (Bitmap bmp : bitmaps) {
                final Request fileRequest = Request.newUploadPhotoRequest(
                        session, bmp, callback);
                requests.add(fileRequest);
            }

            Request.executeBatchAsync(requests);
        }
    }

    public void uploadVideo(File videoFile) {
        final Session session = getSession();
        final Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                // todo: fire event
//                BusProvider.getInstance().post(new SocialActionPerformedEvent(uploadVideoAction));
            }
        };

        try {
            Request.newUploadVideoRequest(session, videoFile, callback).executeAsync();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void graphPathRequest(String graphPath) {
        final Session session = getSession();
        final Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                // todo: fire event
            }
        };
        Request.newGraphPathRequest(session, graphPath, callback).executeAsync();
    }

    public void graphRequest(String graphPath, Bundle parameters, String httpMethod) {
        final Session session = getSession();
        final Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                // todo: fire event
            }
        };
        new Request(session, graphPath,
                parameters,
                Utils.valueOfIgnoreCase(HttpMethod.class, httpMethod),
                callback, null);
    }

    protected void onLogin() {
        Log.d(TAG, "onLogin");
        BusProvider.getInstance().post(new SocialLoginEvent(null));
        // todo: flag for this? always get it for user mgmt?
        getProfileAsync();
    }

    protected void onSessionStateChanged(Session session, SessionState state, Exception exception) {
    }

    protected void onLogout() {
        Log.d(TAG, "onLogout");
        BusProvider.getInstance().post(new SocialLogoutEvent());
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            onLogin();
        }
        else if (state.isClosed()) {
            onLogout();
        }
        else if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
            // Session updated with new permissions
            // so try publishing once more.
            tokenUpdated();
        }
        else {
            onSessionStateChanged(session, state, exception);
        }
    }

    private void tokenUpdated() {
        // Check if a publish action is in progress
        // awaiting a successful reauthorization
        if (pendingAnnounce) {
            // Publish the action
            handleAnnounce();
        }
    }

    private void handleAnnounce() {
        pendingAnnounce = false;
        Session session = Session.getActiveSession();

        if (session == null || !session.isOpened()) {
            return;
        }

        List<String> permissions = session.getPermissions();
        if (!permissions.containsAll(PUBLISH_PERMISSIONS)) {
            pendingAnnounce = true;
            requestPublishPermissions(session);
            return;
        }

        publish((UpdateStoryAction)mPendingSocialAction);
    }

//    private void handleError(FacebookRequestError error) {
//        DialogInterface.OnClickListener listener = null;
//        String dialogBody = null;
//
//        final Activity activity = mCtxProvider.getActivity();
//        if (error == null) {
//            // There was no response from the server.
//            dialogBody = activity.getString(R.string.error_dialog_default_text);
//        } else {
//            switch (error.getCategory()) {
//                case AUTHENTICATION_RETRY:
//                    // Tell the user what happened by getting the
//                    // message id, and retry the operation later.
//                    String userAction = (error.shouldNotifyUser()) ? "" :
//                            activity.getString(error.getUserActionMessageId());
//                    dialogBody = activity.getString(R.string.error_authentication_retry,
//                            userAction);
//                    listener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface,
//                                            int i) {
//                            // Take the user to the mobile site.
//                            Intent intent = new Intent(Intent.ACTION_VIEW,
//                                    M_FACEBOOK_URL);
//                            activity.startActivity(intent);
//                        }
//                    };
//                    break;
//
//                case AUTHENTICATION_REOPEN_SESSION:
//                    // Close the session and reopen it.
//                    dialogBody =
//                            activity.getString(R.string.error_authentication_reopen);
//                    listener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface,
//                                            int i) {
//                            Session session = Session.getActiveSession();
//                            if (session != null && !session.isClosed()) {
//                                session.closeAndClearTokenInformation();
//                            }
//                        }
//                    };
//                    break;
//
//                case PERMISSION:
//                    // A permissions-related error
//                    dialogBody = activity.getString(R.string.error_permission);
//                    listener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface,
//                                            int i) {
//                            pendingAnnounce = true;
//                            // Request publish permission
//                            requestPublishPermissions(Session.getActiveSession());
//                        }
//                    };
//                    break;
//
//                case SERVER:
//                case THROTTLING:
//                    // This is usually temporary, don't clear the fields, and
//                    // ask the user to try again.
//                    dialogBody = activity.getString(R.string.error_server);
//                    break;
//
//                case BAD_REQUEST:
//                    // This is likely a coding error, ask the user to file a bug.
//                    dialogBody = activity.getString(R.string.error_bad_request,
//                            error.getErrorMessage());
//                    break;
//
//                case OTHER:
//                case CLIENT:
//                default:
//                    // An unknown issue occurred, this could be a code error, or
//                    // a server side issue, log the issue, and either ask the
//                    // user to retry, or file a bug.
//                    dialogBody = activity.getString(R.string.error_unknown,
//                            error.getErrorMessage());
//                    break;
//            }
//        }
//
//        // Show the error and pass in the listener so action
//        // can be taken, if necessary.
//        new AlertDialog.Builder(activity)
//                .setPositiveButton(R.string.error_dialog_button_text, listener)
//                .setTitle(R.string.error_dialog_title)
//                .setMessage(dialogBody)
//                .show();
//    }

//    private void onPostActionResponse(Response response) {
//        final Activity activity = mCtxProvider.getActivity();
//        if (activity == null) {
//            // if the user removes the app from the website,
//            // then a request will have caused the session to
//            // close (since the token is no longer valid),
//            // which means the splash fragment will be shown
//            // rather than this one, causing activity to be null.
//            // If the activity is null, then we cannot
//            // show any dialogs, so we return.
//            return;
//        }
//
//        PostResponse postResponse =
//                response.getGraphObjectAs(PostResponse.class);
//
//        if (postResponse != null && postResponse.getId() != null) {
//            String dialogBody = String.format(getString(
//                            R.string.result_dialog_text),
//                    postResponse.getId());
//            new AlertDialog.Builder(activity)
//                    .setPositiveButton(R.string.result_dialog_button_text,
//                            null)
//                    .setTitle(R.string.result_dialog_title)
//                    .setMessage(dialogBody)
//                    .show();
//        } else {
//            handleError(response.getError());
//        }
//    }

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.e(TAG, String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.i(TAG, "uiHelper.onActivityResult.onComplete");
            if (FacebookDialog.getNativeDialogDidComplete(data)) {
                if (FacebookDialog.getNativeDialogCompletionGesture(data) == null
                        || FacebookDialog.COMPLETION_GESTURE_CANCEL.equals(
                        FacebookDialog.getNativeDialogCompletionGesture(data))) {
                    // track cancel
                    Log.d(TAG, "FB dialog cancelled");
                } else {
                    // track post
                    String postId = FacebookDialog.getNativeDialogPostId(data);
                    Log.d(TAG, "postId = " + postId);
                    // todo: get action/mission
//                    BusProvider.getInstance().post(new SocialActionPerformedEvent());
                }
            } else {
                // track cancel
                Log.d(TAG, "FB dialog cancelled");
            }
        }
    };

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
}
