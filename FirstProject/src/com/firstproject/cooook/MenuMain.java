package com.firstproject.cooook;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.CategoryDao;
import com.firstproject.cooook.dao.MenuDao;
import com.firstproject.cooook.vo.CategoryVO;
import com.firstproject.cooook.vo.MenuVO;

public class MenuMain {
    Scanner sc = new Scanner(System.in);
    MenuDao mdao = new MenuDao();
    public void runMenu() {
        System.out.println("\n🔍 원하시는 검색 기능을 선택하세요");
        System.out.println("1. 카테고리별 메뉴 검색");
        System.out.println("2. 메뉴 이름 키워드 검색");
        System.out.print("선택 > ");
        int choice = sc.nextInt();
        sc.nextLine();  // 개행문자 제거

        switch (choice) {
            case 1:
                // 🔽 카테고리 목록 출력
                CategoryDao cdao = new CategoryDao();
                List<CategoryVO> cList = cdao.selectCategory();
                System.out.println("📁 카테고리 목록:");
                for (CategoryVO c : cList) {
                    System.out.println("▶ ID: " + c.getCategoryId() + " | 이름: " + c.getCategoryName());
                }

                // 🔽 사용자로부터 검색할 카테고리 ID 입력
                System.out.print("검색하실 카테고리 번호를 입력하세요 > ");
                int search = sc.nextInt();
                sc.nextLine();  // 버퍼 비우기

                // 🔽 해당 카테고리의 메뉴 목록 출력
                List<MenuVO> mList = mdao.getCategorySearchMenu(search);
                System.out.println("\n📋 [해당 카테고리의 메뉴 목록]");
                for (MenuVO m : mList) {
                    System.out.println(m.getMenuID() + " | " + m.getMenuName());
                }
                break;

            case 2:
                // 🔽 키워드로 검색
                System.out.print("🔤 검색할 키워드를 입력하세요: ");
                String keyword = sc.nextLine();

                List<MenuVO> searchList = mdao.getStringSearchMenu(keyword);
                System.out.println("\n🔎 [이름 검색 결과]");
                for (MenuVO m : searchList) {
                    System.out.println(m.getMenuID() + " | " + m.getMenuName());
                }
                break;

            default:
                System.out.println("❌ 잘못된 입력입니다.");
        }
    }


}
