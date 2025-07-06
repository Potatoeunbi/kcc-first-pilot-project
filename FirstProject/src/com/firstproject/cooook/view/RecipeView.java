package com.firstproject.cooook.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.dao.IngredientDao;
import com.firstproject.cooook.dao.RecipeDao;
import com.firstproject.cooook.vo.IngredientVO;
import com.firstproject.cooook.vo.MenuVO;
import com.firstproject.cooook.vo.RecipeVO;
import com.firstproject.cooook.vo.StaffVO;

public class RecipeView {
		private Scanner sc = new Scanner(System.in);
	    private RecipeDao rdao = new RecipeDao();
	    private IngredientDao idao = new IngredientDao();  
	    StaffVO loginUser = Session.getCurrentUser();

	    public void run() {
	        while (true) {
	            System.out.println("\n📦 레시피 관리");
	            System.out.println("1. 레시피 조회");
	            System.out.println("2. 레시피 등록(관리자만 가능합니다.)");
	            System.out.println("3. 레시피 업데이트(관리자만 가능합니다.)");
	            System.out.println("4. 레시피 삭제(관리자만 가능합니다.)");
	            System.out.println("0. 이전으로");
	            System.out.print("선택 > ");
	            String input = sc.nextLine();

	            switch (input) {
	                case "1" -> showRecipeList();
	                case "2" -> addRecipe();
	                case "3" -> updateRecipe();
	                case "4" -> deleteEntireRecipe();
	                case "0" -> { return; }
	                default -> System.out.println("❗ 올바른 번호를 입력하세요.");
	            }
	        }
	    }

	    public void showRecipeList() {
	        // 1. 레시피가 등록된 메뉴 목록 출력
	        List<MenuVO> menus = rdao.selectMenusWithRecipe();  // menuDao가 아니라 recipeDao

	        if (menus.isEmpty()) {
	            System.out.println("⚠️ 레시피가 등록된 메뉴가 없습니다.");
	            return;
	        }

	        System.out.println("\n[ 레시피가 있는 메뉴 목록 ]");
	        System.out.println("--------------------------------");
	        System.out.printf("%-5s %-20s %-8s\n", "ID", "메뉴명", "가격");

	        for (MenuVO menu : menus) {
	            System.out.printf("%-5d %-20s %,8d원\n",
	                menu.getMenuId(),
	                menu.getMenuName(),
	                menu.getPrice()
	            );
	        }

	        try {
	            // 2. 메뉴 ID 입력
	            Scanner sc = new Scanner(System.in);
	            System.out.print("\n레시피를 조회할 메뉴 ID를 입력하세요: ");
	            int menuId = Integer.parseInt(sc.nextLine());

	            // 메뉴 유효성 확인
	            boolean valid = menus.stream().anyMatch(m -> m.getMenuId() == menuId);
	            if (!valid) {
	                System.out.println("❌ 잘못된 입력입니다. (해당 메뉴는 레시피가 등록된 메뉴가 아닙니다)");
	                return;
	            }

	            // 3. 해당 메뉴의 레시피 조회
	            List<RecipeVO> recipes = rdao.selectByMenuId(menuId);

	            if (recipes.isEmpty()) {
	                System.out.println("❌ 해당 메뉴에 등록된 레시피가 없습니다.");
	                return;
	            }

	            System.out.println("\n[" + recipes.get(0).getMenuName() + "] 레시피 목록");
	            System.out.println("--------------------------------------------------");
	            System.out.printf("%-5s %-12s %-7s %-5s %s\n", "재료ID", "재료", "수량", "단위", "설명");

	            for (RecipeVO vo : recipes) {
	                System.out.printf("%-5d %-12s %-7.1f %-5s %s\n",
	                    vo.getRecipeId(),
	                    vo.getIngredientName(),
	                    vo.getQuantity(),
	                    vo.getUnit(),
	                    vo.getDescription()
	                );
	            }

	        } catch (NumberFormatException e) {
	            System.out.println("❌ 잘못된 입력입니다. (숫자를 입력해야 합니다)");
	        } catch (Exception e) {
	            System.out.println("❌ 예외 발생: " + e.getMessage());
	        }
	    }


