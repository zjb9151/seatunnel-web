/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.integration.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public final class CodeGenerateUtils {

    private static final long START_TIMESTAMP = 1609430400000L;
    private static final long LOW_DIGIT_BIT = 5L;
    private static final long MIDDLE_BIT = 2L;
    private static final long MAX_LOW_DIGIT = ~(-1L << LOW_DIGIT_BIT);
    private static final long MIDDLE_LEFT = LOW_DIGIT_BIT;
    private static final long HIGH_DIGIT_LEFT = LOW_DIGIT_BIT + MIDDLE_BIT;
    private static final long SYSTEM_TIMESTAMP = System.currentTimeMillis();
    private static final long SYSTEM_NANOTIME = System.nanoTime();
    private static final int NANOS_PER_MS = 1_000_000;

    private final long machineHash;
    private long lowDigit = 0L;
    private long recordMillisecond = -1L;

    private static CodeGenerateUtils instance;

    private CodeGenerateUtils() throws CodeGenerateException {
        try {
            this.machineHash =
                    Math.abs(Objects.hash(InetAddress.getLocalHost().getHostName()))
                            % (2 << (MIDDLE_BIT - 1));
        } catch (UnknownHostException e) {
            throw new CodeGenerateException(e.getMessage());
        }
    }

    public static synchronized CodeGenerateUtils getInstance() throws CodeGenerateException {
        if (instance == null) {
            instance = new CodeGenerateUtils();
        }
        return instance;
    }

    public synchronized long genCode() throws CodeGenerateException {
        long nowMillisecond = systemMillisecond();
        if (nowMillisecond < recordMillisecond) {
            throw new CodeGenerateException("New code exception because time is set back.");
        }
        if (nowMillisecond == recordMillisecond) {
            lowDigit = (lowDigit + 1) & MAX_LOW_DIGIT;
            if (lowDigit == 0L) {
                while (nowMillisecond <= recordMillisecond) {
                    nowMillisecond = systemMillisecond();
                }
            }
        } else {
            lowDigit = 0L;
        }
        recordMillisecond = nowMillisecond;
        return (nowMillisecond - START_TIMESTAMP) << HIGH_DIGIT_LEFT
                | machineHash << MIDDLE_LEFT
                | lowDigit;
    }

    private long systemMillisecond() {
        return SYSTEM_TIMESTAMP + (System.nanoTime() - SYSTEM_NANOTIME) / NANOS_PER_MS;
    }

    public static class CodeGenerateException extends RuntimeException {
        public CodeGenerateException(String message) {
            super(message);
        }
    }
}
