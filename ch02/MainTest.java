package ch02;

import java.util.StringTokenizer;

public class MainTest {
	public static void main(String[] args) {
		String str = "hello>bye@we are the world";
		
		StringTokenizer s = new StringTokenizer(str, ">|@");
		String a = s.nextToken();
		String b = s.nextToken();
		String c = s.nextToken();
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
	}
	
}
