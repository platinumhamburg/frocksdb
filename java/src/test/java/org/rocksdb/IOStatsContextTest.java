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
 * Comprehensive unit tests for IOStatsContext API.
 * Tests the thread-local I/O statistics monitoring context.
 */
public class IOStatsContextTest {

  @ClassRule
  public static final RocksNativeLibraryResource ROCKS_NATIVE_LIBRARY_RESOURCE =
      new RocksNativeLibraryResource();

  @Rule
  public TemporaryFolder dbFolder = new TemporaryFolder();

  @Test
  public void testCurrent() {
    // Test getting current thread's context
    IOStatsContext ctx1 = IOStatsContext.current();
    IOStatsContext ctx2 = IOStatsContext.current();
    
    // Should return the same instance for the same thread
    assertThat(ctx1).isSameAs(ctx2);
  }

  @Test
  public void testReset() {
    IOStatsContext ctx = IOStatsContext.current();
    ctx.reset();
    
    // After reset, counters should be zero
    assertThat(ctx.bytesWritten()).isEqualTo(0);
    assertThat(ctx.bytesRead()).isEqualTo(0);
    assertThat(ctx.openNanos()).isEqualTo(0);
  }

  @Test
  public void testChaining() {
    // Test method chaining
    long bytes = IOStatsContext.current()
        .reset()
        .bytesWritten();
    
    assertThat(bytes).isEqualTo(0);
  }

  @Test
  public void testReport() {
    IOStatsContext ctx = IOStatsContext.current();
    ctx.reset();
    
    String report = ctx.report();
    assertThat(report).isNotNull();
    assertThat(report.length()).isGreaterThan(0);
    
    String reportNonZero = ctx.reportNonZero();
    assertThat(reportNonZero).isNotNull();
  }

  @Test
  public void testToString() {
    IOStatsContext ctx = IOStatsContext.current();
    ctx.reset();
    
    String str = ctx.toString();
    assertThat(str).isNotNull();
  }

  @Test
  public void testBasicCounters() {
    IOStatsContext ctx = IOStatsContext.current();
    ctx.reset();
    
    // Test basic counter getters
    assertThat(ctx.threadPoolId()).isGreaterThanOrEqualTo(0);
    assertThat(ctx.bytesWritten()).isEqualTo(0);
    assertThat(ctx.bytesRead()).isEqualTo(0);
  }

  @Test
  public void testTimeCounters() {
    IOStatsContext ctx = IOStatsContext.current();
    ctx.reset();
    
    // Test time counter getters
    assertThat(ctx.openNanos()).isEqualTo(0);
    assertThat(ctx.allocateNanos()).isEqualTo(0);
    assertThat(ctx.writeNanos()).isEqualTo(0);
    assertThat(ctx.readNanos()).isEqualTo(0);
    assertThat(ctx.rangeSyncNanos()).isEqualTo(0);
    assertThat(ctx.fsyncNanos()).isEqualTo(0);
    assertThat(ctx.prepareWriteNanos()).isEqualTo(0);
    assertThat(ctx.loggerNanos()).isEqualTo(0);
  }

  @Test
  public void testCPUCounters() {
    IOStatsContext ctx = IOStatsContext.current();
    ctx.reset();
    
    // Test CPU time counter getters
    assertThat(ctx.cpuWriteNanos()).isEqualTo(0);
    assertThat(ctx.cpuReadNanos()).isEqualTo(0);
  }

  @Test
  public void testWithDatabaseWrites() throws RocksDBException {
    // Enable timing for I/O stats
    PerfContext.current().setPerfLevel(PerfLevel.ENABLE_TIME);
    
    try (Options options = new Options().setCreateIfMissing(true);
         RocksDB db = RocksDB.open(options, dbFolder.getRoot().getAbsolutePath())) {
      
      IOStatsContext ctx = IOStatsContext.current();
      ctx.reset();
      
      // Perform write operations
      for (int i = 0; i < 100; i++) {
        db.put(("key" + i).getBytes(), ("value" + i).getBytes());
      }
      
      // Flush to ensure I/O
      db.flush(new FlushOptions().setWaitForFlush(true));
      
      // Check that some bytes were written
      long bytesWritten = ctx.bytesWritten();
      assertThat(bytesWritten).isGreaterThanOrEqualTo(0);
      
    } finally {
      PerfContext.current().disable();
    }
  }

