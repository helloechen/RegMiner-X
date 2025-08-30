package Utility;

import Action.GitAdapter;
import Obj.ClassInfo;
import Obj.CommitMessage;
import Persistent.Serialization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class ProcedureUtility {

    public static List<CommitMessage> getAllCommits(File commits_file) {
        var list=new ArrayList<CommitMessage>();

        try {
            var reader=new BufferedReader(new FileReader(commits_file));
            String line;
            while((line=reader.readLine())!=null){
                CommitMessage commitMessage= Serialization.json2Bean(line, CommitMessage.class);
                list.add(commitMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String> getAllFiles(GitAdapter gitAdapter, List<CommitMessage> commitments, String projectName) throws IOException {
        File fileDirectory=new File("./AllFiles/");
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
        System.out.println(fileDirectory.getAbsoluteFile());
        File file =new File(fileDirectory.getPath()+File.separator+projectName+".csv");
        var uniqueList=new ArrayList<String>();
        var uniqueSet=new HashSet<String>();
        if(file.exists()){
            var reader=new BufferedReader(new FileReader(file));
            String line;
            while((line=reader.readLine())!=null){
                uniqueList.add(line);
            }
        }else{
            for(CommitMessage commitMessage:commitments){
                var list=gitAdapter.getJavaFilesCommit(commitMessage.getCommitId());
                for(var fileName:list){
                    uniqueSet.add(fileName);
                }
            }
            var writer=new BufferedWriter(new FileWriter(file));
            uniqueSet.forEach((file_name)-> {
                try {
                    uniqueList.add(file_name);
                    writer.write(file_name+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        }
        return uniqueList;
    }

    public static void WriteCommits(List<CommitMessage> commitments, File file) {
        try {
            if(!file.exists()){
                file.createNewFile();
            }else{
                return ;
            }

            BufferedWriter writer=new BufferedWriter(new FileWriter(file));

            for(CommitMessage commitmessage:commitments){
                writer.write(Serialization.ObjToJSON(commitmessage)+"\r\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteClassInfo(File file,ClassInfo testClass, ArrayList<ClassInfo> production_list) throws IOException {
        if(file.exists()){
            file =new File((file.getPath().substring(0,file.getPath().indexOf(".json"))+"_"+(new Random().nextInt(100000)+".json")));
        }
        BufferedWriter writer=new BufferedWriter(new FileWriter(file,false));
        writer.write(Serialization.ObjToJSON(testClass)+"\r\n");
        if(production_list!=null){
            for(ClassInfo info:production_list){
                writer.write(Serialization.ObjToJSON(info)+"\r\n");
            }
        }
        writer.close();
    }
}
