package net.sourceforge.subsonic.android;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class net.sourceforge.subsonic.android.SubsonicActivityTest \
 * net.sourceforge.subsonic.android.tests/android.test.InstrumentationTestRunner
 */
public class SubsonicActivityTest extends ActivityInstrumentationTestCase<SubsonicActivity> {

    public SubsonicActivityTest() {
        super("net.sourceforge.subsonic.android", SubsonicActivity.class);
    }

}
