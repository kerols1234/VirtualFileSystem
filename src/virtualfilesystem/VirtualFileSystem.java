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

                    if (m.equalsIgnoreCase("Contiguous Allocation")) {
                        m = "c";
                        break;
                    } else if (m.equalsIgnoreCase("Linked Allocation")) {
                        m = "l";
                        break;
                    } else if (m.equalsIgnoreCase("Indexed Allocation")) {
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
            comands(method);

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
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

                if (alloc == null || !root.insertFile(arr[1], alloc)) {
                    System.out.println("error");
                    continue;
                }

                deallocateOrAllocateOfBlocks(alloc, 1);

            } else if (arr[0].equalsIgnoreCase("CreateFolder") && arr.length == 2) {

                if (!root.insertSubDirectories(arr[1])) {
                    System.out.println("error");
                }

            } else if (arr[0].equalsIgnoreCase("DeleteFile") && arr.length == 2) {

                FileVS f = root.checkPathOfFile(arr[1]);
                if (f == null) {
                    System.out.println("error");
                } else {
                    f.setDeleted(true);
                    deallocateOrAllocateOfBlocks(f.getAllocatedBlocks(), 0);
                }

            } else if (arr[0].equalsIgnoreCase("DeleteFolder") && arr.length == 2) {

                Directory d = root.checkPathOfDirectory(arr[1]);
                if (d == null) {
                    System.out.println("error");
                } else {
                    d.setDeleted(true);
                    deallocateOfDataInDirectory(d);
                }

            } else if (arr[0].equalsIgnoreCase("DisplayDiskStatus") && arr.length == 1) {
                DisplayDiskStatus();
            } else if (arr[0].equalsIgnoreCase("DisplayDiskStructure") && arr.length == 1) {

                root.printDirectoryStructure("",false);

            } else if (arr[0].equalsIgnoreCase("Exit")) {
                method.write(numberOfBlocks, root);
                break;
            } else {
                System.out.println("error");
            }

        }
    }

}
