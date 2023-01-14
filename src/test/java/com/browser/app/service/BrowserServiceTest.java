package com.browser.app.service;

import com.browser.app.models.Browser;
import com.browser.app.models.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class BrowserServiceTest {


    @InjectMocks
    private BrowserService browserService;

    @Test
    public void testIdentifyBrowserAndVersionWhenFirefoxUserAgentReturnBrowserFirefox() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0";
        String actualBrowser = browserService.identifyBrowserAndVersion(userAgent.toLowerCase());
        Assert.assertEquals("Firefox 108.0", actualBrowser);
    }

    @Test
    public void testIdentifyBrowserAndVersionWhenUserAgentReturnUnknownBrowser() {
        String userAgent = "hello";
        String actualBrowser = browserService.identifyBrowserAndVersion(userAgent.toLowerCase());
        Assert.assertEquals("Unknown Browser", actualBrowser);
    }

    @Test
    public void testIdentifyBrowserAndVersionWhenAdditionalParsingBrowserIsNotNull() {
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko";
        String actualBrowser = browserService.identifyBrowserAndVersion(userAgent.toLowerCase());
        Assert.assertEquals("IExplorer 11.0", actualBrowser);
    }

    @Test
    public void testEvaluateConditionsWhenMeetsSingleBrowserCondition() {
        String userAgent = "123 fast US ";
        Condition condition = new Condition("US", true);
        Browser browser = new Browser(Collections.singletonList(condition));
        int index = 0;
        boolean meets = true;

        boolean actual = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(browserService, "evaluateConditions", userAgent, browser, index, meets));
        Assert.assertTrue(actual);
    }

    @Test
    public void testEvaluateConditionsWhenDoesNotMeetsAllBrowserConditions() {
        String userAgent = "123 fast qwert ";
        Condition condition = new Condition("US", true);
        Browser browser = new Browser(Collections.singletonList(condition));
        int index = 0;
        boolean meets = true;

        boolean actual = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(browserService, "evaluateConditions", userAgent, browser, index, meets));
        Assert.assertFalse(actual);
    }

    @Test
    public void testEvaluateConditionsWhenMeetsMultipleConditions() {
        String userAgent = "ABC qwe JKL";
        Condition condition = new Condition("ABC", true);
        Condition condition2 = new Condition(true, "XYZ");
        Condition condition3 = new Condition("OPQ");
        Condition condition4 = new Condition("JKL", true);
        Browser browser = new Browser(Arrays.asList(condition, condition2, condition3, condition4));

        boolean actual = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(browserService, "evaluateConditions",
                userAgent, browser, 0, true));
        Assert.assertTrue(actual);
    }

    @Test
    public void testIdentifyBrowserReturnsBrowser() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0";

        Browser actualBrowser = ReflectionTestUtils.invokeMethod(browserService, "identifyBrowser", userAgent.toLowerCase());
        Assert.assertEquals("Firefox", actualBrowser.getName());
    }

    @Test
    public void testIdentifyBrowserReturnsNull() {
        String userAgent = "qWDQ QWED";

        Browser actualBrowser = ReflectionTestUtils.invokeMethod(browserService, "identifyBrowser", userAgent.toLowerCase());
        Assert.assertNull(actualBrowser);
    }

    @Test
    public void testParseVersionReturnsBrowserNameAndVersionWhenAdditionalKeywordBrowserIsNotNull() {
        String userAgent = "Mozilla/5.0 (Windows US 10.0; Win64; x64; AR:108.0) Gecko/20100101 Fox/108.0";
        Browser browser = new Browser("Fox", null, "x/");

        String actualNameAndVersion = ReflectionTestUtils.invokeMethod(browserService, "parseVersion", userAgent, browser);
        Assert.assertEquals("Fox 108.", actualNameAndVersion);
    }

    @Test
    public void testParseVersionReturnsBrowserNameWhenAdditionalKeywordBrowserIsNull() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:108.0) Gecko/20100101 Pet/108.SAR";
        Browser browser = new Browser("Pet", "SAR");

        String actualName = ReflectionTestUtils.invokeMethod(browserService, "parseVersion", userAgent, browser);
        Assert.assertEquals("Pet", actualName);
    }

    @Test
    public void testParseVersionWhenAdditionalParsingIsNotNullReturnsBrowserNameAndVersion() {
        String userAgent = "Mozilla/5.0VZ  Cat/108.0";
        Browser browser = new Browser("Cat", null, "t/", "PY");

        String actualNameAndVersion = ReflectionTestUtils.invokeMethod(browserService, "parseVersion", userAgent,
                browser);
        Assert.assertEquals("Cat 108.", actualNameAndVersion);
    }

    @Test
    public void testAdditionalParsingWhenParseVersionByIsExtraSplit() {
        String unparsedVersion = "PARSED VERSION TEST";
        String parseVersionBy = "ADD_SPLIT";

        String actual = ReflectionTestUtils.invokeMethod(browserService, "additionalParsing", unparsedVersion,
                parseVersionBy);
        Assert.assertEquals("PARSED", actual);
    }

    @Test
    public void testAdditionalParsingWhenParseVersionByIsNotExtraSplit() {
        String unparsedVersion = "DATABASE TEST";
        String parseVersionBy = "NOTE";

        String actual = ReflectionTestUtils.invokeMethod(browserService, "additionalParsing", unparsedVersion,
                parseVersionBy);
        Assert.assertEquals("DATA", actual);

    }
}