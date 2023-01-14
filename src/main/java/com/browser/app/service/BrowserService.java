package com.browser.app.service;

import com.browser.app.models.Browser;
import com.browser.app.models.Condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Locale.ENGLISH;

public class BrowserService {
    private static final String CHROME = "Chrome";
    private static final String CHROMIUM = "chromium";
    private static final String FIREFOX = "Firefox";
    private static final String SEAMONKEY = "seamonkey";
    private static final String SAFARI = "Safari";
    private static final String OPERA_OLD_KEYWORD = "opr";
    private static final String OPERA = "Opera";
    private static final String IE = "IExplorer";
    private static final String IE_OLD_KEYWORD = "msie";
    private static final String IE_NEW_KEYWORD = "trident/7.0;";
    private static final String EDG = "edg";
    private static final String EDGE = "Edge";
    private static final String SPACE = " ";

    private static final String SLASH = "/";
    private static final String SUBSTRING = "SUB";
    private static final String EXTRA_SPLIT = "ADD_SPLIT";
    private static final String OPERA_NEW_KEYWORD_V = "version";
    private static final String IE_NEW_KEYWORD_V = "rv:";
    private static final String UNKNOWN = "Unknown Browser";
    private static final List<Browser> browseList;
    public static final int END_INDEX = 4;


    public String identifyBrowserAndVersion(String userAgent) {
        Browser userBrowser = identifyBrowser(userAgent);
        return userBrowser != null ? parseVersion(userAgent, userBrowser) : UNKNOWN;
    }

    private Browser identifyBrowser(String userAgent) {
        for (Browser browser : browseList) {
            boolean meetsAllConditions = evaluateConditions(userAgent, browser, 0, true);
            if (meetsAllConditions) {
                return browser;
            }
        }
        return null;
    }

    private boolean evaluateConditions(String userAgent, Browser browser, int index, boolean meets) {
        while (index < browser.getConditions().size()) {
            Condition condition = browser.getConditions().get(index);
            if (meets || condition.isOptional()) {
                boolean doesMeetCondition = (condition.isShouldContain() && userAgent.contains(condition.getKeyword()))
                        || (!condition.isShouldContain() && !userAgent.contains(condition.getKeyword()));

                return meets && evaluateConditions(userAgent, browser, index + 1, doesMeetCondition);
            }
            index += 1;
        }
        return meets;
    }

    private String parseVersion(String userAgent, Browser userBrowser) {
        String splitWith = userBrowser.getAdditionalKeyword() != null ? userBrowser.getAdditionalKeyword() : userBrowser.getSplitByKeyword();
        String[] splitUpUserAgent = userAgent.split(splitWith);
        if (splitUpUserAgent.length > 1) {
            String unparsedVersion = userBrowser.getAdditionalParsing() != null ?
                    additionalParsing(splitUpUserAgent[1], userBrowser.getAdditionalParsing()) : splitUpUserAgent[1];

            return userBrowser.getName() + SPACE + unparsedVersion.trim();
        }
        return userBrowser.getName();
    }

    private String additionalParsing(String unparsedVersion, String parseVersionBy) {
        if (parseVersionBy.equalsIgnoreCase(EXTRA_SPLIT)) {
            String[] strings = unparsedVersion.split(SPACE);
            return strings[0];
        } else {
            return unparsedVersion.substring(0, END_INDEX);
        }
    }

    static {
        browseList = new ArrayList<>();

        Condition condition = new Condition(OPERA_OLD_KEYWORD, true);
        Browser browserOperaOld = new Browser(OPERA, OPERA_OLD_KEYWORD + SLASH,
                Collections.singletonList(condition));

        Browser browserOperaNew = new Browser(OPERA, OPERA_NEW_KEYWORD_V + SLASH,
                Collections.singletonList(new Condition(OPERA.toLowerCase(ENGLISH), true)));

        Browser browserEdge = new Browser(EDGE, EDG + SLASH,
                Collections.singletonList(new Condition(EDG, true)));

        Browser browserFirefox = new Browser(FIREFOX, FIREFOX.toLowerCase(ENGLISH) + SLASH,
                Arrays.asList(new Condition(FIREFOX.toLowerCase(ENGLISH), true),
                        new Condition(SEAMONKEY)));

        Browser browserChrome = new Browser(CHROME, CHROME.toLowerCase(ENGLISH) + SLASH,
                EXTRA_SPLIT, Arrays.asList(new Condition(CHROME.toLowerCase(ENGLISH), true),
                new Condition(CHROMIUM)));

        Browser browserIe = new Browser(IE, IE_NEW_KEYWORD, IE_NEW_KEYWORD_V, SUBSTRING,
                Collections.singletonList(new Condition(IE_NEW_KEYWORD, true)));

        Browser browserSafari = new Browser(SAFARI, SAFARI.toLowerCase(ENGLISH) + SLASH, Arrays.asList(
                new Condition(SAFARI.toLowerCase(ENGLISH), true),
                new Condition(true, CHROME.toLowerCase(ENGLISH)),
                new Condition(true, CHROMIUM)
        ));

        browseList.add(browserOperaOld);
        browseList.add(browserOperaNew);
        browseList.add(browserEdge);
        browseList.add(browserFirefox);
        browseList.add(browserChrome);
        browseList.add(browserIe);
        browseList.add(browserSafari);
    }

}
