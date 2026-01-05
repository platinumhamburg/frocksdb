// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).
//
// This file implements the "bridge" between Java and C++ and enables
// calling c++ ROCKSDB_NAMESPACE::PerfContext methods from Java side.

#include <jni.h>

#include "include/org_rocksdb_PerfContext.h"
#include "rocksdb/perf_context.h"
#include "rocksjni/portal.h"

/*
 * Class:     org_rocksdb_PerfContext
 * Method:    resetNative
 * Signature: ()V
 */
void Java_org_rocksdb_PerfContext_resetNative(JNIEnv*, jclass) {
  ROCKSDB_NAMESPACE::get_perf_context()->Reset();
}

/*
 * Class:     org_rocksdb_PerfContext
 * Method:    perfContextToString
 * Signature: (Z)Ljava/lang/String;
 */
jstring Java_org_rocksdb_PerfContext_perfContextToString(
    JNIEnv* env, jclass, jboolean jexclude_zero_counters) {
  const bool exclude_zero_counters = jexclude_zero_counters == JNI_TRUE;
  const std::string str =
      ROCKSDB_NAMESPACE::get_perf_context()->ToString(exclude_zero_counters);
  return ROCKSDB_NAMESPACE::JniUtil::toJavaString(env, &str);
}

/*
 * Class:     org_rocksdb_PerfContext
 * Method:    enablePerLevelPerfContextNative
 * Signature: ()V
 */
void Java_org_rocksdb_PerfContext_enablePerLevelPerfContextNative(JNIEnv*, jclass) {
  ROCKSDB_NAMESPACE::get_perf_context()->EnablePerLevelPerfContext();
}

/*
 * Class:     org_rocksdb_PerfContext
 * Method:    disablePerLevelPerfContextNative
 * Signature: ()V
 */
void Java_org_rocksdb_PerfContext_disablePerLevelPerfContextNative(JNIEnv*, jclass) {
  ROCKSDB_NAMESPACE::get_perf_context()->DisablePerLevelPerfContext();
}

// Performance counter getters

jlong Java_org_rocksdb_PerfContext_userKeyComparisonCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->user_key_comparison_count);
}

jlong Java_org_rocksdb_PerfContext_blockCacheHitCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_cache_hit_count);
}

jlong Java_org_rocksdb_PerfContext_blockReadCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_read_count);
}

jlong Java_org_rocksdb_PerfContext_blockReadByteNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_read_byte);
}

jlong Java_org_rocksdb_PerfContext_blockReadTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_read_time);
}

jlong Java_org_rocksdb_PerfContext_blockCacheIndexHitCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_cache_index_hit_count);
}

jlong Java_org_rocksdb_PerfContext_indexBlockReadCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->index_block_read_count);
}

jlong Java_org_rocksdb_PerfContext_blockCacheFilterHitCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_cache_filter_hit_count);
}

jlong Java_org_rocksdb_PerfContext_filterBlockReadCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->filter_block_read_count);
}

jlong Java_org_rocksdb_PerfContext_compressionDictBlockReadCountNative(JNIEnv*,
                                                                 jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->compression_dict_block_read_count);
}

jlong Java_org_rocksdb_PerfContext_blockChecksumTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_checksum_time);
}

jlong Java_org_rocksdb_PerfContext_blockDecompressTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_decompress_time);
}

jlong Java_org_rocksdb_PerfContext_getReadBytesNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_read_bytes);
}

jlong Java_org_rocksdb_PerfContext_multigetReadBytesNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->multiget_read_bytes);
}

jlong Java_org_rocksdb_PerfContext_iterReadBytesNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->iter_read_bytes);
}

jlong Java_org_rocksdb_PerfContext_internalKeySkippedCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->internal_key_skipped_count);
}

jlong Java_org_rocksdb_PerfContext_internalDeleteSkippedCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->internal_delete_skipped_count);
}

jlong Java_org_rocksdb_PerfContext_internalRecentSkippedCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->internal_recent_skipped_count);
}

jlong Java_org_rocksdb_PerfContext_internalMergeCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->internal_merge_count);
}

jlong Java_org_rocksdb_PerfContext_getSnapshotTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_snapshot_time);
}

jlong Java_org_rocksdb_PerfContext_getFromMemtableTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_from_memtable_time);
}

jlong Java_org_rocksdb_PerfContext_getFromMemtableCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_from_memtable_count);
}

jlong Java_org_rocksdb_PerfContext_getPostProcessTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_post_process_time);
}

jlong Java_org_rocksdb_PerfContext_getFromOutputFilesTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_from_output_files_time);
}

jlong Java_org_rocksdb_PerfContext_seekOnMemtableTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_on_memtable_time);
}

jlong Java_org_rocksdb_PerfContext_seekOnMemtableCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_on_memtable_count);
}

jlong Java_org_rocksdb_PerfContext_nextOnMemtableCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->next_on_memtable_count);
}

jlong Java_org_rocksdb_PerfContext_prevOnMemtableCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->prev_on_memtable_count);
}

jlong Java_org_rocksdb_PerfContext_seekChildSeekTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_child_seek_time);
}

jlong Java_org_rocksdb_PerfContext_seekChildSeekCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_child_seek_count);
}

jlong Java_org_rocksdb_PerfContext_seekMinHeapTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_min_heap_time);
}

jlong Java_org_rocksdb_PerfContext_seekMaxHeapTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_max_heap_time);
}

