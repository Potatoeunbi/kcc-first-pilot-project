package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.CategoryDao;
import com.firstproject.cooook.util.Util;
import com.firstproject.cooook.vo.CategoryVO;

public class CategoryView {
	static Scanner scanner = new Scanner(System.in);
	
	public void showMenu() {
		while (true) {
        	System.out.println("\n========================================\n");
            System.out.println("📋 카테고리 관리\n");
            System.out.println("1. 카테고리 조회");
            System.out.println("2. 카테고리 추가");
            System.out.println("3. 카테고리 수정");
            System.out.println("4. 카테고리 삭제");
            System.out.println("0. 나가기");
            System.out.println("\n========================================\n");
            System.out.print("메뉴 선택 ▶ ");
            
            String input = scanner.next();            
            if (!Util.isInteger(input)) continue;
    		
    		int menu =  Integer.parseInt(input);

			switch (menu) {
				case 1:	selectCategory();	break;
				case 2:	insertCategory();	break;
				case 3:	updateCategory();	break;
				case 4:	deleteCategory();	break;
				case 0:	return;
				default: System.out.println("❗ 잘못된 입력입니다.");
			}
		}
	}
	
	public void insertCategory() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n============= [📋 카테고리 추가] =============\n");
		System.out.print("상위 카테고리 번호를 입력하세요. (0번은 루트노드) :");
		
        String input = scanner.next();
        
        if (!Util.isInteger(input)) return;
		
		int parentCategoryId =  Integer.parseInt(input);

		System.out.print("카테고리를 입력하세요. : ");
		String categoryName = scanner.next();
		
		CategoryVO category = new CategoryVO();
		category.setCategoryId(0);
		category.setCategoryName(categoryName);
		category.setParentId(parentCategoryId);
		
		CategoryDao categoryDao = new CategoryDao();
		categoryDao.insertCategory(category);
		
        System.out.println("\n✅ 카테고리 등록 완료!");
        System.out.println("\n========================================\n");

		selectCategory();
	}
	
	public void updateCategory() {
		System.out.println("카테고리를 수정합니다.");
		System.out.print("변경할 카테고리 번호를 입력하세요. :");

        String input = scanner.next();        
        if (!Util.isInteger(input)) return;

		CategoryVO category = new CategoryVO();
		category.setCategoryId(Integer.parseInt(input));
				
		System.out.print("카테고리 이름은 0번, 위치는 1번을 입력하세요.");		
		input = scanner.next();        
        if (!Util.isInteger(input)) return;

		int updateType = Integer.parseInt(input);
		
		if (!(updateType == 0 || updateType == 1)) {
			System.out.println("잘못입력했습니다.");
			return;
		}

		if (updateType == 0) {
			System.out.print("카테고리를 입력하세요. : ");			
			category.setCategoryName(scanner.next());
		} else {
			System.out.print("상위 카테고리 번호를 입력하세요. (0번은 루트노드) :");			
			input = scanner.next();        
	        if (!Util.isInteger(input)) return;
	        
			category.setParentId(Integer.parseInt(input));
		}
		
		CategoryDao categoryDao = new CategoryDao();
		categoryDao.updateCategory(category, updateType);		
		
		selectCategory();			
	}
	

	public void deleteCategory() {
		System.out.println("카테고리를 삭제합니다.");

		System.out.print("삭제할 카테고리 번호를 입력하세요. :");
		int categoryId = scanner.nextInt();
		
		CategoryDao categoryDao = new CategoryDao();
		List<CategoryVO> categories = categoryDao.selectCategory(categoryId);		

		for (CategoryVO category :categories) {
			printTree(category, "", false);
		}		

		System.out.print("삭제하시겠습니까? (Y/N) :");
		
		if (!scanner.next().toLowerCase().equals("y")) return;

		categoryDao.deleteCategory(categoryId);
		selectCategory();
	}
	
	public void selectCategory() {
		CategoryDao categoryDao = new CategoryDao();
		List<CategoryVO> categories = categoryDao.selectCategory();		

		for (CategoryVO category :categories) {
			printTree(category, "", false);
		}
	}
	
	
	public void printTree(CategoryVO category, String prefix, boolean isLast) {
        System.out.println(prefix + (isLast ? "└── " : "├── ") + category.getCategoryName());
        for (int i = 0; i < category.getChild().size(); i++) {
            boolean last = (i == category.getChild().size() - 1);
            printTree(category.getChild().get(i), prefix + (isLast ? "    " : "│   "), last);
        }
	}
}
