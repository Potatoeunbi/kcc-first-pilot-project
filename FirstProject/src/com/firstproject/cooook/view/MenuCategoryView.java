package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.MenuCategoryDAO;
import com.firstproject.cooook.dao.CategoryDao;
import com.firstproject.cooook.vo.CategoryVO;
import com.firstproject.cooook.vo.Menu;
import com.firstproject.cooook.dao.MenuRepository;
import com.firstproject.cooook.vo.MenuCategory;
import com.firstproject.cooook.util.Util;

public class MenuCategoryView {
    private MenuCategoryDAO menuCategoryDAO = new MenuCategoryDAO();
    private MenuRepository menuDAO = new MenuRepository();
    private CategoryDao categoryDAO = new CategoryDao();
    private Scanner scanner = new Scanner(System.in);

    public void showMenuCategoryView() {
        while (true) {
            UIHelper.printTitle("📂 메뉴-카테고리 연결 관리");
            System.out.println();
            System.out.println("1. 메뉴-카테고리 연결 조회");
            System.out.println("2. 메뉴-카테고리 연결 등록");
            System.out.println("3. 메뉴-카테고리 연결 수정");
            System.out.println("4. 메뉴-카테고리 연결 삭제");
            System.out.println("0. 메인 메뉴로");
            System.out.println();
            System.out.print("메뉴 선택 ▶: ");
            
            String input = scanner.next();
            if (!Util.isInteger(input)) continue;
            
            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1: showMenuCategoryList(); break;
                case 2: addMenuCategory();      break;
                case 3: updateMenuCategory();   break;
                case 4: deleteMenuCategory();   break;
                case 0: return;
                default: UIHelper.printError("잘못된 입력입니다.");
            }
        }
    }

    private void showMenuCategoryList() {
        List<MenuCategory> menuCategories = menuCategoryDAO.getAllMenuCategories();
        UIHelper.printMenuCategoryTree(menuCategories);     
        printEnter();
    }

    private void addMenuCategory() {
        UIHelper.printTitle("📝 새 메뉴-카테고리 연결 등록");
        
        System.out.println("메뉴를 선택하세요.");
        List<Menu> menus = menuDAO.getAllMenus();
        UIHelper.printBoxedList("🍽️ 메뉴 목록", menus, menu -> String.format("ID: %3d | 이름: %s", menu.getMenuId(), menu.getMenuName()));
        System.out.print("메뉴 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int menuId = Integer.parseInt(input);
        Menu targetMenu = menuDAO.getMenuById(menuId);
        
        if (targetMenu == null) {
            UIHelper.printError("존재하지 않는 메뉴 ID입니다.");
            return;
        }
        
        System.out.println("카테고리를 선택하세요.");
        List<CategoryVO> categories = categoryDAO.selectCategory();
        UIHelper.printCategoryTree(categories);
        System.out.print("카테고리 ID ▶: ");
        input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int categoryId = Integer.parseInt(input);
        CategoryVO targetCategory = categoryDAO.selectCategoryById(categoryId);
        
        if (targetCategory == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return;
        }
        
        MenuCategory newMenuCategory = new MenuCategory();
        newMenuCategory.setMenuId(menuId);
        newMenuCategory.setCategoryId(categoryId);
        
        if (menuCategoryDAO.insertMenuCategory(newMenuCategory) > 0) {
            UIHelper.printSuccess("메뉴-카테고리 연결이 성공적으로 등록되었습니다!");
        } else {
            UIHelper.printError("메뉴-카테고리 연결 등록에 실패했습니다.");
        }
        
        printEnter();
    }

    private void updateMenuCategory() {
        UIHelper.printTitle("✏️ 메뉴-카테고리 연결 수정");
        
        List<MenuCategory> menuCategories = menuCategoryDAO.getAllMenuCategories();
        
        if (menuCategories.isEmpty()) {
            UIHelper.printWarning("수정할 메뉴-카테고리 연결이 없습니다.");
            return;
        }
        
        System.out.println("수정할 메뉴-카테고리 연결을 선택하세요.");
        UIHelper.printMenuCategoryTree(menuCategories);
        
        System.out.println("메뉴를 선택하세요.");
        System.out.print("메뉴 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int menuId = Integer.parseInt(input);
        Menu targetMenu = menuDAO.getMenuById(menuId);
        
        if (targetMenu == null) {
            UIHelper.printError("존재하지 않는 메뉴 ID입니다.");
            return;
        }
        
        System.out.println("카테고리를 선택하세요.");
        System.out.print("카테고리 ID ▶: ");
        input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int categoryId = Integer.parseInt(input);
        CategoryVO targetCategory = categoryDAO.selectCategoryById(categoryId);
        
        if (targetCategory == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return;
        }
        
        MenuCategory targetMenuCategory = menuCategoryDAO.getMenuCategoryById(menuId, categoryId);
        
        if (targetMenuCategory == null) {
            UIHelper.printError("존재하지 않는 메뉴-카테고리 연결 ID입니다.");
            return;
        }
        
        System.out.println("변경할 카테고리를 선택하세요.");
        System.out.print("카테고리 ID ▶: ");
        input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int newCategoryId = Integer.parseInt(input);
        CategoryVO newCategory = categoryDAO.selectCategoryById(newCategoryId);
        
        if (newCategory == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return;
        }

        if (newCategoryId == targetCategory.getCategoryId()) {
            UIHelper.printWarning("이미 선택한 카테고리입니다.");
            return;
        }
        
        if (menuCategoryDAO.updateMenuCategory(targetMenuCategory, newCategoryId) > 0) {
            UIHelper.printSuccess("메뉴-카테고리 연결이 성공적으로 수정되었습니다!");
        } else {
            UIHelper.printError("메뉴-카테고리 연결 수정에 실패했습니다.");
        }

        printEnter();
    }

    private void deleteMenuCategory() {
        UIHelper.printTitle("❌ 메뉴-카테고리 연결 삭제");
        
        List<MenuCategory> menuCategories = menuCategoryDAO.getAllMenuCategories();
        
        if (menuCategories.isEmpty()) {
            UIHelper.printWarning("삭제할 메뉴-카테고리 연결이 없습니다.");
            return;
        }
        
        System.out.println("삭제할 메뉴-카테고리 연결을 선택하세요.");
        UIHelper.printMenuCategoryTree(menuCategories);

        System.out.println("메뉴를 선택하세요.");
        System.out.print("메뉴 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int menuId = Integer.parseInt(input);
        Menu targetMenu = menuDAO.getMenuById(menuId);
        
        if (targetMenu == null) {
            UIHelper.printError("존재하지 않는 메뉴 ID입니다.");
            return;
        }

        System.out.println("카테고리를 선택하세요.");
        System.out.print("카테고리 ID ▶: ");
        input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int categoryId = Integer.parseInt(input);
        CategoryVO targetCategory = categoryDAO.selectCategoryById(categoryId);
        
        if (targetCategory == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return;
        }

        MenuCategory targetMenuCategory = menuCategoryDAO.getMenuCategoryById(menuId, categoryId);
        
        if (targetMenuCategory == null) {
            UIHelper.printError("존재하지 않는 메뉴-카테고리 연결 ID입니다.");
            return;
        }
        

        System.out.println("정말로 '" + targetMenu.getMenuName() + " - " + targetCategory.getCategoryName() + "' 메뉴-카테고리 연결을 삭제하시겠습니까? (y/N)");
        System.out.print("확인 ▶: ");        
        String confirm = scanner.next();
        
        if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
            if (menuCategoryDAO.deleteMenuCategory(targetMenuCategory.getMenuId(), targetMenuCategory.getCategoryId()) > 0) {
            UIHelper.printSuccess("메뉴-카테고리 연결이 성공적으로 삭제되었습니다!");
        } else {
            UIHelper.printError("메뉴-카테고리 연결 삭제에 실패했습니다.");
        }
        } else {
            UIHelper.printWarning("삭제가 취소되었습니다.");
        }
        
        printEnter();
    }

    private void printEnter() {
        System.out.print("계속하려면 Enter를 누르세요...");
        scanner.nextLine(); // 버퍼 정리
        scanner.nextLine(); // 실제 입력 대기
    }
}
