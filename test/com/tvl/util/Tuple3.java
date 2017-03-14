// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.base.Objects;

final class Tuple3<T1, T2, T3> {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;

    public Tuple3(T1 item1, T2 item2, T3 item3) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> create(T1 item1, T2 item2, T3 item3) {
        return new Tuple3<T1, T2, T3>(item1, item2, item3);
    }

    public T1 getItem1() {
        return item1;
    }

    public T2 getItem2() {
        return item2;
    }

    public T3 getItem3() {
        return item3;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Tuple3)) {
            return false;
        }

        Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>)obj;
        return Objects.equal(getItem1(), other.getItem1())
            && Objects.equal(getItem2(), other.getItem2())
            && Objects.equal(getItem3(), other.getItem3());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getItem1(), getItem2(), getItem3());
    }
}
