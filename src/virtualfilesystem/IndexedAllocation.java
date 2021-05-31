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
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndexedAllocation implements Allocation {

    @Override
    public void write(int numberOfBlocks, Directory root) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File("DiskStructure.txt"));
            String d = String.valueOf(numberOfBlocks) + "\n";
            d += "i\n";
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
                i++;
                String[] temp2 = data.get(i).split(" ");
                int size = temp2.length;
                int[] arr = new int[size];
                for (int a = 0; a < size; a++) {
                    arr[a] = Integer.parseInt(temp2[a]);
                    stateOfBlocks.set(Integer.parseInt(temp2[a]), 1);
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

        int[] arr = new int[size + 1];
        int temp = 0;
        for (int i = 0; i < stateOfBlocks.size(); i++) {
            if (stateOfBlocks.get(i) == 0) {
                arr[temp] = i;
                temp++;
            }
            if (temp == size + 1) {
                return arr;
            }
        }
        return null;
    }

    @Override
    public String getAllPaths(String p, String paths, Directory d) {
        ArrayList<FileVS> files = d.getFiles();
        ArrayList<Directory> subDirectories = d.getSubDirectories();
        String temp;

        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).isDeleted()) {
                paths += p + "/" + files.get(i).getFileName() + " " + files.get(i).getAllocatedBlocks()[0] + "\n";
                int[] temp2 = files.get(i).getAllocatedBlocks();
                String x = "";
                for (int j = 0; j < temp2.length; j++) {
                    x += " " + String.valueOf(temp2[j]);
                }
                paths += x.trim() + "\n";
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
