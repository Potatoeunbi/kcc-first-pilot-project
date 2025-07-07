package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.dao.MenuDao;
import com.firstproject.cooook.dao.OrderDao;
import com.firstproject.cooook.vo.OrderVO;
import com.firstproject.cooook.vo.RoleVO;
import com.firstproject.cooook.vo.StaffVO;

public class OrderManageView {
    private final Scanner sc = new Scanner(System.in);
    private final OrderDao orderDao = new OrderDao();

    public void run() {
        while (true) {
            System.out.println("\n\n============= [📦 주문 관리] =============");
            System.out.println("1. 주문 목록 보기");
            System.out.println("2. 주문 추가");
            System.out.println("3. 주문 수정");
            System.out.println("4. 주문 삭제");
            System.out.println("0. 뒤로가기");
            System.out.println("========================================");
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
        System.out.println("\n============= [📦 주문 목록] =============");
        for (OrderVO o : orders) {
            System.out.printf("번호: %d | 직원명 : %s | 메뉴명: %s | 수량: %d | 총액: %d | 주문일: %s\n",
                    o.getOrderId(), o.getStaffName(), o.getMenuName(),
                    o.getQuantity(), o.getTotalPrice(), o.getCreatedAt());
        }
        System.out.println("========================================");
    }

    private void insertOrder() {
        try {
            OrderVO o = new OrderVO();
            StaffVO staff = Session.getCurrentUser();
            
            System.out.println("\n============= [📦 주문 추가] =============");
            o.setStaffId(staff.getStaffId());

//            checkMenuId(false);
            System.out.print("메뉴 번호: ");
            o.setMenuId(Integer.parseInt(sc.nextLine()));

            System.out.print("수량: ");
            o.setQuantity(Integer.parseInt(sc.nextLine()));

            System.out.print("총 금액: ");
            o.setTotalPrice(Integer.parseInt(sc.nextLine()));

            orderDao.insertOrder(o);
            System.out.println("✅ 주문이 추가되었습니다.");
        } catch (Exception e) {
            System.out.println("❌ 입력 오류: " + e.getMessage());
        }
    }

    private void updateOrder() {
        try {
            System.out.println("\n============= [📦 주문 수정] =============");
            System.out.print("수정할 주문 번호: ");
            int orderId = Integer.parseInt(sc.nextLine());
            OrderVO o = new OrderVO();
            
            if(checkIsOrder(orderId)) {
           	 	o.setOrderId(orderId);
            }else {
            	System.out.println("❌ 수정 실패: 올바르지 않은 주문 번호입니다.");
            	return;
            }
            
           
            o.setOrderId(orderId);

            System.out.print("변경할 수량 (Enter 생략): ");
            String qty = sc.nextLine();
            if (!qty.isEmpty()) o.setQuantity(Integer.parseInt(qty));

            System.out.print("변경할 총 금액 (Enter 생략): ");
            String price = sc.nextLine();
            if (!price.isEmpty()) o.setTotalPrice(Integer.parseInt(price));

            orderDao.updateOrder(o);
            System.out.println("✅ 주문이 수정되었습니다.");
        } catch (Exception e) {
            System.out.println("❌ 수정 오류: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        try {
            System.out.println("\n============= [📦 주문 삭제] =============");
            System.out.print("삭제할 주문 번호: ");
            int orderId = Integer.parseInt(sc.nextLine());
            
            
            if(!checkIsOrder(orderId)) {
            	System.out.println("❌ 수정 실패: 올바르지 않은 주문 번호입니다.");
            	return;
            }
            

            int affected = orderDao.softDeleteOrder(orderId);
            if (affected > 0) {
                System.out.println("✅ 주문이 삭제되었습니다.");
            } else {
                System.out.println("❌ 해당 번호의 주문이 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("❌ 삭제 오류: " + e.getMessage());
        }
    }
    
    private boolean checkIsOrder(int orderId) {
    	boolean isValid = false;
    	List<OrderVO> orderList = orderDao.getAllOrders();
        for (OrderVO order : orderList) {
            if (order.getOrderId() == orderId) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }
    
    
    private void printMenuAll() {
//    	MenuDao menuDao = new MenuDao();
//    	roleList = menuDao.getAllMenus(); // 역할 목록 조회
//
//    	System.out.println("\n\n┌─────── 메뉴 선택 ──────┐");
//    	for (RoleVO role : roleList) {
//    	    System.out.printf("  %d: %s\n", role.getRoleId(), role.getRoleName());
//    	}
//    	System.out.println("└─────────────────────┘");
    }
    
    private int checkMenuId(boolean updateMode) {
        int roleId = -1;
        boolean isValid = false;
//    	while (!isValid) {
//            //메뉴 all select
//    		printMenuAll();
//             System.out.print(updateMode ? "변경할 메뉴 번호 (Enter 생략): " : "메뉴 번호: ");
//             String input = sc.nextLine();
//             if (updateMode && input.isBlank()) break;
//             
//             try {
//                 roleId = Integer.parseInt(input);
//                 
//                 for (RoleVO role : roleList) {
//                     if (role.getRoleId() == roleId) {
//                         isValid = true;
//                         break;
//                     }
//                 }
//             
//                 if (!isValid) {
//                     System.out.println("❌ 존재하지 않는 역할입니다. 다시 입력해주세요.");
//                 }
//             } catch (NumberFormatException e) {
//                 System.out.println("❌ 숫자로 입력해주세요.");
//             }
//          }
//    	  
         return roleId;
    }
}
