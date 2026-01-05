// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).
//
// This file implements the "bridge" between Java and C++ and enables
// calling c++ ROCKSDB_NAMESPACE::IOStatsContext methods from Java side.

#include <jni.h>

#include "include/org_rocksdb_IOStatsContext.h"
#include "rocksdb/iostats_context.h"
#include "rocksjni/portal.h"

/*
 * Class:     org_rocksdb_IOStatsContext
 * Method:    reset
 * Signature: ()V
 */
void Java_org_rocksdb_IOStatsContext_resetNative(JNIEnv*, jclass) {
  ROCKSDB_NAMESPACE::get_iostats_context()->Reset();
}

/*
 * Class:     org_rocksdb_IOStatsContext
 * Method:    ioStatsContextToString
 * Signature: (Z)Ljava/lang/String;
 */
jstring Java_org_rocksdb_IOStatsContext_ioStatsContextToStringNative(
    JNIEnv* env, jclass, jboolean jexclude_zero_counters) {
  const bool exclude_zero_counters = jexclude_zero_counters == JNI_TRUE;
  const std::string str = ROCKSDB_NAMESPACE::get_iostats_context()->ToString(
      exclude_zero_counters);
  return ROCKSDB_NAMESPACE::JniUtil::toJavaString(env, &str);
}

// IO Stats counter getters

jlong Java_org_rocksdb_IOStatsContext_threadPoolIdNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->thread_pool_id);
}

jlong Java_org_rocksdb_IOStatsContext_bytesWrittenNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->bytes_written);
}

jlong Java_org_rocksdb_IOStatsContext_bytesReadNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->bytes_read);
}

jlong Java_org_rocksdb_IOStatsContext_openNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->open_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_allocateNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->allocate_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_writeNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->write_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_readNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->read_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_rangeSyncNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->range_sync_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_fsyncNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->fsync_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_prepareWriteNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->prepare_write_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_loggerNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->logger_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_cpuWriteNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->cpu_write_nanos);
}

jlong Java_org_rocksdb_IOStatsContext_cpuReadNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_iostats_context()->cpu_read_nanos);
}

