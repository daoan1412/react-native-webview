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
#include "./ad_block_client.h"

#include <sys/types.h>
#include <assert.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

using std::string;

static AdBlockClient adBlockClient;

extern "C"
JNIEXPORT void JNICALL
Java_com_reactnativecommunity_webview_RNCWebViewManager_createAdblockServer(JNIEnv *env, jobject instance,
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
                                                                 jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);

    bool isAdsUrl = adBlockClient.matches(url, FONoFilterOption, "https://www.google.com.vn/");
    env->ReleaseStringUTFChars(url_, url);
    return static_cast<jboolean>(isAdsUrl);

}