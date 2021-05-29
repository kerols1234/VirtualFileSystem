package virtualfilesystem;

public class FileVS {
    private String filePath;
    private String fileName;
    private int[] allocatedBlocks;
    private boolean deleted = false;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int[] getAllocatedBlocks() {
        return allocatedBlocks;
    }

    public void setAllocatedBlocks(int[] allocatedBlocks) {
        this.allocatedBlocks = allocatedBlocks;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
