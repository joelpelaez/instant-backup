/*
 *  This file is part of Inofity Java Interface
 *  inotify_module.cpp - inotify_module native (For Android only)
 *  Copyright (C) 2012  Joel Peláez Jorge <joelpelaez@gmail.com>
 *
 *  Inofity Java Interface is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Inofity Java Interface Control is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Inofity Java Interface.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <fcntl.h>
#include <sys/inotify.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <errno.h>

#include <android/log.h>

#define LOG_TAG "DarkSoft Inotify Module"

#define LOG_INFO(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static const char *class_inotify_name =
    "org/darksoft/android/nativelib/inotify/Inotify";
#if 0 // Unused classname
static const char *class_watcher_name =
"org/darksoft/android/nativelib/inotify/Inotify$Watcher";
#endif
static const char *class_event_name =
    "org/darksoft/android/nativelib/inotify/Inotify$Event";
static const char *class_file_descriptor = "java/io/FileDescriptor";

struct __list_fields_id
{
  jfieldID descriptor;
  jfieldID watcher;
  jfieldID name;
  jfieldID mask;
  jfieldID fd;
} fields_list;

struct __list_method_id
{

} methods_list;

static void
Inotify_nativeInotifyInit(JNIEnv *env, jobject obj)
{
  int fd = -1;
  LOG_INFO("Init Inotify Module");
  fd = inotify_init();

  if (fd == -1)
    {
      char *errormsg = (char *) malloc(sizeof(char) * 128);
      strerror_r(errno, errormsg, sizeof(char) * 128);
      LOG_INFO("Error in inotifyInit: %s", errormsg);
      free(errormsg);
      return;
    }

  env->SetIntField(obj, fields_list.descriptor, fd);
}

static jint
Inotify_nativeInotifyAddWatch(JNIEnv *env, jobject obj, jstring name,
    jlong mask)
{
  const char *file = env->GetStringUTFChars(name, 0);
  int fd = env->GetIntField(obj, fields_list.descriptor);
  int wd = inotify_add_watch(fd, file, mask);
  if (wd == -1)
    {
      char *errormsg = (char *) malloc(sizeof(char) * 128);
      strerror_r(errno, errormsg, sizeof(char) * 128);
      LOG_INFO("Error in inotifyAddWatch: %s", errormsg);
      free(errormsg);
    }
  return wd;
}

static void
Inotify_nativeInotifyRmWatch(JNIEnv *env, jobject obj, jint watcher)
{
  int fd = env->GetIntField(obj, fields_list.descriptor);
  inotify_rm_watch(fd, watcher);
}

static void
Inotify_nativeInotifyClose(JNIEnv *env, jobject obj)
{
  int fd = env->GetIntField(obj, fields_list.descriptor);
  close(fd);
}

static void
Inotify_nativeSetFileDescriptor(JNIEnv *env, jobject obj, jobject filed)
{
  int fd = env->GetIntField(obj, fields_list.descriptor);
  env->SetIntField(filed, fields_list.fd, fd);
}

static jobject
Inotify_nativeParseEvent(JNIEnv *env, jobject obj, jbyteArray byteArray)
{
  jclass clazz = env->FindClass(class_event_name);
  jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
  jobject obj_event = env->NewObject(clazz, constructor);
  jint wd = env->GetIntField(obj_event, fields_list.watcher);
  jboolean isCopy = JNI_FALSE;
  jbyte* array = env->GetByteArrayElements(byteArray, &isCopy);
  struct inotify_event *ev;
  ev = (struct inotify_event *) malloc(
      sizeof(struct inotify_event) + NAME_MAX + 1);
  memcpy(ev, array, sizeof(struct inotify_event) + NAME_MAX + 1);
  jstring name = NULL;
  if (ev->name)
    name = env->NewStringUTF(ev->name);
  env->SetObjectField(obj_event, fields_list.name, name);
  env->SetIntField(obj_event, fields_list.watcher, ev->wd);
  env->SetLongField(obj_event, fields_list.mask, ev->mask);
  free(ev);
  return obj_event;
}

static JNINativeMethod inotifyMethods[] =
  {
    { "nativeInotifyInit", "()V", (void *) Inotify_nativeInotifyInit },
    { "nativeInotifyAddWatch", "(Ljava/lang/String;J)I",
        (void *) Inotify_nativeInotifyAddWatch },
    { "nativeInotifyRmWatch", "(I)V", (void *) Inotify_nativeInotifyRmWatch },
    { "nativeInotifyClose", "()V", (void *) Inotify_nativeInotifyClose },
    { "nativeSetFileDescriptor", "(Ljava/io/FileDescriptor;)V",
        (void *) Inotify_nativeSetFileDescriptor },
    { "nativeParseEvent",
        "([B)Lorg/darksoft/android/nativelib/inotify/Inotify$Event;",
        (void *) Inotify_nativeParseEvent } };

jint
JNI_OnLoad(JavaVM *vm, void *reserved)
{
  LOG_INFO("In Inotify Module");
  JNIEnv *env;

  if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
    {
      LOG_INFO("In Inotify Module: Exit with -1");
      return -1;
    }
  LOG_INFO("In Inotify Module: FindClass");
  jclass clazz = env->FindClass(class_inotify_name);
  LOG_INFO("In Inotify Module: Registering Natives");
  env->RegisterNatives(clazz, inotifyMethods, sizeof(inotifyMethods));

  LOG_INFO("In Inotify Module: Getting Fields ID");
  jclass event = env->FindClass(class_event_name);
  fields_list.descriptor = env->GetFieldID(clazz, "mInotify", "I");
  fields_list.watcher = env->GetFieldID(event, "mWatch", "I");
  fields_list.mask = env->GetFieldID(event, "mMask", "J");
  fields_list.name = env->GetFieldID(event, "mName", "Ljava/lang/String;");
  jclass fdclass = env->FindClass(class_file_descriptor);
  fields_list.fd = env->GetFieldID(fdclass, "descriptor", "I");

  LOG_INFO("IN Inotify Module: Exit with Version %d", JNI_VERSION_1_4);

  return JNI_VERSION_1_4;
}