	    public void addRecipe() {
	    	if (loginUser == null || loginUser.getRoleId() != 1) {
	    	    System.out.println("❌ 관리자만 접근할 수 있는 기능입니다.");
	    	    return;
	    	}
	        Scanner sc = new Scanner(System.in);

	        // 1. 레시피가 아직 등록되지 않은 메뉴 목록 출력
	        List<MenuVO> availableMenus = rdao.selectMenusWithoutRecipe();
	        if (availableMenus.isEmpty()) {
	            System.out.println("⚠️ 등록 가능한 메뉴가 없습니다. 모든 메뉴에 레시피가 등록되어 있습니다.");
	            return;
	        }

	        System.out.println("[ 레시피 등록 대상 메뉴 목록 ]");
	        for (MenuVO menu : availableMenus) {
	            System.out.printf("%d. %s (%,d원)\n", menu.getMenuId(), menu.getMenuName(), menu.getPrice());
	        }

	        try {
	            // 2. 메뉴 ID 입력
	            System.out.print("레시피를 등록할 메뉴 ID 입력: ");
	            int menuId = Integer.parseInt(sc.nextLine());

	            // 등록 대상 메뉴인지 확인
	            boolean menuExists = availableMenus.stream().anyMatch(m -> m.getMenuId() == menuId);
	            if (!menuExists) {
	                System.out.println("❌ 잘못된 입력입니다. (해당 메뉴는 등록 대상이 아닙니다)");
	                return;
	            }

	            // 3. 전체 재료 출력
	            List<IngredientVO> ingredients = idao.selectAll();
	            System.out.println("\n[ 재료 목록 ]");
	            for (IngredientVO ing : ingredients) {
	                System.out.printf("%d. %s [%s]\n", ing.getIngredientId(), ing.getIngredientName(), ing.getUnitDefault());
	            }

	            // 4. 재료 정보 입력 (콤마로 구분)
	            System.out.print("재료 ID들 (콤마로 구분): ");
	            String[] ingredientIds = sc.nextLine().split(",");

	            System.out.print("수량들 (콤마로 구분): ");
	            String[] quantities = sc.nextLine().split(",");

	            System.out.print("단위들 (콤마로 구분): ");
	            String[] units = sc.nextLine().split(",");

	            System.out.print("설명들 (콤마로 구분, 없으면 엔터): ");
	            String line = sc.nextLine();
	            String[] descriptions = line.isBlank() ? new String[ingredientIds.length] : line.split(",");

	            // 5. 유효성 검사
	            if (ingredientIds.length != quantities.length ||
	                quantities.length != units.length ||
	                units.length != descriptions.length) {
	                System.out.println("❌ 잘못된 입력입니다. (입력 개수 불일치)");
	                return;
	            }

	            // 6. insert 반복
	            for (int i = 0; i < ingredientIds.length; i++) {
	                int ingId = Integer.parseInt(ingredientIds[i].trim());
	                double qty = Double.parseDouble(quantities[i].trim());
	                if (qty <= 0) {
	                    System.out.println("❌ 잘못된 입력입니다. (수량은 양수여야 합니다)");
	                    return;
	                }

	                RecipeVO vo = new RecipeVO();
	                vo.setMenuId(menuId);
	                vo.setCategoryId(ingId);
	                vo.setQuantity(qty);
	                vo.setUnit(units[i].trim());

	                String desc = (descriptions[i] != null) ? descriptions[i].trim() : "";
	                vo.setDescription(desc);

	                rdao.recipeInsert(vo);
	            }

	            System.out.println("✅ 레시피 등록 완료!");

	        } catch (NumberFormatException e) {
	            System.out.println("❌ 잘못된 입력입니다. (숫자를 입력해야 하는 항목에 문자가 입력됨)");
	        } catch (Exception e) {
	            System.out.println("❌ 예외 발생: " + e.getMessage());
	        }
	    }

