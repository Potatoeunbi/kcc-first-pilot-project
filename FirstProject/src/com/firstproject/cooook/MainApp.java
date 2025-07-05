package com.firstproject.cooook;

import com.firstproject.cooook.view.LoginView;
import com.firstproject.cooook.view.MenuView;

public class MainApp {

	public static void main(String[] args) {
		printBanner();
		
	    
//		LoginView loginView = new LoginView();
//		loginView.runLogin();


	}
	
	private static void printBanner() {
	    String ANSI_RESET = "\u001B[0m";
	    String ANSI_YELLOW = "\u001B[33m";
	    String ANSI_BLUE = "\u001B[34m";
	    String ANSI_GREEN = "\u001B[32m";

	    //아래는 그냥 임시로 뒀음.
	    System.out.println(ANSI_YELLOW);
	    System.out.println("╔════════════════════════════════════════════════╗");
	    System.out.println("║                                                ║");
	    System.out.println("║   ██████╗ ███████╗ █████╗  ██████╗  ██████╗    ║");
	    System.out.println("║  ██╔════╝ ██╔════╝██╔══██╗██╔════╝ ██╔═══██╗   ║");
	    System.out.println("║  ██║  ███╗█████╗  ███████║██║  ███╗██║   ██║   ║");
	    System.out.println("║  ██║   ██║██╔══╝  ██╔══██║██║   ██║██║   ██║   ║");
	    System.out.println("║  ╚██████╔╝███████╗██║  ██║╚██████╔╝╚██████╔╝   ║");
	    System.out.println("║   ╚═════╝ ╚══════╝╚═╝  ╚═╝ ╚═════╝  ╚═════╝    ║");
	    System.out.println("║                                                ║");
	    System.out.println("╚════════════════════════════════════════════════╝");
	    System.out.println(ANSI_GREEN + "		   🚀 재고 재고 재고! 관리 시스템! 🚀" + ANSI_RESET);
	    System.out.println();
	    

	    MenuView mm = new MenuView();
	    mm.runMenu();
	}

}
