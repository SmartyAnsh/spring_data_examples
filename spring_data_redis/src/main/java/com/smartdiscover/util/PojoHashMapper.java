package com.smartdiscover.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.hash.HashMapper;

import java.util.Map;

public class PojoHashMapper<T> implements HashMapper<T, String, String> {

    final Class<T> typeParameterClass;
    ObjectMapper mapper = new ObjectMapper();

    public PojoHashMapper(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public Map<String, String> toHash(T object) {
        return mapper.convertValue(object, Map.class);

    }

    @Override
    public T fromHash(Map<String, String> hash) {
        T obj = mapper.convertValue(hash, typeParameterClass);
        return obj;
    }

}