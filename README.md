# hippo-android

The Android application for hippo, a HIPAA-compliant video conferencing system. Powered by [hippo-backend](https://github.com/Cornell-Engineering-World-Health/hippo-backend "hippo-backend")'s REST API. Built with OpenTok, Google OAth, and Java. 

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
