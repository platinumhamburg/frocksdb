// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

package org.rocksdb;

/**
 * Performance monitoring context for the current thread.
 * 
 * <p>Example usage:
 * <pre>{@code
 * try (PerfContext ctx = PerfContext.enable(PerfLevel.ENABLE_COUNT)) {
 *     db.put(key, value);
 *     System.out.println("Cache hits: " + ctx.blockCacheHitCount());
 * } // automatically disabled
 * }</pre>
 * 
 * <p>Manual usage:
 * <pre>{@code
 * PerfContext ctx = PerfContext.current();
 * ctx.setPerfLevel(PerfLevel.ENABLE_COUNT).reset();
 * 
 * db.put(key, value);
 * System.out.println("Cache hits: " + ctx.blockCacheHitCount());
 * 
 * ctx.disable();
 * }</pre>
 */
public class PerfContext implements AutoCloseable {

  // Thread-local instance
  private static final ThreadLocal<PerfContext> CURRENT = new ThreadLocal<PerfContext>() {
    @Override
    protected PerfContext initialValue() {
      return new PerfContext();
    }
  };

  private PerfLevel currentLevel = PerfLevel.DISABLE;

  // Private constructor - use current() to get instance
  private PerfContext() {}

  /**
   * Get the performance context for the current thread.
   *
   * @return the current thread's PerfContext
   */
  public static PerfContext current() {
    return CURRENT.get();
  }

  /**
   * Enable performance monitoring and return the context.
   * Convenience method for use with try-with-resources.
   *
   * @param level the performance level to enable
   * @return the current thread's PerfContext
   */
  public static PerfContext enable(PerfLevel level) {
    return current().setPerfLevel(level).reset();
  }

  /**
   * Set the performance level for the current thread.
   *
   * @param level the performance level
   * @return this context for chaining
   */
  public PerfContext setPerfLevel(PerfLevel level) {
    RocksDB.setPerfLevel(level);
    this.currentLevel = level;
    return this;
  }

  /**
   * Get the current performance level.
   *
   * @return the current performance level
   */
  public PerfLevel getPerfLevel() {
    return RocksDB.getPerfLevel();
  }

  /**
   * Reset all performance counters to zero.
   * Also resets I/O statistics for convenience.
   *
   * @return this context for chaining
   */
  public PerfContext reset() {
    resetNative();
    IOStatsContext.current().reset();
    return this;
  }

  /**
   * Disable performance monitoring for the current thread.
   *
   * @return this context for chaining
   */
  public PerfContext disable() {
    return setPerfLevel(PerfLevel.DISABLE);
  }

  /**
   * Check if performance monitoring is enabled.
   *
   * @return true if enabled (level is not DISABLE)
   */
  public boolean isEnabled() {
    return currentLevel != PerfLevel.DISABLE;
  }

  /**
   * Get I/O statistics context for the current thread.
   * This is a convenience method equivalent to IOStatsContext.current().
   *
   * @return the current thread's IOStatsContext
   */
  public IOStatsContext ioStats() {
    return IOStatsContext.current();
  }

  /**
   * Get a string representation of all performance counters.
   *
   * @return string representation
   */
  public String report() {
    return perfContextToString(false);
  }

  /**
   * Get a string representation excluding zero counters.
   *
   * @return string representation
   */
  public String reportNonZero() {
    return perfContextToString(true);
  }

  /**
   * Disable performance monitoring when used with try-with-resources.
   */
  @Override
  public void close() {
    disable();
  }

  // ====== Performance Counter Methods ======

  private static native void resetNative();

  private static native String perfContextToString(final boolean excludeZeroCounters);

  /**
   * Enable per level perf context and allocate storage for PerfContextByLevel.
   *
   * @return this context for chaining
   */
  public PerfContext enablePerLevelPerfContext() {
    enablePerLevelPerfContextNative();
    return this;
  }

  /**
   * Temporarily disable per level perf context by setting the flag to false.
   *
   * @return this context for chaining
   */
  public PerfContext disablePerLevelPerfContext() {
    disablePerLevelPerfContextNative();
    return this;
  }

  private static native void enablePerLevelPerfContextNative();
  private static native void disablePerLevelPerfContextNative();

  // ====== Performance Counter Getters ======

