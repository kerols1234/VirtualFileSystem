/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualfilesystem;

import java.util.ArrayList;

/**
 *
 * @author kerols
 */
public interface Allocation {

    public void write(int numberOfBlocks, Directory root);

    public void read(ArrayList<String> data, ArrayList<Integer> stateOfBlocks, Directory root);

    public int[] allocateBlocks(int size, int numberOfBlocks, ArrayList<Integer> stateOfBlocks);

    public String getAllPaths(String p, String paths, Directory d);

}
