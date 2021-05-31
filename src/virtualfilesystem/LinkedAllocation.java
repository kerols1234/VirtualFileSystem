/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualfilesystem;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerols
 */
public class LinkedAllocation implements Allocation {

    @Override
    public void read(ArrayList<String> data, ArrayList<Integer> stateOfBlocks, Directory root) {

        for (int i = 0; i < data.size(); i++) {
            String[] temp = data.get(i).split(" ");
            if (temp[0].contains(".")) {
                i++;
                ArrayList<Integer> blocks = new ArrayList<>();
                while (true) {

                    String[] line = data.get(i).split(" ");
                    blocks.add(Integer.parseInt(line[0]));
                    if (line[0].equalsIgnoreCase(temp[2])) {
                        break;
                    }
                    i++;
                }

                int size = blocks.size();
                int[] arr = new int[size];
                for (int a = 0; a < size; a++) {
                    arr[a] = blocks.get(a);
                    stateOfBlocks.set(arr[a], 1);
                }
                
                root.insertFile(temp[0], arr);
            } else {
                root.insertSubDirectories(temp[0]);
            }
        }
    }

    @Override
    public void write(int numberOfBlocks, Directory root) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File("DiskStructure.txt"));
            String d = String.valueOf(numberOfBlocks) + "\n";
            d += "l\n";
            writer.print(getAllPaths("root", d, root));
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VirtualFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getAllPaths(String p, String paths, Directory d) {
        ArrayList<FileVS> files = d.getFiles();
        ArrayList<Directory> subDirectories = d.getSubDirectories();
        String temp;

        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).isDeleted()) {
                paths += p + "/" + files.get(i).getFileName() + " " + files.get(i).getAllocatedBlocks()[0] + " " + files.get(i).getAllocatedBlocks()[files.get(i).getAllocatedBlocks().length - 1] + "\n";
                int[] temp2 = files.get(i).getAllocatedBlocks();
                for (int j = 1; j < temp2.length; j++) {
                    paths += String.valueOf(temp2[j - 1]) + " " + String.valueOf(temp2[j]) + "\n";
                }
                paths += String.valueOf(temp2[temp2.length - 1]) + " nil\n";
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

    @Override
    public int[] allocateBlocks(int size, int numberOfBlocks, ArrayList<Integer> stateOfBlocks) {

        if (size == 0) {
            return null;
        }

        int[] arr = new int[size];
        int j = 0;
        for (int i = 0; i < numberOfBlocks && j < size; i++) {
            if (stateOfBlocks.get(i) == 0) {
                arr[j] = i;
                j++;
            }
        }

        if (j != size) {
            return null;
        }

        return arr;
    }

}
