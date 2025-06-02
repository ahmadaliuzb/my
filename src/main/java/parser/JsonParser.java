package parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, String> jsonToMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static String mapToJson(Map<String, String> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String objectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static  <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        try {
            return mapper.convertValue(map, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> objectToMap(Object object) {
        try {
            return mapper.convertValue(object, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
