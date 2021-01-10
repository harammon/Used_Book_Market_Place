package app;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.dizitart.no2.Document;
import org.dizitart.no2.filters.Filters;

public class App {
	
	private final BookController mBookController;
	private final UserController mUserController;
	private final InputController mInputController;
	private String mCurrentUser = null;
	public App() {
		mInputController = new InputController();
		
		mUserController = new UserController(mInputController);
		mBookController = new BookController(mInputController);
	}
	
	private void running() {

		boolean isRunnging = true;
		
		while (isRunnging) {
			
			if (mCurrentUser == null) {
				
				switch (mInputController.menu(":::: WELCOME ::::\n1. 로그인\n2. 회원가입\n3. 종료", Arrays.asList(1, 2, 3))) {
				case 1:
					mCurrentUser = mUserController.login();
					break;
					
				case 2:
					mCurrentUser = mUserController.join();
					break;
					
				case 3:
					isRunnging = false;
				}
			} else {
				if (mCurrentUser.equals("admin")) {
					switch (mInputController.menu(":::: 관리자 메뉴 ::::\n1. 사용자 관리\n2. 도서 검색\n3. 도서 관리\n4. 로그아웃\n5. 종료", Arrays.asList(1, 2, 3, 4))) {
					case 1:
						mUserController.listUsers();
						break;
						
					case 2:
						mBookController.searchBooks(mCurrentUser, true);
						break;
						
					case 3:
						mBookController.listAllBooks();
						break;
						
					case 4:
						mCurrentUser = null;
						break;
						
					case 5:
						isRunnging = false;
					}
				} else {
					switch (mInputController.menu(":::: 사용자 메뉴 ::::\n1. 도서 검색\n2. 도서 등록\n3. 도서 관리\n4. 로그아웃\n5. 종료", Arrays.asList(1, 2, 3, 4, 5))) {
					case 1:
						mBookController.searchBooks(mCurrentUser, false);
						break;
						
					case 2:
						mBookController.addBook(mCurrentUser);
						break;
						
					case 3:
						mBookController.listMyBooks(mCurrentUser);
						break;
						
					case 4:
						mCurrentUser = null;
						break;
						
					case 5:
						isRunnging = false;
					}
				}
			}
			System.out.println();
		}
		
		mInputController.close();
	}
	
	public static void main(final String[] argv) {
		App app = new App();
		app.running();
	}
}