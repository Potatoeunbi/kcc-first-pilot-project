package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.RoleDao;
import com.firstproject.cooook.dao.StaffDao;
import com.firstproject.cooook.util.PasswordUtil;
import com.firstproject.cooook.vo.RoleVO;
import com.firstproject.cooook.vo.StaffVO;

public class StaffManageView {
    private Scanner sc = new Scanner(System.in);
    private StaffDao staffDao = new StaffDao();
    private List<RoleVO> roleList  = null;

    public void run() {
        while (true) {
            System.out.println("\n\n============= [👤 작업자 관리] =============\n");
            System.out.println("1. 작업자 보기");
            System.out.println("2. 작업자 추가");
            System.out.println("3. 작업자 수정");
            System.out.println("4. 작업자 삭제");
            System.out.println("0. 뒤로가기");
            System.out.println("\n========================================\n");
            System.out.print("메뉴 선택 ▶ ");
            String input = sc.nextLine();

            switch (input) {
            	case "1":
            		printStaffAll();
            		break;
                case "2":
                    insertStaff();
                    break;
                case "3":
                    updateStaff();
                    break;
                case "4":
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
            int roleId = -1;
            boolean isValid = false;
            StaffVO staff = new StaffVO();
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n============= [👤 작업자 추가] =============\n");
            System.out.print("성 (first name): ");
            staff.setFirstName(sc.nextLine());

            System.out.print("이름 (last name): ");
            staff.setLastName(sc.nextLine());

            System.out.print("이메일: ");
            staff.setEmail(sc.nextLine());

            System.out.print("비밀번호: ");
            staff.setPassword(PasswordUtil.hashPassword(sc.nextLine()));

            System.out.print("전화번호: ");
            staff.setPhone(sc.nextLine());

            while (!isValid) {
                printRoleAll();
                System.out.print("권한 번호: ");
                try {
                    roleId = Integer.parseInt(sc.nextLine());

                    for (RoleVO role : roleList) {
                        if (role.getRoleId() == roleId) {
                            isValid = true;
                            break;
                        }
                    }

                    if (!isValid) {
                        System.out.println("❌ 존재하지 않는 역할입니다. 다시 입력해주세요.");
                    }else {
                    	 staff.setRoleId(roleId);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ 숫자로 입력해주세요.");
                }
            }
            
            staffDao.insertStaff(staff);
            System.out.println("\n✅ 작업자 등록 완료!");
            System.out.println("\n========================================\n");
        } catch (Exception e) {
            System.out.println("❌ 입력 오류: " + e.getMessage());
        }
    }

    private void updateStaff() {
        try {
        	String roleId = null;
            boolean isValid = false;
            StaffVO staff = new StaffVO();
            System.out.println("\n============= [👤 작업자 수정] =============\n");
            System.out.print("수정할 작업자 번호 : ");
            int staffId = Integer.parseInt(sc.nextLine());
            
            if(checkIsStaff(staffId)) {
            	 staff.setStaffId(staffId);
            }else {
            	System.out.println("❌ 수정 실패: 올바르지 않은 작업자 번호입니다.");
            	return;
            }

            System.out.print("변경할 성 (Enter 생략): ");
            String fn = sc.nextLine();
            if (!fn.isEmpty()) staff.setFirstName(fn);

            System.out.print("변경할 이름 (Enter 생략): ");
            String ln = sc.nextLine();
            if (!ln.isEmpty()) staff.setLastName(ln);

            
            System.out.print("변경할 이메일 (Enter 생략): ");
            String email = sc.nextLine();
            if (!email.isEmpty()) staff.setEmail(email);

            System.out.print("변경할 비밀번호 (Enter 생략): ");
            String pw = sc.nextLine();
            if (!pw.isEmpty()) staff.setPassword(PasswordUtil.hashPassword(pw));

            
            System.out.print("변경할 전화번호 (Enter 생략): ");
            String phone = sc.nextLine();
            if (!phone.isEmpty()) staff.setPhone(phone);

            while (!isValid) {
                printRoleAll();
                System.out.print("변경할 권한 번호 (Enter 생략): ");
                try {
                    roleId = sc.nextLine();

                    for (RoleVO role : roleList) {
                        if (role.getRoleId() == Integer.parseInt(roleId)) {
                            isValid = true;
                            break;
                        }
                    }

                    if (!isValid) {
                        System.out.println("❌ 존재하지 않는 역할입니다. 다시 입력해주세요.");
                    }else {
                    	if (!roleId.isEmpty()) staff.setRoleId(Integer.parseInt(roleId));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ 숫자로 입력해주세요.");
                }
            }
            
            staffDao.updateStaff(staff);
            System.out.println("\n✅ 작업자 수정 완료!");
        } catch (Exception e) {
            System.out.println("❌ 수정 실패: " + e.getMessage());
        }
    }

    private boolean checkIsStaff(int staffId) {
    	boolean isValid = false;
    	List<StaffVO> staffList = staffDao.getStaffAll();
        for (StaffVO staff : staffList) {
            if (staff.getStaffId() == staffId) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }
    
    private void printRoleAll() {
    	RoleDao roleDao = new RoleDao();
    	roleList = roleDao.getAllRoles(); // 역할 목록 조회

    	System.out.println("\n\n┌─────── 권한 선택 ──────┐");
    	for (RoleVO role : roleList) {
    	    System.out.printf("  %d: %s\n", role.getRoleId(), role.getRoleName());
    	}
    	System.out.println("└─────────────────────┘");
    }

    private void printStaffAll() {
    	List<StaffVO> staffList = staffDao.getStaffAll(); // 역할 목록 조회

    	System.out.println("\n\n┌────────────────────────── 작업자 목록 ──────────────────────────┐");
    	for (StaffVO staff : staffList) {
    	    System.out.printf("  %d: %s%s, %s, %s, %s, %s\n", staff.getStaffId(), staff.getFirstName()
    	    		, staff.getLastName(), staff.getRoleName(), staff.getEmail(), staff.getPhone()
    	    		, staff.getCreatedAt());
    	}
    	System.out.println("└──────────────────────────────────────────────────────────────┘");
    }
    
    
    private void deleteStaff() {
        try {
            System.out.println("\n============= [👤 작업자 삭제] =============\n");
            System.out.print("삭제할 작업자 번호 : ");
            int staffId = Integer.parseInt(sc.nextLine());
            int affectedRows = (int) staffDao.softDeleteStaff(staffId);
            if (affectedRows == 0) {
	            System.out.println("❌ 삭제 실패: 올바르지 않은 작업자 번호입니다.");
	        } else {
	        	   System.out.println("\n✅ 작업자 삭제 완료!");
	        }
        } catch (Exception e) {
            System.out.println("❌ 삭제 실패: " + e.getMessage());
        }
    }
}
