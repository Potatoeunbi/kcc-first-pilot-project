package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.CategoryDao;
import com.firstproject.cooook.util.Util;
import com.firstproject.cooook.vo.CategoryVO;

public class CategoryView {
	private CategoryDao categoryDao = new CategoryDao();
	static Scanner scanner = new Scanner(System.in);
	
	public void showMenu() {
		while (true) {
            UIHelper.printTitle("📂 카테고리 관리");
			System.out.println();
            System.out.println("1. 카테고리 조회");
            System.out.println("2. 카테고리 등록");
            System.out.println("3. 카테고리 수정");
            System.out.println("4. 카테고리 삭제");
            System.out.println("0. 나가기");
			System.out.println();
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
				default: UIHelper.printError("잘못된 입력입니다.");
			}
		}
	}
	
	public void selectCategory() {
		List<CategoryVO> categories = categoryDao.selectCategory();
		UIHelper.printCategoryTree(categories);
		printEnter();
	}	
	
	public void insertCategory() {
        UIHelper.printTitle("📝 새 카테고리 등록");
        System.out.print("카테고리명 입력 ▶: ");
        scanner.nextLine(); // 버퍼 정리
        String categoryName = scanner.nextLine().trim();
        
        if (!Util.isNotEmpty(categoryName) || !Util.isLengthInRange(categoryName, 1, 100)) return;
        
        int parentId = selectParentCategory();
        if (parentId == -1) return;
        
        CategoryVO newCategory = new CategoryVO();
        newCategory.setCategoryName(categoryName);
        newCategory.setParentId(parentId); 
        
        if (categoryDao.insertCategory(newCategory) > 0) {
            UIHelper.printSuccess("카테고리가 성공적으로 등록되었습니다!");
        } else {
            UIHelper.printError("카테고리 등록에 실패했습니다.");
        }
        
        printEnter();
	}
	
    private int selectParentCategory() {
        List<CategoryVO> categories = categoryDao.selectCategory();
        
        if (categories.isEmpty()) {
            return 0;
        }
        
        System.out.println();
        System.out.println("부모 카테고리를 선택하세요 (최상위 카테고리는 0 입력):");      
        UIHelper.printCategoryTree(categories);
        
        System.out.print("부모 카테고리 ID ▶: ");
        String parentInput = scanner.next();
        
        if (!Util.isInteger(parentInput)) return -1;
        
        int parentChoice = Integer.parseInt(parentInput);
        if (parentChoice == 0) {
            return 0;
        }
        
        CategoryVO parent = categoryDao.selectCategoryById(parentChoice);
        if (parent == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return -1;
        }
        
        return parentChoice;
    }
    
	public void updateCategory() {
		UIHelper.printTitle("✏️ 카테고리 수정");
        
        List<CategoryVO> categories = categoryDao.selectCategory();
        
        if (categories.isEmpty()) {
            UIHelper.printWarning("수정할 카테고리가 없습니다.");
            return;
        }
        
        System.out.println("수정할 카테고리를 선택하세요:");      
        UIHelper.printCategoryTree(categories);
        
        System.out.print("카테고리 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int categoryId = Integer.parseInt(input);
        CategoryVO targetCategory = categoryDao.selectCategoryById(categoryId);
        
        if (targetCategory == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return;
        }

        System.out.println();
        System.out.println("카테고리 이름은 0번, 위치는 1번을 입력하세요.");
        System.out.print("선택 ▶: ");
        
        String updateInput = scanner.next();
        if (!Util.isInteger(updateInput)) return;
        
        int updateChoice = Integer.parseInt(updateInput);

        if (!(updateChoice == 0 || updateChoice == 1)) {
            UIHelper.printError("잘못된 입력입니다.");
            return;
        }

        if (updateChoice == 0) {
            System.out.print("새 카테고리명 입력 ▶: ");
            scanner.nextLine(); // 버퍼 정리
            String categoryName = scanner.nextLine().trim();
            if (!Util.isNotEmpty(categoryName) || !Util.isLengthInRange(categoryName, 1, 100)) return;
            targetCategory.setCategoryName(categoryName);
        } else {
            int parentId = selectParentCategory();
            if (parentId == -1) return;
            if (categoryDao.isCircularReference(categoryId, parentId)) {
                UIHelper.printError("자신의 하위 카테고리를 부모로 지정할 수 없습니다.");
                return;
            }
            targetCategory.setParentId(parentId);
        }
        
        if (categoryDao.updateCategory(targetCategory, updateChoice)) {
            UIHelper.printSuccess("카테고리가 성공적으로 수정되었습니다!");
        } else {
            UIHelper.printError("카테고리 수정에 실패했습니다.");
        }        
        printEnter();			
	}
	

	public void deleteCategory() {
		UIHelper.printTitle("🗑️ 카테고리 삭제");
        
        List<CategoryVO> categories = categoryDao.selectCategory();
        
        if (categories.isEmpty()) {
            UIHelper.printWarning("삭제할 카테고리가 없습니다.");
            return;
        }
        
        System.out.println("삭제할 카테고리를 선택하세요:");      
        UIHelper.printCategoryTree(categories);
        
        System.out.print("카테고리 ID ▶: ");
        String input = scanner.next();
        if (!Util.isInteger(input)) return;
        
        int categoryId = Integer.parseInt(input);
        CategoryVO targetCategory = categoryDao.selectCategoryById(categoryId);
        
        if (targetCategory == null) {
            UIHelper.printError("존재하지 않는 카테고리 ID입니다.");
            return;
        }
        
        // 하위 카테고리 확인
		List<CategoryVO> children = categoryDao.selectChildCategory(categoryId);
        if (children.size() > 0) {
			UIHelper.printWarning("이 카테고리에는 하위 카테고리가 있습니다.");
            UIHelper.printCategoryTree(children);
            System.out.println("하위 카테고리도 함께 삭제됩니다.");
        }
        
        System.out.println("정말로 '" + targetCategory.getCategoryName() + "' 카테고리를 삭제하시겠습니까? (y/N)");
        System.out.print("확인 ▶: ");
        String confirm = scanner.next();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (categoryDao.deleteCategory(categoryId)) {
                UIHelper.printSuccess("카테고리가 성공적으로 삭제되었습니다!");
            } else {
                UIHelper.printError("카테고리 삭제에 실패했습니다.");
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
