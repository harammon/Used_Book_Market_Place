package app;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.filters.Filters;

public class BookController {
	
	private final String mDataBasePath;
	private final InputController mInputController;

	public BookController(InputController inputController) {
		this(inputController, "./library.db");
	}
	
	public BookController(InputController inputController, String dataBasePath) {
		mDataBasePath = dataBasePath;
		mInputController = inputController;
	}
	
	public void addBook(String id) {
		System.out.println();
		System.out.println(":::: ���� ��� ::::");
		
		String name = mInputController.read("å �̸��� �Է� �ϼ��� : ");
		String isbn = mInputController.read("ISBN�� �Է� �ϼ���(��������) : ");
		if(!isbn.equals("")){
			try {
			Long.parseLong(isbn);
		} catch (NumberFormatException e) {
			System.out.println("ISBN���� ���ڸ� �Է��� �� �ֽ��ϴ�");
			return;
			}
		}
		String writer = mInputController.read("���ڸ� �Է� �ϼ���(��������) : ");
		String publisher = mInputController.read("���ǻ縦 �Է� �ϼ���(��������) : ");
		String year = mInputController.read("���ǳ⵵�� �Է� �ϼ���(��������) : ");
		if(!year.equals("")){
			try {
			Integer.parseInt(year);
		} catch (NumberFormatException e) {
			System.out.println("���ǳ⵵���� ���ڸ� �Է��� �� �ֽ��ϴ�");
			return;
			}
		}
		
		String price = mInputController.read("������ �Է� �ϼ���(��������) : ");
		if(!price.equals("")){
			try {
			Integer.parseInt(price);
		} catch (NumberFormatException e) {
			System.out.println("���ݿ��� ���ڸ� �Է��� �� �ֽ��ϴ�");
			return;
			}
		}
		
		String status = mInputController.read("���¸� �Է� �ϼ��� Excellent / Good / Fair (��������) : ");
		
		if (status.equals("") || status.equals("Excellent") || status.equals("Good") || status.equals("Fair")) {
			if (!mInputController.isEmpty(name, "å �̸��� �Է� ���ּ���")) {
				Nitrite dataBase = Nitrite.builder()
					    .compressed()
					    .filePath(mDataBasePath)
					    .openOrCreate();
				
				dataBase.getCollection("books").insert(Document
						.createDocument("id", id)	// userId
						.put("name", name)
						.put("isbn", isbn)
						.put("writer", writer)
						.put("publisher", publisher)
						.put("year", year)
						.put("price", price)
						.put("status", status));
				
				dataBase.commit();
				dataBase.close();
			}
		} else {
			System.out.println("���´� Excellent, Good, Fair �������� �ϳ��� �Է� �ؾ��մϴ�");
		}
	}
	
	private void updateBook(Document document) {
		System.out.println();
		
		String name = mInputController.read("å �̸��� �Է� �ϼ��� : ");
		String isbn = mInputController.read("ISBN�� �Է� �ϼ���(��������) : ");
		if(!isbn.equals("")){
			try {
			Long.parseLong(isbn);
		} catch (NumberFormatException e) {
			System.out.println("ISBN���� ���ڸ� �Է��� �� �ֽ��ϴ�");
			return;
			}
		}
		String writer = mInputController.read("���ڸ� �Է� �ϼ���(��������) : ");
		String publisher = mInputController.read("���ǻ縦 �Է� �ϼ���(��������) : ");
		String year = mInputController.read("���ǳ⵵�� �Է� �ϼ���(��������) : ");
		if(!year.equals("")){
			try {
			Integer.parseInt(year);
		} catch (NumberFormatException e) {
			System.out.println("���ǳ⵵���� ���ڸ� �Է��� �� �ֽ��ϴ�");
			return;
			}
		}
		String price = mInputController.read("������ �Է� �ϼ���(��������) : ");
		if(!price.equals("")){
			try {
			Integer.parseInt(price);
		} catch (NumberFormatException e) {
			System.out.println("���ݿ��� ���ڸ� �Է��� �� �ֽ��ϴ�");
			return;
			}
		}
		String status = mInputController.read("���¸� �Է� �ϼ��� Excellent / Good / Fair (��������) : ");
		
		if (status.equals("") || status.equals("Excellent") || status.equals("Good") || status.equals("Fair")) {
			if (!mInputController.isEmpty(name, "å �̸��� �Է� ���ּ���")) {
				Nitrite dataBase = Nitrite.builder()
					    .compressed()
					    .filePath(mDataBasePath)
					    .openOrCreate();
				
				document.replace("name", name);
				document.replace("isbn", isbn);
				document.replace("writer", writer);
				document.replace("publisher", publisher);
				document.replace("year", year);
				document.replace("price", price);
				document.replace("status", status);
				
				dataBase.getCollection("books").update(document);
				
				dataBase.commit();
				dataBase.close();
			}
		} else {
			System.out.println("���´� Excellent, Good, Fair �������� �ϳ��� �Է� �ؾ��մϴ�");
		}
	}
	
	private void deleteBook(Document document) {
		Nitrite dataBase = Nitrite.builder()
			    .compressed()
			    .filePath(mDataBasePath)
			    .openOrCreate();
		
		if (dataBase.hasCollection("books")) {
			dataBase.getCollection("books").remove(document);
			dataBase.commit();
		}
		
		dataBase.close();
	}
	
