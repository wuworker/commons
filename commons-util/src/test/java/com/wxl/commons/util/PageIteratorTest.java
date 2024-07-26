package com.wxl.commons.util;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Create by wuxingle on 2020/10/13
 * 分页迭代器测试
 */
public class PageIteratorTest {

    @Test
    public void test1() {
        ArrayList<Integer> list = IntStream.range(0, 10)
                .collect(Lists::newArrayList, ArrayList::add, ArrayList::addAll);

        PageIterator<Integer> it = new PageIterator<>(3,
                (start, count) ->
                        list.stream().skip(start).limit(count).collect(Collectors.toList()));

        while (it.hasNext()) {
            Integer next = it.next();
            System.out.println(next);
        }
    }

    @Test
    public void test2() {
        ArrayList<Integer> list = IntStream.range(0, 15)
                .collect(Lists::newArrayList, ArrayList::add, ArrayList::addAll);

        PageIterator<Integer> it = new PageIterator<>(3,
                (start, count) ->
                        list.stream().skip(start).limit(count).collect(Collectors.toList()));

        it.next();
        it.next();

        List<Integer> batch = it.nextBatch(7);
        System.out.println(batch);

        it.next();

        List<Integer> batch1 = it.nextBatch(10);
        System.out.println(batch1);
    }
}

