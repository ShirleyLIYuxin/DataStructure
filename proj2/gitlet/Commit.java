package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /* the number of milliseconds since January 1, 1970, 00:00:00 GMT */
    private long timeStamp;
    /* mapping of file names to blob references */
    private HashMap<String,String> files;
    private HashMap<String,String> orginalFiles;

    /* file operation: ADD EDIT DELETE */
    private HashMap<String,String> operations;
    /* main parent */
    private String parentM;
    /* second parent (for merges) */
    private String parentS;
    /* code for the commit */
    private String id;

    /* TODO: fill in the rest of this class. */
    public Commit(){
        message = "initial commit";
        timeStamp = 0L;
        files = new HashMap<>();
        orginalFiles = new HashMap<>();
        operations = new HashMap<>();
        parentM = "";
        parentS = "";
        refreshId();
    }
    public Commit(Commit parent){
        message = "";
        timeStamp = System.currentTimeMillis();
        files = parent.getSnapshot();
        orginalFiles = parent.getSnapshot();
        operations = new HashMap<>();
        parentM = parent.getId();
        parentS = "";
        refreshId();
    }

    public boolean hasOperations(){
        return !operations.isEmpty();
    }
    public HashMap<String,String> getSnapshot(){
        HashMap<String,String> ret = new HashMap<>();
        if (files.size()>0) {
            for (Map.Entry<String,String> entry : files.entrySet()) {
                if (!operations.containsKey(entry.getKey()) || !operations.get(entry.getKey()).equals("DELETE")) {
                    ret.put(entry.getKey(),entry.getValue());
                }
            }
        }
        return ret;
    }

    public String getFileCodeName(String fileName){
        String ret = "";
        if (fileName.length() > 0 && files.containsKey(fileName)) {
            ret = files.get(fileName);
        }
        return ret;
    }

    public void addFile(String fileFullName, String codeName){
        if (files.containsKey(fileFullName)) {
            if (files.get(fileFullName).equals(codeName)) {
                if (operations.containsKey(fileFullName) && operations.get(fileFullName).equals("DELETE")) {
                    operations.remove(fileFullName);
                }
            } else {
                files.put(fileFullName,codeName);
                if (!operations.containsKey(fileFullName)) {
                    operations.put(fileFullName, "EDIT");
                }
            }
        } else {
            files.put(fileFullName,codeName);
            operations.put(fileFullName,"ADD");
        }
    }
    public void restoreFile(String fileFullName, String codeName){
        files.put(fileFullName,codeName);
        if(operations.containsKey(fileFullName)){
            operations.remove(fileFullName);
        }
    }
    public void removeFile(String fileFullName){
        if (files.containsKey(fileFullName)) {
            if (operations.containsKey(fileFullName)) {
                if (operations.get(fileFullName).equals("ADD")) {
                    files.remove(fileFullName);
                    operations.remove(fileFullName);
                } else {
                    files.put(fileFullName,getPreVersion(fileFullName));
                    operations.put(fileFullName,"DELETE");
                }
            }else{
                operations.put(fileFullName,"DELETE");
            }
        }
    }
    public boolean isJustAdd(String fileFullName){
        return (operations.containsKey(fileFullName) && operations.get(fileFullName).equals("ADD"));
    }
    public boolean hasTracked(String fileFullName){
        return files.containsKey(fileFullName);
    }
    public TreeMap<String,String> getStagedFiles(){
        TreeMap<String,String> ret = new TreeMap<>();
        if (!operations.isEmpty()) {
            for (Map.Entry<String,String> entry : operations.entrySet()) {
                ret.put(entry.getKey(),entry.getValue());
            }
        }
        return ret;
    }
    public String getPreVersion(String fileName){
        String ret = "";
        if (orginalFiles.containsKey(fileName)) {
            ret = orginalFiles.get(fileName);
        }
        return ret;
    }

    /* calculate id */
    public String refreshId(){
        ArrayList<String> a = new ArrayList<>();
        a.add(message);
        timeStamp = System.currentTimeMillis();
        a.add(String.valueOf(timeStamp));
        if (files.size()>0) {
            for (Map.Entry<String,String> entry : files.entrySet()) {
                a.add(entry.getKey());
                a.add(entry.getValue());
            }
        }
        if(operations.size()>0){
            for (Map.Entry<String,String> entry : operations.entrySet()){
                a.add(entry.getKey() + "_" + entry.getValue());
            }
        }
        a.add(parentM);
        a.add(parentS);
        id = Utils.sha1(a.toArray());
        return id;
    }

    public String getId(){
        return id;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String _message){
        message = _message;
    }
    public long getTimeStamp(){
        return timeStamp;
    }
    public void setTimeStamp(long _timeStamp) {
        timeStamp = _timeStamp;
    }
    public String getParentM() {
        return parentM;
    }
    public void setParentM(String _parentM){
        parentM = _parentM;
    }
    public String getParentS() {
        return parentS;
    }
    public void setParentS(String _parentS){
        parentS = _parentS;
    }

}
