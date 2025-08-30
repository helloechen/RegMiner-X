package Utility;

import Action.GitAdapter;
import Date.DateAction;
import Obj.CommitMessage;
import Resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MatchUtility {
    public static List<CommitMessage> getCommits_range(int index,CommitMessage[] all){

        CommitMessage nowCommit=all[index];  
        List<CommitMessage> result=new ArrayList<>();
        for(int i=index;i<all.length;i++){
            if(isInRange(nowCommit,all[i])){
                result.add(all[i]);
            }
        }
        return result;
    }

    private static boolean isInRange(CommitMessage now, CommitMessage previous){
        ResourceBundle resourceBundle=ResourceBundle.getBundle("parameter");
        String PositiveTime=resourceBundle.getString("PositiveTime");
        return DateAction.get_diff(now.getCommitDate(), previous.getCommitDate()) <= Double.parseDouble(PositiveTime);

    }


    public static List<RevCommit> getCommits_range(GitAdapter adapter,CommitMessage index){
        try {
            ResourceBundle resourceBundle=ResourceBundle.getBundle("parameter");
            String PositiveTime=resourceBundle.getString("PositiveTime").trim();
            var endDate= new Date(DateAction.ConvertTDate(index.getCommitDate()).getTime()-Long.parseLong(PositiveTime)*1000);
            var files=adapter.getRevLog(index.getCommitId(), endDate);
            return files;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

  
    public static List<String> getFilesName(GitAdapter adapter, List<CommitMessage> commitMessages) {
        List<String> files_name = null;
        try {
            files_name = ProcedureUtility.getAllFiles(adapter, commitMessages, adapter.getProjectName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files_name;
    }

    public static List<CommitMessage>  getCommitMessages(GitAdapter adapter) {
        var commitMessages=new ArrayList<CommitMessage>();
        File commitDirectory=new File(Resource.commitInfo);

        if(!commitDirectory.exists()){
            commitDirectory.mkdir();
        }

        File Commits_File = new File(commitDirectory.getPath()+File.separator+ adapter.getProjectName() + ".csv");
        System.out.println(Commits_File.getPath());

        if (Commits_File.exists()) {
            commitMessages = (ArrayList<CommitMessage>) ProcedureUtility.getAllCommits(Commits_File);
        } else {
            try {
                commitMessages = (ArrayList<CommitMessage>) adapter.getNo_MergeCommitMessages();
                ProcedureUtility.WriteCommits(commitMessages, Commits_File);
            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
            }
        }
        return commitMessages;
    }
}
