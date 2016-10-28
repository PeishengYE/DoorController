package com.radioyps.doorcontroller;

/**
 * Created by developer on 26/10/16.
 */


        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;
        import android.test.AndroidTestCase;

public class TestPractice extends AndroidTestCase {
    /*
        This gets run before every test.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testThatDemonstratesAssertions() throws Throwable {
        int a = 5;
        int b = 3;
        int c = 5;
        int d = 10;

        assertEquals("X should be equal", a, c);
        assertTrue("Y should be true", d > a);
        assertFalse("Z should be false", a == b);

        if (b > d) {
            fail("XX should never happen");
        }

//        Utils.getGCMRemoteToken()
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString(mContext.getString(R.string.pref_client_ip_port_key), "").apply();
        int port = Utils.getPreferredIPPort(mContext);
          assertEquals("port should equal", port, (int)Integer.valueOf(mContext.getString(R.string.pref_client_default_ip_port)));
          assertEquals("port should equal", port, Integer.parseInt(mContext.getString(R.string.pref_client_default_ip_port)));

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
