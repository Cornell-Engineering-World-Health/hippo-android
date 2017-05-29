# hippo-android

The Android application for hippo, a HIPAA-compliant video conferencing system. Powered by [hippo-backend](https://github.com/Cornell-Engineering-World-Health/hippo-backend "hippo-backend")'s REST API. Built with OpenTok Android SDK and Google OAth. 

Features include:
* Secure video calls
* User accounts
* Video interface controls
* Synchronization with our web interface

See our web version, [hippo-frontend](https://github.com/Cornell-Engineering-World-Health/hippo-frontend).

View our live web deployment at https://aqueous-stream-90183.herokuapp.com.

Learn more about our team, [Cornell Engineering World Health](https://ewh.engineering.cornell.edu/).

For local deployment, follow these instructions:

## Setup

1. Configure the backend repo according to [hippo-backend](https://github.com/Cornell-Engineering-World-Health/hippo-backend).

2. Clone this repository (outside of hippo-backend).

3. Open Android Studio. Select 'Import project (Eclipse, ADT, Gradle, etc.)'.

4. Select 'HippoAndroid' from the 'hippo-android' directory you just cloned.

5. 'Import project from external model' with 'Gradle'. Keep remaining default settings and hit 'Finish'.

6. Replace `API_KEY` with your own key in [VideoCallActivity.java](/HippoAndroid/app/src/main/java/edu/cornell/engineering/ewh/hippoandroid/VideoCallActivity.java). Make sure to use the same OpenTok API key as you used in your hippo-backend `.env` file.

7. 'Build' > 'Make Project'.

## Run

1. 'Run' > 'Run 'app'' in Android Studio, or hit the green play button.

If you have problems running in the emulator:
 - 'Build' > 'Clean project'
 - 'Tools' > 'Android' > 'Sync Project with Gradle Files'
 
## Google OAuth
Refer to [here](https://developers.google.com/identity/sign-in/android/start-integrating) for extensive instructions.
#### Prerequisites
1. Add the Google Play Services SDK:
- In Android Studio, select **Tools > Android > SDK Manager**.
- Scroll to the bottom of the package list and select **Extras > Google Repository**. The package is downloaded to your computer and installed in your SDK environment at *android-sdk-folder*/extras/google/google_play_services.
#### Configuration File
1. Find SHA-1 hash of your signing certificate [here](https://developers.google.com/android/guides/client-auth).
2. Create or select existing project for application [here](https://developers.google.com/mobile/add?platform=android&cntapi=signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fsign-in%3Fconfigured%3Dtrue&cntlbl=Continue%20Adding%20Sign-In).
3. Follow instructions to get a configuration file to add to your project.
4. Copy the google-services.json file you just downloaded into the app/ or mobile/ directory of your Android Studio project.
#### Google Services Plugin
1. Add the dependency to your project-level build.gradle:
```
classpath 'com.google.gms:google-services:3.0.0'
```
2. Add the plugin to your app-level build.gradle:
```
apply plugin: 'com.google.gms.google-services'
```
#### Google Play Services
1. Add Google Play Services as a dependency:
```
apply plugin: 'com.android.application'
    ...
    dependencies {
        compile 'com.google.android.gms:play-services-auth:9.8.0'
    }
```
