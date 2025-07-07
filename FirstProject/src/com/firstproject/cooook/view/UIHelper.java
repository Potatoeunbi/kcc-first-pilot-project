package com.firstproject.cooook.view;

import java.util.List;
import java.util.function.Function;

import com.firstproject.cooook.vo.CategoryVO;

public class UIHelper {
    private static final int count = 100;
    private static int left = 0;
    private static int right = 0;

    public static <T> void printBoxedList(String title, String emptyTitle, List<T> list, Function<T, String> formatter) {
        calWidth(title);
        String displayString = "│" + " ".repeat(left + 1) + title + " ".repeat(right - 1) + "│";
        
        System.out.println("┌" + "─".repeat(count) + "┐");
        System.out.println(displayString);
        System.out.println("├" + "─".repeat(count) + "┤");
        
        if (list == null || list.isEmpty()) {             
            calWidth(emptyTitle);
            displayString = "│" + " ".repeat(left + 1) + emptyTitle + " ".repeat(right) + "│";
            System.out.println(displayString);
        } else {
            for (T item : list) {
                displayString = formatter.apply(item);
                displayString = truncateWithWidth(displayString, count);
                calWidth(displayString);
                displayString = "│" + displayString + " ".repeat(left + right) + "│";
                System.out.println(displayString);
            }
        }
        System.out.println("└" + "─".repeat(count) + "┘");
    }

    public static void printCategoryTree(List<CategoryVO> categories) {
        String title = "📂 카테고리 목록";
        calWidth(title);
        String displayString = "│" + " ".repeat(left + 1) + title + " ".repeat(right - 1) + "│";
        
        System.out.println("┌" + "─".repeat(count) + "┐");
        System.out.println(displayString);
        System.out.println("├" + "─".repeat(count) + "┤");
        
        if (categories == null || categories.isEmpty()) {                
            title = "📭 등록된 카테고리가 없습니다";
            calWidth(title);
            displayString = "│" + " ".repeat(left + 1) + title + " ".repeat(right) + "│";
            System.out.println(displayString);
        } else {
            for (CategoryVO category : categories) {
                String indent = "    ".repeat(category.getLevel() - 1) + "📂"; 
                String displayName = category.getCategoryName() + "(" + category.getCategoryId() + ")";
                calWidth(indent + displayName);
                displayString = "│" + indent + displayName + " ".repeat(left + right) + "│";
                System.out.println(displayString);
            }
        }
        System.out.println("└" + "─".repeat(count) + "┘");
    }

    public static String truncateWithWidth(String input, int maxWidth) {
        int width = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            int charWidth = isKorean(ch) ? 2 : 1;

            if (width + charWidth > maxWidth - 3) {
                result.append("...");
                break;
            }
            result.append(ch);
            width += charWidth;
        }
        return result.toString();
    }

    private static boolean isKorean(char ch) {
        return (ch >= 0xAC00 && ch <= 0xD7A3);
    }

    public static void calWidth(String koreaString) {        
        int cal = count - koreaString.length() - koreaString.replaceAll("[^가-힣]", "").length();
        left = cal / 2;
        right = cal - left;
    }
        
    public static void printTitle(String title) {
        calWidth(title);
        String displayString = " ".repeat(left) + title + " ".repeat(right);
        System.out.println();
        printDivider();
        System.out.println(displayString);
        printDivider();        
    }

    public static void printDivider() {
        System.out.println("=".repeat(count));
    }
    
    public static void printSuccess(String message) {
        System.out.println("✅ " + message);
    }
    
    public static void printError(String message) {
        System.out.println("❌ " + message);
    }
    
    public static void printWarning(String message) {
        System.out.println("⚠️ " + message);
    }
    
    public static void printInfo(String message) {
        System.out.println("💡 " + message);
    }
} 
    
