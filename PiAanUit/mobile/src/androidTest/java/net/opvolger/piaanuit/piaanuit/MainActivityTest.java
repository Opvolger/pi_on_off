package net.opvolger.piaanuit.piaanuit;

import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.opvolger.piaanuit.api.ApiModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        mActivityRule = new ActivityTestRule<>(MainActivity.class, true, true);
        server = new MockWebServer();
        server.start();
        String baseUrl = server.url("/").toString();

        // Singleton
        ApiModule apimodule = ApiModule.getInstance();
        apimodule.url = baseUrl;
    }

    @After
    public void tearDown() throws IOException {
        mActivityRule = null;

        // Singleton leeg halen, anders blijft hij oude mock server houden.
        // Dit probleem moet ik nog overwinnen
        ApiModule.resetInstance();
        server.shutdown();
        server = null;
    }

    @Test
    public void LightOn_Error_x_On() throws Exception {

        String serverResponse = "On";

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(serverResponse));

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText(R.string.on)).perform(click());

        onView(withText(serverResponse)).check(matches(isDisplayed()));
    }

    @Test
    public void LightOn() throws Exception {

        String serverResponse = "Test";

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(serverResponse));

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText(R.string.on)).perform(click());

        onView(withText(serverResponse)).check(matches(isDisplayed()));
    }

    @Test
    public void LightOn_Error_x_On_Fix() throws Exception {

        String serverResponse = "On";

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(serverResponse));

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText(R.string.on)).perform(click());

        onView(withId(R.id.info)).check(matches(isDisplayed())).check(matches(withText(serverResponse)));
    }

    @Test
    public void LightOffAndOn() throws Exception {

        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().equals("/On")){
                    return new MockResponse().setBody("On").setResponseCode(200);
                } else if (request.getPath().equals("/Off")){
                    return new MockResponse().setBody("Off").setResponseCode(200);
                }
                return new MockResponse().setResponseCode(404);
            }
        });

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText(R.string.off)).perform(click());

        onView(withId(R.id.info)).check(matches(isDisplayed())).check(matches(withText("Off")));

        RecordedRequest request = server.takeRequest();
        assertEquals("/Off", request.getPath());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText(R.string.on)).perform(click());

        onView(withId(R.id.info)).check(matches(isDisplayed())).check(matches(withText("On")));

        request = server.takeRequest();
        assertEquals("/On", request.getPath());
    }
}
