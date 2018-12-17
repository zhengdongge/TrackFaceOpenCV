# TrackFaceOpenCV
First install the opencv manager apk as environment support.

Choose apk that match the cpu architecture of the phone you used.

Normally it will be armeabi-v7a or arm64-v8a for recent phones(2016-2018), x86_64 for pc simulator.

Then install TrackFaceOpenCV1.apk. If opencv manager not installed correctly apk will focus stop(Crush and return to home screen).

There are two ways of using camera, one is protrait with less data to process and faster tracking speed and landscape with more data and slower tracking(different speed when using different phones)。

The opencv manager apk can be reduced by using jni and ndk to compile .so file, but this may still different from phone to phone. So i used the apk as support for now.
