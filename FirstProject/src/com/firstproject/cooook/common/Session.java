package com.firstproject.cooook.common;

import com.firstproject.cooook.vo.StaffVO;

public class Session {
 private static StaffVO currentUser;

 public static void setCurrentUser(StaffVO staff) {
     currentUser = staff;
 }

 public static StaffVO getCurrentUser() {
     return currentUser;
 }

 public static void clear() {
     currentUser = null;
 }
}
