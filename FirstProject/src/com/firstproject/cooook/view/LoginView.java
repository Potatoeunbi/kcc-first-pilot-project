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
            UIHelper.printTitle("🔐 로그인 화면");

            System.out.print("아이디(이메일) \t: ");
            String email = sc.nextLine();

            System.out.print("비밀번호 \t: ");
            String pw = sc.nextLine();

            loginStaff = staffDao.login(email, pw);
            if (loginStaff == null) {
                System.out.println();
                UIHelper.printError("로그인 실패: 이메일 또는 비밀번호가 잘못되었습니다.");
            }else {
            	break;
            }
    	}

        Session.setCurrentUser(loginStaff);

        System.out.println();
        UIHelper.printDivider();
        System.out.println();
        UIHelper.printSuccess("로그인 성공! " + loginStaff.getFirstName()+ loginStaff.getLastName() + "님 환영합니다!");
        System.out.println();
        UIHelper.printDivider();

        new MainView().showMenu();
    }
}