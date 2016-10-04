LOCAL_PATH := $(call my-dir)

LOCAL_MODULE := curve25519
LOCAL_SRC_FILES := ../libs/curve25519.so
include $(PREBUILT_SHARED_LIBRARY)
