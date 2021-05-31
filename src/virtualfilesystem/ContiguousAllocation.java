/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualfilesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerols
 */
public class ContiguousAllocation implements Allocation {

    @Override
    public void write(int numberOfBlocks, Directory root) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File("DiskStructure.txt"));
            String d = String.valueOf(numberOfBlocks) + "\n";
            d += "c\n";
            writer.print(getAllPaths("root", d, root));
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VirtualFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void read(ArrayList<String> data, ArrayList<Integer> stateOfBlocks, Directory root) {
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

    @Override
    public int[] allocateBlocks(int size, int numberOfBlocks, ArrayList<Integer> stateOfBlocks) {
        if (size == 0) {
            return null;
        }

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

    @Override
    public String getAllPaths(String p, String paths, Directory d) {
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
                paths = getAllPaths(temp, paths, subDirectories.get(i));
            }
        }

        return paths;
    }

}
