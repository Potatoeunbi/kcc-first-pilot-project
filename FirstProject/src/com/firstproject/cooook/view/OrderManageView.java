package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.dao.MenuDao;
import com.firstproject.cooook.dao.OrderDao;
import com.firstproject.cooook.vo.MenuVO;
import com.firstproject.cooook.vo.OrderVO;
import com.firstproject.cooook.vo.StaffVO;

public class OrderManageView {
    private final Scanner sc = new Scanner(System.in);
    private final OrderDao orderDao = new OrderDao();
    private final MenuDao menuDao = new MenuDao();
    private List<MenuVO> menuList  = null;


    public void run() {
        while (true) {
            UIHelper.printTitle("📦 주문 관리");
            System.out.println("1. 주문 조회");
            System.out.println("2. 주문 등록");
            System.out.println("3. 주문 수정");
            System.out.println("4. 주문 삭제");
            System.out.println("0. 메인 메뉴로");
            System.out.println();
            System.out.print("메뉴 선택 ▶ ");

            String input = sc.nextLine();
            switch (input) {
                case "1":
                    printOrderAll();
                    break;
                case "2":
                    insertOrder();
                    break;
                case "3":
                    updateOrder();
                    break;
                case "4":
                    deleteOrder();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ 잘못된 입력입니다.");
            }
        }
    }

    private void printOrderAll() {
    	List<OrderVO> orders = orderDao.getAllOrders();
        UIHelper.printBoxedList("[📦 전체 주문 목록]", "주문이 없습니다.", orders, o -> String.format("번호: %d | 직원명 : %s | 메뉴명: %s | 수량: %d | 총액: %d | 주문일: %s",
        		 o.getOrderId(), o.getStaffName(), o.getMenuName(),
                 o.getQuantity(), o.getTotalPrice(), o.getCreatedAt()));
    }

    private void insertOrder() {
        try {
            OrderVO o = new OrderVO();
            StaffVO staff = Session.getCurrentUser();
            
            UIHelper.printTitle("[📦 주문 추가]");
            o.setStaffId(staff.getStaffId());

            MenuVO menu = checkMenuId(false);
            o.setMenuId(menu.getMenuId());

            System.out.print("수량: ");
            
            int quantity = Integer.parseInt(sc.nextLine());
            o.setQuantity(quantity);

            o.setTotalPrice(quantity*menu.getPrice());

            orderDao.insertOrder(o);
            UIHelper.printSuccess("주문이 추가되었습니다.");
        } catch (Exception e) {
            UIHelper.printError("입력 오류: " + e.getMessage());
        }
    }

    private void updateOrder() {
        try {
            UIHelper.printTitle("[📦 주문 수정]");
            System.out.print("수정할 주문 번호: ");
            int orderId = Integer.parseInt(sc.nextLine());
            
            OrderVO originalOrder = orderDao.getOrderById(orderId);
            if(originalOrder == null) {
            	UIHelper.printError("수정 실패: 올바르지 않은 주문 번호입니다.");
            	return;
            }
            
            //order_id 정의
            OrderVO updatedOrder = new OrderVO();
            updatedOrder.setOrderId(orderId);
            
            //menu_id 정의
            MenuVO menu = checkMenuId(true);
            if(menu!=null) updatedOrder.setMenuId(menu.getMenuId());
           
            //quantity 정의
            System.out.print("변경할 수량 (Enter 생략): ");
            String qty = sc.nextLine();
            int quantity = qty.isEmpty() ? originalOrder.getQuantity() : Integer.parseInt(qty);
            updatedOrder.setQuantity(quantity);

            //총 가격 계산 (변경된 메뉴 있으면 그 가격으로)
            MenuVO priceMenu = (menu != null) ? menu : menuDao.getMenuById(originalOrder.getMenuId());
            if (priceMenu != null) {
                updatedOrder.setTotalPrice(priceMenu.getPrice() * quantity);
            }

            orderDao.updateOrder(updatedOrder);
            UIHelper.printSuccess("주문이 수정되었습니다.");
        } catch (Exception e) {
            UIHelper.printError("수정 실패: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        try {
            UIHelper.printTitle("[📦 주문 삭제]");
            System.out.print("삭제할 주문 번호: ");
            int orderId = Integer.parseInt(sc.nextLine());
            
            
            OrderVO originalOrder = orderDao.getOrderById(orderId);
            if(originalOrder == null) {
            	UIHelper.printError("삭제 실패: 올바르지 않은 주문 번호입니다.");
            	return;
            }
            

            int affected = orderDao.softDeleteOrder(orderId);
            if (affected > 0) {
                UIHelper.printSuccess("주문이 삭제되었습니다.");
            } else {
                UIHelper.printError("해당 번호의 주문이 없습니다.");
            }
        } catch (Exception e) {
        	UIHelper.printError("삭제 실패: " + e.getMessage());
        }
    }
    
    private void printMenuAll() {
    	menuList = menuDao.selectAllMenus(); // 역할 목록 조회
    	UIHelper.printBoxedList("메뉴 선택", "메뉴가 없습니다.", menuList, menu -> String.format("번호: %d | 이름: %s | 가격: %s", menu.getMenuId(), menu.getMenuName(), menu.getPrice()));
    }
    
    private MenuVO checkMenuId(boolean updateMode) {
        int menuId = -1;
        MenuVO currentMenu = null;
        boolean isValid = false;
    	while (!isValid) {
    		printMenuAll();
             System.out.print(updateMode ? "변경할 메뉴 번호 (Enter 생략): " : "메뉴 번호: ");
             String input = sc.nextLine();
             if (updateMode && input.isBlank()) break;
             
             try {
            	 menuId = Integer.parseInt(input);

            	 currentMenu = menuDao.getMenuById(menuId);
            	 if(currentMenu != null) {
            		 isValid = true;
            	 }
            	 
                 if (!isValid) {
                     UIHelper.printError("존재하지 않는 메뉴입니다. 다시 입력해주세요.");
                 }
             } catch (NumberFormatException e) {
                 UIHelper.printError("숫자로 입력해주세요.");
             }
          }
    	  
         return currentMenu;
    }
}
