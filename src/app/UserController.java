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
			System.out.println("admin�� ���̵�� ����Ҽ� �����ϴ�");
			return null;
		} else {
			if (mInputController.isEmpty(id, "���� ���̵�� ��� �Ұ��մϴ�")) {
				return null;
			} else {
				Nitrite dataBase = Nitrite.builder()
					    .compressed()
					    .filePath(mDataBasePath)
					    .openOrCreate();
				
				final String result;
				
				if (dataBase.hasCollection("users") &&
						(dataBase.getCollection("users").find(Filters.eq("id", id)).size() > 0)) {
					System.out.println("�̹� ���Ե� ���̵� �Դϴ�");
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
		System.out.println(":::: ȸ������ ::::");
		
		String id = mInputController.read("���̵� �Է� �ϼ��� : ");
		if (id.equals("admin")) {
			System.out.println("admin�� ���̵�� ����Ҽ� �����ϴ�");
			return null;
		} else {
			String pw 		= mInputController.read("��й�ȣ�� �Է� �ϼ��� : ");
			String name 	= mInputController.read("�̸��� �Է� �ϼ��� : ");
			String email	= mInputController.read("�̸����� �Է� �ϼ��� : ");
			
			return _join(id, pw, name, email);
		}
	}
	
	public String login() {
		System.out.println();
		System.out.println(":::: �α��� ::::");
		
		String id 	= mInputController.read("���̵� �Է� �ϼ��� : ");
		String pw 	= mInputController.read("��й�ȣ�� �Է� �ϼ��� : ");
		
		return _login(id, pw);
	}
	
	public String _login(String id, String pw) {
		if (mInputController.isEmpty(id, "���̵� �Է� ���ּ���") ||
				mInputController.isEmpty(pw, "��й�ȣ�� �Է� ���ּ���")) {
			return null;
		} else {
			if (id.equals("admin")) {
				
				if (pw.equals("nayana")) {
					return "admin";
				} else {
					System.out.println("��й�ȣ�� Ʋ�Ƚ��ϴ�");
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
						System.out.println("�ش� ȸ���� Deactivated�����Դϴ�. �����ڿ��� �����ϼ���.");
						result = null;
					}
				}
				
				else {
					System.out.println("���̵� Ȥ�� ��й�ȣ�� Ȯ���ϼ���");
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
			System.out.println("Ȱ��ȭ �� ������ ������ �� �����ϴ�");
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
		System.out.println(":::: ����� ���� ::::");
		
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
			
			switch (mInputController.menu("1. ���� ���� \n2. ���� ���� \n3. �ڷΰ���", Arrays.asList(1, 2, 3))) {
			case 1: {
				int indexOfUser = mInputController.menu("���� ���� �� ���� ��ȣ (0 -> �ڷΰ���) : ", false, candidates);
				switch (indexOfUser) {
				case 0:
					return;
					
				default:
					toggleStatusOfUser(users.get(indexOfUser - 1));
					break;
				}
			} break;
				
			case 2: {
				int indexOfUser = mInputController.menu("���� �� ���� ��ȣ (0 -> �ڷΰ���) : ", false, candidates);
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
