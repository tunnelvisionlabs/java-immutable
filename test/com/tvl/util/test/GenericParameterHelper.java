// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util.test;

import java.util.Random;

public class GenericParameterHelper {

    private int data;

    public GenericParameterHelper() {
        data = new Random().nextInt();
    }

    public GenericParameterHelper(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public void setData(int value) {
        data = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GenericParameterHelper)) {
            return false;
        }

        GenericParameterHelper other = (GenericParameterHelper)obj;
        return data == other.data;
    }

    @Override
    public int hashCode() {
        return data;
    }

}
