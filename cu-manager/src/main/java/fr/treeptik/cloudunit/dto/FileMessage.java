package fr.treeptik.cloudunit.dto;

/**
 * Created by nicolas on 10/06/2016.
 */
public class FileMessage {
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

    private String fileContent;

    private String fileName;

    private String filePath;


    public String getFileContent() {
        return fileContent;
    }

    @Override
    public String toString() {
        return "FileMessage{" +
                "fileContent='" + fileContent + '\'' +
                '}';
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
