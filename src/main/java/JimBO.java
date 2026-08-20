import java.util.Scanner;

public class JimBO {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        String banner = "   ___   _____  ___  ___ ______   _____ \n"
                + "  |_  | |_   _| |  \\/  | | ___ \\ |  _  |\n"
                + "    | |   | |   | .  . | | |_/ / | | | |\n"
                + "    | |   | |   | |\\/| | | ___ \\ | | | |\n"
                + "/\\__/ /  _| |_  | |  | | | |_/ / \\ \\_/ /\n"
                + "\\____/   \\___/  \\_|  |_/ \\____/   \\___/ \n";

        System.out.println(LINE);
        System.out.println(banner);
        System.out.println("Hello! I'm JimBO.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }
            System.out.println(command);
            System.out.println(LINE);
        }
        scanner.close();
    }
}
