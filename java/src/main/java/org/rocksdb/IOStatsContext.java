// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

package org.rocksdb;

/**
 * I/O statistics context for the current thread.
 * 
 * <p>Example usage:
 * <pre>{@code
 * try (IOStatsContext ioCtx = IOStatsContext.current().reset()) {
 *     db.put(key, value);
 *     System.out.println("Bytes written: " + ioCtx.bytesWritten());
 *     System.out.println("Write throughput: " + ioCtx.writeThroughputMBps() + " MB/s");
 * }
 * }</pre>
 * 
 * <p>IOStatsContext is thread-local and tracks I/O operations for the current thread.
 * It works in conjunction with PerfContext to provide comprehensive performance monitoring.
 */
public class IOStatsContext {

  // Thread-local instance
  private static final ThreadLocal<IOStatsContext> CURRENT = new ThreadLocal<IOStatsContext>() {
    @Override
    protected IOStatsContext initialValue() {
      return new IOStatsContext();
    }
  };

  // Private constructor - use current() to get instance
  private IOStatsContext() {}

  /**
   * Get the I/O statistics context for the current thread.
   *
   * @return the current thread's IOStatsContext
   */
  public static IOStatsContext current() {
    return CURRENT.get();
  }

  /**
   * Reset all I/O statistics counters to zero.
   *
   * @return this context for chaining
   */
  public IOStatsContext reset() {
    resetNative();
    return this;
  }

  /**
   * Get a string representation of all I/O statistics counters.
   *
   * @return string representation
   */
  public String report() {
    return ioStatsContextToStringNative(false);
  }

  /**
   * Get a string representation excluding zero counters.
   *
   * @return string representation
   */
  public String reportNonZero() {
    return ioStatsContextToStringNative(true);
  }

  private static native void resetNative();
  private static native String ioStatsContextToStringNative(final boolean excludeZeroCounters);

  // ====== I/O Statistics Counter Methods ======

  /**
   * Get the thread pool id.
   *
   * @return the thread pool id
   */
  public long threadPoolId() {
    return threadPoolIdNative();
  }

  /**
   * Get number of bytes that has been written.
   *
   * @return number of bytes written
   */
  public long bytesWritten() {
    return bytesWrittenNative();
  }

  /**
   * Get number of bytes that has been read.
   *
   * @return number of bytes read
   */
  public long bytesRead() {
    return bytesReadNative();
  }

  /**
   * Get time spent in open() and fopen().
   *
   * @return time in nanoseconds
   */
  public long openNanos() {
    return openNanosNative();
  }

  /**
   * Get time spent in fallocate().
   *
   * @return time in nanoseconds
   */
  public long allocateNanos() {
    return allocateNanosNative();
  }

  /**
   * Get time spent in write() and pwrite().
   *
   * @return time in nanoseconds
   */
  public long writeNanos() {
    return writeNanosNative();
  }

  /**
   * Get time spent in read() and pread().
   *
   * @return time in nanoseconds
   */
  public long readNanos() {
    return readNanosNative();
  }

  /**
   * Get time spent in sync_file_range().
   *
   * @return time in nanoseconds
   */
  public long rangeSyncNanos() {
    return rangeSyncNanosNative();
  }

  /**
   * Get time spent in fsync.
   *
   * @return time in nanoseconds
   */
  public long fsyncNanos() {
    return fsyncNanosNative();
  }

  /**
   * Get time spent in preparing write (fallocate etc).
   *
   * @return time in nanoseconds
   */
  public long prepareWriteNanos() {
    return prepareWriteNanosNative();
  }

  /**
   * Get time spent in Logger::Logv().
   *
   * @return time in nanoseconds
   */
  public long loggerNanos() {
    return loggerNanosNative();
  }

  /**
   * Get CPU time spent in write() and pwrite().
   *
   * @return time in nanoseconds
   */
  public long cpuWriteNanos() {
    return cpuWriteNanosNative();
  }

  /**
   * Get CPU time spent in read() and pread().
   *
   * @return time in nanoseconds
   */
  public long cpuReadNanos() {
    return cpuReadNanosNative();
  }


  @Override
  public String toString() {
    return reportNonZero();
  }

  // Native method declarations
  private static native long threadPoolIdNative();
  private static native long bytesWrittenNative();
  private static native long bytesReadNative();
  private static native long openNanosNative();
  private static native long allocateNanosNative();
  private static native long writeNanosNative();
  private static native long readNanosNative();
  private static native long rangeSyncNanosNative();
  private static native long fsyncNanosNative();
  private static native long prepareWriteNanosNative();
  private static native long loggerNanosNative();
  private static native long cpuWriteNanosNative();
  private static native long cpuReadNanosNative();
}