  public long userKeyComparisonCount() {
    return userKeyComparisonCountNative();
  }
  private static native long userKeyComparisonCountNative();
  public long blockCacheHitCount() { return blockCacheHitCountNative(); }
  public long blockReadCount() { return blockReadCountNative(); }
  public long blockReadByte() { return blockReadByteNative(); }
  public long blockReadTime() { return blockReadTimeNative(); }
  public long blockCacheIndexHitCount() { return blockCacheIndexHitCountNative(); }
  public long indexBlockReadCount() { return indexBlockReadCountNative(); }
  public long blockCacheFilterHitCount() { return blockCacheFilterHitCountNative(); }
  public long filterBlockReadCount() { return filterBlockReadCountNative(); }
  public long compressionDictBlockReadCount() { return compressionDictBlockReadCountNative(); }
  public long blockChecksumTime() { return blockChecksumTimeNative(); }
  public long blockDecompressTime() { return blockDecompressTimeNative(); }
  public long getReadBytes() { return getReadBytesNative(); }
  public long multigetReadBytes() { return multigetReadBytesNative(); }
  public long iterReadBytes() { return iterReadBytesNative(); }
  public long internalKeySkippedCount() { return internalKeySkippedCountNative(); }
  public long internalDeleteSkippedCount() { return internalDeleteSkippedCountNative(); }
  public long internalRecentSkippedCount() { return internalRecentSkippedCountNative(); }
  public long internalMergeCount() { return internalMergeCountNative(); }
  public long getSnapshotTime() { return getSnapshotTimeNative(); }
  public long getFromMemtableTime() { return getFromMemtableTimeNative(); }
  public long getFromMemtableCount() { return getFromMemtableCountNative(); }
  public long getPostProcessTime() { return getPostProcessTimeNative(); }
  public long getFromOutputFilesTime() { return getFromOutputFilesTimeNative(); }
  public long seekOnMemtableTime() { return seekOnMemtableTimeNative(); }
  public long seekOnMemtableCount() { return seekOnMemtableCountNative(); }
  public long nextOnMemtableCount() { return nextOnMemtableCountNative(); }
  public long prevOnMemtableCount() { return prevOnMemtableCountNative(); }
  public long seekChildSeekTime() { return seekChildSeekTimeNative(); }
  public long seekChildSeekCount() { return seekChildSeekCountNative(); }
  public long seekMinHeapTime() { return seekMinHeapTimeNative(); }
  public long seekMaxHeapTime() { return seekMaxHeapTimeNative(); }
  public long seekInternalSeekTime() { return seekInternalSeekTimeNative(); }
  public long findNextUserEntryTime() { return findNextUserEntryTimeNative(); }
  public long writeWalTime() { return writeWalTimeNative(); }
  public long writeMemtableTime() { return writeMemtableTimeNative(); }
  public long writeDelayTime() { return writeDelayTimeNative(); }
  public long writeSchedulingFlushesCompactionsTime() { return writeSchedulingFlushesCompactionsTimeNative(); }
  public long writePreAndPostProcessTime() { return writePreAndPostProcessTimeNative(); }
  public long writeThreadWaitNanos() { return writeThreadWaitNanosNative(); }
  public long dbMutexLockNanos() { return dbMutexLockNanosNative(); }
  public long dbConditionWaitNanos() { return dbConditionWaitNanosNative(); }
  public long mergeOperatorTimeNanos() { return mergeOperatorTimeNanosNative(); }
  public long readIndexBlockNanos() { return readIndexBlockNanosNative(); }
  public long readFilterBlockNanos() { return readFilterBlockNanosNative(); }
  public long newTableBlockIterNanos() { return newTableBlockIterNanosNative(); }
  public long newTableIteratorNanos() { return newTableIteratorNanosNative(); }
  public long blockSeekNanos() { return blockSeekNanosNative(); }
  public long findTableNanos() { return findTableNanosNative(); }
  public long bloomMemtableHitCount() { return bloomMemtableHitCountNative(); }
  public long bloomMemtableMissCount() { return bloomMemtableMissCountNative(); }
  public long bloomSstHitCount() { return bloomSstHitCountNative(); }
  public long bloomSstMissCount() { return bloomSstMissCountNative(); }
  public long keyLockWaitTime() { return keyLockWaitTimeNative(); }
  public long keyLockWaitCount() { return keyLockWaitCountNative(); }
  public long envNewSequentialFileNanos() { return envNewSequentialFileNanosNative(); }
  public long envNewRandomAccessFileNanos() { return envNewRandomAccessFileNanosNative(); }
  public long envNewWritableFileNanos() { return envNewWritableFileNanosNative(); }
  public long envReuseWritableFileNanos() { return envReuseWritableFileNanosNative(); }
  public long envNewRandomRwFileNanos() { return envNewRandomRwFileNanosNative(); }
  public long envNewDirectoryNanos() { return envNewDirectoryNanosNative(); }
  public long envFileExistsNanos() { return envFileExistsNanosNative(); }
  public long envGetChildrenNanos() { return envGetChildrenNanosNative(); }
  public long envGetChildrenFileAttributesNanos() { return envGetChildrenFileAttributesNanosNative(); }
  public long envDeleteFileNanos() { return envDeleteFileNanosNative(); }
  public long envCreateDirNanos() { return envCreateDirNanosNative(); }
  public long envCreateDirIfMissingNanos() { return envCreateDirIfMissingNanosNative(); }
  public long envDeleteDirNanos() { return envDeleteDirNanosNative(); }
  public long envGetFileSizeNanos() { return envGetFileSizeNanosNative(); }
  public long envGetFileModificationTimeNanos() { return envGetFileModificationTimeNanosNative(); }
  public long envRenameFileNanos() { return envRenameFileNanosNative(); }
  public long envLinkFileNanos() { return envLinkFileNanosNative(); }
  public long envLockFileNanos() { return envLockFileNanosNative(); }
  public long envUnlockFileNanos() { return envUnlockFileNanosNative(); }
  public long envNewLoggerNanos() { return envNewLoggerNanosNative(); }
  public long getCpuNanos() { return getCpuNanosNative(); }
  public long iterNextCpuNanos() { return iterNextCpuNanosNative(); }
  public long iterPrevCpuNanos() { return iterPrevCpuNanosNative(); }
  public long iterSeekCpuNanos() { return iterSeekCpuNanosNative(); }
  public long encryptDataNanos() { return encryptDataNanosNative(); }
  public long decryptDataNanos() { return decryptDataNanosNative(); }

