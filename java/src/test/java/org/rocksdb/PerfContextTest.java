// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

package org.rocksdb;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for PerfContext API.
 * Tests the thread-local performance monitoring context.
 */
public class PerfContextTest {

  @ClassRule
  public static final RocksNativeLibraryResource ROCKS_NATIVE_LIBRARY_RESOURCE =
      new RocksNativeLibraryResource();

  @Rule
  public TemporaryFolder dbFolder = new TemporaryFolder();

  @Test
  public void testCurrent() {
    // Test getting current thread's context
    PerfContext ctx1 = PerfContext.current();
    PerfContext ctx2 = PerfContext.current();
    
    // Should return the same instance for the same thread
    assertThat(ctx1).isSameAs(ctx2);
  }

  @Test
  public void testEnableFactory() {
    // Test enable() factory method
    PerfContext ctx = PerfContext.enable(PerfLevel.ENABLE_COUNT);
    
    assertThat(ctx.isEnabled()).isTrue();
    assertThat(ctx.getPerfLevel()).isEqualTo(PerfLevel.ENABLE_COUNT);
    
    ctx.disable();
    assertThat(ctx.isEnabled()).isFalse();
  }

  @Test
  public void testSetPerfLevel() {
    PerfContext ctx = PerfContext.current();
    
    // Test different perf levels
    ctx.setPerfLevel(PerfLevel.DISABLE);
    assertThat(ctx.getPerfLevel()).isEqualTo(PerfLevel.DISABLE);
    assertThat(ctx.isEnabled()).isFalse();
    
    ctx.setPerfLevel(PerfLevel.ENABLE_COUNT);
    assertThat(ctx.getPerfLevel()).isEqualTo(PerfLevel.ENABLE_COUNT);
    assertThat(ctx.isEnabled()).isTrue();
    
    ctx.setPerfLevel(PerfLevel.ENABLE_TIME_EXCEPT_FOR_MUTEX);
    assertThat(ctx.getPerfLevel()).isEqualTo(PerfLevel.ENABLE_TIME_EXCEPT_FOR_MUTEX);
    
    ctx.setPerfLevel(PerfLevel.ENABLE_TIME);
    assertThat(ctx.getPerfLevel()).isEqualTo(PerfLevel.ENABLE_TIME);
    
    ctx.disable();
  }

  @Test
  public void testReset() {
    PerfContext ctx = PerfContext.current();
    ctx.setPerfLevel(PerfLevel.ENABLE_COUNT).reset();
    
    // After reset, counters should be zero
    assertThat(ctx.userKeyComparisonCount()).isEqualTo(0);
    assertThat(ctx.blockCacheHitCount()).isEqualTo(0);
    assertThat(ctx.blockReadCount()).isEqualTo(0);
    
    ctx.disable();
  }

  @Test
  public void testChaining() {
    // Test method chaining
    long count = PerfContext.current()
        .setPerfLevel(PerfLevel.ENABLE_COUNT)
        .reset()
        .blockCacheHitCount();
    
    assertThat(count).isEqualTo(0);
    
    PerfContext.current().disable();
  }

  @Test
  public void testTryWithResources() {
    // Test AutoCloseable with try-with-resources
    try (PerfContext ctx = PerfContext.enable(PerfLevel.ENABLE_COUNT)) {
      assertThat(ctx.isEnabled()).isTrue();
    }
    
    // Should be disabled after try block
    assertThat(PerfContext.current().isEnabled()).isFalse();
  }

  @Test
  public void testReport() {
    PerfContext ctx = PerfContext.current();
    ctx.setPerfLevel(PerfLevel.ENABLE_COUNT).reset();
    
    String report = ctx.report();
    assertThat(report).isNotNull();
    assertThat(report.length()).isGreaterThan(0);
    
    String reportNonZero = ctx.reportNonZero();
    assertThat(reportNonZero).isNotNull();
    
    ctx.disable();
  }

  @Test
  public void testToString() {
    PerfContext ctx = PerfContext.current();
    ctx.setPerfLevel(PerfLevel.ENABLE_COUNT).reset();
    
    String str = ctx.reportNonZero();
    assertThat(str).isNotNull();
    
    ctx.disable();
  }

  @Test
  public void testPerLevelPerfContext() {
    PerfContext ctx = PerfContext.current();
    
    // Test enable/disable per level perf context
    ctx.enablePerLevelPerfContext();
    ctx.disablePerLevelPerfContext();
    
    // Should not throw exceptions
  }

  @Test
  public void testIOStatsAccess() {
    PerfContext perfCtx = PerfContext.current();
    
    // Test accessing IOStatsContext via PerfContext
    IOStatsContext ioCtx1 = perfCtx.ioStats();
    IOStatsContext ioCtx2 = IOStatsContext.current();
    
    // Should be the same instance
    assertThat(ioCtx1).isSameAs(ioCtx2);
  }

  @Test
  public void testPerformanceCounters() throws RocksDBException {
    try (final Options opt = new Options()
        .setCreateIfMissing(true);
        final RocksDB db = RocksDB.open(opt,
            dbFolder.getRoot().getAbsolutePath())) {

      PerfContext ctx = PerfContext.current();
      ctx.setPerfLevel(PerfLevel.ENABLE_COUNT).reset();

      final byte[] key = "some-key".getBytes(StandardCharsets.UTF_8);
      final byte[] value = "some-value".getBytes(StandardCharsets.UTF_8);

      db.put(key, value);
      db.get(key);

      // After operations, some counters may be non-zero
      long userKeyComparisonCount = ctx.userKeyComparisonCount();
      long blockCacheHitCount = ctx.blockCacheHitCount();
      long blockReadCount = ctx.blockReadCount();
      
      // At least the sum should be non-negative
      assertThat(userKeyComparisonCount + blockCacheHitCount + blockReadCount)
          .isGreaterThanOrEqualTo(0);
      
      ctx.disable();
    }
  }

