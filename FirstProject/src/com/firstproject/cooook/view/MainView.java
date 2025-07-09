package com.firstproject.cooook.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.firstproject.cooook.common.RoleFeatureCode;
import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.dao.RoleDao;
import com.firstproject.cooook.util.Util;
import com.firstproject.cooook.vo.StaffVO;

public class MainView {
	private Scanner sc = new Scanner(System.in);


    public void showMenu() {           
        StaffVO staff = Session.getCurrentUser();
        
        RoleDao roleFeatureDao = new RoleDao();
        List<String> featureCodes = roleFeatureDao.getFeaturesByRoleId(staff.getRoleId());

        while (true) {     
           UIHelper.printTitle("📋 " + staff.getRoleName() + " 메뉴");
           int index = 1;
           Map<Integer, String> menuIndexToFeature = new LinkedHashMap<>();
           for (String featureCode : featureCodes) {
               String label = RoleFeatureCode.FEATURE_NAME_MAP.getOrDefault(featureCode, featureCode);
               System.out.println(index + ". " + label);
               menuIndexToFeature.put(index, featureCode);
               index++;
           }
            
           System.out.println("0. 로그아웃");
           System.out.println("999. 출력 길이 수정");
           System.out.println();
           System.out.print("메뉴 선택 ▶ ");

           int choice = -1;
           
           try {
        	   choice = Integer.parseInt(sc.nextLine());
           } catch (NumberFormatException e) {
        	   UIHelper.printError("유효한 숫자를 입력해주세요.");
        	   continue;
           }
           
           if (choice == 0) {
               System.out.println("🔒 로그아웃 되었습니다.");
               return;
           }

           if (choice == 999) {
                System.out.print("변경할 길이를 입력하세요. (최소길이 50) ▶: ");
                String input = sc.next();
                if (!Util.isInteger(input)) continue;
                
                int count = Integer.parseInt(input);

                if (count < 50) {
                    UIHelper.printError("50보다 큰 값을 입력하세요.");
                    continue;
                }

                UIHelper.setCount(count);
                continue;
           }

           String selectedFeature = menuIndexToFeature.get(choice);
           if (selectedFeature == null) {
        	   UIHelper.printError("잘못된 입력입니다.");
               return;
           }

           // 기능별 실행
           switch (selectedFeature) {
               case RoleFeatureCode.WORKER_MANAGE -> new StaffManageView().run();
               case RoleFeatureCode.ROLE_MANAGE -> new RoleManageView().run();
               case RoleFeatureCode.ORDER_MANAGE -> new OrderManageView().run();
               case RoleFeatureCode.MATERIAL_MANAGE -> new RecipeView().run();
               case RoleFeatureCode.CATEGORY_MANAGE -> new CategoryView().showMenu();
               case RoleFeatureCode.MENU_MANAGE -> new MenuView2().showMenuView();
               case RoleFeatureCode.MENU_CATEGORY_MANAGE -> new MenuCategoryView().showMenuCategoryView();
                
               default -> UIHelper.printError("구현되지 않은 기능입니다..");
           }
        }
    }
    
}