  // Native method declarations
  private static native long blockCacheHitCountNative();
  private static native long blockReadCountNative();
  private static native long blockReadByteNative();
  private static native long blockReadTimeNative();
  private static native long blockCacheIndexHitCountNative();
  private static native long indexBlockReadCountNative();
  private static native long blockCacheFilterHitCountNative();
  private static native long filterBlockReadCountNative();
  private static native long compressionDictBlockReadCountNative();
  private static native long blockChecksumTimeNative();
  private static native long blockDecompressTimeNative();
  private static native long getReadBytesNative();
  private static native long multigetReadBytesNative();
  private static native long iterReadBytesNative();
  private static native long internalKeySkippedCountNative();
  private static native long internalDeleteSkippedCountNative();
  private static native long internalRecentSkippedCountNative();
  private static native long internalMergeCountNative();
  private static native long getSnapshotTimeNative();
  private static native long getFromMemtableTimeNative();
  private static native long getFromMemtableCountNative();
  private static native long getPostProcessTimeNative();
  private static native long getFromOutputFilesTimeNative();
  private static native long seekOnMemtableTimeNative();
  private static native long seekOnMemtableCountNative();
  private static native long nextOnMemtableCountNative();
  private static native long prevOnMemtableCountNative();
  private static native long seekChildSeekTimeNative();
  private static native long seekChildSeekCountNative();
  private static native long seekMinHeapTimeNative();
  private static native long seekMaxHeapTimeNative();
  private static native long seekInternalSeekTimeNative();
  private static native long findNextUserEntryTimeNative();
  private static native long writeWalTimeNative();
  private static native long writeMemtableTimeNative();
  private static native long writeDelayTimeNative();
  private static native long writeSchedulingFlushesCompactionsTimeNative();
  private static native long writePreAndPostProcessTimeNative();
  private static native long writeThreadWaitNanosNative();
  private static native long dbMutexLockNanosNative();
  private static native long dbConditionWaitNanosNative();
  private static native long mergeOperatorTimeNanosNative();
  private static native long readIndexBlockNanosNative();
  private static native long readFilterBlockNanosNative();
  private static native long newTableBlockIterNanosNative();
  private static native long newTableIteratorNanosNative();
  private static native long blockSeekNanosNative();
  private static native long findTableNanosNative();
  private static native long bloomMemtableHitCountNative();
  private static native long bloomMemtableMissCountNative();
  private static native long bloomSstHitCountNative();
  private static native long bloomSstMissCountNative();
  private static native long keyLockWaitTimeNative();
  private static native long keyLockWaitCountNative();
  private static native long envNewSequentialFileNanosNative();
  private static native long envNewRandomAccessFileNanosNative();
  private static native long envNewWritableFileNanosNative();
  private static native long envReuseWritableFileNanosNative();
  private static native long envNewRandomRwFileNanosNative();
  private static native long envNewDirectoryNanosNative();
  private static native long envFileExistsNanosNative();
  private static native long envGetChildrenNanosNative();
  private static native long envGetChildrenFileAttributesNanosNative();
  private static native long envDeleteFileNanosNative();
  private static native long envCreateDirNanosNative();
  private static native long envCreateDirIfMissingNanosNative();
  private static native long envDeleteDirNanosNative();
  private static native long envGetFileSizeNanosNative();
  private static native long envGetFileModificationTimeNanosNative();
  private static native long envRenameFileNanosNative();
  private static native long envLinkFileNanosNative();
  private static native long envLockFileNanosNative();
  private static native long envUnlockFileNanosNative();
  private static native long envNewLoggerNanosNative();
  private static native long getCpuNanosNative();
  private static native long iterNextCpuNanosNative();
  private static native long iterPrevCpuNanosNative();
  private static native long iterSeekCpuNanosNative();
  private static native long encryptDataNanosNative();
  private static native long decryptDataNanosNative();
}