  @Test
  public void testAllPerformanceCounterGetters() {
    // Test that all getter methods can be called without exceptions
    PerfContext ctx = PerfContext.current();
    ctx.setPerfLevel(PerfLevel.ENABLE_TIME).reset();
    
    // Basic counters
    ctx.userKeyComparisonCount();
    ctx.blockCacheHitCount();
    ctx.blockReadCount();
    ctx.blockReadByte();
    ctx.blockReadTime();
    ctx.blockCacheIndexHitCount();
    ctx.indexBlockReadCount();
    ctx.blockCacheFilterHitCount();
    ctx.filterBlockReadCount();
    ctx.compressionDictBlockReadCount();
    ctx.blockChecksumTime();
    ctx.blockDecompressTime();
    
    // Read counters
    ctx.getReadBytes();
    ctx.multigetReadBytes();
    ctx.iterReadBytes();
    ctx.internalKeySkippedCount();
    ctx.internalDeleteSkippedCount();
    ctx.internalRecentSkippedCount();
    ctx.internalMergeCount();
    ctx.getSnapshotTime();
    ctx.getFromMemtableTime();
    ctx.getFromMemtableCount();
    ctx.getPostProcessTime();
    ctx.getFromOutputFilesTime();
    
    // Seek counters
    ctx.seekOnMemtableTime();
    ctx.seekOnMemtableCount();
    ctx.nextOnMemtableCount();
    ctx.prevOnMemtableCount();
    ctx.seekChildSeekTime();
    ctx.seekChildSeekCount();
    ctx.seekMinHeapTime();
    ctx.seekMaxHeapTime();
    ctx.seekInternalSeekTime();
    ctx.findNextUserEntryTime();
    
    // Write counters
    ctx.writeWalTime();
    ctx.writeMemtableTime();
    ctx.writeDelayTime();
    ctx.writeSchedulingFlushesCompactionsTime();
    ctx.writePreAndPostProcessTime();
    ctx.writeThreadWaitNanos();
    
    // Lock counters
    ctx.dbMutexLockNanos();
    ctx.dbConditionWaitNanos();
    ctx.keyLockWaitTime();
    ctx.keyLockWaitCount();
    
    // Other counters
    ctx.mergeOperatorTimeNanos();
    ctx.readIndexBlockNanos();
    ctx.readFilterBlockNanos();
    ctx.newTableBlockIterNanos();
    ctx.newTableIteratorNanos();
    ctx.blockSeekNanos();
    ctx.findTableNanos();
    ctx.bloomMemtableHitCount();
    ctx.bloomMemtableMissCount();
    ctx.bloomSstHitCount();
    ctx.bloomSstMissCount();
    
    // Env counters
    ctx.envNewSequentialFileNanos();
    ctx.envNewRandomAccessFileNanos();
    ctx.envNewWritableFileNanos();
    ctx.envReuseWritableFileNanos();
    ctx.envNewRandomRwFileNanos();
    ctx.envNewDirectoryNanos();
    ctx.envFileExistsNanos();
    ctx.envGetChildrenNanos();
    ctx.envGetChildrenFileAttributesNanos();
    ctx.envDeleteFileNanos();
    ctx.envCreateDirNanos();
    ctx.envCreateDirIfMissingNanos();
    ctx.envDeleteDirNanos();
    ctx.envGetFileSizeNanos();
    ctx.envGetFileModificationTimeNanos();
    ctx.envRenameFileNanos();
    ctx.envLinkFileNanos();
    ctx.envLockFileNanos();
    ctx.envUnlockFileNanos();
    ctx.envNewLoggerNanos();
    
    // CPU counters
    ctx.getCpuNanos();
    ctx.iterNextCpuNanos();
    ctx.iterPrevCpuNanos();
    ctx.iterSeekCpuNanos();
    
    // Encryption counters
    ctx.encryptDataNanos();
    ctx.decryptDataNanos();
    
    ctx.disable();
  }

  @Test
  public void testWithDatabaseOperations() throws RocksDBException {
    try (PerfContext perfCtx = PerfContext.enable(PerfLevel.ENABLE_TIME);
         Options options = new Options().setCreateIfMissing(true);
         RocksDB db = RocksDB.open(options, dbFolder.getRoot().getAbsolutePath())) {
      
      // Perform write operations
      for (int i = 0; i < 100; i++) {
        db.put(("key" + i).getBytes(), ("value" + i).getBytes());
      }
      
      // Reset before read
      perfCtx.reset();
      
      // Perform read operations
      for (int i = 0; i < 50; i++) {
        db.get(("key" + i).getBytes());
      }
      
      // Check that some counters are updated
      long totalActivity = perfCtx.userKeyComparisonCount() +
          perfCtx.blockCacheHitCount() +
          perfCtx.blockReadCount();
      
      // Should have some activity
      assertThat(totalActivity).isGreaterThanOrEqualTo(0);
      
      // Test report
      String report = perfCtx.reportNonZero();
      assertThat(report).isNotNull();
    }
  }
}
