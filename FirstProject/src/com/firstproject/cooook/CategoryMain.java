package com.firstproject.cooook;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.CategoryDao;
import com.firstproject.cooook.vo.CategoryVO;

public class CategoryMain {
	static Scanner scanner = new Scanner(System.in);
	
	public void runCategory() {
		while (true) {
			System.out.println("메뉴를 입력하세요.");
			System.out.println("(S)elect, (A)llSelect, (I)nsert, (U)pdate, (D)elete, (Q)uit");
			System.out.println("메뉴 입력 : ");
			String menu = scanner.next();

			switch (menu.toLowerCase().charAt(0)) {
			case 's':
				selectCategory();				
				break;
			case 'i':
				insertCategory();
				break;
			case 'u':
				updateCategory();
				break;
			case 'd':
				deleteCategory();
				break;
			case 'q':
				System.out.println("프로그램을 종료합니다.");
				scanner.close();
				System.exit(0);
			default:
				System.out.println("메뉴를 잘못 입력했습니다.");
			}
		}
	}
	
	public void insertCategory() {
		System.out.println("카테고리를 삽입합니다.");
		System.out.print("상위 카테고리 번호를 입력하세요. (0번은 루트노드) :");
		int parentCategoryId = scanner.nextInt();
		System.out.print("카테고리를 입력하세요. : ");
		String categoryName = scanner.next();
		
		CategoryVO category = new CategoryVO();
		category.setCategoryId(0);
		category.setCategoryName(categoryName);
		category.setParentId(parentCategoryId);
		
		CategoryDao categoryDao = new CategoryDao();
		categoryDao.insertCategory(category);
		
		selectCategory();
	}
	public void updateCategory() {
		System.out.println("카테고리를 수정합니다.");
		System.out.print("카테고리 이름은 0번, 위치는 1번을 입력하세요.");
		int updateType = scanner.nextInt();

		System.out.print("변경할 카테고리 번호를 입력하세요. :");
		int categoryId = scanner.nextInt();

		CategoryVO category = new CategoryVO();
		category.setCategoryId(categoryId);
		switch (updateType) {
		case 0:
			System.out.print("카테고리를 입력하세요. : ");
			String categoryName = scanner.next();
			
			category.setCategoryName(categoryName);
			break;
		case 1:
			System.out.print("상위 카테고리 번호를 입력하세요. (0번은 루트노드) :");
			int parentCategoryId = scanner.nextInt();
			
			category.setParentId(parentCategoryId);
			break;
		default:
			System.out.println("잘못 입력했습니다.");
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
		String isDelete = scanner.next();
		
		if (isDelete.toLowerCase().equals("Y")) {
			categoryDao.deleteCategory(categoryId);
			selectCategory();			
		} else {
			System.out.println("취소하셨습니다.");
		}
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
