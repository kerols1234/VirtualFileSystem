/*
20180205
20180308
20180131
 */
package virtualfilesystem;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class VirtualFileSystem {

    static int numberOfBlocks = 0;
    static int sizeOfBlock = 1;
    static Directory root = new Directory();
    static ArrayList<Integer> stateOfBlocks = new ArrayList<>();
    static User user = new User();
    static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        try {
            File myObj = new File("DiskStructure.txt");
            String m;
            Scanner myReader = new Scanner(myObj);
            Scanner scanner = new Scanner(System.in);
            ArrayList<String> data = new ArrayList<>();
            Allocation method;

            root.setDirectoryName("root");

            String d = myReader.nextLine();

            if (d.equalsIgnoreCase("new")) {

                System.out.println("Enter number of blocks: ");
                numberOfBlocks = scanner.nextInt();
                m = scanner.nextLine();

                while (true) {
                    System.out.println("Choose method");
                    System.out.println("1-Contiguous Allocation method");
                    System.out.println("2-Linked Allocation method");
                    System.out.println("3-Indexed Allocation method");
                    m = scanner.nextLine();

                    if (m.equalsIgnoreCase("1")) {
                        m = "c";
                        break;
                    } else if (m.equalsIgnoreCase("2")) {
                        m = "l";
                        break;
                    } else if (m.equalsIgnoreCase("3")) {
                        m = "i";
                        break;
                    }
                    System.out.println("error");
                }
            } else {

                numberOfBlocks = Integer.parseInt(d.split(" ")[0]);
                m = myReader.nextLine();

                while (myReader.hasNextLine()) {
                    data.add(myReader.nextLine());
                }

            }

            for (int i = 0; i < numberOfBlocks; i++) {
                stateOfBlocks.add(0);
            }

            if (m.equalsIgnoreCase("l")) {
                method = new LinkedAllocation();
            } else if (m.equalsIgnoreCase("c")) {
                method = new ContiguousAllocation();
            } else {
                method = new IndexedAllocation();
            }

            PrintWriter writer = new PrintWriter(myObj);
            writer.print("");
            writer.close();
            myReader.close();

            method.read(data, stateOfBlocks, root);

            readUsers();
            user = users.get(0);

            readCapabilities();

            comands(method);

            writeUsers();
            writeCapabilities();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }

    public static void readUsers() throws FileNotFoundException {
        File myObj = new File("user.txt");
        Scanner myReader = new Scanner(myObj);
        String d;
        while (myReader.hasNextLine()) {
            d = myReader.nextLine();
            String[] t = d.split(",");
            User u = new User(t[0], t[1]);
            users.add(u);
        }
    }

    public static void writeUsers() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("user.txt"));
        String d = "";
        for (int i = 0; i < users.size(); i++) {
            d += users.get(i).getName() + "," + users.get(i).getPassword() + "\n";
        }
        writer.print(d);
        writer.close();
    }

    public static void readCapabilities() throws FileNotFoundException {
        File myObj = new File("capabilities.txt");
        Scanner myReader = new Scanner(myObj);
        String d;
        while (myReader.hasNextLine()) {
            d = myReader.nextLine();
            String[] t = d.split(",");
            int y = 2;
            for (int j = 0; j < users.size(); j++) {
                users.get(j).getPath().add(t[0]);
                users.get(j).getCapabilities().add(Integer.parseInt(t[y]));
                y += 2;
            }
        }
    }

    public static void writeCapabilities() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("capabilities.txt"));
        String d = "";
        for (int i = 0; i < users.get(0).getPath().size(); i++) {
            d += users.get(0).getPath().get(i);
            for (int j = 0; j < users.size(); j++) {
                String x = (users.get(j).getCapabilities().get(i)).toString();
                if (x.length() == 1) {
                    x = "0" + x;
                }
                d += "," + users.get(j).getName() + "," + x;
            }
            d += "\n";
        }
        writer.print(d);
        writer.close();
    }

    public static void deallocateOrAllocateOfBlocks(int[] data, int state) {
        for (int i = 0; i < data.length; i++) {
            stateOfBlocks.set(data[i], state);
        }
    }

    public static void deallocateOfDataInDirectory(Directory d) {
        ArrayList<FileVS> files = d.getFiles();
        ArrayList<Directory> subDirectories = d.getSubDirectories();

        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).isDeleted()) {
                files.get(i).setDeleted(true);
                deallocateOrAllocateOfBlocks(files.get(i).getAllocatedBlocks(), 0);
            }
        }

        for (int i = 0; i < subDirectories.size(); i++) {
            if (!subDirectories.get(i).isDeleted()) {
                subDirectories.get(i).setDeleted(true);
                deallocateOfDataInDirectory(subDirectories.get(i));
            }
        }
    }

    public static void DisplayDiskStatus() {
        int size = numberOfBlocks;
        for (int i = 0; i < numberOfBlocks; i++) {
            if (stateOfBlocks.get(i) == 1) {
                size--;
                System.out.println("Block number " + i + " Allocated");
            } else {
                System.out.println("Block number " + i + " Free");
            }
        }
        root.printDirectoryStructure("", true);
        System.out.println("Free space:" + size);
        System.out.println("Allocated space: " + (numberOfBlocks - size));
    }

    public static User checkUser(String name) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getName().equalsIgnoreCase(name)) {
                return users.get(i);
            }
        }
        return null;
    }

    public static void getAllPathsOfDirectories(String w, User u, Directory d) { 
        ArrayList<String> p = user.getPath();
        for(int i = 0; i <p.size(); i++){
            u.getPath().add(p.get(i));
            u.getCapabilities().add(0);
        }
    }

    public static Boolean checkAccess(String path, int c) {

        String[] temp = path.split("/");
        String t = "root";

        int b = 10;
        if(c == 1){
            b = 1;
        }
        
        if (user.getName().equalsIgnoreCase("admin")) {
            return true;
        }

        ArrayList<String> p = user.getPath();
        for (int i = 1; i < temp.length; i++) {

            if (temp[i].contains(".")) {
                continue;
            }

            for (int j = 0; j < p.size(); j++) {
                int x = user.getCapabilities().get(j);
                if (p.get(j).equalsIgnoreCase(t) && (x == b || x == 11)) {
                    return true;
                }
            }

            t += "/" + temp[i];

        }

        for (int j = 0; j < p.size(); j++) {
            int x = user.getCapabilities().get(j);
            if (p.get(j).equalsIgnoreCase(t) && (x == 10 || x == 11)) {
                return true;
            }
        }

        return false;
    }

    public static void deleteDirectories(String path) {

        ArrayList<String> p = user.getPath();

        for (int i = p.size() - 1; i > -1; i--) {

            if (p.get(i).contains(path)) {

                for (int j = 0; j < users.size(); j++) {
                    users.get(j).getPath().remove(i);
                    users.get(j).getCapabilities().remove(i);
                }

            }

        }
    }

    public static void comands(Allocation method) {

        String comand;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("----------------------------------------------------------");
            comand = scanner.nextLine();
            String[] arr = comand.split(" ");

            if (arr[0].equalsIgnoreCase("CreateFile") && arr.length == 3) {

                int size = Integer.parseInt(arr[2]);
                int[] alloc = method.allocateBlocks(size, numberOfBlocks, stateOfBlocks);

                if (!checkAccess(arr[1],0)) {
                    System.out.println("you do not have access to create file in this folder");
                    continue;
                }

                if (alloc == null || !root.insertFile(arr[1], alloc)) {
                    System.out.println("error");
                    continue;
                }

                deallocateOrAllocateOfBlocks(alloc, 1);

            } else if (arr[0].equalsIgnoreCase("CreateFolder") && arr.length == 2) {

                if (!checkAccess(arr[1],0)) {
                    System.out.println("you do not have access to create file in this folder");
                    continue;
                }

                if (!root.insertSubDirectories(arr[1])) {
                    System.out.println("error");
                    continue;
                }

                users.get(0).getPath().add(arr[1]);
                users.get(0).getCapabilities().add(11);
                for (int i = 1; i < users.size(); i++) {
                    users.get(i).getPath().add(arr[1]);
                    users.get(i).getCapabilities().add(00);
                }

            } else if (arr[0].equalsIgnoreCase("DeleteFile") && arr.length == 2) {

                if (!checkAccess(arr[1],1)) {
                    System.out.println("you do not have access to create file in this folder");
                    continue;
                }

                FileVS f = root.checkPathOfFile(arr[1]);
                if (f == null) {
                    System.out.println("error");
                } else {
                    f.setDeleted(true);
                    deallocateOrAllocateOfBlocks(f.getAllocatedBlocks(), 0);
                }

            } else if (arr[0].equalsIgnoreCase("DeleteFolder") && arr.length == 2) {

                if (!checkAccess(arr[1],1)) {
                    System.out.println("you do not have access to create file in this folder");
                    continue;
                }

                Directory d = root.checkPathOfDirectory(arr[1]);
                if (d == null) {
                    System.out.println("error");
                } else {
                    d.setDeleted(true);
                    deallocateOfDataInDirectory(d);
                    deleteDirectories(arr[1]);
                }

            } else if (arr[0].equalsIgnoreCase("DisplayDiskStatus") && arr.length == 1) {
                DisplayDiskStatus();
            } else if (arr[0].equalsIgnoreCase("DisplayDiskStructure") && arr.length == 1) {

                root.printDirectoryStructure("", false);

            } else if (arr[0].equalsIgnoreCase("Exit")) {
                method.write(numberOfBlocks, root);
                break;
            } else if (arr[0].equalsIgnoreCase("Login") && arr.length == 3) {
                User u = checkUser(arr[1]);
                if (u != null && u.getPassword().equalsIgnoreCase(arr[2])) {
                    user = u;
                } else {
                    System.out.println("error");
                }

            } else if (arr[0].equalsIgnoreCase("Grant") && arr.length == 4) {

                if (!user.getName().equalsIgnoreCase("admin")) {

                    System.out.println("your not allowed to enter this command");

                } else {

                    User u = checkUser(arr[1]);
                    Directory dir = root.checkPathOfDirectory(arr[2]);
                    if (u != null && dir != null) {
                        ArrayList<String> p = u.getPath();
                        for (int i = 0; i < p.size(); i++) {
                            if (p.get(i).equalsIgnoreCase(arr[2])) {
                                u.getCapabilities().set(i, Integer.parseInt(arr[3]));
                                break;
                            }
                        }
                    } else {
                        System.out.println("error");
                    }

                }

            } else if (arr[0].equalsIgnoreCase("CUser") && arr.length == 3) {
                if (!user.getName().equalsIgnoreCase("admin")) {

                    System.out.println("your not allowed to enter this command");

                } else {

                    User u = checkUser(arr[1]);
                    if (u == null) {
                        u = new User(arr[1], arr[2]);
                        users.add(u);
                        getAllPathsOfDirectories("root", u, root);
                    } else {
                        System.out.println("error");
                    }

                }
            } else if (arr[0].equalsIgnoreCase("TellUser") && arr.length == 1) {
                System.out.println(user.getName());
            } else {
                System.out.println("error");
            }

        }
    }

}
