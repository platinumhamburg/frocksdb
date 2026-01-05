// Copyright (c) 2011-present, Facebook, Inc.  All rights reserved.
//  This source code is licensed under both the GPLv2 (found in the
//  COPYING file in the root directory) and Apache 2.0 License
//  (found in the LICENSE.Apache file in the root directory).

package org.rocksdb;

/**
 * How much perf stats to collect. Affects perf_context and iostats_context.
 */
public enum PerfLevel {
  /**
   * Unknown setting
   */
  UNINITIALIZED((byte) 0x0),
  
  /**
   * Disable perf stats
   */
  DISABLE((byte) 0x1),
  
  /**
   * Enable only count stats
   */
  ENABLE_COUNT((byte) 0x2),
  
  /**
   * Other than count stats, also enable time stats except for mutexes
   */
  ENABLE_TIME_EXCEPT_FOR_MUTEX((byte) 0x3),
  
  /**
   * Other than time, also measure CPU time counters. Still don't measure
   * time (neither wall time nor CPU time) for mutexes.
   */
  ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX((byte) 0x4),
  
  /**
   * Enable count and time stats
   */
  ENABLE_TIME((byte) 0x5),
  
  /**
   * N.B. Must always be the last value!
   */
  OUT_OF_BOUNDS((byte) 0x6);

  private final byte value;

  PerfLevel(final byte value) {
    this.value = value;
  }

  /**
   * Get the internal representation value.
   *
   * @return the internal representation value
   */
  public byte getValue() {
    return value;
  }

  /**
   * Get the PerfLevel from the internal representation value.
   *
   * @param value the internal representation value
   * @return the PerfLevel
   * @throws IllegalArgumentException if the value does not match a PerfLevel
   */
  public static PerfLevel getPerfLevel(final byte value) {
    for (final PerfLevel perfLevel : PerfLevel.values()) {
      if (perfLevel.value == value) {
        return perfLevel;
      }
    }
    throw new IllegalArgumentException(
        "Illegal value provided for PerfLevel: " + value);
  }
}

