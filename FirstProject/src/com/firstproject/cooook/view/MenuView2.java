package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;
import com.firstproject.cooook.vo.Menu;
import com.firstproject.cooook.dao.MenuRepository;
import com.firstproject.cooook.util.Util;

public class MenuView2 {
    private MenuRepository menuRepository = new MenuRepository();
    private Scanner scanner = new Scanner(System.in);
    
    public void showMenuView() {
        while (true) {
            UIHelper.printTitle("📂 메뉴 관리");
            System.out.println();
            System.out.println("1. 메뉴 조회");
            System.out.println("2. 메뉴 등록");
            System.out.println("3. 메뉴 수정");
            System.out.println("4. 메뉴 삭제");
            System.out.println("0. 메인 메뉴로");
            System.out.println();
            System.out.print("메뉴 선택 ▶: ");
            
            String input = scanner.next();
            if (!Util.isInteger(input)) continue;
            
            int choice = Integer.parseInt(input);
            
            switch (choice) {
                case 1: showMenuList(); break;
                case 2: addMenu();      break;
                case 3: updateMenu();   break;
                case 4: deleteMenu();   break;
                case 0: return;
                default: UIHelper.printError("잘못된 입력입니다.");
            }
        }
    }

    private void showMenuList() {
        List<Menu> menus = menuRepository.getAllMenus();
        UIHelper.printBoxedList("[🍽️ 전체 메뉴 목록]", "등록된 메뉴가 없습니다.", menus, menu -> String.format("ID: %3d | 이름: %s", menu.getMenuId(), menu.getMenuName()));
        printEnter();
    }
    
    private void addMenu() {
        UIHelper.printTitle("📝 새 메뉴 등록");
        System.out.print("메뉴명 입력 ▶: ");
        scanner.nextLine(); // 버퍼 정리
        String menuName = scanner.nextLine().trim();
        
        if (!Util.isNotEmpty(menuName) || !Util.isLengthInRange(menuName, 1, 100)) return;
        
        if (isMenuNameExists(menuName, null)) {
            UIHelper.printError("이미 존재하는 메뉴명입니다.");
            return;
        }

        Menu newMenu = new Menu();
        newMenu.setMenuName(menuName);
        
        if (menuRepository.insertMenu(newMenu) > 0) {
            UIHelper.printSuccess("메뉴가 성공적으로 등록되었습니다!");
        } else {
            UIHelper.printError("메뉴 등록에 실패했습니다.");
        }
        
        printEnter();
    }
    
    private void updateMenu() {
        UIHelper.printTitle("✏️ 메뉴 수정");
        
        List<Menu> menus = menuRepository.getAllMenus();
        
        if (menus.isEmpty()) {
            UIHelper.printWarning("수정할 메뉴가 없습니다.");
            return;
        }
        
        System.out.println("수정할 메뉴를 선택하세요:");
        UIHelper.printBoxedList("🍽️ 메뉴 목록", menus, menu -> String.format("ID: %3d | 이름: %s", menu.getMenuId(), menu.getMenuName()));
        
        System.out.print("메뉴 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int menuId = Integer.parseInt(input);
        Menu targetMenu = menuRepository.getMenuById(menuId);
        
        if (targetMenu == null) {
            UIHelper.printError("존재하지 않는 메뉴 ID입니다.");
            return;
        }
        
        System.out.print("새 메뉴명 입력 ▶: ");
        scanner.nextLine(); // 버퍼 정리
        String newName = scanner.nextLine().trim();
        
        if (!Util.isNotEmpty(newName) || !Util.isLengthInRange(newName, 1, 100)) return;
        
        if (isMenuNameExists(newName, menuId)) {
            UIHelper.printError("이미 존재하는 메뉴명입니다.");
            return;
        }
        
        targetMenu.setMenuName(newName);
        
        if (menuRepository.updateMenu(targetMenu)) {
            UIHelper.printSuccess("메뉴가 성공적으로 수정되었습니다!");
        } else {
            UIHelper.printError("메뉴 수정에 실패했습니다.");
        }
        
        printEnter();
    }

    private void deleteMenu() {
        UIHelper.printTitle("🗑️ 메뉴 삭제");
        
        List<Menu> menus = menuRepository.getAllMenus();
        
        if (menus.isEmpty()) {
            UIHelper.printWarning("삭제할 메뉴가 없습니다.");
            return;
        }
        
        System.out.println("삭제할 메뉴를 선택하세요:");
        UIHelper.printBoxedList("🍽️ 메뉴 목록", menus, menu -> String.format("ID: %3d | 이름: %s", menu.getMenuId(), menu.getMenuName()));
        
        System.out.print("메뉴 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int menuId = Integer.parseInt(input);
        Menu targetMenu = menuRepository.getMenuById(menuId);
        
        if (targetMenu == null) {
            UIHelper.printError("존재하지 않는 메뉴 ID입니다.");
            return;
        }
        
        System.out.println("정말로 '" + targetMenu.getMenuName() + "' 메뉴를 삭제하시겠습니까? (y/N)");
        System.out.print("확인 ▶: ");
        String confirm = scanner.next();
        
        if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
            if (menuRepository.deleteMenu(menuId)) {
                UIHelper.printSuccess("메뉴가 성공적으로 삭제되었습니다!");
            } else {
                UIHelper.printError("메뉴 삭제에 실패했습니다.");
            }
        } else {
            UIHelper.printWarning("삭제가 취소되었습니다.");
        }
        
        printEnter();
    }
    
    private boolean isMenuNameExists(String menuName, Integer excludeId) {
        List<Menu> menus = menuRepository.getAllMenus();
        
        for (Menu menu : menus) {
            if (menu.getMenuName().equals(menuName)) {
                if (excludeId != null && menu.getMenuId() == excludeId) continue;
                return true;
            }
        }
        return false;
    }

    private void printEnter() {
        System.out.print("계속하려면 Enter를 누르세요...");
        scanner.nextLine(); // 버퍼 정리
        scanner.nextLine(); // 실제 입력 대기
    }
} 