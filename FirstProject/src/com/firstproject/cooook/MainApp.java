package com.firstproject.cooook;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.MenuDao;
import com.firstproject.cooook.vo.MenuVO;

public class MainApp {

    public static void main(String[] args) {
        printBanner();

        Scanner sc = new Scanner(System.in);
        MenuDao menuDao = new MenuDao();

        System.out.print("🔎 조회할 카테고리 번호를 입력하세요: ");
        int categoryId = sc.nextInt();

        List<MenuVO> menuList = menuDao.getAllMenu(categoryId);

        System.out.println("\n📋 선택하신 카테고리의 메뉴 목록:");
        if (menuList.isEmpty()) {
            System.out.println("⚠️ 해당 카테고리에 등록된 메뉴가 없습니다.");
        } else {
            for (MenuVO menu : menuList) {
                System.out.println("메뉴 ID: " + menu.getMenuID() + " | 메뉴명: " + menu.getMenuName());
            }
        }

        sc.close();
    }

    private static void printBanner() {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_GREEN = "\u001B[32m";

        System.out.println(ANSI_YELLOW);
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║                                                ║");
        System.out.println("║   ██████╗ ███████╗ █████╗  ██████╗  ██████╗    ║");
        System.out.println("║  ██╔════╝ ██╔════╝██╔══██╗██╔════╝ ██╔═══██╗   ║");
        System.out.println("║  ██║  ███╗█████╗  ███████║██║  ███╗██║   ██║   ║");
        System.out.println("║  ██║   ██║██╔══╝  ██╔══██║██║   ██║██║   ██║   ║");
        System.out.println("║  ╚██████╔╝███████╗██║  ██║╚██████╔╝╚██████╔╝   ║");
        System.out.println("║   ╚═════╝ ╚══════╝╚═╝  ╚═╝ ╚═════╝  ╚═════╝    ║");
        System.out.println("║                                                ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println(ANSI_GREEN + "		   🚀 재고 재고 재고! 관리 시스템! 🚀" + ANSI_RESET);
        System.out.println();
    }
}
