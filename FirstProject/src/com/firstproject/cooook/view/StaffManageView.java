package com.firstproject.cooook.view;

import java.util.Scanner;

import com.firstproject.cooook.dao.StaffDao;
import com.firstproject.cooook.util.PasswordUtil;
import com.firstproject.cooook.vo.StaffVO;

public class StaffManageView {
    private Scanner sc = new Scanner(System.in);
    private StaffDao staffDao = new StaffDao();

    public void run() {
        while (true) {
            System.out.println("\n=== [👤 작업자 관리] ===");
            System.out.println("1. 작업자 추가");
            System.out.println("2. 작업자 수정");
            System.out.println("3. 작업자 삭제");
            System.out.println("0. 뒤로가기");
            System.out.print("입력 ▶ ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    insertStaff();
                    break;
                case "2":
                    updateStaff();
                    break;
                case "3":
                    deleteStaff();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ 잘못된 입력입니다.");
            }
        }
    }

    private void insertStaff() {
        try {
            StaffVO staff = new StaffVO();
            System.out.print("이름 (first_name): ");
            staff.setFirstName(sc.nextLine());

            System.out.print("성 (last_name): ");
            staff.setLastName(sc.nextLine());

            System.out.print("이메일: ");
            staff.setEmail(sc.nextLine());

            System.out.print("비밀번호: ");
            staff.setPassword(PasswordUtil.hashPassword(sc.nextLine()));

            System.out.print("전화번호: ");
            staff.setPhone(sc.nextLine());

            System.out.print("role_id (1=관리자, 2=작업자): ");
            staff.setRoleId(Integer.parseInt(sc.nextLine()));

            staffDao.insertStaff(staff);
            System.out.println("✅ 작업자 등록 완료!");
        } catch (Exception e) {
            System.out.println("❌ 입력 오류: " + e.getMessage());
        }
    }

    private void updateStaff() {
        try {
            StaffVO staff = new StaffVO();

            System.out.print("수정할 staff_id: ");
            staff.setStaffId(Integer.parseInt(sc.nextLine()));

            System.out.print("변경할 이름 (Enter 생략): ");
            String fn = sc.nextLine();
            if (!fn.isEmpty()) staff.setFirstName(fn);

            System.out.print("변경할 전화번호 (Enter 생략): ");
            String phone = sc.nextLine();
            if (!phone.isEmpty()) staff.setPhone(phone);

            System.out.print("변경할 비밀번호 (Enter 생략): ");
            String pw = sc.nextLine();
            if (!pw.isEmpty()) staff.setPassword(PasswordUtil.hashPassword(pw));

            staffDao.updateStaff(staff);
            System.out.println("✅ 작업자 수정 완료!");
        } catch (Exception e) {
            System.out.println("❌ 수정 실패: " + e.getMessage());
        }
    }

    private void deleteStaff() {
        try {
            System.out.print("삭제할 작업자 이메일: ");
            String email = sc.nextLine();
            staffDao.softDeleteStaff(email);
        } catch (Exception e) {
            System.out.println("❌ 삭제 실패: " + e.getMessage());
        }
    }
}
