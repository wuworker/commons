package com.wxl.commons.util;

import java.util.*;

/**
 * Create by wuxingle on 2020/10/13
 * 分页迭代器
 */
public class PageIterator<E> implements Iterator<E> {

    /**
     * 数据生成接口
     *
     * @param <E>
     */
    public interface DataLoader<E> {

        /**
         * 数据生成
         *
         * @param start 分页起始
         * @param count 分页大小
         * @return 数据list
         */
        List<E> loadMore(int start, int count);
    }

    // 获取的开始索引
    private static final int DEFAULT_START = 0;
    // 批量获取大小
    private static final int DEFAULT_COUNT = 50;

    /**
     * 当前数据索引
     */
    private int index;

    /**
     * 分页起始
     */
    private int start;

    /**
     * 分页大小
     */
    private int count;

    /**
     * 数据list
     */
    private List<E> list;

    /**
     * 数据生成接口
     */
    private final DataLoader<E> dataLoader;

    /**
     * 是否还要加载数据
     */
    private boolean hasMore;

    public PageIterator(DataLoader<E> dataLoader) {
        this(DEFAULT_START, DEFAULT_COUNT, dataLoader);
    }

    public PageIterator(int count, DataLoader<E> dataLoader) {
        this(DEFAULT_START, count, dataLoader);
    }

    public PageIterator(int start, int count, DataLoader<E> dataLoader) {
        this.start = start;
        this.count = count;
        this.dataLoader = dataLoader;
        this.index = 0;
        this.list = Collections.emptyList();
        this.hasMore = true;
    }

    @Override
    public boolean hasNext() {
        if (index < list.size()) {
            return true;
        }
        loadMore();
        return index < list.size();
    }

    @Override
    public E next() {
        if (index < list.size()) {
            return list.get(index++);
        }
        loadMore();
        if (index < list.size()) {
            return list.get(index++);
        }
        throw new NoSuchElementException();
    }

    /**
     * 批量返回
     *
     * @param maxSize 最大个数限制
     */
    public List<E> nextBatch(int maxSize) {
        List<E> data = new ArrayList<>();

        int loadSize = 0;
        while (loadSize < maxSize && hasNext()) {
            int remain = list.size() - index;

            // 剩余大小 > 需要的
            if (loadSize + remain > maxSize) {
                data.addAll(list.subList(index, index + (maxSize - loadSize)));
                index += (maxSize - loadSize);
                break;
            }
            data.addAll(list.subList(index, list.size()));
            index = list.size();
            loadSize += remain;
        }

        return data;
    }

    /**
     * 加载更多数据
     */
    private void loadMore() {
        if (!hasMore) {
            return;
        }
        list = Optional.ofNullable(dataLoader.loadMore(start, count))
                .orElse(Collections.emptyList());
        start += count;
        index = 0;
        // 说明没有更多数据
        if (list.size() < count) {
            hasMore = false;
        }
    }
}
