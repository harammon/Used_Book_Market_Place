package junit;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.App;
import app.InputController;
import app.UserController;

class AppTest {

	private final InputStream mSystemIn = System.in;
	private final PrintStream mSystemOut = System.out;
	
	private ByteArrayInputStream mCustomIn = null;
	private ByteArrayOutputStream mCustomOut = null;
	
	private void customIn(String str) {
		System.setIn(mCustomIn = 
				new ByteArrayInputStream(str.getBytes()));
	}

	@BeforeEach
	public void beforeEach() {
		System.setOut(new PrintStream(
				mCustomOut = new ByteArrayOutputStream()));
	}
	
	@Test
	void testUserControllerJoin0() {
	  String dataBaseName = String.format("library_%d.db", System.currentTimeMillis());
	  
	  UserController userController = new UserController(new InputController(), "./" + dataBaseName);
	  
	  userController._join("", "", "", "");
	  assertEquals("공백 아이디는 사용 불가합니다" + System.lineSeparator(), mCustomOut.toString());
	  mCustomOut.reset();
	  
	  try {
	    Files.deleteIfExists(FileSystems.getDefault().getPath(".", dataBaseName));
	  } catch (IOException e) {
	    e.printStackTrace();
	  }
	}

	@Test
	void testUserControllerJoin1() {
	  String dataBaseName = String.format("library_%d.db", System.currentTimeMillis());
	  
	  UserController userController = new UserController(new InputController(), "./" + dataBaseName);
	  
	  userController._join("admin", "", "", "");
	  assertEquals("admin은 아이디로 사용할수 없습니다" + System.lineSeparator(), mCustomOut.toString());
	  mCustomOut.reset();
	  
	  try {
	    Files.deleteIfExists(FileSystems.getDefault().getPath(".", dataBaseName));
	  } catch (IOException e) {
	    e.printStackTrace();
	  }
	}

	@Test
	void testUserControllerJoin2() {
	  String dataBaseName = String.format("library_%d.db", System.currentTimeMillis());
	  
	  UserController userController = new UserController(new InputController(), "./" + dataBaseName);
	  
	  assertEquals("id", userController._join("id", "password", "name", "email"));
	  
	  userController._join("id", "password", "name", "email");
	  assertEquals("이미 가입된 아이디 입니다" + System.lineSeparator(), mCustomOut.toString());
	  mCustomOut.reset();
	  
	  try {
	    Files.deleteIfExists(FileSystems.getDefault().getPath(".", dataBaseName));
	  } catch (IOException e) {
	    e.printStackTrace();
	  }
	}

	@Test
	void testUserControllerLoginByAdmin0() {
		UserController userController = new UserController(new InputController());
		
		userController._login("", "nayana");
		assertEquals("아이디를 입력 해주세요" + System.lineSeparator(), mCustomOut.toString());
		mCustomOut.reset();
	}
	@Test
	void testUserControllerLoginByAdmin1() {
		UserController userController = new UserController(new InputController());
			
		userController._login("admin", "");
		assertEquals("비밀번호를 입력 해주세요" + System.lineSeparator(), mCustomOut.toString());
		mCustomOut.reset();
	}
	@Test
	void testUserControllerLoginByAdmin2() {
		UserController userController = new UserController(new InputController());
			
		
		assertEquals("admin", userController._login("admin", "nayana"));
		mCustomOut.reset();
	}
	@Test
	void testUserControllerLoginByAdmin3() {
		UserController userController = new UserController(new InputController());
		
		assertEquals(null, userController._login("admin", "nayana1"));
		mCustomOut.reset();
	}
	
	
	@Test
	void testUserControllerLoginByNormal4() {
		String dataBaseName = String.format("library_%d.db", System.currentTimeMillis());
	
		UserController userController = new UserController(new InputController(), "./" + dataBaseName);
				
		userController._join("id", "password", "name", "email");
		
		assertEquals("id", userController._login("id", "password"));
	}

	@AfterEach
	public void afterEach() {
		System.setIn(mSystemIn);
		System.setOut(mSystemOut);
	}
}
