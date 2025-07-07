package com.firstproject.cooook.util;

public class Util {
    public static boolean isInteger(String str) {
    	boolean result = str != null && str.matches("-?\\d+");
        if (!result) System.out.println("❗ 잘못된 입력입니다.");
        return result;
    }
    
    public static boolean isNotEmpty(String str) {
        boolean result = str != null && !str.trim().isEmpty();
        if (!result) System.out.println("❗ 입력값이 비어있습니다.");
        return result;
    }
    
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        int length = str.trim().length();
        boolean result = length >= minLength && length <= maxLength;
        if (!result) {
            System.out.printf("❗ 입력값은 %d~%d자 사이여야 합니다. (현재: %d자)%n", 
                minLength, maxLength, length);
        }
        return result;
    }
}