	    public void updateRecipe() {
	    	if (loginUser == null || loginUser.getRoleId() != 1) {
	    	    System.out.println("❌ 관리자만 접근할 수 있는 기능입니다.");
	    	    return;
	    	}
	        List<MenuVO> menus = rdao.selectMenusWithRecipe();
	        if (menus.isEmpty()) {
	            System.out.println("⚠️ 레시피가 등록된 메뉴가 없습니다.");
	            return;
	        }

	        Scanner sc = new Scanner(System.in);

	        System.out.println("[ 레시피 수정 대상 메뉴 목록 ]");
	        for (MenuVO m : menus) {
	            System.out.printf("%d. %s (%,d원)\n", m.getMenuId(), m.getMenuName(), m.getPrice());
	        }

	        try {
	            System.out.print("수정할 메뉴 ID 입력: ");
	            int menuId = Integer.parseInt(sc.nextLine());

	            List<RecipeVO> recipes = rdao.selectByMenuId(menuId);
	            if (recipes.isEmpty()) {
	                System.out.println("❌ 해당 메뉴에 등록된 레시피가 없습니다.");
	                return;
	            }

	            // 기존 레시피 출력
	            System.out.println("\n[" + recipes.get(0).getMenuName() + "] 기존 레시피");
	            System.out.printf("%-5s %-12s %-7s %-5s %s\n", "ID", "재료", "수량", "단위", "설명");
	            for (RecipeVO vo : recipes) {
	                System.out.printf("%-5d %-12s %-7.1f %-5s %s\n",
	                    vo.getRecipeId(), vo.getIngredientName(),
	                    vo.getQuantity(), vo.getUnit(), vo.getDescription());
	            }

	            // 🔄 임시 저장 리스트
	            List<Integer> toDelete = new ArrayList<>();
	            List<RecipeVO> toUpdate = new ArrayList<>();
	            List<RecipeVO> toInsert = new ArrayList<>();

	            // 1. 삭제
	            System.out.print("\n삭제할 재료 ID 입력 (콤마, 없으면 엔터): ");
	            String deleteLine = sc.nextLine();
	            if (!deleteLine.isBlank()) {
	                String[] delIds = deleteLine.split(",");
	                for (String id : delIds) {
	                    toDelete.add(Integer.parseInt(id.trim()));
	                }
	            }

	            // 2. 수정
	            System.out.print("수정할 재료 ID 입력 (콤마, 없으면 엔터): ");
	            String updateLine = sc.nextLine();
	            if (!updateLine.isBlank()) {
	                String[] updateIds = updateLine.split(",");
	                for (String id : updateIds) {
	                    int rid = Integer.parseInt(id.trim());

	                    RecipeVO target = recipes.stream()
	                        .filter(r -> r.getRecipeId() == rid)
	                        .findFirst()
	                        .orElse(null);

	                    if (target == null) {
	                        System.out.println("❌ 해당 ID를 찾을 수 없습니다: " + rid);
	                        continue;
	                    }

	                    System.out.print("[" + target.getIngredientName() + "] 새 수량 (" + target.getQuantity() + "): ");
	                    String q = sc.nextLine();
	                    if (!q.isBlank()) target.setQuantity(Double.parseDouble(q));

	                    System.out.print("[" + target.getIngredientName() + "] 새 단위 (" + target.getUnit() + "): ");
	                    String u = sc.nextLine();
	                    if (!u.isBlank()) target.setUnit(u);

	                    System.out.print("[" + target.getIngredientName() + "] 새 설명 (" + target.getDescription() + "): ");
	                    String d = sc.nextLine();
	                    if (!d.isBlank()) target.setDescription(d);

	                    toUpdate.add(target);
	                }
	            }

	            // 3. 추가
	            List<IngredientVO> ingredients = idao.selectAll();
	            System.out.println("\n[ 전체 재료 목록 ]");
	            for (IngredientVO ing : ingredients) {
	                System.out.printf("%d. %s [%s]\n", ing.getIngredientId(), ing.getIngredientName(), ing.getUnitDefault());
	            }

	            System.out.print("\n추가할 재료 ID (콤마, 없으면 엔터): ");
	            String line = sc.nextLine();
	            if (!line.isBlank()) {
	                String[] ingIds = line.split(",");

	                System.out.print("수량들 (콤마로 구분): ");
	                String[] qtys = sc.nextLine().split(",");

	                System.out.print("단위들 (콤마로 구분): ");
	                String[] units = sc.nextLine().split(",");

	                System.out.print("설명들 (콤마로 구분, 없으면 엔터): ");
	                String descLine = sc.nextLine();
	                String[] descs = descLine.isBlank() ? new String[ingIds.length] : descLine.split(",");

	                if (ingIds.length != qtys.length || qtys.length != units.length || units.length != descs.length) {
	                    System.out.println("❌ 잘못된 입력입니다. (입력 개수 불일치)");
	                    return;
	                }

	                for (int i = 0; i < ingIds.length; i++) {
	                    RecipeVO vo = new RecipeVO();
	                    vo.setMenuId(menuId);
	                    vo.setCategoryId(Integer.parseInt(ingIds[i].trim()));
	                    vo.setQuantity(Double.parseDouble(qtys[i].trim()));
	                    vo.setUnit(units[i].trim());
	                    vo.setDescription((descs[i] != null) ? descs[i].trim() : "");
	                    toInsert.add(vo);
	                }
	            }

	            // ✅ 최종 확인
	            System.out.print("\n정말 수정하시겠습니까? (Y/N): ");
	            String confirm = sc.nextLine().trim();

	            if (confirm.equalsIgnoreCase("Y")) {
	                for (int id : toDelete) {
	                    rdao.deleteRecipeById(id);
	                }

	                for (RecipeVO vo : toUpdate) {
	                    rdao.updateRecipe(vo);
	                }

	                for (RecipeVO vo : toInsert) {
	                    rdao.recipeInsert(vo);
	                }

	                System.out.println("✅ 레시피 수정 완료!");
	            } else {
	                System.out.println("⏪ 수정이 취소되었습니다. ");
	            }

	        } catch (NumberFormatException e) {
	            System.out.println("❌ 잘못된 입력입니다. (숫자를 입력해야 하는 항목에 문자 입력됨)");
	        } catch (Exception e) {
	            System.out.println("❌ 예외 발생: " + e.getMessage());
	        }
	    }

