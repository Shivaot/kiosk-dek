package com.hitachi.kioskdesk.helper;

import java.security.Principal;

/**
 * Shiva Created on 25/02/22
 */
public class Utils {

    public static String parseUsername(Principal principal) {
        String username = principal.getName();
        String[] strings = username.split("@");
        String firstAndLastName = strings[0];
        String result;
        if (firstAndLastName.contains(".")) {
            result = firstAndLastName.replace("."," ");
        } else {
            result = firstAndLastName;
        }
        return toTitleCase(result);
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