jlong Java_org_rocksdb_PerfContext_seekInternalSeekTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->seek_internal_seek_time);
}

jlong Java_org_rocksdb_PerfContext_findNextUserEntryTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->find_next_user_entry_time);
}

jlong Java_org_rocksdb_PerfContext_writeWalTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->write_wal_time);
}

jlong Java_org_rocksdb_PerfContext_writeMemtableTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->write_memtable_time);
}

jlong Java_org_rocksdb_PerfContext_writeDelayTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->write_delay_time);
}

jlong Java_org_rocksdb_PerfContext_writeSchedulingFlushesCompactionsTimeNative(
    JNIEnv*, jclass) {
  return static_cast<jlong>(ROCKSDB_NAMESPACE::get_perf_context()
                                ->write_scheduling_flushes_compactions_time);
}

jlong Java_org_rocksdb_PerfContext_writePreAndPostProcessTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->write_pre_and_post_process_time);
}

jlong Java_org_rocksdb_PerfContext_writeThreadWaitNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->write_thread_wait_nanos);
}

jlong Java_org_rocksdb_PerfContext_dbMutexLockNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->db_mutex_lock_nanos);
}

jlong Java_org_rocksdb_PerfContext_dbConditionWaitNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->db_condition_wait_nanos);
}

jlong Java_org_rocksdb_PerfContext_mergeOperatorTimeNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->merge_operator_time_nanos);
}

jlong Java_org_rocksdb_PerfContext_readIndexBlockNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->read_index_block_nanos);
}

jlong Java_org_rocksdb_PerfContext_readFilterBlockNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->read_filter_block_nanos);
}

jlong Java_org_rocksdb_PerfContext_newTableBlockIterNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->new_table_block_iter_nanos);
}

jlong Java_org_rocksdb_PerfContext_newTableIteratorNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->new_table_iterator_nanos);
}

jlong Java_org_rocksdb_PerfContext_blockSeekNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->block_seek_nanos);
}

jlong Java_org_rocksdb_PerfContext_findTableNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->find_table_nanos);
}

jlong Java_org_rocksdb_PerfContext_bloomMemtableHitCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->bloom_memtable_hit_count);
}

jlong Java_org_rocksdb_PerfContext_bloomMemtableMissCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->bloom_memtable_miss_count);
}

jlong Java_org_rocksdb_PerfContext_bloomSstHitCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->bloom_sst_hit_count);
}

jlong Java_org_rocksdb_PerfContext_bloomSstMissCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->bloom_sst_miss_count);
}

jlong Java_org_rocksdb_PerfContext_keyLockWaitTimeNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->key_lock_wait_time);
}

jlong Java_org_rocksdb_PerfContext_keyLockWaitCountNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->key_lock_wait_count);
}

jlong Java_org_rocksdb_PerfContext_envNewSequentialFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_new_sequential_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envNewRandomAccessFileNanosNative(JNIEnv*,
                                                               jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_new_random_access_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envNewWritableFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_new_writable_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envReuseWritableFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_reuse_writable_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envNewRandomRwFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_new_random_rw_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envNewDirectoryNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_new_directory_nanos);
}

jlong Java_org_rocksdb_PerfContext_envFileExistsNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_file_exists_nanos);
}

jlong Java_org_rocksdb_PerfContext_envGetChildrenNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_get_children_nanos);
}

jlong Java_org_rocksdb_PerfContext_envGetChildrenFileAttributesNanosNative(JNIEnv*,
                                                                     jclass) {
  return static_cast<jlong>(ROCKSDB_NAMESPACE::get_perf_context()
                                ->env_get_children_file_attributes_nanos);
}

jlong Java_org_rocksdb_PerfContext_envDeleteFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_delete_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envCreateDirNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_create_dir_nanos);
}

jlong Java_org_rocksdb_PerfContext_envCreateDirIfMissingNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_create_dir_if_missing_nanos);
}

jlong Java_org_rocksdb_PerfContext_envDeleteDirNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_delete_dir_nanos);
}

jlong Java_org_rocksdb_PerfContext_envGetFileSizeNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_get_file_size_nanos);
}

jlong Java_org_rocksdb_PerfContext_envGetFileModificationTimeNanosNative(JNIEnv*,
                                                                   jclass) {
  return static_cast<jlong>(ROCKSDB_NAMESPACE::get_perf_context()
                                ->env_get_file_modification_time_nanos);
}

jlong Java_org_rocksdb_PerfContext_envRenameFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_rename_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envLinkFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_link_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envLockFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_lock_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envUnlockFileNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_unlock_file_nanos);
}

jlong Java_org_rocksdb_PerfContext_envNewLoggerNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->env_new_logger_nanos);
}

jlong Java_org_rocksdb_PerfContext_getCpuNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->get_cpu_nanos);
}

jlong Java_org_rocksdb_PerfContext_iterNextCpuNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->iter_next_cpu_nanos);
}

jlong Java_org_rocksdb_PerfContext_iterPrevCpuNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->iter_prev_cpu_nanos);
}

jlong Java_org_rocksdb_PerfContext_iterSeekCpuNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->iter_seek_cpu_nanos);
}

jlong Java_org_rocksdb_PerfContext_encryptDataNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->encrypt_data_nanos);
}

jlong Java_org_rocksdb_PerfContext_decryptDataNanosNative(JNIEnv*, jclass) {
  return static_cast<jlong>(
      ROCKSDB_NAMESPACE::get_perf_context()->decrypt_data_nanos);
}
