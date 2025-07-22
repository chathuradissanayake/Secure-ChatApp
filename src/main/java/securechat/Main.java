package securechat;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Start (1) Server or (2) Client? ");
        int choice = sc.nextInt();

        if (choice == 1) {
            Server.main(null);
        } else {
            Client.main(null);
        }
    }
}
