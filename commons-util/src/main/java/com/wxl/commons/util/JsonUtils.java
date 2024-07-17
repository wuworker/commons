package com.wxl.commons.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by wuxingle on 2018/1/5.
 * json相关工具类
 */
public class JsonUtils {

    /**
     * 格式化输出,必须为json对象
     */
    public static String toPrettyFormat(Object json) {
        return JSON.toJSONString(json, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
    }

    public static String toJSONString(Object json) {
        return JSON.toJSONString(json, JSONWriter.Feature.WriteMapNullValue);
    }

    public static String toJSONString(Object json, boolean format) {
        if (format) {
            return toPrettyFormat(json);
        } else {
            return toJSONString(json);
        }
    }

    /**
     * 把json字符串解析成对象
     */
    public static Object parse(String text) {
        return JSON.parse(text);
    }

    /**
     * 往json格式的数据放入值
     * key支持：a.b.c这种结构
     */
    public static Object put(Object json, String key, Object value) {
        return putVal(json, key, value, null, false);
    }

    public static Object put(Object json, String key, Object value, @Nullable String split) {
        return putVal(json, key, value, split, false);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> put(Map<String, Object> json, String key, Object value) {
        return (Map<String, Object>) putVal(json, key, value, null, false);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> put(Map<String, Object> json, String key, Object value, @Nullable String split) {
        return (Map<String, Object>) putVal(json, key, value, split, false);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> put(List<Object> json, String key, Object value) {
        return (List<Object>) putVal(json, key, value, null, false);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> put(List<Object> json, String key, Object value, @Nullable String split) {
        return (List<Object>) putVal(json, key, value, split, false);
    }

    /**
     * 只有原来没有值时才放入
     */
    public static Object putIfAbsent(Object json, String key, Object value) {
        return putVal(json, key, value, null, true);
    }

    public static Object putIfAbsent(Object json, String key, Object value, @Nullable String split) {
        return putVal(json, key, value, split, true);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> putIfAbsent(Map<String, Object> json, String key, Object value) {
        return (Map<String, Object>) putVal(json, key, value, null, true);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> putIfAbsent(Map<String, Object> json, String key, Object value, @Nullable String split) {
        return (Map<String, Object>) putVal(json, key, value, split, true);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> putIfAbsent(List<Object> json, String key, Object value) {
        return (List<Object>) putVal(json, key, value, null, true);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> putIfAbsent(List<Object> json, String key, Object value, @Nullable String split) {
        return (List<Object>) putVal(json, key, value, split, true);
    }

    /**
     * 在对list操作时,用数字表示索引
     * <pre> {@code
     *
     *
     *
     *
     *
     * }</pre>
     */
    @SuppressWarnings("unchecked")
    private static Object putVal(Object json, String key, @Nullable Object value, @Nullable String split, boolean onlyIfAbsent) {
        Assert.hasText(key, "put key can not empty");
        //默认用.分隔
        if (StringUtils.isBlank(split)) {
            split = "\\.";
        }
        String[] keys = key.split(split);
        Object findJson = json;
        int i = 0;
        //循环找到最后一层key的值
        for (int len = keys.length - 1; i < len; i++) {
            Object v;
            String currentKey = keys[i];
            String nextKey = keys[i + 1];
            if (findJson instanceof Map) {
                Map<String, Object> findMap = (Map<String, Object>) findJson;
                v = findMap.get(currentKey);
                if (v != null) {
                    findJson = v;
                }
                //下一个为数字则新建list否则新建map
                else if (nextKey.matches("^\\d+$")) {
                    List<Object> list = new ArrayList<>();
                    findMap.put(currentKey, list);
                    findJson = list;
                } else {
                    Map<String, Object> map = new HashMap<>();
                    findMap.put(currentKey, map);
                    findJson = map;
                }
            } else if (findJson instanceof List) {
                List<Object> list = (List<Object>) findJson;
                int index = Integer.parseInt(currentKey);
                boolean toLarge;
                if (toLarge = (index >= list.size())) {
                    for (int j = list.size(); j < index; j++) {
                        list.add(null);
                    }
                    v = null;
                } else {
                    v = list.get(index);
                }
                if (v != null) {
                    findJson = v;
                } else if (nextKey.matches("^\\d+$")) {
                    List<Object> subList = new ArrayList<>();
                    if (toLarge) {
                        list.add(subList);
                    } else {
                        list.set(index, subList);
                    }
                    findJson = subList;
                } else {
                    Map<String, Object> map = new HashMap<>();
                    if (toLarge) {
                        list.add(map);
                    } else {
                        list.set(index, map);
                    }
                    findJson = map;
                }
            } else {
                if (i == 0) {
                    throw new IllegalArgumentException("input object is not a json,must is List or Map, actual is :" + json);
                }
                throw new IllegalArgumentException("can not put '" + key + "', "
                        + "because get '" + keys[i - 1] + "' value is '" + findJson + "'");
            }
        }
        if (findJson instanceof Map) {
            Map<String, Object> findMap = (Map<String, Object>) findJson;
            Object v = findMap.get(keys[i]);
            if (v == null || !onlyIfAbsent) {
                findMap.put(keys[i], value);
            }
        } else if (findJson instanceof List) {
            List<Object> findList = (List<Object>) findJson;
            //如果最后一个是list类型,那么key必须是数字索引
            int index = Integer.parseInt(keys[i]);
            if (index >= findList.size()) {
                for (int j = findList.size(); j < index; j++) {
                    findList.add(null);
                }
                findList.add(value);
            } else {
                Object old = findList.get(index);
                if (old == null || !onlyIfAbsent) {
                    findList.set(index, value);
                }
            }
        } else {
            if (i == 0) {
                throw new IllegalArgumentException("input object is not a json,must is List or Map, actual is :" + json);
            }
            throw new IllegalArgumentException("can not put '" + key + "', "
                    + "because get '" + keys[i - 1] + "' value is '" + findJson + "'");
        }
        return json;
    }


    /**
     * 从json中取出数据
     */
    @Nullable
    public static <T> T get(Object json, String key, Class<T> clazz) {
        return getVal(json, key, clazz, null);
    }

    @Nullable
    public static <T> T get(Object json, String key, Class<T> clazz, @Nullable String split) {
        return getVal(json, key, clazz, split);
    }

    @Nullable
    public static Object get(Object json, String key) {
        return getVal(json, key, Object.class, null);
    }

    @Nullable
    public static Object get(Object json, String key, @Nullable String split) {
        return getVal(json, key, Object.class, split);
    }

    @Nullable
    public static String getString(Object json, String key) {
        return getString(json, key, null);
    }

    @Nullable
    public static String getString(Object json, String key, @Nullable String split) {
        Object obj = getVal(json, key, Object.class, split);
        return obj == null ? null : obj.toString();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> T getVal(Object json, String key, Class<T> clazz, @Nullable String split) {
        Assert.hasText(key, "put key can not empty");
        //默认用.分隔
        if (StringUtils.isBlank(split)) {
            split = "\\.";
        }
        String[] keys = key.split(split);
        Object findJson = json;
        Object val = null;
        for (int i = 0; i < keys.length; i++) {
            if (findJson instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) findJson;
                val = map.get(keys[i]);
            } else if (findJson instanceof List) {
                List<Object> list = (List<Object>) findJson;
                val = list.get(Integer.parseInt(keys[i]));
            } else {
                if (i == 0) {
                    throw new IllegalArgumentException("input object is not a json,must is List or Map, actual is :" + json);
                }
                throw new IllegalArgumentException("can not get '" + key + "', because get '"
                        + keys[i - 1] + "' value is not map or list, value=" + findJson);
            }
            if (val == null) {
                break;
            }
            findJson = val;
        }
        return (T) val;
    }


    /**
     * 删除key
     */
    @Nullable
    public static Object remove(Object json, String key) {
        return remove(json, key, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static Object remove(Object json, String key, @Nullable String split) {
        Assert.hasText(key, "put key can not empty");
        if (StringUtils.isBlank(split)) {
            split = "\\.";
        }
        String[] keys = key.split(split);
        Object findJson = json;
        for (int i = 0; i < keys.length - 1; i++) {
            if (findJson instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) findJson;
                findJson = map.get(keys[i]);
            } else if (findJson instanceof List) {
                List<Object> list = (List<Object>) findJson;
                findJson = list.get(Integer.parseInt(keys[i]));
            } else {
                return null;
            }
        }
        if (findJson instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) findJson;
            return map.remove(keys[keys.length - 1]);
        }
        if (findJson instanceof List) {
            List<Object> list = (List<Object>) findJson;
            int index = Integer.parseInt(keys[keys.length - 1]);
            if (index >= list.size()) {
                return null;
            }
            return list.remove(index);
        }
        return null;
    }

    /**
     * json 遍历处理
     */
    public static void doWithJson(Object json, BiFunction<String, Object, Object> valueHandler) {
        doWithJson(json, valueHandler, null);
    }

    /**
     * json 遍历处理
     *
     * @param json         json
     * @param valueHandler json的value处理
     * @param keyHandler   json的key处理
     */
    @SuppressWarnings("unchecked")
    public static void doWithJson(Object json, @Nullable BiFunction<String, Object, Object> valueHandler, @Nullable BiFunction<String, Object, String> keyHandler) {
        Assert.isTrue(valueHandler != null || keyHandler != null, "value or key ,must not null at least 1!");
        if (json instanceof Map) {
            doWithJson((Map<String, Object>) json, valueHandler, keyHandler);
        } else if (json instanceof List) {
            doWithJson((List<Object>) json, valueHandler, keyHandler);
        } else {
            throw new IllegalArgumentException("input must is a json(Map or List),but actual is " + json.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private static void doWithJson(Map<String, Object> json, @Nullable BiFunction<String, Object, Object> valueHandler, @Nullable BiFunction<String, Object, String> keyHandler) {
        if (keyHandler != null) {
            Map<String, Object> map = new HashMap<>(json.size(), 1);
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String name = keyHandler.apply(key, value);
                if (valueHandler != null) {
                    value = valueHandler.apply(key, value);
                }
                if (StringUtils.isBlank(name)) {
                    continue;
                }
                if (value instanceof Map) {
                    doWithJson((Map<String, Object>) value, valueHandler, keyHandler);
                } else if (value instanceof List) {
                    doWithJson((List<Object>) value, valueHandler, keyHandler);
                }
                map.put(name, value);
            }
            json.clear();
            json.putAll(map);
        } else {
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = valueHandler == null ? entry.getValue() : valueHandler.apply(key, entry.getValue());
                if (value instanceof Map) {
                    doWithJson((Map<String, Object>) value, valueHandler, null);
                } else if (value instanceof List) {
                    doWithJson((List<Object>) value, valueHandler, null);
                }
                entry.setValue(value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void doWithJson(List<Object> json, @Nullable BiFunction<String, Object, Object> valueHandler, @Nullable BiFunction<String, Object, String> keyHandler) {
        for (Object obj : json) {
            if (obj instanceof Map) {
                doWithJson((Map<String, Object>) obj, valueHandler, keyHandler);
            }
        }
    }

}



