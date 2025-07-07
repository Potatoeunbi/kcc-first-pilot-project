package com.firstproject.cooook.view;

import java.util.List;
import java.util.Scanner;

import com.firstproject.cooook.dao.RoleDao;
import com.firstproject.cooook.vo.RoleVO;

public class RoleManageView {
    private Scanner sc = new Scanner(System.in);
    private RoleDao roleDao = new RoleDao();
    
    public void run() {
        while (true) {
            UIHelper.printTitle("[🔒 권한 관리]");
            System.out.println();
            // System.out.println("\n\n============= [🔒 권한 관리] =============\n");
            System.out.println("1. 권한 보기");
            System.out.println("2. 권한 추가");
            System.out.println("3. 권한 수정");
            System.out.println("4. 권한 삭제");
            System.out.println("0. 뒤로가기");
            UIHelper.printDivider();
            System.out.println();
            // System.out.println("\n========================================\n");
            System.out.print("메뉴 선택 ▶ ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    printAllRoles();
                    break;
                case "2":
                    insertRole();
                    break;
                case "3":
                    updateRole();
                    break;
                case "4":
                    deleteRole();
                    break;
                case "0":
                    return;
                default:
                    UIHelper.printError("잘못된 입력입니다.");
                    // System.out.println("❗ 잘못된 입력입니다.");
            }
        }
    }

    private void insertRole() {
        try {
            RoleVO role = new RoleVO();
            UIHelper.printTitle("[🔒 권한 추가]");
            // System.out.println("\n============= [🔒 권한 추가] =============\n");
            System.out.print("권한 이름: ");
            role.setRoleName(sc.nextLine());

            System.out.print("설명: ");
            role.setDescription(sc.nextLine());

            roleDao.insertRole(role);
            UIHelper.printSuccess("권한 등록 완료!");
            // System.out.println("\n✅ 권한 등록 완료!");
        } catch (Exception e) {
            UIHelper.printError("입력 오류: " + e.getMessage());
            // System.out.println("❌ 입력 오류: " + e.getMessage());
        }
    }


    private void updateRole() {
        try {
            UIHelper.printTitle("[🔒 권한 수정]");
            // System.out.println("\n============= [🔒 권한 수정] =============\n");
            System.out.print("수정할 권한 번호 : ");
            int roleId = Integer.parseInt(sc.nextLine());

            // 수정할 권한 존재 여부 확인
            RoleVO existingRole = roleDao.getRoleById(roleId);
            if (existingRole == null) {
                UIHelper.printError("수정 실패: 올바르지 않은 권한 번호입니다.");
                // System.out.println("❌ 수정 실패: 올바르지 않은 권한 번호입니다.");
                return;
            }

            RoleVO role = new RoleVO();
            role.setRoleId(roleId);

            System.out.print("변경할 권한 이름 (Enter 생략): ");
            String roleName = sc.nextLine();
            if (!roleName.isEmpty()) role.setRoleName(roleName);

            System.out.print("변경할 설명 (Enter 생략): ");
            String desc = sc.nextLine();
            if (!desc.isEmpty()) role.setDescription(desc);

            roleDao.updateRole(role);
            UIHelper.printSuccess("권한 수정 완료!");
            // System.out.println("\n✅ 권한 수정 완료!");
        } catch (Exception e) {
            UIHelper.printError("수정 실패: " + e.getMessage());
            // System.out.println("❌ 수정 실패: " + e.getMessage());
        }
    }
    
    private void deleteRole() {
        try {
            UIHelper.printTitle("[🔒 권한 삭제]");
            // System.out.println("\n============= [🔒 권한 삭제] =============\n");
            System.out.print("삭제할 권한 번호 : ");
            int roleId = Integer.parseInt(sc.nextLine());
            int affectedRows = roleDao.deleteRole(roleId);  // 물리 삭제 함수
            if (affectedRows == 0) {
                UIHelper.printError("삭제 실패: 올바르지 않은 권한 번호입니다.");
                // System.out.println("❌ 삭제 실패: 올바르지 않은 권한 번호입니다.");
            } else {
                UIHelper.printSuccess("권한 삭제 완료!");
                // System.out.println("\n✅ 권한 삭제 완료!");
            }
        } catch (Exception e) {
            UIHelper.printError("삭제 실패: " + e.getMessage());
            // System.out.println("❌ 삭제 실패: " + e.getMessage());
        }
    }

    private void printAllRoles() {
        List<RoleVO> roles = roleDao.getAllRoles();
        System.out.println("\n============= [🔒 전체 권한 목록] =============\n");
        for (RoleVO role : roles) {
            System.out.printf("번호: %d | 이름: %s | 설명: %s\n",
                              role.getRoleId(), role.getRoleName(), role.getDescription());
        }
        System.out.println("\n============================================\n");

        
        UIHelper.printBoxedList("[🔒 전체 권한 목록]", "목록이 없습니다.", roles, role -> 
            String.format("번호: %d | 이름: %s | 설명: %s", 
            role.getRoleId(), role.getRoleName(), role.getDescription()));
    }
}

