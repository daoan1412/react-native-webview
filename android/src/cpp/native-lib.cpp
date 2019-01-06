//
// Created by Dao An on 1/4/19.
//

#include <jni.h>
#include <algorithm>
#include <cerrno>
#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include <string>
#include <regex>
#include "./ad_block_client.h"

#include <sys/types.h>
#include <assert.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

using std::string;
using std::regex;

static AdBlockClient adBlockClient;

extern "C"
JNIEXPORT void JNICALL
Java_com_reactnativecommunity_webview_RNCWebViewManager_createAdblockServer(JNIEnv *env,
                                                                            jobject instance,
                                                                            jobject assetManager) {
    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    AAsset *file = AAssetManager_open(mgr, "an.dat", AASSET_MODE_BUFFER);
    size_t fileLength = AAsset_getLength(file);
    char *fileContent = new char[fileLength];
    AAsset_read(file, fileContent, fileLength);
    adBlockClient.deserialize(fileContent);
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_reactnativecommunity_webview_RNCWebViewManager_isAdsUrl(JNIEnv *env, jclass type,
                                                                 jstring url_, jstring host_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    const char *host = env->GetStringUTFChars(host_, 0);

    regex re_js("(.*)\\.js(\\?.+)?");
    regex re_css("(.*)\\.css(\\?.+)?");
    regex re_image("(.*)\\.(gif|png|jpe?g|bmp|ico)(\\?.+)?");
    regex re_font("(.*)\\.(ttf|woff)(\\?.+)?");
    regex re_html("(.*)\\.html?(\\?.+)?");
    FilterOption option;
    if (regex_match(url, re_js)) {
        option = FOScript;
    } else if (regex_match(url, re_css)) {
        option = FOStylesheet;
    } else if (regex_match(url, re_image)) {
        option = FOImage;
    } else if (regex_match(url, re_font)) {
        option = FOFont;
    } else if (regex_match(url, re_html)){
        option = FOSubdocument;
    } else {
        option = FONoFilterOption;
    }
    bool isAdsUrl = adBlockClient.matches(url, option, host);

    env->ReleaseStringUTFChars(url_, url);
    env->ReleaseStringUTFChars(host_, host);

    return static_cast<jboolean>(isAdsUrl);
}