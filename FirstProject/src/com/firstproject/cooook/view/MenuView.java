package com.firstproject.cooook.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.dao.CategoryDao;
import com.firstproject.cooook.dao.MenuDao;
import com.firstproject.cooook.vo.CategoryVO;
import com.firstproject.cooook.vo.MenuVO;
import com.firstproject.cooook.vo.StaffVO;
import com.firstproject.cooook.vo.UpdateMenuVO;

public class MenuView {
    Scanner sc = new Scanner(System.in);
    MenuDao mdao = new MenuDao();
    CategoryDao cdao = new CategoryDao();
    StaffVO loginUser = Session.getCurrentUser();

    public void runMenu() {
        while (true) {
        	UIHelper.printTitle("메뉴 관리");
            System.out.println("1. 카테고리별 메뉴 검색");
            System.out.println("2. 메뉴 이름 키워드 검색");
            System.out.println("3. 메뉴 등록	");
            System.out.println("4. 메뉴 삭제");
            System.out.println("5. 메뉴 수정");
            System.out.println("9. 전체 카테고리 + 메뉴 트리 보기");
            System.out.println("0. 이전으로");
            System.out.print("선택 > ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> searchMenuByCategoryTree();
                case 2 -> searchMenuByKeyword();
                case 3 -> insertMenu();
                case 4 -> deleteMenu();
                case 5 -> updateMenu();
                case 9 -> printCategoryWithMenu(cdao.selectCategory(), "");
                case 0 -> {
                    System.out.println("종료합니다.");
                    return;
                }
                default -> System.out.println("잘못된 선택입니다.");
            }
        }
    }
    
    
    private void printCategoryWithMenuOnlyIfMenuExists(List<CategoryVO> categories, String indent) {
        boolean hasMenu = false;

        for (CategoryVO c : categories) {
            List<MenuVO> menus = mdao.getMenuByCategoryId(c.getCategoryId());

            if (!menus.isEmpty()) {
                if (!hasMenu) {
                    hasMenu = true;
                }

                System.out.println(indent + "📁 " + c.getCategoryName());
                for (MenuVO m : menus) {
                    System.out.println(indent + "   └ 🍽️ ID: " + m.getMenuId()
                        + " | 이름: " + m.getMenuName()
                        + " | 가격: " + m.getPrice() + "원");
                }
            }

            if (c.getChild() != null && !c.getChild().isEmpty()) {
                printCategoryWithMenuOnlyIfMenuExists(c.getChild(), indent + "    ");
            }
        }

        if (!hasMenu && indent.isEmpty()) {
            System.out.println("❌ 해당 카테고리에는 메뉴가 존재하지 않습니다.");
        }
    }

    private void printCategoryTree(List<CategoryVO> list, String prefix) {
        for (CategoryVO c : list) {
            System.out.println(prefix + "▶ ID: " + c.getCategoryId() + " | 이름: " + c.getCategoryName());
            if (c.getChild() != null && !c.getChild().isEmpty()) {
                printCategoryTree(c.getChild(), prefix + "  ");
            }
        }
    }

    private void printCategoryWithMenu(List<CategoryVO> categories, String indent) {
        for (CategoryVO c : categories) {
            System.out.println(indent + "📁 " + c.getCategoryName());

            List<MenuVO> menus = mdao.getMenuByCategoryId(c.getCategoryId());
            if (menus.isEmpty()) {
            } else {
                for (MenuVO m : menus) {
                    System.out.println(indent + "   └ 🍽️ ID: " + m.getMenuId()
                        + " | 이름: " + m.getMenuName()
                        + " | 가격: " + m.getPrice() + "원");
                }
            }

            if (c.getChild() != null && !c.getChild().isEmpty()) {
                printCategoryWithMenu(c.getChild(), indent + "    ");
            }
        }
    }

    private void searchMenuByCategoryTree() {
    	UIHelper.printTitle("카테고리 검색");
        List<CategoryVO> tree = cdao.selectCategory();
        printCategoryTree(tree, "");

        System.out.print("\n검색을 원하는 카테고리를 입력하세요 > ");
        int selectedId = Integer.parseInt(sc.nextLine());

        CategoryVO root = findCategoryById(tree, selectedId);
        if (root == null) {
            System.out.println("❌ 존재하지 않는 카테고리입니다.");
            return;
        }

        printCategoryWithMenuOnlyIfMenuExists(List.of(root), "");
    }

