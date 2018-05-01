package com.xu.ccgv.mynearplaceapplication.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by xz on 01/05/2018.
 */
public class MyAPIAccessTest {

    private String json1 = "{\"name\":\"xu\"}";
    private String json2 = "[{\"name\":\"xu\"}, {\"name\":\"laplap\"}]";

    @Test
    public void testTheTypeAndGosnMethod() {
        Type type1 = new TypeToken<Staff>() {
        }.getType();
        Staff staff = getAPIResult(json1, type1);
        Assert.assertNotNull(staff);
        Type type2 = new TypeToken<List<Staff>>() {
        }.getType();
        List<Staff> list = getAPIResultList(json2, type2);
        Assert.assertNotNull(list);
    }

    private <T> T getAPIResult(String json, Type type) {
        Gson gson = new Gson();
        T object = gson.fromJson(json, type);
        return object;
    }

    private <T> List<T> getAPIResultList(String json, Type type) {
        Gson gson = new Gson();

        List<T> object = gson.fromJson(json, type);
        return object;
    }

    private class Staff {
        public String name;
    }
}