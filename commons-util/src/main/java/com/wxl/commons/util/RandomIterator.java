package com.wxl.commons.util;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Function;

/**
 * Create by wuxingle on 2020/11/09
 * 随机生成对象
 */
public class RandomIterator<T> implements BatchIterator<T> {

    private final int maxSize;

    private final Function<Random, T> generator;

    private final Random random;

    private int size;

    public RandomIterator(int maxSize, Function<Random, T> generator) {
        this(maxSize, generator, new Random());
    }

    public RandomIterator(int maxSize, Function<Random, T> generator, Random random) {
        this.maxSize = maxSize;
        this.generator = generator;
        this.random = random;
    }

    @Override
    public boolean hasNext() {
        return size < maxSize;
    }

    @Override
    public T next() {
        if (size++ < maxSize) {
            return generator.apply(random);
        }
        throw new NoSuchElementException();
    }

}
