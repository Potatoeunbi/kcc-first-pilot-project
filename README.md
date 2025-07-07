# 🍽️ COOOK - 재고 관리 시스템

## 📋 프로젝트 개요

**COOOK**은 식당/카페의 재고와 주문을 효율적으로 관리할 수 있는 콘솔 기반 관리 시스템입니다. 역할 기반 접근 제어(RBAC)를 통해 다양한 권한의 사용자가 시스템을 안전하게 이용할 수 있습니다.

## 🚀 주요 기능

### 🔐 사용자 인증 및 권한 관리
- **로그인 시스템**: 이메일과 SHA-256 해시된 비밀번호를 통한 안전한 인증
- **역할 기반 접근 제어**: 관리자, 직원 등 다양한 역할별 메뉴 접근 권한 관리
- **세션 관리**: 로그인한 사용자 정보를 세션으로 관리

### 👥 직원 관리
- 직원 등록, 수정, 조회, 삭제 (Soft Delete)
- 직원별 역할 할당 및 권한 관리
- 이메일 중복 검증

### 🍽️ 메뉴 관리
- 메뉴 등록, 수정, 조회, 삭제
- 메뉴 카테고리 관리
- 메뉴별 가격 및 정보 관리

### 📦 주문 관리
- 주문 등록, 수정, 조회, 삭제 (Soft Delete)
- 주문별 수량 및 총액 자동 계산
- 주문 이력 관리

### 📂 카테고리 관리
- 계층형 카테고리 구조 지원
- 카테고리별 메뉴 분류
- 트리 형태의 시각적 표시

## 🏗️ 시스템 아키텍처

### 📁 프로젝트 구조
```
FirstProject/
├── src/com/firstproject/cooook/
│   ├── common/           # 공통 클래스
│   │   ├── RoleFeatureCode.java  # 권한 기능 코드 정의
│   │   └── Session.java          # 세션 관리
│   ├── dao/              # 데이터 접근 계층
│   │   ├── StaffDao.java         # 직원 데이터 접근
│   │   ├── MenuDao.java          # 메뉴 데이터 접근
│   │   ├── OrderDao.java         # 주문 데이터 접근
│   │   ├── RoleDao.java          # 역할 데이터 접근
│   │   └── MenuRepository.java   # 메뉴 리포지토리
│   ├── db/               # 데이터베이스 연결
│   │   └── DBUtil.java           # DB 연결 및 리소스 관리
│   ├── util/             # 유틸리티 클래스
│   │   ├── PasswordUtil.java     # 비밀번호 해시 처리
│   │   └── Util.java             # 공통 유틸리티
│   ├── view/             # 사용자 인터페이스
│   │   ├── LoginView.java        # 로그인 화면
│   │   ├── MainView.java         # 메인 메뉴
│   │   ├── StaffManageView.java  # 직원 관리 화면
│   │   ├── MenuView2.java        # 메뉴 관리 화면
│   │   ├── OrderManageView.java  # 주문 관리 화면
│   │   └── UIHelper.java         # UI 헬퍼 클래스
│   ├── vo/               # 값 객체 (Value Objects)
│   │   ├── StaffVO.java          # 직원 정보
│   │   ├── Menu.java             # 메뉴 정보
│   │   ├── OrderVO.java          # 주문 정보
│   │   └── RoleVO.java           # 역할 정보
│   └── MainApp.java      # 애플리케이션 진입점
```

### 🔧 기술 스택
- **언어**: Java 8+
- **데이터베이스**: Oracle Database
- **라이브러리**: 
  - Lombok (코드 간소화)
  - Oracle JDBC Driver (ojdbc10-19.27.0.0.jar)
- **아키텍처**: MVC 패턴, DAO 패턴

## 🎯 핵심 설계 특징

### 1. **역할 기반 접근 제어 (RBAC)**
```java
// RoleFeatureCode.java에서 기능별 권한 정의
public static final String WORKER_MANAGE = "WORKER_MANAGE";
public static final String ROLE_MANAGE = "ROLE_MANAGE";
public static final String ORDER_MANAGE = "ORDER_MANAGE";
// ... 기타 권한들
```

### 2. **안전한 비밀번호 관리**
```java
// SHA-256 해시를 통한 비밀번호 암호화
public static String hashPassword(String password) {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    // ... 해시 처리
}
```

### 3. **트랜잭션 관리**
```java
// 메뉴 삭제 시 연관 데이터 함께 처리
connection.setAutoCommit(false);
// ... 트랜잭션 처리
connection.commit();
```

### 4. **사용자 친화적 UI**
- 이모지를 활용한 직관적인 인터페이스
- 박스 형태의 깔끔한 목록 표시
- 한글 지원 및 적절한 공백 처리

## 🗄️ 데이터베이스 설계

### 주요 테이블
- **staff**: 직원 정보 (이름, 이메일, 비밀번호, 역할 등)
- **roles**: 역할 정보 (역할명, 권한 등)
- **menu**: 메뉴 정보 (메뉴명, 가격 등)
- **orders**: 주문 정보 (주문자, 메뉴, 수량, 총액 등)
- **categories**: 카테고리 정보 (계층형 구조)

## 🚀 실행 방법

### 1. 환경 설정
- Java 8 이상 설치
- Oracle Database 연결 설정
- 프로젝트 라이브러리 설정 (lombok.jar, ojdbc10-19.27.0.0.jar)

### 2. 데이터베이스 연결 설정
```java
// DBUtil.java에서 연결 정보 수정
private static final String URL = "jdbc:oracle:thin:@192.168.2.182:1522:xe";
private static final String USERNAME = "hr";
private static final String PASSWORD = "hr";
```

### 3. 실행
```bash
# MainApp.java 실행
java -cp "lib/*:bin" com.firstproject.cooook.MainApp
```

## 🎨 사용자 인터페이스 특징

### 1. **아스키 아트 배너**
- 프로그램 시작 시 "COOOK" 로고 표시
- 컬러 출력으로 시각적 효과 향상

### 2. **박스 형태 목록 표시**
```java
// UIHelper를 통한 일관된 UI 제공
UIHelper.printBoxedList("제목", list, formatter);
```

### 3. **이모지 활용**
- ✅ 성공 메시지
- ❌ 오류 메시지
- ⚠️ 경고 메시지
- 💡 정보 메시지

## 🔒 보안 기능

1. **비밀번호 암호화**: SHA-256 해시 알고리즘 사용
2. **세션 관리**: 로그인 사용자 정보 안전한 관리
3. **권한 검증**: 기능별 접근 권한 확인
4. **입력 검증**: 사용자 입력 데이터 유효성 검사

## 📈 확장 가능성

- **웹 인터페이스**: 현재 콘솔 기반을 웹으로 확장 가능
- **모바일 앱**: REST API 추가로 모바일 앱 연동 가능
- **재고 관리**: 현재 주문 관리에 재고 관리 기능 추가 가능
- **매출 분석**: 주문 데이터 기반 매출 통계 기능 추가 가능

## 👨‍💻 개발자 정보

이 프로젝트는 Java 학습을 위한 첫 번째 파일럿 프로젝트로 개발되었습니다.

---

**COOOK** - 효율적인 식당 관리의 시작! 🚀