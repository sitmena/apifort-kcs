package com.sitech.util;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ProjectUtil {

    public List<String> stringToList(String value) {
        String[] strSplit = value.split(",");
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
        return strList;
    }
}
