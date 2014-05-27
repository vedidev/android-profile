android-social
==============

[WIP] - Connecting Android store to social networks

Running this WIP example requires 'social' branch on:
lassic/android-store
soomla/android-blueprint
(Probably only for gradle build support)

The library itself is mostly and interface to social providers,
defines events, models and actions relating to social interactions.
Rewards for social actions are based on android-blueprint

BaseSocialAction inheriting from ActionMission
The main event is SocialActionPerformed, which carries a BaseSocialAction
as a property, completes the underlying Mission and causes any Rewards
to be given.

The library will also manage Users (at least SocialAccount)
and some work there started in SoomlaUserManager.

Provider libraries (plugins)
1. socialauth + socialauth-android
This wrapps several social providers together, but with simplistic
UI and APIs.

2. Facebook official SDK v3.14.1
All the FB support, like SSO through the app etc.
When another provider like Twitter is added, it should also
be evaluated, since Twitter don't have an SDK for mobile
(however, Twitter4J seems half popular as an unofficial lib)

* These should eventually be wrapped as submodules or plugins
so the developer can choose which one to work with

Examples:
1. Inside android-social/SoomlaSocialExample
  1.1 MixedExampleActivity
      Shows a basic example of choosing between plugins for login
      + status update action
      Using the FB native provider here is still in progress.

2. Example in android-store[social] branch, used to work with socialauth
   and showed how to get virtual items with social actions (login+status update)
   It needs to be updated to work with the recent addition of the native FB SDK

Major TODO:
* Complete and test FB SDK integration, including publish permissions
  fallback on WebDialog, error handling etc.
* Finalize and agree on library architecture
* Decide on how to work with Unity integration.
  Until now I couldn't decide between 3 different approaches:
  1. Activity (see: FacebookEnabledActivity for stub)
  2. Fragment (see: FacebookEnabledFragment for stub)
  3. IContextProvider (see: FacebookSDKProvider, most complete at this time)
* Separate the provider implementations to submodules  
* Ensure support for older (non-gradle) based projects/builds
* Complete fuller android-store example using either SDK
* Consider android-simple-facebook to save some modeling/wrapping
(however, it is doing the same chase of the FB SDK, and even their
  latest v2.0 branch to support FB 3.14.+ is not working out of the box)
