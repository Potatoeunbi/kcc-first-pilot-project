package com.firstproject.cooook.view;

import java.util.Scanner;

import com.firstproject.cooook.common.Session;
import com.firstproject.cooook.dao.StaffDao;
import com.firstproject.cooook.vo.StaffVO;

public class LoginView {

    private Scanner sc = new Scanner(System.in);
    private StaffDao staffDao = new StaffDao();

    public void runLogin() {
    	
    	StaffVO loginStaff = null;
    	while(true) {
            System.out.println("========================================");
            System.out.println("               🔐 로그인 화면");
            System.out.println("========================================");

            System.out.print("아이디(이메일)  : ");
            String email = sc.nextLine();

            System.out.print("비밀번호       : ");
            String pw = sc.nextLine();

            System.out.println("========================================");

            loginStaff = staffDao.login(email, pw);
            if (loginStaff == null) {
            	System.out.print("\033[H\033[2J");
            	System.out.flush();
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n❌ 로그인 실패: 이메일 또는 비밀번호가 잘못되었습니다.");
            }else {
            	break;
            }
    	}

        Session.setCurrentUser(loginStaff);

        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n========================================");
        System.out.println("\n✅ 로그인 성공! " + loginStaff.getFirstName()+ loginStaff.getLastName() + "님 환영합니다!\n");
        
        new MainView(loginStaff.getRoleName()).showMenu();
    }
}