android-profile
===============

*This project is a part of [The SOOMLA Project](http://project.soom.la) which is a series of open source initiatives with a joint goal to help mobile game developers build engaging and monetizing games more easily.*

The gist:

```Java
    // dedice on rewards (here 1 "sword" virtual item) for social action
    Reward reward = new VirtualItemReward([id], "Update Status for item", 1, "sword");
    
    // optional reward on each action, select from available social providers
    SoomlaProfile.getInstance().login([activity], IProvider.Provider.FACEBOOK, [reward]);
    
    // after login, the user profile on the selected providers is available locally
    UserProfile userProfile = SoomlaProfile.getInstance().getStoredUserProfile();
    
    // on successful login event, you can perform actions on the provider
    // this will post to Facebook, upon completion, the user will get 1 sword!
    SoomlaProfile.getInstance().updateState(IProvider.Provider.FACEBOOK, "cool game!", reward);
```

## android-profile

android-profile is an open code initiative as part of The SOOMLA Project. It is a Java API that unifies interaction with social and identity providers APIs, and optionally ties it together with the game's virtual economy.
This eables easily rewarding players with social actions they perform in-game, and leveraging user profiles.

* See [android-store]() for basic setup (repeated here) and more details on setting up virtual economy and IAP.

## Getting Started (With sources)

1. Clone android-profile. Copy all files from android-profile/SoomlaAndroidProfile subfolders to their equivalent folders in your Android project:

 `git clone git@github.com:soomla/android-profile.git`

2. Make the following changes to your AndroidManifest.xml:

  Set `SoomlaApp` as the main Application by placing it in the `application` tag:

    ```xml
    <application ...
                 android:name="com.soomla.store.SoomlaApp">
    ```

3. Change the value of `StoreConfig.SOOM_SEC` to a secret of you choice. Do this now!
   **You can't change this value after you publish your game!**

4. Create your own implementation of _IStoreAssets_ in order to describe your specific game's assets ([example](https://github.com/soomla/android-store/blob/master/SoomlaAndroidExample/src/com/soomla/example/MuffinRushAssets.java)). Initialize _StoreController_ with the class you just created:

      ```Java
       StoreController.getInstance().initialize(new YourStoreAssetsImplementation(),
                                           "[YOUR CUSTOM GAME SECRET HERE]");
      ```

    > The custom secret is your encryption secret for data saved in the DB. This secret is NOT the secret from step 3 (select a different value).

    > Initialize `StoreController` ONLY ONCE when your application loads.

5. Refer to the [next section](https://github.com/soomla/android-store#whats-next-selecting-social-providers) for information of selecting social providers and setting them up.

Cool, almost there, on to provider selection!


## What's next? Selecting Social Providers

android-profile is a plugin based system when working with social providers (Facebook, Twitter etc.)
As a starting point we created a plugin based on the [socialauth project](https://github.com/3pillarlabs/socialauth-android/) since it supports several providers
out of the box. We plan to add more providers based on native SDKs (FB, I'm looking at you) and hopefully get contributions for more.

Since the socialauth project aggregates several providers together, it also aims to find the shared API between them, which is thinner than the full API for each one. We hope these actions are enough to get you started.

You must select at least one social provider for android-store to work properly. The integration of a social provider is very easy:

#### [SocialAuth](https://github.com/soomla/android-profile-socialauth)

1. Add `AndroidProfileSocialAuth.jar` from the folder `social-providers/socialauth` to your project.
2. Make the following changes in `AndroidManifest.xml`:

  Add the following permission (for socialauth):

  ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- optional: required for uploadImage from SD card -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  ```

   You need to tell us what plugin you're using so add a meta-data tag for that:

  ```xml
      <meta-data android:name="com.soomla.social.provider" android:resource="@array/social_providers"/>
  ```
  As you can see, this points to a resource, a string-array of names which we'll define in the next step

3. Open res/values/strings.xml and add the following resource string-array:
   
  ```xml
      <string-array name="social_providers">
        <item>socialauth.SoomlaSAFacebook</item>
        <item>socialauth.SoomlaSATwitter</item>
        <item>socialauth.SoomlaSAGoogle</item>
      </string-array>
  ``` 
  This can be only a single item or several providers like here.

4. After you initialize `SoomlaProfile`, you can use it to login with a specified provider

  ```Java
      SoomlaProfile.getInstance().login([activity], IProvider.Provider.FACEBOOK);
  ```


5. If you want to allow the test purchases, all you need to do is tell that to the plugin:

  ```Java
      GooglePlayIabService.AllowAndroidTestPurchases = true;
  ```

For Google Play, We recommend that you open the IAB Service and keep it open in the background in cases where you have an in-game storefront. This is how you do that:

When you open the store, call:  
  ```Java
      StoreController.getInstance().startIabServiceInBg();
  ```

When the store is closed, call:  
  ```Java
      StoreController.getInstance().stopIabServiceInBg();
  ```

#### [Facebook](https://github.com/soomla/android-profile-facebook)

Coming Soon.

## UserProfile

As part of a login call to a provider, Soomla will internally try to also fetch the online user profile details via
`UserProfile` and store them in the secure [Soomla Storage](https://github.com/soomla/android-store#storage)
Later, this can be retrieved locally (in offline mode) via:

`UserProfile userProfile = SoomlaProfile.getInstance().getStoredUserProfile(IProvider.Provider.FACEBOOK)`

 This can throw a `UserProfileNotFoundException` if something strange happens to the local storage,
 in that case, you need to require a new login to get the `UserProfile` again.

## Rewards feature

One of the big benefits of using Soomla's profile module for social networks interactions is that you can easily tie it in with the game's virtual economy.
This is done by the ability to sepcify a `Reward` (perhapds more specifically, a `VirtualItemRewrad`) to most social actions defined in `SoomlaProfile`.

For example, to reward a user with a "sword" virtual item upon login to Facebook:

  ```Java
    Reward reward = new VirtualItemReward([id], "Update Status for sword", 1, "sword");
    reward.setRepeatable(false); // only once! not every login :)
    SoomlaProfile.getInstance().login([activity], IProvider.Provider.FACEBOOK, reward);
  ```

  Once login completes sucessfully (wait for `LoginFinishedEvent`, see below on events), the
  reward will be automatically given, and synchronized with Soomla's storage.
  
  The reward id is something you manage and should be unique, much like virtual items.

Don't forget to define your _IProfileEventHandler_ in order to get the events of successful or failed social actions (see [Event Handling](https://github.com/soomla/android-profile#event-handling)).


## Debugging

In order to debug android-store, set `StoreConfig.logDebug` to `true`. This will print all of _android-store's_ debugging messages to logcat.

## Storage

The on-device storage is encrypted and kept in a SQLite database. SOOMLA is preparing a cloud-based storage service that will allow this SQLite to be synced to a cloud-based repository that you'll define.

**Example Usages**

* Give the user 10 pieces of a virtual currency with itemId "currency_coin":

    ```Java
    StoreInventory.giveVirtualItem("currency_coin", 10);
    ```

* Take 10 virtual goods with itemId "green_hat":

    ```Java
    StoreInventory.takeVirtualItem("green_hat", 10);
    ```

* Get the current balance of a virtual good with itemId "green_hat" (here we decided to show you the 'long' way. you can also use StoreInventory):

    ```Java
    VirtualGood greenHat = (VirtualGood)StoreInfo.getVirtualItem("green_hat");
    int greenHatsBalance = StorageManager.getVirtualGoodsStorage().getBalance(greenHat);
    ```

## Security


If you want to protect your game from 'bad people' (and who doesn't?!), you might want to follow some guidelines:

+ SOOMLA keeps the game's data in an encrypted database. In order to encrypt your data, SOOMLA generates a private key out of several parts of information. The Custom Secret is one of them. SOOMLA recommends that you provide this value when initializing `StoreController` and before you release your game. BE CAREFUL: You can change this value once! If you try to change it again, old data from the database will become unavailable.
+ Following Google's recommendation, SOOMLA also recommends that you split your public key and construct it on runtime or even use bit manipulation on it in order to hide it. The key itself is not secret information but if someone replaces it, your application might get fake messages that might harm it.

## Event Handling


For event handling, we use Square's great open-source project [otto](http://square.github.com/otto/). In ordered to be notified of store related events, you can register for specific events and create your game-specific behavior to handle them.

> Your behavior is an addition to the default behavior implemented by SOOMLA. You don't replace SOOMLA's behavior.

In order to register for events:

1. In the class that should receive the event create a function with the annotation '@Subscribe'. Example:

    ```Java
    @Subscribe public void onLoginFinishedEvent(LoginFinishedEvent loginFinishedEvent) {
        ...
    }
    ```

2. You'll also have to register your class in the event bus (and unregister when needed):

   ```Java
   BusProvider.getInstance().register(this);
   ```

   ```Java
   BusProvider.getInstance().unregister(this);
   ```

> If your class is an Activity, register in 'onResume' and unregister in 'onPause'

You can find a full event handler example [here](https://github.com/soomla/android-store/blob/master/SoomlaAndroidExample/src/com/soomla/example/ExampleEventHandler.java).

[List of events](https://github.com/soomla/android-store/tree/master/SoomlaAndroidStore/src/com/soomla/store/events)

[Full documentation and explanation of otto](http://square.github.com/otto/)

## Contribution


We want you!

Fork -> Clone -> Implement -> Insert Comments -> Test -> Pull-Request.

We have great RESPECT for contributors.

## Code Documentation


android-profile follows strict code documentation conventions. If you would like to contribute please read our [Documentation Guidelines](https://github.com/soomla/android-store/blob/master/documentation.md) and follow them. Clear, consistent  comments will make our code easy to understand.

## SOOMLA, Elsewhere ...
