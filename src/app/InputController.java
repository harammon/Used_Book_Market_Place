package app;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class InputController {
	private final Scanner mScanner;
	
	public InputController() {
		mScanner = new Scanner(System.in);
	}
	
	public int menu(String prompt, List<Integer> candidate) {
		return menu(prompt, true, candidate);
	}
	public int menu(String prompt, boolean needMenuPrompt, List<Integer> candidate) {
		
		if (needMenuPrompt) {
			System.out.println(prompt);
			System.out.print("메뉴를 선택하세요 : ");	
		} else {
			System.out.print(prompt);
		}
		
		String input = mScanner.nextLine().trim();
		
		if (input.isEmpty()) {
			return menu(prompt, needMenuPrompt, candidate);
		} else {
			try {
				int choice = Integer.parseInt(input);
				
				if (candidate.contains(choice)) {
					return choice;
				} else {
					return menu(prompt, needMenuPrompt, candidate);
				}
			} catch (NumberFormatException e) {
				return menu(prompt, needMenuPrompt, candidate);
			}
		}
	}
	
	public String read(String prompt) {
		return read(prompt, true);
	}
	public String read(String prompt, boolean trim) {
		System.out.print(prompt);
		
		String input = mScanner.nextLine().trim();
		
		return (trim ? input.trim() : input);
	}
	
	public boolean isEmpty(String input, String message) {
		if ((input != null) && !input.isEmpty()) {
			return false;
		} else {
			System.out.println(message);
			return true;
		}
	}

	public void close() {
		mScanner.close();
	}
}
