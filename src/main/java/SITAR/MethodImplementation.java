package SITAR;

import Action.GitAdapter;
import Bean.FeatureDo;
import Obj.CommitMessage;
import Persistent.Serialization;
import Regrex.RegrexDefinations;
import Utility.gitInstance;
import Resource.Resource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.io.PrintStream;

import java.io.*;
import java.util.*;

import static Utility.MatchUtility.getCommitMessages;

public class MethodImplementation {
    public static void main(String[] args) {
        String ProjectName = Resource.projectName;
        File filelog = new File(ProjectName + "-1.log");
        //重定向日志输出
        try {
            PrintStream fileOut = new PrintStream(filelog);
            System.setOut(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建输出路径
        File FeatureDir = new File(Resource.outputDir);
        File dir = new File(FeatureDir + File.separator + ProjectName + File.separator);
        System.out.println(ProjectName);

        if (dir.exists()) {
            System.out.println("please ensure that ./Feature directory does not has this project name!");
            System.exit(1);
        }

        try {
            processCategory(ProjectName);
//            dir=new File(FeatureDir+File.separator+ProjectName+File.separator);
//
//            if(!dir.exists()){
//                FileUtils.forceMkdir(dir);
//            }
//
//            for(int i=0;i<result.size();i++){
//                try(BufferedWriter writer=new BufferedWriter(
//                        new FileWriter(dir.getPath()+File.separator+(i+1)+".json")
//                )){
//                    writer.write(Serialization.ObjToJSON(result.get(i)));
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processCategory(String ProjectName) throws IOException {
        String reposity = ProjectName;
        File FeatureDir = new File(Resource.outputDir);
        File dir=new File(FeatureDir+File.separator+ProjectName+File.separator);
        int countNumber = 0;

        if(!dir.exists()){
            FileUtils.forceMkdir(dir);
        }

        GitAdapter adapter = gitInstance.get(reposity, "main");

        System.out.println("start processing this project");
        System.out.println(reposity);

        adapter.initGit();
        List<CommitMessage> commitMessages = getCommitMessages(adapter);
//        var result = new ArrayList<FeatureDo>();
        Map<Integer, List<String>> changedFilesMap = new HashMap<>();
        Map<Integer, List<String>> fileListAfterCommit = new HashMap<>();
        HashSet<String> allFileName = new HashSet<>();

        int length = commitMessages.size();
        int exceptionNum = 0;
        int not_found = 0;
        int found = 0;
        int not_exist = 0;
        int countNum = 0;
        int hasNoTest_notFoundTestChange = 0;
        int hasTest_notFoundTestChange = 0;

        for (int i = length - 2; i >=0 ; i--) {
//        for (int i = length - 2; i >=length/2 ; i--) {


            try {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                var CommitMessage = commitMessages.get(i);
                System.out.println("finding file_change in this commit " + i );
                var CommitMessageID = CommitMessage.getCommitId();
                var ParentmessageID = commitMessages.get(i).getLastCommitId();
                System.out.println("the commitID is " + CommitMessageID);
                List<String> changedFiles = adapter.findChangedFile(CommitMessageID, ParentmessageID);
                System.out.println("in commit " + i + " Changedfile as follows");
                for (String changedFile : changedFiles) {
                    allFileName.add(changedFile);
                    System.out.println(changedFile);
                }
                changedFilesMap.put(i, changedFiles);
            }catch (Exception e){
                System.out.println("Exception found!");
            }
        }
        System.out.println("SIZE");
        System.out.println(allFileName.size());

        var firstCommitID = commitMessages.get(length-1).getCommitId();
        List<String> firstChangedFiles = adapter.findChangedFile(firstCommitID, null);
        for (String changedFile : firstChangedFiles) {
            allFileName.add(changedFile);
            System.out.println(changedFile);
        }


        File filelog = new File(ProjectName + "-2.log");
        try {
            PrintStream fileOut = new PrintStream(filelog);
            System.setOut(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = length - 1; i >= 0; i--) {
//        for (int i = length- 1; i >= length/2; i--) {

            try {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                var CommitMessage = commitMessages.get(i);
                System.out.println("parsing fileList structure after this commit " + i );
                var CommitMessageID = CommitMessage.getCommitId();
                System.out.println("the commitID is " + CommitMessageID);
                List<String> structure = adapter.getAllFilePathsAfterCommit(CommitMessageID);
                System.out.println("repository structure after this commit as follows");
                fileListAfterCommit.put(i, structure);
                for (String structureFile : structure) {
                    System.out.println(structureFile);
                }
            }catch (Exception e){
                System.out.println("Exception found!");
            }
        }

        File filelog3 = new File(ProjectName + "-3.log");

        try {
            PrintStream fileOut = new PrintStream(filelog3);
            System.setOut(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long timeLast = adapter.getTime2(commitMessages.get(0).getCommitId());
        for (int i = length - 2; i >= 0 ; i--) {
//        for (int i = length- 2; i >= length/2 ; i--) {

            try{
                var CommitMessage = commitMessages.get(i);
                var CommitMessageID = CommitMessage.getCommitId();
                var time1 = adapter.getTime2(commitMessages.get(i).getCommitId());

                if(timeLast - time1 < 1000*60*60*24*30*3){
                    continue;
                }

                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++" );
                System.out.println("digging pro_change in this commit " + i);
                System.out.println("the commitID is " + CommitMessageID);
                List<String> changedFiles = changedFilesMap.get(i);

                for (String changedFile : changedFiles) {
                    if (!RegrexDefinations.isJava(changedFile)) {
                        continue;
                    }
                    if (changedFile.contains("main")) {
                        System.out.println("found Pro_change for " + changedFile+" in "+ i);
                        int lastSlashIndex = changedFile.lastIndexOf("/");
                        String beforeLastSlash = changedFile.substring(0, lastSlashIndex);
                        String afterLastSlash = changedFile.substring(lastSlashIndex+1);
                        int secondLastSlashIndex = beforeLastSlash.lastIndexOf("/");
                        String tmp1 = beforeLastSlash.substring(secondLastSlashIndex+1);
                        String pureClassName = changedFile.substring(secondLastSlashIndex + 1);
                        String ClassName = "/"+pureClassName;
                        if (pureClassName.endsWith(".java")) {
                            pureClassName = pureClassName.substring(0, pureClassName.length() - 5); // ".java" 的长度是 5
                        }
                        String pureTestName = "/"+pureClassName + "Test.java";
                        String pureTestName2 ="/"+tmp1+"/" +"Test" + afterLastSlash;
                        String pureTestName3 ="/"+tmp1+"/" + "test" + afterLastSlash;
                        String pureTestName4 ="/"+pureClassName + "test.java";
//                    if (changedFile.contains("src")) {
//                        System.out.println("found Pro_change for " + changedFile+" in "+ i);
//                        int lastSlashIndex = changedFile.lastIndexOf("/");
//                        String beforeLastSlash = changedFile.substring(0, lastSlashIndex);
//                        String afterLastSlash = changedFile.substring(lastSlashIndex+1);
//                        int secondLastSlashIndex = beforeLastSlash.lastIndexOf("/");
//                        String tmp1 = beforeLastSlash.substring(secondLastSlashIndex+1);
//                        String pureClassName = changedFile.substring(secondLastSlashIndex + 1);
//                        String ClassName = "/"+pureClassName;
//                        if (pureClassName.endsWith(".java")) {
//                            pureClassName = pureClassName.substring(0, pureClassName.length() - 5); // ".java" 的长度是 5
//                        }
//                        String pureTestName = "/"+pureClassName + "Test.java";
//                        String pureTestName2 ="/"+tmp1+"/" +"Test" + afterLastSlash;
//                        String pureTestName3 ="/"+tmp1+"/" + "test" + afterLastSlash;
//                        String pureTestName4 ="/"+pureClassName + "test.java";

                        if(containsString(allFileName,pureTestName) || containsString(allFileName,pureTestName2 ) || containsString(allFileName,pureTestName3) || containsString(allFileName,pureTestName4)){
                            Boolean flag = false;
                            HashMap<Integer,String> tmp = new HashMap<Integer, String>();
                            for (int j = i; j >= 0; j--) {
                                List<String> changedFiles2 = changedFilesMap.get(j);
                                for (String changedFile2 : changedFiles2) {
                                     if (changedFile2.contains(pureTestName) || changedFile2.contains(pureTestName2) ||
                                            changedFile2.contains(pureTestName3) || changedFile2.contains(pureTestName4)) {
                                        tmp.put(j, changedFile2);
                                    }

                                    if(changedFile2.contains(ClassName) && j != i){
                                        if (tmp.keySet().size() == 1 || tmp.keySet().size() == 2){
                                            Integer maxKey = Collections.max(tmp.keySet());
                                            if (maxKey != j){
                                                found++;
                                                FeatureDo featureDo = new FeatureDo();
                                                featureDo.setProject(ProjectName);
                                                var proTime = adapter.getTime(commitMessages.get(i).getCommitId());
                                                featureDo.setPro_commitID(CommitMessageID);
                                                featureDo.setProd_path(changedFile);
                                                featureDo.setProd_time(proTime);
                                                var testTime = adapter.getTime(commitMessages.get(maxKey).getCommitId());
                                                featureDo.setTest_commitID(commitMessages.get(maxKey).getCommitId());
                                                featureDo.setTest_time(testTime);
                                                featureDo.setTest_path(tmp.get(maxKey));
                                                featureDo.setIsfound("found test change");
//                                                result.add(featureDo);
                                                try(BufferedWriter writer=new BufferedWriter(
                                                        new FileWriter(dir.getPath()+File.separator+(countNumber+1)+".json")
                                                )){
                                                    countNumber++;
                                                    writer.write(Serialization.ObjToJSON(featureDo));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                System.out.println(featureDo);
                                            }
                                        }
//                                        for(int num : tmp.keySet()){
//                                            if(num != j){
//                                                found++;
//                                                FeatureDo featureDo = new FeatureDo();
//                                                featureDo.setProject(ProjectName);
//                                                var proTime = adapter.getTime(commitMessages.get(i).getCommitId());
//                                                featureDo.setPro_commitID(CommitMessageID);
//                                                featureDo.setProd_path(changedFile);
//                                                featureDo.setProd_time(proTime);
//                                                var testTime = adapter.getTime(commitMessages.get(num).getCommitId());
//                                                featureDo.setTest_commitID(commitMessages.get(num).getCommitId());
//                                                featureDo.setTest_time(testTime);
//                                                featureDo.setTest_path(tmp.get(num));
//                                                featureDo.setIsfound("found test change");
////                                                result.add(featureDo);
//                                                try(BufferedWriter writer=new BufferedWriter(
//                                                        new FileWriter(dir.getPath()+File.separator+(countNumber+1)+".json")
//                                                )){
//                                                    countNumber++;
//                                                    writer.write(Serialization.ObjToJSON(featureDo));
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                }
//                                                System.out.println(featureDo);
//                                            }
//                                        }
                                        flag = true;
                                        System.out.println("the next change for the same production code is in commit " + j+" "+commitMessages.get(j).getCommitId()+" "+changedFile2  );

                                        if(tmp.keySet().isEmpty()){
                                            List<String> repoStructure = fileListAfterCommit.get(i + 1);
                                            boolean hasTest = false;
                                            for(String file :repoStructure){
                                                if(file.contains(pureTestName) || file.contains(pureTestName2) ||
                                                        file.contains(pureTestName3) || file.contains(pureTestName4)){
                                                    hasTest = true;
                                                    hasTest_notFoundTestChange++;
                                                    FeatureDo featureDo = new FeatureDo();
                                                    featureDo.setProject(ProjectName);
                                                    var proTime = adapter.getTime(commitMessages.get(i).getCommitId());
                                                    featureDo.setPro_commitID(CommitMessageID);
                                                    featureDo.setProd_path(changedFile);
                                                    featureDo.setProd_time(proTime);
                                                    featureDo.setIsfound("not found test change");
                                                    featureDo.setTest_commitID("");
                                                    featureDo.setTest_path(file);
                                                    featureDo.setTest_time("");
//                                                    result.add(featureDo);
                                                    try(BufferedWriter writer=new BufferedWriter(
                                                            new FileWriter(dir.getPath()+File.separator+(countNumber+1)+".json")
                                                    )){
                                                        countNumber++;
                                                        writer.write(Serialization.ObjToJSON(featureDo));
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                    System.out.println("*"+featureDo);
                                                }
                                            }

                                            if(!hasTest){
                                                hasNoTest_notFoundTestChange ++;
                                            }
                                        }
                                        System.out.println();
                                    }
                                }
                                if (flag) {
                                    break;
                                }
                            }
                            if (!flag) {
                                System.out.println("the next change for the same production code not exist, it is the last change");
//                                for(int num : tmp.keySet()){
//                                    found++;
//                                    FeatureDo featureDo = new FeatureDo();
//                                    featureDo.setProject(ProjectName);
//                                    var proTime = adapter.getTime(commitMessages.get(i).getCommitId());
//                                    featureDo.setPro_commitID(CommitMessageID);
//                                    featureDo.setProd_path(changedFile);
//                                    featureDo.setProd_time(proTime);
//                                    var testTime = adapter.getTime(commitMessages.get(num).getCommitId());
//                                    featureDo.setTest_commitID(commitMessages.get(num).getCommitId());
//                                    featureDo.setTest_time(testTime);
//                                    featureDo.setTest_path(tmp.get(num));
//                                    featureDo.setIsfound("found test change");
////                                    result.add(featureDo);
//                                    try(BufferedWriter writer=new BufferedWriter(
//                                            new FileWriter(dir.getPath()+File.separator+(countNumber+1)+".json")
//                                    )){
//                                        countNumber++;
//                                        writer.write(Serialization.ObjToJSON(featureDo));
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                    }
//                                    System.out.println(featureDo);
//                                }
//                                System.out.println("the next change for the same production code not exist, it is the last change");
                            }
                        }else {
                            System.out.println("this production code have no testcase at all!" + "\n");
                            not_exist++;
                        }
                    }
                }
            }catch (Exception e){
                System.out.println("Exception found!");
                exceptionNum++;
            }
        }
        System.out.println(found);
        System.out.println(hasTest_notFoundTestChange);
        System.out.println(hasNoTest_notFoundTestChange);
        return;
    }

    public static boolean containsString(Set<String> stringSet, String target) {
        for (String str : stringSet) {
            if (str.contains(target)) {
                return true;
            }
        }
        return false;
    }
}




