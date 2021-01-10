package app;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dizitart.no2.Document;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.filters.Filters;

public class UserController {
	
	private final String mDataBasePath;
	private final InputController mInputController;

	public UserController(InputController inputController) {
		this(inputController, "./library.db");
	}
	
	public UserController(InputController inputController, String dataBasePath) {
		mDataBasePath = dataBasePath;
		mInputController = inputController;
	}

	public String _join(String id, String pw, String name, String email) {
		
		if (id.equals("admin")) {
			System.out.println("admin은 아이디로 사용할수 없습니다");
			return null;
		} else {
			if (mInputController.isEmpty(id, "공백 아이디는 사용 불가합니다")) {
				return null;
			} else {
				Nitrite dataBase = Nitrite.builder()
					    .compressed()
					    .filePath(mDataBasePath)
					    .openOrCreate();
				
				final String result;
				
				if (dataBase.hasCollection("users") &&
						(dataBase.getCollection("users").find(Filters.eq("id", id)).size() > 0)) {
					System.out.println("이미 가입된 아이디 입니다");
					result = null;
				} else {
					dataBase.getCollection("users").insert(Document
							.createDocument("id", id)
							.put("pw", pw)
							.put("name", name)
							.put("email", email)
							.put("status", "activated"));
					
					dataBase.commit();
					
					result = id;
				}
				
				dataBase.close();
				return result;
			}
		}
	}
	
	public String join() {
		System.out.println();
		System.out.println(":::: 회원가입 ::::");
		
		String id = mInputController.read("아이디를 입력 하세요 : ");
		if (id.equals("admin")) {
			System.out.println("admin은 아이디로 사용할수 없습니다");
			return null;
		} else {
			String pw 		= mInputController.read("비밀번호를 입력 하세요 : ");
			String name 	= mInputController.read("이름을 입력 하세요 : ");
			String email	= mInputController.read("이메일을 입력 하세요 : ");
			
			return _join(id, pw, name, email);
		}
	}
	
	public String login() {
		System.out.println();
		System.out.println(":::: 로그인 ::::");
		
		String id 	= mInputController.read("아이디를 입력 하세요 : ");
		String pw 	= mInputController.read("비밀번호를 입력 하세요 : ");
		
		return _login(id, pw);
	}
	
	public String _login(String id, String pw) {
		if (mInputController.isEmpty(id, "아이디를 입력 해주세요") ||
				mInputController.isEmpty(pw, "비밀번호를 입력 해주세요")) {
			return null;
		} else {
			if (id.equals("admin")) {
				
				if (pw.equals("nayana")) {
					return "admin";
				} else {
					System.out.println("비밀번호가 틀렸습니다");
					return null;
				}
			} else {
				String result = null;
				
				Nitrite dataBase = Nitrite.builder()
					    .compressed()
					    .filePath(mDataBasePath)
					    .openOrCreate();
				
				if (dataBase.hasCollection("users") &&
						(dataBase.getCollection("users").find(Filters.and(Filters.eq("id", id), Filters.eq("pw", pw), Filters.eq("status", "activated"))).size() > 0)) {
					
					result = id;
				} 
				else if (dataBase.hasCollection("users") &&
						(dataBase.getCollection("users").find(Filters.and(Filters.eq("id", id), Filters.eq("pw", pw))).size() > 0)) {
					if(dataBase.getCollection("users").find(Filters.and(Filters.eq("status", "deactivated"))).size()>0) {
						System.out.println("해당 회원은 Deactivated상태입니다. 관리자에게 문의하세요.");
						result = null;
					}
				}
				
				else {
					System.out.println("아이디 혹은 비밀번호를 확인하세요");
					result = null;
				}
				dataBase.close();
				return result;
			}
		}
	}
	
	private void toggleStatusOfUser(Document document) {
		Nitrite dataBase = Nitrite.builder()
			    .compressed()
			    .filePath(mDataBasePath)
			    .openOrCreate();
		
		if (dataBase.hasCollection("users")) {
			
			document.replace(
					"status",
					document.get("status").equals("activated") ?
							"deactivated" :
							"activated");
			
			dataBase.getCollection("users").update(document);
			dataBase.commit();
		}
		
		dataBase.close();
	}
	
	private void deleteUser(Document document) {
		if (document.get("status").equals("activated")) {
			System.out.println("활성화 된 계정은 삭제할 수 없습니다");
		} else {
			Nitrite dataBase = Nitrite.builder()
				    .compressed()
				    .filePath(mDataBasePath)
				    .openOrCreate();
			
			if (dataBase.hasCollection("users")) {
				dataBase.getCollection("users").remove(document);
			}
			
			if (dataBase.hasCollection("books")) {
				dataBase.getCollection("books").remove(Filters.eq("id", document.get("id")));
			}
			
			dataBase.commit();
			dataBase.close();	
		}
	}

	public void listUsers() {
		System.out.println();
		System.out.println(":::: 사용자 관리 ::::");
		
		Nitrite dataBase = Nitrite.builder()
			    .compressed()
			    .filePath(mDataBasePath)
			    .openOrCreate();
		
		ArrayList<Document> users = new ArrayList<>();
		if (dataBase.hasCollection("users")) {
			users.addAll(dataBase.getCollection("users").find().toList());
		}
		dataBase.close();
		
		if (users.size() > 0) {
			for (int index = 0 ; index < users.size() ; ++index) {
				System.out.println(String.format(
						"%4d.%20s%20s%20s%24s%20s",
						(index + 1),
						users.get(index).get("id"),
						users.get(index).get("pw"),
						users.get(index).get("name"),
						users.get(index).get("email"),
						users.get(index).get("status")));
			}
			System.out.println("--------------------------------");
			
			final List<Integer> candidates = IntStream.rangeClosed(0, users.size()).boxed().collect(Collectors.toList());
			
			switch (mInputController.menu("1. 유저 관리 \n2. 유저 삭제 \n3. 뒤로가기", Arrays.asList(1, 2, 3))) {
			case 1: {
				int indexOfUser = mInputController.menu("상태 변경 할 유저 번호 (0 -> 뒤로가기) : ", false, candidates);
				switch (indexOfUser) {
				case 0:
					return;
					
				default:
					toggleStatusOfUser(users.get(indexOfUser - 1));
					break;
				}
			} break;
				
			case 2: {
				int indexOfUser = mInputController.menu("삭제 할 유저 번호 (0 -> 뒤로가기) : ", false, candidates);
				switch (indexOfUser) {
				case 0:
					return;
					
				default:
					deleteUser(users.get(indexOfUser - 1));
					break;
				}
			} break;
				
			default:
				return;
			}
		}
	}
}