	public void listAllBooks() {
		System.out.println();
		System.out.println(":::: ���� ���� ::::");
		
		ArrayList<Document> books = listBooks(Filters.ALL);
		
		if (books.size() > 0) {
			final List<Integer> candidates = IntStream.rangeClosed(0, books.size()).boxed().collect(Collectors.toList());
			
			System.out.println("--------------------------------");
			switch (mInputController.menu("1. ����\n2. �ڷΰ���", Arrays.asList(1, 2, 3))) {
			case 1: {
				int indexOfBook = mInputController.menu("���� �� å ��ȣ (0 -> �ڷΰ���) : ", false, candidates);
				switch (indexOfBook) {
				case 0:
					return;
					
				default:
					deleteBook(books.get(indexOfBook - 1));
					break;
				}
			} break;
				
			default:
				return;
			}
		}
	}
	
	public void listMyBooks(String id) {
		System.out.println();
		System.out.println(":::: ���� ���� ::::");
		
		ArrayList<Document> books = listBooks(Filters.eq("id", id));
		
		if (books.size() > 0) {
			final List<Integer> candidates = IntStream.rangeClosed(0, books.size()).boxed().collect(Collectors.toList());
			
			System.out.println("--------------------------------");
			switch (mInputController.menu("1. ����\n2. ����\n3. �ڷΰ���", Arrays.asList(1, 2, 3))) {
			case 1: {
				int indexOfBook = mInputController.menu("���� �� å ��ȣ (0 -> �ڷΰ���) : ", false, candidates);
				switch (indexOfBook) {
				case 0:
					return;
					
				default:
					updateBook(books.get(indexOfBook - 1));
					break;
				}
			} break;
				
			case 2: {
				int indexOfBook = mInputController.menu("���� �� å ��ȣ (0 -> �ڷΰ���) : ", false, candidates);
				switch (indexOfBook) {
				case 0:
					return;
					
				default:
					deleteBook(books.get(indexOfBook - 1));
					break;
				}
			} break;
				
			default:
				return;
			}
		}
	}
	
	public void searchBooks(String id, boolean fromAdmin) {
		System.out.println();
		System.out.println(":::: ���� �˻� ::::");
		
		final String key;
		
		switch (mInputController.menu("1. �������� ã��\n2. ISBN���� ã��\n3. ���ڷ� ã��\n4. ���ǻ�� ã��\n5. ���ǳ⵵�� ã��\n6. �Ǹ��� ID�� ã��\n7. �ڷΰ���", Arrays.asList(1, 2, 3, 4, 5, 6, 7))) {
		case 1:	key = "name";		break;
		case 2:	key = "isbn";		break;
		case 3:	key = "writer";		break;
		case 4:	key = "publisher";	break;
		case 5:	key = "year";		break;
		case 6:	key = "id";			break;
		
		case 7:
		default:
			return;
		}
		
		String query = mInputController.read("�˻��� : ");
		
		if (!mInputController.isEmpty(query, "�˻�� �Է� ���ּ���")) {
			
			final ArrayList<Document> books;
			if (fromAdmin) {
				books = listBooks(Filters.eq(key, query));	
			} else {
				books = listBooks(Filters.and(Filters.eq(key, query), Filters.not(Filters.eq("id", id))));
			}
			
			if (books.size() > 0) {
				System.out.println("--------------------------------");
				
				if (!fromAdmin) {
					int indexOfBook = mInputController.menu("������ å ��ȣ (0 -> �ڷΰ���) : ", false, IntStream.rangeClosed(0, books.size()).boxed().collect(Collectors.toList()));
					
					switch (indexOfBook) {
					case 0:
						return;
						
					default: {
						Document bookDocument = books.get(indexOfBook - 1);
						
						final String sellerId = bookDocument.get("id", String.class);
						
						Nitrite dataBase = Nitrite.builder()
							    .compressed()
							    .filePath(mDataBasePath)
							    .openOrCreate();
						
						final Document sellerDocument = dataBase.getCollection("users").find(Filters.eq("id", sellerId)).firstOrDefault();
						
						dataBase.close();
						
						System.out.println("�Ǹ���(" + sellerDocument.get("email") + ")���� ���� ��û ������ ���� �մϴ�");
					} break;
					}
				}
			}
		}
	}

	private ArrayList<Document> listBooks(Filter filter) {
		Nitrite dataBase = Nitrite.builder()
			    .compressed()
			    .filePath(mDataBasePath)
			    .openOrCreate();
		
		ArrayList<Document> results = new ArrayList<>();
		if (dataBase.hasCollection("books")) {
			results.addAll(dataBase.getCollection("books").find(filter, FindOptions.sort("id", SortOrder.Ascending)).toList());
		}
		dataBase.close();
		
		if (results.size() > 0) {
			for (int index = 0 ; index < results.size() ; ++index) {
				System.out.println(String.format(
						"%4d.%20s%12s%12s%20s%8s%12s��%16s",
						(index + 1),
						results.get(index).get("name"),
						results.get(index).get("isbn"),
						results.get(index).get("writer"),
						results.get(index).get("publisher"),
						results.get(index).get("year"),
						results.get(index).get("price"),
						results.get(index).get("status")));
			}
		}
		
		return results;
	}
}
