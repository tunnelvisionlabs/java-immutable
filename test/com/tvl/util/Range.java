// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

class Range implements Iterable<Integer> {

    private final int start;
    private final int count;

    public Range(int start, int count) {
        this.start = start;
        this.count = count;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int current = start;
            private int remaining = count;

            @Override
            public boolean hasNext() {
                return remaining != 0;
            }

            @Override
            public Integer next() {
                if (remaining == 0) {
                    throw new NoSuchElementException();
                }

                remaining--;
                return current++;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported");
            }
        };
    }
}
