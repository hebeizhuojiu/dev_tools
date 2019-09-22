package com.xuecheng.cms.Vo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestFlow {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<FlowResult> list = getList();
        ResultBean<FlowResult> bean = reduceProperty(list, FlowResult.class);
        FlowResult t = bean.getT();
        List<FlowInfo> flowList = t.getFlowList();
        List<String> strList = t.getStrList();
    }

    private static <T> ResultBean<T> reduceProperty(List<T> list, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ResultBean<T> bean = new ResultBean<>();
        T obj = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        // 将类型list中的对象规整为一个泛型对象，由于有多次请求，所以只对对象中的list属性规整，
        for (Field field : fields) {
            if (field.getType() == List.class) {
                addListProperty(list, clazz, obj, field);
            }
        }
        // 将查询结果封装给ResultBean对象的T属性
        bean.setT(obj);
        return bean;
    }

    private static <T> void addListProperty(List<T> list, Class<T> clazz, T obj, Field field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<Object> defaultList = new ArrayList<>();
        String methodName = getMethodName(field);
        // 属性类型为list的需要提供标准get、set方法
        Method getMethod = clazz.getMethod("get" + methodName);
        Method setMethod = clazz.getMethod("set" + methodName, List.class);
        for (T t : list) {
            Object o = getMethod.invoke(t);
            if (o instanceof List) {
                List<Object> propList = (List<Object>) o;
                defaultList.addAll(propList);
            }
        }
        setMethod.invoke(obj, defaultList);
    }

    private static String getMethodName(Field field) {
        String fieldName = field.getName();
        String s = fieldName.substring(0, 1).toUpperCase();
        String methodName = s + fieldName.substring(1);
        return methodName;
    }

    private static List<FlowResult> getList() {
        ArrayList<FlowResult> resultList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FlowResult result = new FlowResult();
            ArrayList<FlowInfo> list = new ArrayList<>();
            List<String> objects = new CopyOnWriteArrayList<>();
            for (int l = 0; l < 10; l++) {
                FlowInfo info = new FlowInfo();
                info.setFlowName("flow" + l);
                info.setFlowDelay(10 + i + "");
                list.add(info);
                objects.add("asdasd");
            }
            result.setName("mmmm");
            result.setFlowList(list);
            result.setStrList(objects);
            resultList.add(result);
        }
        return resultList;
    }
}
