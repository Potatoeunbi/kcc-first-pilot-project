package com.firstproject.cooook.view;

import java.util.Scanner;

import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.util.Util;

public class MainView {
	private String rollName;
	private Scanner sc = new Scanner(System.in);
	
	public MainView(String rollName) {
		this.rollName = rollName;
	}

    public void showMenu() {
        while (true) {
        	System.out.println("\n========================================\n");
        	if (rollName.equals("관리자")) {
        		System.out.println("📋 관리자 메뉴\n");
                System.out.println("1. 작업자 관리");
                System.out.println("2. 권한 관리");
                System.out.println("3. 주문 관리");
                System.out.println("4. 재료 관리");
                System.out.println("5. 카테고리 관리");
                System.out.println("0. 로그아웃");
        	} else {
                System.out.println("📋 작업자 메뉴");
                System.out.println("1. 주문 관리");	
        	}
        	
            System.out.println("\n========================================\n");
            System.out.print("메뉴 선택 ▶ ");
            
            String input = sc.next();            
            if (!Util.isInteger(input)) continue;
    		
    		int choice =  Integer.parseInt(input);
    		
            switch (choice) {
                case 1:
                	if (rollName.equals("관리자")) {
                        new StaffManageView().run();                		
                	} else {
                        new OrderManageView().run();
                	}                	
                    break;
                case 2:
                    new RoleManageView().run();
                    break;
                case 3:
                    new OrderManageView().run();
                    break;
                case 4:
//                    new IngredientManageView().run();
                    break;
                case 5:
                    new CategoryView().showMenu();
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
