package fr.treeptik.cloudunit.dto;

/**
 * Created by nicolas on 10/06/2016.
 */
public class FileRequestBody {
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
        return "FileRequestBody{" +
                "fileContent='" + fileContent + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }


    private String convertPathFromUI(String path) {
        if (path != null) {
            path = path.replaceAll("____", "/");
            path = path.replaceAll("__", "/");
        }
        return path;
    }

}
