package virtualfilesystem;

import java.util.ArrayList;

public class Directory {

    private String directoryName;
    private ArrayList<FileVS> files = new ArrayList<>();
    private ArrayList<Directory> subDirectories = new ArrayList<>();
    private boolean deleted = false;

    public ArrayList<FileVS> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FileVS> files) {
        this.files = files;
    }

    public ArrayList<Directory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(ArrayList<Directory> subDirectories) {
        this.subDirectories = subDirectories;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public Boolean addSubDirectory(Directory d) {
        for (int i = 0; i < subDirectories.size(); i++) {
            if (d.getDirectoryName().equalsIgnoreCase(subDirectories.get(i).getDirectoryName()) && !subDirectories.get(i).isDeleted()) {
                return false;
            }
        }
        subDirectories.add(d);
        return true;
    }

    public Boolean addFile(FileVS f) {
        for (int i = 0; i < files.size(); i++) {
            if (f.getFileName().equalsIgnoreCase(files.get(i).getFileName()) && !files.get(i).isDeleted()) {
                return false;
            }
        }
        files.add(f);
        return true;
    }

    public Boolean insertSubDirectories(String path) {
        int index = path.lastIndexOf("/");
        String directoryN = path.substring(index + 1);
        String p = path.substring(0, index);
        Directory d = checkPathOfDirectory(p);
        if (d == null) {
            return false;
        }

        Directory newDirectory = new Directory();
        newDirectory.setDirectoryName(directoryN);

        return d.addSubDirectory(newDirectory);
    }

    public Boolean insertFile(String path, int[] allocatedBlocks) {
        int index = path.lastIndexOf("/");
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String p = path.substring(0, index);
        Directory d = checkPathOfDirectory(p);
        if (d == null) {
            return false;
        }
        FileVS newFile = new FileVS();
        newFile.setFileName(fileName);
        newFile.setAllocatedBlocks(allocatedBlocks);

        return d.addFile(newFile);
    }

    public Directory checkPathOfDirectory(String dirName) {
        String[] data = dirName.split("/", 2);
        Directory d = null;
        if (data[0].equalsIgnoreCase(directoryName) && !isDeleted()) {
            if (data.length == 1) {
                return this;
            }

            for (int i = 0; i < subDirectories.size(); i++) {
                if (!subDirectories.get(i).isDeleted()) {
                    d = subDirectories.get(i).checkPathOfDirectory(data[1]);
                    if (d != null) {
                        break;
                    }
                }
            }
        }
        return d;
    }

    public FileVS checkPathOfFile(String dirName) {
        String[] data = dirName.split("/", 2);
        FileVS f = null;
        if (data[0].equalsIgnoreCase(directoryName) && !isDeleted()) {

            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).getFileName().equalsIgnoreCase(data[1]) && !files.get(i).isDeleted()) {
                    return files.get(i);

                }
            }

            for (int i = 0; i < subDirectories.size(); i++) {
                if (!subDirectories.get(i).isDeleted()) {
                    f = subDirectories.get(i).checkPathOfFile(data[1]);
                    if (f != null) {
                        break;
                    }
                }
            }
        }
        return f;
    }

    public void printDirectoryStructure(String space, Boolean state) {
        if (!deleted) {
            System.out.println(space + directoryName);
            space += "  ";
            for (int i = 0; i < files.size(); i++) {
                if (!files.get(i).isDeleted()) {
                    if (state) {
                        System.out.print(space + files.get(i).getFileName());
                        for (int j = 0; j < files.get(i).getAllocatedBlocks().length; j++) {
                            System.out.print(" " + files.get(i).getAllocatedBlocks()[j]);
                        }
                        System.out.println();
                    } else {
                        System.out.println(space + files.get(i).getFileName());
                    }

                }
            }
            for (int i = 0; i < subDirectories.size(); i++) {
                if (!subDirectories.get(i).isDeleted()) {
                    subDirectories.get(i).printDirectoryStructure(space, state);
                }
            }
        }
    }

}
