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

        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = isDone[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + status + "] " + tasks[i]);
                }
                System.out.println(LINE);
            } else if (command.startsWith("mark ")) {
                int index = Integer.parseInt(command.substring(5)) - 1;
                isDone[index] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [X] " + tasks[index]);
                System.out.println(LINE);
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
                System.out.println(LINE);
            }
        }
        scanner.close();
    }
}