	    public void deleteEntireRecipe() {
	    	if (loginUser == null || loginUser.getRoleId() != 1) {
	    	    System.out.println("❌ 관리자만 접근할 수 있는 기능입니다.");
	    	    return;
	    	}
	        List<MenuVO> menus = rdao.selectMenusWithRecipe();
	        if (menus.isEmpty()) {
	            System.out.println("⚠️ 레시피가 등록된 메뉴가 없습니다.");
	            return;
	        }

	        Scanner sc = new Scanner(System.in);

	        System.out.println("[ 레시피 완전 삭제 대상 메뉴 목록 ]");
	        for (MenuVO menu : menus) {
	            System.out.printf("%d. %s (%,d원)\n", menu.getMenuId(), menu.getMenuName(), menu.getPrice());
	        }

	        try {
	            System.out.print("\n레시피를 완전히 삭제할 메뉴 ID를 입력하세요: ");
	            int menuId = Integer.parseInt(sc.nextLine());

	            List<RecipeVO> recipes = rdao.selectByMenuId(menuId);
	            if (recipes.isEmpty()) {
	                System.out.println("❌ 해당 메뉴에 등록된 레시피가 없습니다.");
	                return;
	            }

	            System.out.println("\n[" + recipes.get(0).getMenuName() + "] 레시피가 다음과 같이 등록되어 있습니다:");
	            System.out.printf("%-5s %-12s %-7s %-5s %s\n", "ID", "재료", "수량", "단위", "설명");
	            for (RecipeVO vo : recipes) {
	                System.out.printf("%-5d %-12s %-7.1f %-5s %s\n",
	                    vo.getRecipeId(), vo.getIngredientName(),
	                    vo.getQuantity(), vo.getUnit(), vo.getDescription());
	            }

	            System.out.print("\n정말 이 메뉴의 레시피를 전부 삭제하시겠습니까? (Y/N): ");
	            String confirm = sc.nextLine().trim();

	            if (confirm.equalsIgnoreCase("Y")) {
	                int result = rdao.deleteRecipeByMenuId(menuId);
	                System.out.println("✅ " + " 레시피가 삭제되었습니다.");
	            } else {
	                System.out.println("⏪ 삭제를 취소했습니다.");
	            }

	        } catch (NumberFormatException e) {
	            System.out.println("❌ 숫자를 정확히 입력해주세요.");
	        } catch (Exception e) {
	            System.out.println("❌ 예외 발생: " + e.getMessage());
	        }
	    }



	    
}
