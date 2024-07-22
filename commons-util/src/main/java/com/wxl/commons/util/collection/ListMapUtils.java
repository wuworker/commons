package com.wxl.commons.util.collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by wuxingle on 2017/12/17 0017.
 * 集合相关工具类
 */
public class ListMapUtils {

    /**
     * 对list分页
     *
     * @param pageNo   第几页
     * @param pageSize 一页几个
     */
    public static <T> List<T> pageOfList(List<T> list, int pageNo, int pageSize) {
        Assert.isTrue(pageNo > 0 && pageSize > 0, "pageNo and pageSize must > 0");
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        int start = (pageNo - 1) * pageSize;
        if (start >= list.size()) {
            return Collections.emptyList();
        }
        int end = start + pageSize;
        return list.subList(start, Math.min(end, list.size()));
    }


    /**
     * 获取map里的值
     */
    public static <K, V> String getMapStringValue(Map<K, V> map, K key) {
        V v = map.get(key);
        return v == null ? null : v.toString();
    }

    @SuppressWarnings("unchecked")
    public static <K, V> V getMapValue(Map<K, Object> map, K key, Class<V> clazz) {
        return (V) map.get(key);
    }


    /**
     * 把map的value转为string
     */
    public static <K, V> Map<K, String> toStringMap(Map<K, V> map) {
        Map<K, String> result = new HashMap<>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            result.put(key, value == null ? null : value.toString());
        }
        return result;
    }


    /**
     * 根据value拿key，拿第一个
     */
    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 合并2个map,转为hashMap
     */
    public static <K, V> Map<K, V> mergeToHash(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> map = new HashMap<>();
        if (MapUtils.isNotEmpty(map1)) {
            map.putAll(map1);
        }
        if (MapUtils.isNotEmpty(map2)) {
            map.putAll(map2);
        }
        return map;
    }


}