    private CategoryVO findCategoryById(List<CategoryVO> list, int targetId) {
        for (CategoryVO c : list) {
            if (c.getCategoryId() == targetId) return c;
            if (c.getChild() != null) {
                CategoryVO found = findCategoryById(c.getChild(), targetId);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void searchMenuByKeyword() {
    	UIHelper.printTitle("메뉴 이름 검색");
        System.out.print("검색할 키워드 입력 > ");
        String keyword = sc.nextLine();

        List<MenuVO> menus = mdao.getStringSearchMenu(keyword);
        if (menus.isEmpty()) {
            System.out.println("해당 키워드를 포함하는 메뉴가 없습니다.");
        } else {
            System.out.println(" 검색 결과");
            for (MenuVO menu : menus) {
                System.out.println("- 메뉴 ID: " + menu.getMenuId() + ", 이름: " + menu.getMenuName() + ", 가격: " + menu.getPrice());
            }
        }
    }

    private void deleteMenu() {
    	UIHelper.printTitle("메뉴 삭제");
    	if (loginUser == null || loginUser.getRoleId() != 1) {
    	    System.out.println("❌ 관리자만 접근할 수 있는 기능입니다.");
    	    return;
    	}
        printCategoryWithMenu(cdao.selectCategory(), "");
        System.out.print("삭제할 메뉴 ID 입력 > ");
        int menuId = Integer.parseInt(sc.nextLine());

        System.out.print("정말 삭제하시겠습니까? (y/n) > ");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            mdao.deleteMenu(menuId);
        } else {
            System.out.println("삭제가 취소되었습니다.");
        }
    }

    public void insertMenu() {
    	UIHelper.printTitle("메뉴 등록");
    	if (loginUser == null || loginUser.getRoleId() != 1) {
    	    System.out.println("❌ 관리자만 접근할 수 있는 기능입니다.");
    	    return;
    	}
        System.out.print("메뉴 이름: ");
        String name = sc.nextLine();
        System.out.print("가격: ");
        int price = Integer.parseInt(sc.nextLine());

        List<CategoryVO> categories = cdao.selectCategory();
        System.out.println("카테고리 목록:");
        printCategoryTree(categories, "  ");

        System.out.print("카테고리 ID들 (쉼표로 구분) 입력 > ");
        String[] inputs = sc.nextLine().split(",");
        List<Integer> categoryIds = new ArrayList<>();
        for (String input : inputs) {
            categoryIds.add(Integer.parseInt(input.trim()));
        }

        MenuVO menu = new MenuVO();
        menu.setMenuName(name);
        menu.setPrice(price);
        menu.setCategoryIds(categoryIds);

        mdao.insertMenu(menu);
        System.out.println("✅ 메뉴가 등록되었습니다.");
    }

    public void updateMenu() {
    	UIHelper.printTitle("메뉴 수정");
    	if (loginUser == null || loginUser.getRoleId() != 1) {
    	    System.out.println("❌ 관리자만 접근할 수 있는 기능입니다.");
    	    return;
    	}
        System.out.println("📋 전체 카테고리 및 메뉴 트리:");
        printCategoryWithMenu(cdao.selectCategory(), "");

        System.out.print("수정할 메뉴 ID 입력 > ");
        int menuId = Integer.parseInt(sc.nextLine());

        UpdateMenuVO detail = mdao.selectMenuDetailById(menuId);
        if (detail == null) {
            System.out.println("❌ 해당 메뉴가 존재하지 않습니다.");
            System.out.println("※ 메뉴 ID는 '🍽️' 아이콘 옆에 있는 숫자입니다.");
            return;
        }

        UpdateMenuVO update = new UpdateMenuVO();
        update.setMenuId(menuId);

        System.out.print("새 이름 입력 (그대로면 엔터): ");
        String name = sc.nextLine();
        if (!name.isEmpty()) update.setMenuName(name);

        System.out.print("새 가격 입력 (그대로면 엔터): ");
        String priceInput = sc.nextLine();
        if (!priceInput.isEmpty()) update.setPrice(Integer.parseInt(priceInput));

        System.out.print("카테고리를 새로 지정하시겠습니까? (y/n): ");
        String changeCat = sc.nextLine();
        if (changeCat.equalsIgnoreCase("y")) {
            List<CategoryVO> flatList = cdao.selectCategoryFlat();
            List<Integer> validIds = flatList.stream().map(CategoryVO::getCategoryId).toList();

            System.out.print("새 상위 카테고리 ID 입력 > ");
            int parentId = Integer.parseInt(sc.nextLine());

            System.out.print("새 하위 카테고리 ID 입력 > ");
            int childId = Integer.parseInt(sc.nextLine());

            if (!validIds.contains(parentId) || !validIds.contains(childId)) {
                System.out.println("❌ 잘못된 카테고리입니다. 수정을 취소합니다.");
            } else {
                mdao.deleteMenuCategoryByMenuId(menuId);

                List<Integer> newCategoryIds = new ArrayList<>();
                newCategoryIds.add(parentId);
                newCategoryIds.add(childId);

                mdao.insertMenuCategories(menuId, newCategoryIds);
            }
        }

        int result = mdao.updateMenuByVO(update);
        if (result > 0) {
            System.out.println("✅ 메뉴가 성공적으로 수정되었습니다.");
        } else {
            System.out.println("❌ 메뉴 수정에 실패했습니다.");
        }
    }
}
