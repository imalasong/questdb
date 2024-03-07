/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.std.str;

import io.questdb.std.Mutable;
import io.questdb.std.Unsafe;

/**
 * An immutable flyweight for a UTF-16 string stored in native memory.
 */
public class DirectString extends AbstractCharSequence implements DirectCharSequence, Mutable {
    private long hi;
    private int len;
    private long lo;

    @Override
    public char charAt(int index) {
        return Unsafe.getUnsafe().getChar(lo + ((long) index << 1));
    }

    @Override
    public void clear() {
        hi = lo = 0;
        len = 0;
    }

    @Override
    public int hashCode() {
        if (lo == hi) {
            return 0;
        }

        int h = 0;
        for (long p = lo; p < hi; p += 2) {
            h = 31 * h + Unsafe.getUnsafe().getChar(p);
        }
        return h;
    }

    @Override
    public long hi() {
        return hi;
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public long lo() {
        return lo;
    }

    public DirectString of(long lo, long hi) {
        this.lo = lo;
        this.hi = hi;
        this.len = (int) ((hi - lo) >>> 1);
        return this;
    }

    public DirectString of(long address, int len) {
        this.lo = address;
        this.hi = address + ((long) len << 1);
        this.len = len;
        return this;
    }

    @Override
    public long ptr() {
        return lo;
    }

    @Override
    public int size() {
        return (int) (hi - lo);
    }

    @Override
    protected CharSequence _subSequence(int start, int end) {
        DirectString seq = new DirectString();
        seq.lo = this.lo + start;
        seq.hi = this.lo + end;
        return seq;
    }
}
