package Utility;

import Action.GitAdapter;
import Resource.Resource;

import java.util.ResourceBundle;


public class gitInstance {
    private static GitAdapter adapter = null;

    private static GitAdapter ReadInformation(String resourceName) {
        ResourceBundle res = ResourceBundle.getBundle(resourceName);
        String remotePath = res.getString("RemoteGit");
        String localPath = res.getString("LocalGit");
        String branchName = res.getString("branchName");
        String projectName = res.getString("projectName");
        String filePath = localPath + "/" + projectName ;
        return adapter = new GitAdapter(remotePath, filePath, branchName);
    }

    public static GitAdapter get(String resourceName) {
        return adapter = ReadInformation(resourceName);
    }

    public static GitAdapter get(String projectName, String branchName) {
        return adapter = ReadInformation(projectName, branchName);
    }

    private static GitAdapter ReadInformation(String projectName, String branchName1) {
        var remotePath = Resource.remotePath;
        var localPath = Resource.gitrepository;
        var branchName = Resource.branchName;
        String filePath = localPath + "/" + projectName  ;
        return adapter = new GitAdapter(remotePath, filePath, branchName);
    }
}
