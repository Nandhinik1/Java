import java.util.Scanner;

public class StringSorting {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter String:");
		String str= sc.nextLine();
		StringSorting obj=new StringSorting();
		System.out.println("Sorted string is:"+obj.sort(str));
	}
	
	String sort(String str) {
		char[] ch=str.toCharArray();
		for(int i=0; i<str.length();i++) {
			for(int j=i+1;j<str.length();j++) {
				if(ch[i]>ch[j]) {
					char temp= ch[i];
					ch[i]=ch[j];
					ch[j]=temp;
				}
			}
		}
		return new String(ch);
	}

}


/*
 * Enter String: 
 * nandhini 
 * Sorted string is:adhiinnn
 */
