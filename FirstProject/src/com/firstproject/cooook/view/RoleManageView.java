package com.firstproject.cooook.view;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.firstproject.cooook.common.RoleFeatureCode;
import com.firstproject.cooook.dao.RoleDao;
import com.firstproject.cooook.db.DBUtil;
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
  	  Connection con = null;
        try {
            RoleVO role = new RoleVO();
            UIHelper.printTitle("[🔒 권한 추가]");
            System.out.print("권한 이름: ");
            role.setRoleName(sc.nextLine());

            System.out.print("설명: ");
            role.setDescription(sc.nextLine());

            
            con = DBUtil.getConnection();
            con.setAutoCommit(false); // 트랜잭션 시작
            
            mergeRoleFeature(con, false, -1);
            roleDao.insertRole(con, role);
            con.commit();
            UIHelper.printSuccess("권한 등록 완료!");
        } catch (Exception e) {
        	DBUtil.rollback(con);
            UIHelper.printError("입력 오류: " + e.getMessage());
        } finally {
            DBUtil.setAutoCommitTrue(con);
            DBUtil.close(con);
        }
    }


    private void updateRole() {
    	  Connection con = null;
        try {
            UIHelper.printTitle("[🔒 권한 수정]");
            System.out.print("수정할 권한 번호 : ");
            int roleId = Integer.parseInt(sc.nextLine());

            // 수정할 권한 존재 여부 확인
            RoleVO existingRole = roleDao.getRoleById(roleId);
            if (existingRole == null) {
                UIHelper.printError("수정 실패: 올바르지 않은 권한 번호입니다.");
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
            
            
            con = DBUtil.getConnection();
            con.setAutoCommit(false); // 트랜잭션 시작
            
            mergeRoleFeature(con, true, roleId);
            roleDao.updateRole(con, role);
            
            con.commit();
            UIHelper.printSuccess("권한 수정 완료!");
        } catch (Exception e) {
        	DBUtil.rollback(con);
            UIHelper.printError("수정 실패: " + e.getMessage());
        } finally {
            DBUtil.setAutoCommitTrue(con);
            DBUtil.close(con);
        }
    }
    
    private void deleteRole() {
        try {
            UIHelper.printTitle("[🔒 권한 삭제]");
            System.out.print("삭제할 권한 번호 : ");
            int roleId = Integer.parseInt(sc.nextLine());
            int affectedRows = roleDao.softDeleteRole(roleId);  // 물리 삭제 함수
            if (affectedRows == 0) {
                UIHelper.printError("삭제 실패: 올바르지 않은 권한 번호입니다.");
            } else {
                UIHelper.printSuccess("권한 삭제 완료!");
            }
        } catch (Exception e) {
            UIHelper.printError("삭제 실패: " + e.getMessage());
        }
    }

    private void printAllRoles() {
        List<RoleVO> roles = roleDao.getAllRoles();
        UIHelper.printBoxedList("[🔒 전체 권한 목록]", "목록이 없습니다.", roles, role -> {
            String featureNames = convertFeatureCodesToNames(role.getFeatureCodes());
            return String.format("번호: %d | 이름: %s | 설명: %s | 권한: %s",
                role.getRoleId(),
                role.getRoleName(),
                role.getDescription(),
                featureNames);
        });
    }
    
    private String convertFeatureCodesToNames(String featureCodesStr) {
        if (featureCodesStr == null || featureCodesStr.isEmpty()) return "";

        String[] codes = featureCodesStr.split(",");  // DB에서는 쉼표로 연결되어 있다고 가정
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < codes.length; i++) {
            String code = codes[i].trim();
            String name = RoleFeatureCode.FEATURE_NAME_MAP.getOrDefault(code, code);
            sb.append(name);
            if (i < codes.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    
    private void printAllRoleFetures() {
        UIHelper.printTitle("[🔒 권한 코드]");
        RoleFeatureCode.menuIndexToFeature.keySet().stream()
        .sorted() // 오름차순 정렬
        .forEach(number -> {
            String featureCode = RoleFeatureCode.menuIndexToFeature.get(number);
            String featureName = RoleFeatureCode.FEATURE_NAME_MAP.get(featureCode);
            System.out.printf("번호 : %d | 메뉴명 : %s%n", number, featureName);
        });
    }
    
    private void mergeRoleFeature(Connection con, boolean updateMode, int roleId) {
    	 boolean hasValidInput = false;
    	if(!updateMode) {
    		roleId = roleDao.selectNextRoleSeq();
    	}else {
    		int deleteRows = roleDao.deleteRoleFeature(con, roleId);
    	}
    	
    	String featureInput = null;
    	printAllRoleFetures();
    	 while (true) {
	    	System.out.print(updateMode? "변경할 권한 코드 (예: 1,3,4) (Enter 생략): " : "변경할 권한 기능 번호들 (예: 1,3,4): ");
	    	featureInput = sc.nextLine().trim();
	
	    	if (!featureInput.isBlank()) {
	    	    String[] selectedIndexes = featureInput.split(",");
	    	    int displayOrder = 1;
	
	    	    for (String indexStr : selectedIndexes) {
	    	        try {
	    	            int idx = Integer.parseInt(indexStr.trim());
	    	            String featureCode = RoleFeatureCode.menuIndexToFeature.get(idx);
	    	            if (featureCode != null) {
	    	                roleDao.insertRoleFeature(con, roleId, featureCode, displayOrder++);
	    	                hasValidInput = true;
	    	            } else {
	    	                UIHelper.printError("잘못된 메뉴 번호: " + idx);
	    	                hasValidInput = false;
	    	            }
	    	        } catch (NumberFormatException e) {
	    	            UIHelper.printError("숫자가 아닌 값이 포함되어 있음:" + indexStr);
	    	        }
	    	    }
	    	}else {
	    		break;
	    	}
	    	
	    	if (hasValidInput) {
	            break;  // 올바른 입력 한번 처리 후 종료
	   	    }
    	 }
    }
    
}

