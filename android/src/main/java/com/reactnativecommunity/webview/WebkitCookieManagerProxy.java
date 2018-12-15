package com.reactnativecommunity.webview;

import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.io.IOException;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class WebkitCookieManagerProxy extends CookieManager implements CookieJar {

    private CookieManager webkitCookieManager;

    private static final String TAG = WebkitCookieManagerProxy.class.getSimpleName();


    WebkitCookieManagerProxy(CookieStore store, CookiePolicy cookiePolicy) {
        this.webkitCookieManager = CookieManager.getInstance();
    }


    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        // make sure our args are valid
        if ((uri == null) || (responseHeaders == null))
            return;

        // save our url once
        String url = uri.toString();

        // go over the headers
        for (String headerKey : responseHeaders.keySet()) {
            // ignore headers which aren't cookie related
            if ((headerKey == null)
                    || !(headerKey.equalsIgnoreCase("Set-Cookie2") || headerKey
                    .equalsIgnoreCase("Set-Cookie")))
                continue;

            // process each of the headers
            for (String headerValue : responseHeaders.get(headerKey)) {
                webkitCookieManager.setCookie(url, headerValue);
            }
        }
    }

    public Map<String, List<String>> get(URI uri,
                                         Map<String, List<String>> requestHeaders) throws IOException {
        // make sure our args are valid
        if ((uri == null) || (requestHeaders == null))
            throw new IllegalArgumentException("Argument is null");

        // save our url once
        String url = uri.toString();

        // prepare our response
        Map<String, List<String>> res = new HashMap<String, List<String>>();

        // get the cookie
        String cookie = webkitCookieManager.getCookie(url);

        // return it
        if (cookie != null) {
            res.put("Cookie", Arrays.asList(cookie));
        }

        return res;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        HashMap<String, List<String>> generatedResponseHeaders = new HashMap<>();
        ArrayList<String> cookiesList = new ArrayList<>();
        for (Cookie c : cookies) {
            // toString correctly generates a normal cookie string
            cookiesList.add(c.toString());
        }
        generatedResponseHeaders.put("Set-Cookie", cookiesList);
        try {
            put(url.uri(), generatedResponseHeaders);
        } catch (IOException e) {
            Log.e(TAG, "Error adding cookies through okhttp", e);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList<Cookie> cookieArrayList = new ArrayList<>();
        try {
            Map<String, List<String>> cookieList = get(url.uri(), new HashMap<String, List<String>>());
            // Format here looks like: "Cookie":["cookie1=val1;cookie2=val2;"]
            for (List<String> ls : cookieList.values()) {
                for (String s : ls) {
                    String[] cookies = s.split(";");
                    for (String cookie : cookies) {
                        Cookie c = Cookie.parse(url, cookie);
                        cookieArrayList.add(c);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "error making cookie!", e);
        }
        return cookieArrayList;
    }

    @Override
    public void setAcceptCookie(boolean accept) {

    }

    @Override
    public boolean acceptCookie() {
        return false;
    }

    @Override
    public void setAcceptThirdPartyCookies(WebView webview, boolean accept) {

    }

    @Override
    public boolean acceptThirdPartyCookies(WebView webview) {
        return false;
    }

    @Override
    public void setCookie(String url, String value) {

    }

    @Override
    public void setCookie(String url, String value, @Nullable ValueCallback<Boolean> callback) {

    }

    @Override
    public String getCookie(String url) {
        return null;
    }

    @Override
    public void removeSessionCookie() {

    }

    @Override
    public void removeSessionCookies(@Nullable ValueCallback<Boolean> callback) {

    }

    @Override
    public void removeAllCookie() {

    }

    @Override
    public void removeAllCookies(@Nullable ValueCallback<Boolean> callback) {

    }

    @Override
    public boolean hasCookies() {
        return false;
    }

    @Override
    public void removeExpiredCookie() {

    }

    @Override
    public void flush() {

    }
}