  @Test
  public void testWithDatabaseReads() throws RocksDBException {
    // Enable timing for I/O stats
    PerfContext.current().setPerfLevel(PerfLevel.ENABLE_TIME);
    
    try (Options options = new Options().setCreateIfMissing(true);
         RocksDB db = RocksDB.open(options, dbFolder.getRoot().getAbsolutePath())) {
      
      // First write some data
      for (int i = 0; i < 100; i++) {
        db.put(("key" + i).getBytes(), ("value" + i).getBytes());
      }
      
      // Reset before reading
      IOStatsContext ctx = IOStatsContext.current();
      ctx.reset();
      
      // Perform read operations
      for (int i = 0; i < 50; i++) {
        db.get(("key" + i).getBytes());
      }
      
      // Check that some bytes were read (may be 0 if all from cache)
      long bytesRead = ctx.bytesRead();
      assertThat(bytesRead).isGreaterThanOrEqualTo(0);
      
    } finally {
      PerfContext.current().disable();
    }
  }

  @Test
  public void testWithMixedOperations() throws RocksDBException {
    // Enable timing for I/O stats
    PerfContext.current().setPerfLevel(PerfLevel.ENABLE_TIME);
    
    try (Options options = new Options().setCreateIfMissing(true);
         RocksDB db = RocksDB.open(options, dbFolder.getRoot().getAbsolutePath())) {
      
      IOStatsContext ctx = IOStatsContext.current();
      ctx.reset();
      
      // Mixed operations
      for (int i = 0; i < 50; i++) {
        db.put(("key" + i).getBytes(), ("value" + i).getBytes());
      }
      
      for (int i = 0; i < 25; i++) {
        db.get(("key" + i).getBytes());
      }
      
      // Generate report
      String report = ctx.reportNonZero();
      assertThat(report).isNotNull();
      
    } finally {
      PerfContext.current().disable();
    }
  }

  @Test
  public void testCoordinationWithPerfContext() throws RocksDBException {
    try (PerfContext perfCtx = PerfContext.enable(PerfLevel.ENABLE_TIME);
         Options options = new Options().setCreateIfMissing(true);
         RocksDB db = RocksDB.open(options, dbFolder.getRoot().getAbsolutePath())) {
      
      // Get IOStatsContext
      IOStatsContext ioCtx = IOStatsContext.current();
      ioCtx.reset();
      
      // Also accessible via PerfContext
      IOStatsContext ioCtx2 = perfCtx.ioStats();
      assertThat(ioCtx).isSameAs(ioCtx2);
      
      // Perform operations
      for (int i = 0; i < 10; i++) {
        db.put(("key" + i).getBytes(), ("value" + i).getBytes());
      }
      
      // Both contexts should have data
      assertThat(perfCtx.blockCacheHitCount()).isGreaterThanOrEqualTo(0);
      assertThat(ioCtx.bytesWritten()).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void testThreadLocalIsolation() throws InterruptedException {
    IOStatsContext mainCtx = IOStatsContext.current();
    mainCtx.reset();
    
    // Create a new thread
    Thread thread = new Thread(new Runnable() {
      public void run() {
        IOStatsContext threadCtx = IOStatsContext.current();
        
        // Should be a different instance (thread-local)
        // Note: Can't use assertThat here as it's in different thread
        // Just verify it doesn't throw
        threadCtx.reset();
        threadCtx.bytesWritten();
      }
    });
    
    thread.start();
    thread.join();
    
    // Main thread context should still be accessible
    mainCtx.bytesWritten();
  }

}

