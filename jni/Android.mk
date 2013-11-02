LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := inotify_module
LOCAL_SRC_FILES := inotify_module.cpp
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
