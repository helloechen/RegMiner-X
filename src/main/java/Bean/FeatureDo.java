package Bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FeatureDo {
    String prod_path;
    String test_path;
    String prod_time;
    String test_time;
    String prod_commitID;
    String test_commitID;
    String project;
    String isfound;

    public String getProd_path() {
        return prod_path;
    }

    public void setProd_path(String prod_path) {
        this.prod_path = prod_path;
    }

    public String getTest_path() {
        return test_path;
    }

    public void setTest_path(String test_path) {
        this.test_path = test_path;
    }

    public String getProd_time() {
        return prod_time;
    }

    public void setProd_time(String prod_time) {
        this.prod_time = prod_time;
    }

    public String getTest_time() {
        return test_time;
    }

    public void setTest_time(String test_time) {
        this.test_time = test_time;
    }

    public String getPro_commitID() {
        return prod_commitID;
    }

    public void setPro_commitID(String pro_commitID) {
        this.prod_commitID = pro_commitID;
    }

    public String getTest_commitID() {
        return test_commitID;
    }

    public void setTest_commitID(String test_commitID) {
        this.test_commitID = test_commitID;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getIsfound() {
        return isfound;
    }

    public void setIsfound(String isfound) {
        this.isfound = isfound;
    }

    @Override
    public String toString() {
        return "FeatureDo{" +
                "projectName='" + project + '\'' +
                ", prod_path='" + prod_path + '\'' +
                ", test_path='" + test_path + '\'' +
                ", prod_time='" + prod_time + '\'' +
                ", test_time='" + test_time + '\'' +
                ", prod_CommitID='" + prod_commitID + '\'' +
                ", test_CommitID='" + test_commitID + '\'' +
                '}';
    }
}