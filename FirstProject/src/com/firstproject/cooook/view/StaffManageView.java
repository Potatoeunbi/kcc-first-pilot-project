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
        	UIHelper.printTitle("👤 작업자 관리");
            System.out.println("1. 작업자 보기");
            System.out.println("2. 작업자 추가");
            System.out.println("3. 작업자 수정");
            System.out.println("4. 작업자 삭제");
            System.out.println("0. 뒤로가기");
            System.out.println();
            System.out.print("메뉴 선택 ▶ ");
            String input = sc.nextLine();

            switch (input) {
            	case "1":   printStaffAll();    break;
                case "2":   insertStaff();      break;
                case "3":   updateStaff();      break;
                case "4":   deleteStaff();      break;
                case "0":   return;
                default:    UIHelper.printError("잘못된 입력입니다.");
            }
        }
    }

    private void insertStaff() {
        try {
            StaffVO staff = new StaffVO();
            UIHelper.printTitle("[👤 작업자 추가]");            
            String firstName = inputNonEmpty("성 (first name): ");
            staff.setFirstName(firstName);
            
            String lastName = inputNonEmpty("이름 (last name): ");
            staff.setLastName(lastName);

            staff.setEmail(checkEmail(false));
            
            staff.setPassword(PasswordUtil.hashPassword(checkPassword(false)));

            staff.setPhone(checkPhone(false));
            
            staff.setRoleId(checkRoleId(false));
            
            staffDao.insertStaff(staff);
            UIHelper.printSuccess("작업자 등록 완료!");
        } catch (Exception e) {
        	UIHelper.printError("입력 오류: " + e.getMessage());
        }
    }

    private void updateStaff() {
        try {
            StaffVO staff = new StaffVO();
            UIHelper.printTitle("[👤 작업자 수정]");
            System.out.print("수정할 작업자 번호 : ");
            int staffId = Integer.parseInt(sc.nextLine());
            
            if(checkIsStaff(staffId)) {
            	 staff.setStaffId(staffId);
            }else {
            	UIHelper.printError("수정 실패: 올바르지 않은 작업자 번호입니다.");
            	return;
            }

            System.out.print("변경할 성 (Enter 생략): ");
            String fn = sc.nextLine();
            if (!fn.isEmpty()) staff.setFirstName(fn);

            System.out.print("변경할 이름 (Enter 생략): ");
            String ln = sc.nextLine();
            if (!ln.isEmpty()) staff.setLastName(ln);
            
            String email = checkEmail(true);
            if(!email.isEmpty()) staff.setEmail(email);
            
            
            String pw = checkPassword(true);
            if (!pw.isEmpty()) staff.setPassword(PasswordUtil.hashPassword(pw));

            
            String phone = checkPhone(true);
            if (!phone.isEmpty()) staff.setPhone(phone);

            int roleId = checkRoleId(true);
            if(roleId > 0) staff.setRoleId(roleId);
            
            staffDao.updateStaff(staff);
            UIHelper.printSuccess("작업자 수정 완료!");
        } catch (Exception e) {
        	UIHelper.printError("수정 실패: " + e.getMessage());
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
    	UIHelper.printBoxedList("권한 선택", "권한이 없습니다.", roleList, role -> String.format("  %d: %s", role.getRoleId(), role.getRoleName()));
    }

    private void printStaffAll() {
        List<StaffVO> staffList = staffDao.getStaffAll();

        UIHelper.printBoxedList("[👤 전체 작업자 목록]", "작업자가 없습니다.", staffList, 
        staff -> String.format("번호: %3d | 이름: %s | 권한: %s | 이메일: %s | 전화번호: %s | 생성일: %s",
                staff.getStaffId(),
                staff.getFirstName() + " " + staff.getLastName(),
                staff.getRoleName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getCreatedAt()));
    }

    private void deleteStaff() {
        try {
        	UIHelper.printTitle("[👤 작업자 삭제]");
            System.out.print("삭제할 작업자 번호 : ");
            int staffId = Integer.parseInt(sc.nextLine());
            int affectedRows = (int) staffDao.softDeleteStaff(staffId);
            if (affectedRows == 0) {
            	UIHelper.printError("삭제 실패: 올바르지 않은 작업자 번호입니다.");
	        } else {
	        	UIHelper.printSuccess("작업자 삭제 완료!");
	        }
        } catch (Exception e) {
        	UIHelper.printError("삭제 실패: " + e.getMessage());
        }
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasLetter = password.matches(".*[A-Za-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>\\[\\]~`\\-_+=/\\\\].*");

        return hasLetter && hasDigit && hasSpecial;
    }
    
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;

        // 전화번호 패턴 (지역번호 or 휴대폰)
        String regex = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$";

        // 전체 형식 검사 먼저
        if (!phone.matches(regex)) {
            return false;
        }

        // 세부 검사 (지역번호 or 휴대폰)
        String[] parts = phone.split("-");
        String first = parts[0];

        // 휴대폰: 010, 011, 016~019
        if (first.matches("01[016789]")) return true;

        // 지역번호: 02, 031~064
        if (first.equals("02") || first.matches("0[3-6][0-9]")) return true;

        return false;
    }

    
    private String checkEmail(boolean updateMode) {
    	String email = null;
    	
        while (true) {
            System.out.print(updateMode ? "변경할 이메일 (Enter 생략): " : "이메일: ");
            email = sc.nextLine();
            if(updateMode && email.isEmpty()) break;

            // 1. 형식 체크
            if (!isValidEmail(email)) {
                UIHelper.printError("이메일 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }

            // 2. 중복 체크
            if (staffDao.selectEmailCount(email) > 0) {
                UIHelper.printWarning("이미 존재하는 이메일입니다. 다시 입력해주세요.");
                continue;
            }
            
            break;
        }
        
        return email;
    }
    
    private String checkPassword(boolean updateMode) {
    	String password = null;
    	
        while (true) {
            System.out.print(updateMode ? "변경할 비밀번호 (Enter 생략, 영문자+숫자+특수문자+8자 이상): " : "비밀번호 (영문자+숫자+특수문자+8자 이상): ");
            password = sc.nextLine().trim();
            if(updateMode && password.isEmpty()) break;

            // 1. 형식 체크
            if (!isValidPassword(password)) {
                UIHelper.printError("비밀번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }
            
            break;
        }
        
        return password;
    }
    
    private String checkPhone(boolean updateMode) {
    	String phone = null;
    	
        while (true) {
            System.out.print(updateMode ? "변경할 전화번호 (Enter 생략, - 포함): " : "전화번호 (- 포함): ");
            phone = sc.nextLine().trim();
            if(updateMode && phone.isEmpty()) break;

            // 1. 형식 체크
            if (!isValidPhone(phone)) {
                UIHelper.printError("전화번호 형식이 올바르지 않습니다. 다시 입력해주세요.");
                continue;
            }
            
            break;
        }
        
        return phone;
    }
    
    private int checkRoleId(boolean updateMode) {
        int roleId = -1;
        boolean isValid = false;
    	while (!isValid) {
             printRoleAll();
             System.out.print(updateMode ? "변경할 권한 번호 (Enter 생략): " : "권한 번호: ");
             String input = sc.nextLine();
             if (updateMode && input.isBlank()) break;
             
             try {
                 roleId = Integer.parseInt(input);
                 
                 for (RoleVO role : roleList) {
                     if (role.getRoleId() == roleId) {
                         isValid = true;
                         break;
                     }
                 }
             
                 if (!isValid) {
                     UIHelper.printError("존재하지 않는 역할입니다. 다시 입력해주세요.");
                 }
             } catch (NumberFormatException e) {
                 UIHelper.printError("숫자로 입력해주세요.");
             }
          }
    	  
         return roleId;
    }
    
    //@Potatoeunbi inputNonEmpty 함수 모두 찾아서 공통 함수로 빼야 함. 만일 하나밖에 없으면 주석만 제거
    private String inputNonEmpty(String label) {
        String input = "";
        while (input.isBlank()) {
            System.out.print(label);
            input = sc.nextLine();
            if (input.isBlank()) {
                UIHelper.printError("공백 입력은 불가합니다. 다시 입력해주세요.");
            }
        }
        return input;
    }
}
