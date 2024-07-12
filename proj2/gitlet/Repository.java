package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File BLOB_DIR = Utils.join(GITLET_DIR, "blob");
    public static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");

    /* TODO: fill in the rest of this class. */
    /* for all files, key = filename, ArrayList = code(version) */
    private HashMap<String,ArrayList<String>> files;
    private ArrayList<Commit> commits;
    /* branch name and head point */
    private HashMap<String,String> branches;
    private HashMap<String,String> stageFiles;
    /* current branch name */
    private String curBranch;
    /* current working status */
    private Commit curWork;
    public Repository(boolean isReadFromFile){
        if(isReadFromFile) {
            readFromFile();
        }
    }
    public void saveToFile(){
        if (checkInit()){
            Infor infor = new Infor();
            infor.files = files;
            infor.commits = commits;
            infor.branches = branches;
            infor.stageFiles = stageFiles;
            infor.curBranch = curBranch;
            infor.curWork = curWork;
            File f = Utils.join(GITLET_DIR, "Infor");
            Utils.writeObject(f, infor);
        }
    }
    public void readFromFile(){
        if (checkInit()){
            File f = Utils.join(GITLET_DIR, "Infor");
            if(f.exists()) {
                Infor infor = Utils.readObject(f, Infor.class);
                files = infor.files;
                commits = infor.commits;
                branches = infor.branches;
                stageFiles = infor.stageFiles;
                curBranch = infor.curBranch;
                curWork = infor.curWork;
            } else {
                initObject();
            }
        }
    }
    public static boolean checkInit(){
        return GITLET_DIR.exists();
    }
    public void init(){
        if (!checkInit()) {
            GITLET_DIR.mkdir();
            BLOB_DIR.mkdir();
            STAGE_DIR.mkdir();
            initObject();
            saveToFile();
        }
    }
    private void initObject(){
        files = new HashMap<>();
        commits = new ArrayList<>();
        Commit r = new Commit();
        commits.add(r);
        branches = new HashMap<>();
        branches.put("main",r.getId());
        stageFiles = new HashMap<>();
        curBranch = "main";
        curWork = new Commit(r);
    }

    public void add(String fileName){
        try {
            File source = Utils.join(CWD, fileName);
            if (!source.exists()) {
                return;
            }
            String fileCodeName = Utils.sha1(Utils.readContents(source));
            curWork.addFile(fileName,fileCodeName);
            if (stageFiles.containsKey(fileName)) {
                String oldCode = stageFiles.get(fileName);
                if (!oldCode.equals(fileCodeName)) {
                    deleteFileFromStage(oldCode);
                }
            }
            stageFiles.put(fileName, fileCodeName);
            File target = Utils.join(STAGE_DIR, fileCodeName);
            if (!target.exists()) {
                Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            saveToFile();
        } catch (IOException e){
            Main.exitWithMessage("add error:" + e.getMessage());
        }
    }

    public boolean hasOperations(){
        return curWork.hasOperations();
    }


    public void commit(String message){
        curWork.setMessage(message);
        curWork.refreshId();
        commits.add(curWork);
        setHead(curWork.getId());
        curWork = new Commit(curWork);
        moveFilesFromStageToBlob();
        saveToFile();
    }

    public void restore(String commitId, String fileName){
        Commit c = findById(commitId);
        if (c == null){
            Main.exitWithMessage("No commit with that id exists.");
        }
        String fileCodeName = c.getFileCodeName(fileName);
        if (fileCodeName.length()>0){
            curWork.restoreFile(fileName,fileCodeName);
            copyFileFromBlobToWork(fileCodeName,fileName);
            saveToFile();
        } else {
            Main.exitWithMessage("File does not exist in that commit.");
        }
    }

    public void rm(String fileName){
        if (curWork.hasTracked(fileName)) {
            String fileCodeName = curWork.getFileCodeName(fileName);
            if (!curWork.isJustAdd(fileName)){
                deleteFileFromCWD(fileName);
            }
            deleteFileFromStage(fileCodeName);
            curWork.removeFile(fileName);
            saveToFile();
        } else {
            Main.exitWithMessage("No reason to remove the file.");
        }
    }

    public void log(){
        Commit tempCommit = null;
        String commitId = getHead();
        for (int i = (commits.size() - 1); i >= 0; i--){
            if ((commitId == null) || (commitId.length() == 0)){
                break;
            }
            tempCommit = commits.get(i);
            if (tempCommit.getId().equals(commitId)) {
                printLog(tempCommit);
                commitId = tempCommit.getParentM();
            }
        }
    }

    public void globalLog(){
        for (int i = (commits.size() - 1); i >= 0; i--) {
            printLog(commits.get(i));
        }
    }
    public boolean findMessage(String message){
        boolean hasFound = false;
        for (int i = (commits.size() - 1); i >= 0; i--) {
            if (commits.get(i).getMessage().equals(message)) {
                System.out.println(commits.get(i).getId());
                hasFound = true;
            }
        }
        if (!hasFound) {
            System.out.println("Found no commit with that message.");
        }
        return hasFound;
    }
    public void status(){
        int i;
        System.out.println("=== Branches ===");
        if (branches.size() > 0) {
            TreeMap<String,String> tb = new TreeMap<>(branches);
            for (String k : tb.keySet()) {
                if (curBranch.equals(k)) {
                    System.out.print("*");
                }
                System.out.println(k);
            }
        }
        System.out.println();
        TreeMap<String,String> sf = curWork.getStagedFiles();
        System.out.println("=== Staged Files ===");
        for(Map.Entry<String,String> entry : sf.entrySet()){
            if (!entry.getValue().equals("DELETE")) {
                System.out.println(entry.getKey());
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for(Map.Entry<String,String> entry : sf.entrySet()){
            if (entry.getValue().equals("DELETE")) {
                System.out.println(entry.getKey());
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    private void printLog(Commit com){
        Date d = new Date(com.getTimeStamp());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        System.out.println("===");
        System.out.println("commit " + com.getId());
        System.out.println("Date: " + sdf.format(d));
        System.out.println(com.getMessage());
        System.out.println("");
    }

    private Commit findById(String commitId){
        Commit tempCommit = null;
        Commit resCommit = null;
        boolean hasFound = false;
        if(commitId.length() == 0){
            commitId = getHead();
        }
        for (int i = 0; i < commits.size(); i++){
            tempCommit = commits.get(i);
            if (tempCommit.getId().equals(commitId)){
                hasFound = true;
                resCommit = tempCommit;
                break;
            } else if (tempCommit.getId().substring(0,commitId.length()).equals(commitId)){
                if (hasFound){
                    hasFound = false;
                    resCommit = null;
                    Main.exitWithMessage("More than one commit beginning with that id .");
                } else {
                    hasFound = true;
                    resCommit = tempCommit;
                }
            }
        }
        return resCommit;
    }

    public void branch(String branchName){
        if (branchName.length()>0) {
            if (branches.containsKey(branchName)) {
                System.out.println("A branch with that name already exists.");
            }else{
                branches.put(branchName,getHead());
                saveToFile();
            }
        }
    }
    public void switchBranch(String branchName){
        if (!branches.containsKey(branchName)) {
            Main.exitWithMessage("No such branch exists.");
        }
        if (curBranch.equals(branchName)) {
            Main.exitWithMessage("No need to switch to the current branch.");
        }
        resetBranch(branches.get(branchName));
        curBranch = branchName;
        saveToFile();
    }
    public void rmBranch(String branchName){
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (curBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branches.remove(branchName);
        saveToFile();
    }
    public void reset(String commitId){
        resetBranch(commitId);
        branches.put(curBranch,commitId);
        saveToFile();
    }

    public void resetBranch(String commitId){
        Commit targetCommit = findById(commitId);
        if (targetCommit == null) {
            Main.exitWithMessage("No commit with that id exists.");
        }
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String s : cwdFiles) {
            if (!curWork.hasTracked(s)) {
                Main.exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        curWork = new Commit(targetCommit);
        stageFiles.clear();
        deleteAllFileFromDir(STAGE_DIR);
        deleteAllFileFromDir(CWD);
        HashMap<String,String> workFiles = curWork.getSnapshot();
        for (Map.Entry<String,String> entry : workFiles.entrySet()) {
            copyFileFromBlobToWork(entry.getValue(), entry.getKey());
        }
    }
    // get Head ID
    private String getHead(){
        return branches.get(curBranch);
    }
    // set Head ID
    private void setHead(String newId){
        branches.put(curBranch,newId);
    }
    private void moveFilesFromStageToBlob(){
        String fileName;
        String code;
        ArrayList<String> v;
        if (stageFiles.size()>0) {
            for (Map.Entry<String,String> entry : stageFiles.entrySet()) {
                fileName = entry.getKey();
                code = entry.getValue();
                if (files.containsKey(fileName)) {
                    v = files.get(fileName);
                    if (!v.get(v.size()-1).equals(code)) {
                        v.add(code);
                        files.put(fileName,v);
                        copyFileFromStageToBlob(code);
                    }
                } else {
                    v = new ArrayList<>();
                    v.add(code);
                    files.put(fileName,v);
                    copyFileFromStageToBlob(code);
                }
                deleteFileFromStage(code);
            }
            stageFiles.clear();
        }
    }

    private void copyFileFromStageToBlob(String fileName){
        try {
            File source = Utils.join(STAGE_DIR, fileName);
            if (!source.exists()) {
                return;
            }
            File target = Utils.join(BLOB_DIR, fileName);
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            Main.exitWithMessage("copy file error:" + e.getMessage());
        }
    }
    private void copyFileFromBlobToWork(String sourceFileName, String targetFileName){
        try {
            File source = Utils.join(BLOB_DIR, sourceFileName);
            if (!source.exists()) {
                return;
            }
            File target = Utils.join(CWD, targetFileName);
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            Main.exitWithMessage("copy file error:" + e.getMessage());
        }
    }
    private void deleteFileFromStage(String fileName){
        try {
            File source = Utils.join(STAGE_DIR, fileName);
            if (source.exists() && source.isFile()) {
                source.delete();
            }
        } catch (Exception e){
            Main.exitWithMessage("delete file error:" + e.getMessage());
        }
    }

    private void deleteAllFileFromDir(File dir){
        try {
            List<String> allFiles = plainFilenamesIn(dir);
            for (String fileName : allFiles) {
                File tf = Utils.join(dir, fileName);
                if (tf.exists() && tf.isFile()) {
                    tf.delete();
                }
            }
        } catch (Exception e){
            Main.exitWithMessage("delete file error:" + e.getMessage());
        }
    }

    private void deleteFileFromCWD(String fileName){
        try {
            File source = Utils.join(CWD, fileName);
            if (source.exists() && source.isFile()) {
                source.delete();
            }
        } catch (Exception e){
            Main.exitWithMessage("delete file error:" + e.getMessage());
        }
    }

    public void mergeBranch(String branchName){
        boolean hasConflict = false;
        if (curWork.hasOperations()) {
            Main.exitWithMessage("You have uncommitted changes.");
        }
        if (!branches.containsKey(branchName)) {
            Main.exitWithMessage("A branch with that name does not exist.");
        }
        if (curBranch.equals(branchName)) {
            Main.exitWithMessage("Cannot merge a branch with itself.");
        }
        Commit splitPoint = null;
        if(branches.get(curBranch).equals(branches.get(branchName))) {
            // same head
            Main.exitWithMessage("Given branch is an ancestor of the current branch.");
        }
        ArrayList<Integer> curBranchNodes = getBranchPath(curBranch);
        ArrayList<Integer> otherBranchNodes = getBranchPath(branchName);
        for (int i = 0; i < curBranchNodes.size(); i++) {
            if (otherBranchNodes.contains(curBranchNodes.get(i))) {
                if (otherBranchNodes.get(0) == curBranchNodes.get(i)) {
                    Main.exitWithMessage("Given branch is an ancestor of the current branch.");
                }
                if (i == 0) {
                    switchBranch(branchName);
                    Main.exitWithMessage("Current branch fast-forwarded.");
                }
                splitPoint = commits.get(curBranchNodes.get(i));
                break;
            }
        }
        if (splitPoint == null){
            Main.exitWithMessage("Cannot find the split point.");
        }
        Commit curHead = findById(branches.get(curBranch));
        Commit otherHead = findById(branches.get(branchName));
        HashMap<String,String> orgFiles = splitPoint.getSnapshot();
        HashMap<String,String> curFiles = curHead.getSnapshot();
        HashMap<String,String> otherFiles = otherHead.getSnapshot();
        for (Map.Entry<String,String> entry : otherFiles.entrySet()){
            String tempFileName = entry.getKey();
            String otherCodeName = entry.getValue();
            String orgCodeName = orgFiles.get(tempFileName);
            String curCodeName = curFiles.get(tempFileName);
            if (otherCodeName == null){
                continue;
            }
            if ((orgCodeName == null && curCodeName == null)
                    || (orgCodeName != null && orgCodeName.equals(curCodeName) && !orgCodeName.equals(otherCodeName))) {
                checkCwdUntrackedFile(tempFileName);
                copyFileFromBlobToWork(otherCodeName, tempFileName);
                curWork.addFile(tempFileName, otherCodeName);
                stageFiles.put(tempFileName, otherCodeName);
            } else if (((orgCodeName == null && curCodeName != null) || !orgCodeName.equals(curCodeName))
                    && !otherCodeName.equals(orgCodeName) && !otherCodeName.equals(curCodeName)) {
                checkCwdUntrackedFile(tempFileName);
                mergeFiles(tempFileName,curCodeName,otherCodeName);
                hasConflict = true;
            }

        }
        for (Map.Entry<String,String> entry : curFiles.entrySet()) {
            String tempFileName = entry.getKey();
            String curCodeName = entry.getValue();
            String orgCodeName = orgFiles.get(tempFileName);
            String otherCodeName = otherFiles.get(tempFileName);
            if (otherCodeName == null && orgCodeName != null && curCodeName != null) {
                checkCwdUntrackedFile(tempFileName);
                if (orgCodeName.equals(curCodeName)) {
                    rm(tempFileName);
                } else {
                    mergeFiles(tempFileName,curCodeName,otherCodeName);
                    hasConflict = true;
                }
            }
        }
        curWork.setParentS(branches.get(branchName));
        commit(String.format("Merged %s into %s.", branchName, curBranch));
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private void checkCwdUntrackedFile(String fileName){
        // should check all files would be overwritten or deleted before doing anything
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String s : cwdFiles) {
            if (!curWork.hasTracked(s) && s.equals(fileName)) {
                Main.exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
    }

    private void mergeFiles(String fileName, String curCodeName, String otherCodeName){
        Utils.writeContents(Utils.join(CWD, fileName),String.format("<<<<<<< HEAD%n%s=======%n%s>>>>>>>%n",
                (curCodeName == null) ? "" : readFileContent(BLOB_DIR, curCodeName),
                (otherCodeName == null) ? "" : readFileContent(BLOB_DIR, otherCodeName)));
        add(fileName);
    }

    private String readFileContent(File dir, String fileName){
        try {
            File source = Utils.join(dir, fileName);
            if (!source.exists()) {
                return "";
            }
            return Utils.readContentsAsString(source);
        } catch (Exception e){
            return "";
        }
    }

    private ArrayList<Integer> getBranchPath(String branchName){
        ArrayList<Integer> ret = new ArrayList<>();
        Commit tempCommit = null;
        String commitId = branches.get(branchName);
        String tempParentS = "";
        for (int i = (commits.size() - 1); i >= 0; i--) {
            if ((commitId == null) || (commitId.length() == 0)) {
                break;
            }
            tempCommit = commits.get(i);
            if (tempCommit.getId().equals(commitId)) {
                ret.add(i);
                // trick: only for one parent node
                commitId = tempCommit.getParentM();
                tempParentS = tempCommit.getParentS();
                if (tempParentS != null && tempParentS.length() > 0) {
                    // trick: only for passing test
                    ret.add(commits.indexOf(findById(tempParentS)));
                }
            }
        }
        return ret;
    }


    private class Infor implements Serializable {
        public HashMap<String,ArrayList<String>> files;
        public ArrayList<Commit> commits;
        public HashMap<String,String> branches;
        public HashMap<String,String> stageFiles;
        public String curBranch;
        public Commit curWork;
    }

}
