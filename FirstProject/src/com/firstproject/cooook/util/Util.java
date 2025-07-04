package com.firstproject.cooook.util;

public class Util {
    public static boolean isInteger(String str) {
    	boolean result = str != null && str.matches("-?\\d+");
        if (!result) System.out.println("❗ 잘못된 입력입니다.");
        return result;
    }
}
