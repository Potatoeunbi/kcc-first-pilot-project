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
    
    private void printCategoryTree(List<CategoryVO> list, String prefix) {
        for (CategoryVO c : list) {
            System.out.println(prefix + "▶ ID: " + c.getCategoryId() + " | 이름: " + c.getCategoryName());
            if (c.getChild() != null && !c.getChild().isEmpty()) {
                printCategoryTree(c.getChild(), prefix + "  ");
            }
        }
    }
    
    private void printLeafCategories(List<CategoryVO> list, String prefix) {
        for (CategoryVO c : list) {
            if (c.getChild() == null || c.getChild().isEmpty()) {
                System.out.println(prefix + "▶ ID: " + c.getCategoryId() + " | 이름: " + c.getCategoryName());
            } else {
                printLeafCategories(c.getChild(), prefix + "  ");
            }
        }
    }
    public void runMenu() {
        System.out.println("\n🔍 원하시는 검색 기능을 선택하세요");
        System.out.println("1. 카테고리별 메뉴 검색");
        System.out.println("2. 메뉴 이름 키워드 검색");
        System.out.println("3. 메뉴 등록");
        System.out.print("선택 > ");
        int choice = sc.nextInt();
        sc.nextLine();  

        switch (choice) {
        case 1:
            CategoryDao cdao = new CategoryDao();
            List<CategoryVO> tree = cdao.selectCategory();  

            System.out.println("📁 카테고리 목록 (트리 구조):");
            printCategoryTree(tree, "");  

            System.out.print("\n📥 검색을 원하는 하위 카테고리 번호를 입력하세요 > ");
            int search = sc.nextInt();
            sc.nextLine();

            List<MenuVO> mList = mdao.getCategorySearchMenu(search);
            System.out.println("\n📋 [해당 카테고리의 메뉴 목록]");
            if (mList.isEmpty()) {
                System.out.println("❌ 해당 카테고리에 등록된 메뉴가 없습니다.");
            } else {
                for (MenuVO m : mList) {
                    System.out.println(m.getMenuID() + " | " + m.getMenuName() + "| 가격: " + m.getPrice());
                }
            }
            break;


            case 2:
                System.out.print("🔤 검색할 키워드를 입력하세요: ");
                String keyword = sc.nextLine();

                List<MenuVO> searchList = mdao.getStringSearchMenu(keyword);
                System.out.println("\n🔎 [이름 검색 결과]");
                for (MenuVO m : searchList) {
                    System.out.println(m.getMenuID() + " | " + m.getMenuName() + "| 가격: " + m.getPrice());
                }
                break;
            case 3:
                System.out.println("📥 메뉴 등록을 시작합니다.");

                // 전체 카테고리 트리 불러옴
                CategoryDao cdao3 = new CategoryDao();
                List<CategoryVO> mlist = cdao3.selectCategory();

                System.out.println("\n📁 [하위 카테고리 목록]");
                printLeafCategories(mlist, "  ");  // 하위만 출력

                System.out.print("\n✅ 등록할 하위 카테고리 ID 입력 > ");
                int subId = sc.nextInt();
                sc.nextLine();

                System.out.print("🍽️ 메뉴 이름: ");
                String name = sc.nextLine();

                System.out.print("💰 가격: ");
                int price = sc.nextInt();
                sc.nextLine();

                MenuVO menu = new MenuVO();
                menu.setMenuName(name);
                menu.setCategoryId(subId);
                menu.setPrice(price);

                mdao.insertMenu(menu);
                System.out.println("✅ 메뉴가 성공적으로 등록되었습니다.");
                break;

            default:
                System.out.println("❌ 잘못된 입력입니다.");
        }
    }


}
