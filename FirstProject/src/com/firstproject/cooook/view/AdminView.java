package com.firstproject.cooook.view;

import java.util.Scanner;

import com.firstproject.cooook.common.Session;

public class AdminView {
    private Scanner sc = new Scanner(System.in);

    public void showMenu() {
        while (true) {
            System.out.println("📋 관리자 메뉴");
            System.out.println("1. 작업자 관리");
            System.out.println("2. 주문 관리");
            System.out.println("3. 재료 관리");
            System.out.println("4. 카테고리 관리");
            System.out.println("0. 로그아웃");

            System.out.print("메뉴 선택: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    new StaffManageView().run();
                    break;
                case 2:
                    new OrderManageView().run();
                    break;
                case 3:
//                    new IngredientManageView().run();
                    break;
                case 4:
//                    new CategoryManageView().run();
                    break;
                case 0:
                    System.out.println("🔒 로그아웃 되었습니다.");
                    Session.clear();
                    return;
                default:
                    System.out.println("❗ 잘못된 입력입니다.");
            }
        }
    }
}
