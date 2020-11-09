package com.wxl.commons.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Create by wuxingle on 2020/11/09
 * 增加批量返回的iterator
 */
public interface BatchIterator<T> extends Iterator<T> {

    /**
     * 一次性返回最多maxSize个元素
     *
     * @param maxSize 返回的最大个数
     */
    default List<T> nextBatch(int maxSize) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < maxSize && hasNext(); i++) {
            list.add(next());
        }
        return list;
    }

    /**
     * 返回剩余所有元素
     */
    default List<T> nextBatch() {
        List<T> list = new ArrayList<>();
        while (hasNext()) {
            list.add(next());
        }
        return list;
    }

    /**
     * 封装为BatchIterator
     */
    static <T> BatchIterator<T> toBatch(Iterator<T> it) {
        return new BatchIterator<T>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                it.forEachRemaining(action);
            }
        };
    }

}
