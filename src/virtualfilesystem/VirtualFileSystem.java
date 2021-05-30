package virtualfilesystem;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualFileSystem {

    static int numberOfBlocks = 0;
    static int sizeOfBlock = 1;
    static Directory root = new Directory();
    static ArrayList<Integer> stateOfBlocks = new ArrayList<>();

    public static void main(String[] args) {
        try {
            File myObj = new File("DiskStructure.txt");
            Scanner myReader = new Scanner(myObj);
            Scanner scanner = new Scanner(System.in);
            ArrayList<String> data = new ArrayList<>();
            root.setDirectoryName("root");

            String d = myReader.nextLine();

            if (d.equalsIgnoreCase("new")) {

                System.out.println("Enter number of blocks: ");
                numberOfBlocks = scanner.nextInt();

            } else {

                numberOfBlocks = Integer.parseInt(d.split(" ")[0]);

                while (myReader.hasNextLine()) {
                    data.add(myReader.nextLine());
                }

            }

            for (int i = 0; i < numberOfBlocks; i++) {
                stateOfBlocks.add(0);
            }

            PrintWriter writer = new PrintWriter(myObj);
            writer.print("");
            writer.close();
            myReader.close();
            contiguousAllocationRead(data);
            comands();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }

    public static void contiguousAllocationRead(ArrayList<String> data) {

        for (int i = 0; i < data.size(); i++) {
            String[] temp = data.get(i).split(" ");
            if (temp[0].contains(".")) {
                int size = Integer.parseInt(temp[2]);
                int[] arr = new int[size];
                for (int j = Integer.parseInt(temp[1]), a = 0; a < size; j++, a++) {
                    arr[a] = j;
                    stateOfBlocks.set(j, 1);
                }
                root.insertFile(temp[0], arr);
            } else {
                root.insertSubDirectories(temp[0]);
            }
        }
    }

    public static void contiguousAllocationWrite() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File("DiskStructure.txt"));
            String d = String.valueOf(numberOfBlocks) + "\n";
            writer.print(getAllPathsOfSystem("root", d, root));
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VirtualFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getAllPathsOfSystem(String p, String paths, Directory d) {
        ArrayList<FileVS> files = d.getFiles();
        ArrayList<Directory> subDirectories = d.getSubDirectories();
        String temp;

        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).isDeleted()) {
                paths += p + "/" + files.get(i).getFileName() + " " + files.get(i).getAllocatedBlocks()[0] + " " + files.get(i).getAllocatedBlocks().length + "\n";
            }
        }

        for (int i = 0; i < subDirectories.size(); i++) {
            if (!subDirectories.get(i).isDeleted()) {
                temp = p + "/" + subDirectories.get(i).getDirectoryName();
                paths += temp + "\n";
                paths = getAllPathsOfSystem(temp, paths, subDirectories.get(i));
            }
        }

        return paths;
    }

    public static int[] contiguousAllocationToBlocks(int size) {
        int[] arr = new int[size];
        int temp = 0, start = -1;
        TreeMap<Integer, Integer> max = new TreeMap<>();

        max.put(0, -1);

        for (int i = 0; i < numberOfBlocks; i++) {
            if (stateOfBlocks.get(i) == 0) {
                temp++;
                if (start == -1) {
                    start = i;
                }
            } else if (i != 0 && stateOfBlocks.get(i - 1) == 0 && !max.containsKey(temp)) {
                max.put(temp, start);
                temp = 0;
                start = -1;
            }
        }

        if (stateOfBlocks.get(numberOfBlocks - 1) == 0 && !max.containsKey(temp)) {
            max.put(temp, start);
        }

        if (max.lastEntry().getKey() < size) {
            return null;
        }

        for (int i = max.lastEntry().getValue(), j = 0; j < size; i++, j++) {
            arr[j] = i;
        }

        return arr;
    }

    public static void indexedAllocationRead(ArrayList<String> data) {

        for (int i = 0; i < data.size(); i++) {
            String[] temp = data.get(i).split(" ");
            if (temp[0].contains(".")) {
                i++;
                String[] temp2 = data.get(i).split(" ");
                int size = temp2.length;
                int[] arr = new int[size];
                for (int a = 0; a < size;a++) {
                    arr[a] = Integer.parseInt(temp2[a]);
                    stateOfBlocks.set(Integer.parseInt(temp2[a]), 1);
                }
                root.insertFile(temp[0], arr);
            } else {
                root.insertSubDirectories(temp[0]);
            }
        }
    }
 
    public static void indexedAllocationWrite() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File("DiskStructure.txt"));
            String d = String.valueOf(numberOfBlocks) + "\n";
            writer.print(indexedGetAllPathsOfSystem("root", d, root));
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VirtualFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String indexedGetAllPathsOfSystem(String p, String paths, Directory d) {
        ArrayList<FileVS> files = d.getFiles();
        ArrayList<Directory> subDirectories = d.getSubDirectories();
        String temp;

        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).isDeleted()) {
                paths += p + "/" + files.get(i).getFileName() + " " + files.get(i).getAllocatedBlocks()[0] + "\n";
                int [] temp2 = files.get(i).getAllocatedBlocks();
                String x = "";
                for(int j = 0; j < temp2.length; j++){
                   x += " " + String.valueOf(temp2[j]);
                }
                paths += x + "\n";
            }
        }

        for (int i = 0; i < subDirectories.size(); i++) {
            if (!subDirectories.get(i).isDeleted()) {
                temp = p + "/" + subDirectories.get(i).getDirectoryName();
                paths += temp + "\n";
                paths = indexedGetAllPathsOfSystem(temp, paths, subDirectories.get(i));
            }
        }

        return paths;
    }

    public static int[] indexedAllocationToBlocks(int size) {
        int[] arr = new int[size];
        int temp = 0, start = -1;
        TreeMap<Integer, Integer> max = new TreeMap<>();

        max.put(0, -1);

        for (int i = 0; i < numberOfBlocks; i++) {
            if (stateOfBlocks.get(i) == 0) {
                temp++;
                if (start == -1) {
                    start = i;
                }
            } else if (i != 0 && stateOfBlocks.get(i - 1) == 0 && !max.containsKey(temp)) {
                max.put(temp, start);
                temp = 0;
                start = -1;
            }
        }

        if (stateOfBlocks.get(numberOfBlocks - 1) == 0 && !max.containsKey(temp)) {
            max.put(temp, start);
        }

        if (max.lastEntry().getKey() < size) {
            return null;
        }

        for (int i = max.lastEntry().getValue(), j = 0; j < size; i++, j++) {
            arr[j] = i;
        }

        return arr;
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
        System.out.println("Free space:" + size);
        System.out.println("Allocated space: " + (numberOfBlocks - size));
    }

    public static void comands() {

        String comand;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("----------------------------------------------------------");
            comand = scanner.nextLine();
            String[] arr = comand.split(" ");

            if (arr[0].equalsIgnoreCase("CreateFile") && arr.length == 3) {

                int size = Integer.parseInt(arr[2]);
                int[] alloc = contiguousAllocationToBlocks(size);

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

                root.printDirectoryStructure("");

            } else if (arr[0].equalsIgnoreCase("Exit")) {
                contiguousAllocationWrite();
                break;
            } else {
                System.out.println("error");
            }

        }
    }

}
