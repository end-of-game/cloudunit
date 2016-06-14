package fr.treeptik.cloudunit.dto;

/**
 * Created by stagiaire on 10/06/2016.
 */
public class ScriptRequestBody {

    private String scriptContent;

    public String getFileContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    @Override
    public String toString() {
        return "FileRequestBody{" +
                "scriptContent='" + scriptContent + '\'' +
                '}';
    }

}
