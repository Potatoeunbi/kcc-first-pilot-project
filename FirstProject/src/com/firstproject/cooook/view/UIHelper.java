package com.firstproject.cooook.view;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.firstproject.cooook.vo.CategoryVO;
import com.firstproject.cooook.vo.MenuCategory;

public class UIHelper {
    private static final int count = 100;
    private static int left = 0;
    private static int right = 0;

    public static <T> void printBoxedList(String title, String emptyTitle, List<T> list, Function<T, String> formatter) {
        calWidth(title);
        String displayString = "│" + " ".repeat(left) + title + " ".repeat((right + left) % 2 == 0 ? right - 1 : right - 2) + "│";
        
        System.out.println("┌" + "─".repeat(count - 2) + "┐");
        System.out.println(displayString);
        System.out.println("├" + "─".repeat(count - 2) + "┤");
        
        if (list == null || list.isEmpty()) {             
            calWidth(emptyTitle);
            displayString = "│" + " ".repeat(left) + emptyTitle + " ".repeat(right - 2) + "│";
            System.out.println(displayString);
        } else {
            for (T item : list) {
                displayString = formatter.apply(item);
                displayString = truncateWithWidth(displayString, count - 2);
                calWidth(displayString);
                displayString = "│" + displayString + " ".repeat(left + right - 2) + "│";
                System.out.println(displayString);
            }
        }
        System.out.println("└" + "─".repeat(count - 2) + "┘");
    }

    public static <T> void printBoxedList(String title, List<T> list, Function<T, String> formatter) {
        calWidth(title);
        String displayString = "│" + " ".repeat(left) + title + " ".repeat(right % 2 == 0 ? right - 1 : right - 2) + "│";
        
        System.out.println("┌" + "─".repeat(count - 2) + "┐");
        System.out.println(displayString);
        System.out.println("├" + "─".repeat(count - 2) + "┤");
    
        for (T item : list) {
            displayString = formatter.apply(item);
            displayString = truncateWithWidth(displayString, count - 2);
            calWidth(displayString);
            displayString = "│" + displayString + " ".repeat(left + right - 2) + "│";
            System.out.println(displayString);
        }
        System.out.println("└" + "─".repeat(count - 2) + "┘");
    }

    public static void printCategoryTree(List<CategoryVO> categories) {
        String title = "📂 카테고리 목록";
        calWidth(title);
        String displayString = "│" + " ".repeat(left) + title + " ".repeat(right - 2) + "│";
        
        System.out.println("┌" + "─".repeat(count - 2) + "┐");
        System.out.println(displayString);
        System.out.println("├" + "─".repeat(count - 2) + "┤");
        
        if (categories == null || categories.isEmpty()) {                
            title = "📭 등록된 카테고리가 없습니다";
            calWidth(title);
            displayString = "│" + " ".repeat(left) + title + " ".repeat(right - 1) + "│";
            System.out.println(displayString);
        } else {
            for (CategoryVO category : categories) {
                String indent = "    ".repeat(category.getLevel() - 1) + "📂"; 
                String displayName = " " + category.getCategoryName() + "(" + category.getCategoryId() + ")";
                calWidth(indent + displayName);
                displayString = "│" + indent + displayName + " ".repeat(left + right - 2) + "│";
                System.out.println(displayString);
            }
        }
        System.out.println("└" + "─".repeat(count - 2) + "┘");
    }

    public static void printMenuCategoryTree(List<MenuCategory> menuCategories) {
        String title = "📂 메뉴 카테고리 목록";
        calWidth(title);
        String displayString = "│" + " ".repeat(left) + title + " ".repeat(right - 2) + "│";
        
        System.out.println("┌" + "─".repeat(count - 2) + "┐");
        System.out.println(displayString);
        System.out.println("├" + "─".repeat(count - 2) + "┤");
        
        if (menuCategories == null || menuCategories.isEmpty()) {                
            title = "📭 등록된 메뉴 카테고리가 없습니다";
            calWidth(title);
            displayString = "│" + " ".repeat(left) + title + " ".repeat(right - 1) + "│";
            System.out.println(displayString);
        } else {
            for (MenuCategory menuCategory : menuCategories) {
                String icon = "";
                int menu = 0;
                switch (menuCategory.getType()) {
                    case "CATEGORY":        
                        icon = "📂";
                        break;
                    case "MENU":            
                        icon = "🍽️";
                        menu = 1;
                        break;
                    case "HEADER": 
                        icon = "📭";
                        break;
                    case "UNCATEGORIZED":
                        icon = "🍽️";
                        menu = 1;
                        break;
                }

                String indent = "    ".repeat(menuCategory.getLevel() - 1) + icon; 
                String displayName = " " + menuCategory.getName();
                calWidth(indent + displayName);
                displayString = "│" + indent + displayName + " ".repeat(left + right + menu - 2) + "│";
                System.out.println(displayString);
            }
        }
        System.out.println("└" + "─".repeat(count - 2) + "┘");
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
        String ANSI_RESET  = "\u001B[0m";
        String ANSI_RED    = "\u001B[31m";
        String ANSI_GREEN  = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_BLUE   = "\u001B[34m";
        String ANSI_PURPLE = "\u001B[35m";
        String ANSI_CYAN   = "\u001B[36m";
        String ANSI_WHITE  = "\u001B[37m";

        Random random = new Random();
        int colorCode = random.nextInt(7);  // 0~7 중 하나

        String chosenColor = "";

        switch (colorCode) {
            case 0: chosenColor = ANSI_RED;     break;
            case 1: chosenColor = ANSI_GREEN;   break;
            case 2: chosenColor = ANSI_YELLOW;  break;
            case 3: chosenColor = ANSI_BLUE;    break;
            case 4: chosenColor = ANSI_PURPLE;  break;
            case 5: chosenColor = ANSI_CYAN;    break;
            case 6: chosenColor = ANSI_WHITE;   break;
        }

        calWidth(title);
        String displayString = " ".repeat(left) + title + " ".repeat(right);        
	    System.out.println(chosenColor);
        printDivider();
        System.out.println(displayString);
        printDivider();              
	    System.out.println(ANSI_RESET);  
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
    
