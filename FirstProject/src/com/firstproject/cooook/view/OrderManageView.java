package com.firstproject.cooook.view;

import java.util.Scanner;

import com.firstproject.cooook.dao.OrderDao;
import com.firstproject.cooook.vo.OrderVO;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderManageView {
    private Scanner sc = new Scanner(System.in);
    private OrderDao orderDao = new OrderDao();

    public void run() {
        while (true) {
            System.out.println("\n=== [📦 주문 관리] ===");
            System.out.println("1. 주문 추가");
            System.out.println("2. 주문 수정");
            System.out.println("3. 주문 삭제");
            System.out.println("0. 뒤로가기");
            System.out.print("입력 ▶ ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    insertOrder();
                    break;
                case "2":
                    updateOrder();
                    break;
                case "3":
                    deleteOrder();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ 잘못된 입력입니다.");
            }
        }
    }

    private void insertOrder() {
        try {
            OrderVO order = new OrderVO();
            System.out.print("user_id: ");
            order.setUserId(Integer.parseInt(sc.nextLine()));

            System.out.print("product_id: ");
            order.setProductId(Integer.parseInt(sc.nextLine()));

            System.out.print("quantity: ");
            order.setQuantity(Integer.parseInt(sc.nextLine()));

            System.out.print("total_price: ");
            order.setTotalPrice(Double.parseDouble(sc.nextLine()));

            order.setOrderDate(new Date());

            System.out.print("status: ");
            order.setStatus(sc.nextLine());

            System.out.print("shipping address: ");
            order.setShippingAddr(sc.nextLine());

            System.out.print("payment method: ");
            order.setPaymentMethod(sc.nextLine());

            System.out.print("staff_id: ");
            order.setStaffId(Integer.parseInt(sc.nextLine()));

            orderDao.insertOrder(order);
            System.out.println("✅ 주문 등록 완료!");
        } catch (Exception e) {
            System.out.println("❌ 입력 오류: " + e.getMessage());
        }
    }

    private void updateOrder() {
        try {
            OrderVO order = new OrderVO();

            System.out.print("수정할 order_id: ");
            order.setOrderId(Integer.parseInt(sc.nextLine()));

            System.out.print("수정할 quantity (Enter 생략): ");
            String qty = sc.nextLine();
            if (!qty.isEmpty()) order.setQuantity(Integer.parseInt(qty));

            System.out.print("수정할 status (Enter 생략): ");
            String status = sc.nextLine();
            if (!status.isEmpty()) order.setStatus(status);

            System.out.print("수정할 배송주소 (Enter 생략): ");
            String addr = sc.nextLine();
            if (!addr.isEmpty()) order.setShippingAddr(addr);

            orderDao.updateOrder(order);
            System.out.println("✅ 주문 수정 완료!");
        } catch (Exception e) {
            System.out.println("❌ 수정 실패: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        try {
            System.out.print("삭제할 order_id: ");
            int id = Integer.parseInt(sc.nextLine());
            orderDao.softDeleteOrder(id);
        } catch (Exception e) {
            System.out.println("❌ 삭제 실패: " + e.getMessage());
        }
    }
}
