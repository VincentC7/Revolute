package fr.miage.choquert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

    public static String toJsonString(Object o) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(o);
    }

}
