import java.text.DecimalFormat;
import java.io.*;
import java.util.*;

/*
    Data structure is as follows
    A Map with key=fileSize and value=ArrayList of filenames
*/
public class Generate_File_Names_With_FileSize_And_Checksum {
    private static List<String> includedFolderList = new ArrayList<String>();
    private static List<String> filmFileList = new ArrayList<String>(100000);
    private static List<String> exclusionList = new ArrayList<String>();
    private static List<String> drivesList = new ArrayList<String>();
    private static DecimalFormat decimalFormatter = new DecimalFormat("#,###,###,000");
    private static boolean checksumReqd = false;
    /******************************************************************************************/
    public static void main(String[] args) throws Exception {
        populteDrivesList();
        Map hm = PropertiesLoader.loadToHashMap("input.properties");
        includedFolderList = (List<String>)hm.get("INCLUDE_PATH");
        exclusionList = (List<String>)hm.get("EXCLUDE_PATH");
        //System.out.println("includedFolderList = "+includedFolderList);
        //System.out.println("exclusionList   = "+exclusionList);
        
        //-------------------------------------------------------------------------------------
        if(args != null && args.length > 0) {
            String aa = args[0];
            if(aa.equals("checksum")) {
                checksumReqd = true;
            }
        }
        //System.out.println("loading to Map - start");
        File output = new File("output");
        output.mkdirs();
        for(int i=0;i<includedFolderList.size();i++) {
            String inputFolder = includedFolderList.get(i);
            System.out.println("REM   input.folder : "+inputFolder);
            if(inputFolder.contains("?")) {
                inputFolder = replaceLogicalWithPhysicalDriveLetter(inputFolder);
            }
            filmFileList = getFileListFromFolder(inputFolder);
            if(filmFileList.size() > 0) {
                String physicalDriveLetter = inputFolder.substring(0,1);
                //System.out.println("Drive letter="+driveLetter);
                String driveName = getDriveName(physicalDriveLetter);
                String outputFileName = inputFolder;
                outputFileName = outputFileName.replaceAll(":","_");
                outputFileName = outputFileName.replaceAll("\\\\","_");
                outputFileName = outputFileName.replaceAll("__","_");
                FileOutputStream fos = new FileOutputStream(new File(output+"/"+outputFileName+".txt"));
                
                fos.write(("DRIVE_NAME="+driveName+"\r\n").getBytes());
                for(int j=0;j<filmFileList.size();j++) {
                  fos.write((filmFileList.get(j)+"\r\n").getBytes());
                }
                fos.close();
                filmFileList = new ArrayList<String>();
            }
        }
        //System.out.println("loading to Map - end");
        //System.out.println("filmFileList.size()="+filmFileList.size());
        //System.out.println("REM   -------- program end");
    }
    /******************************************************************************************/
    public static List<String> getFileListFromFolder(String sourcePath) {
        //System.out.println(sourcePath);
        if(exclusionList.contains(sourcePath)) {
            return new ArrayList<String>();
        }
        File dir = new File(sourcePath);
        if(!dir.exists()) {
            return new ArrayList<String>();
        }
        if(sourcePath.endsWith(".git")) {
            return new ArrayList<String>();
        }
        List<String> fileTree = new ArrayList<String>();
        try {
            for (File entry : dir.listFiles()) {
                if (entry.isFile()) {
                    String fileName = entry.getAbsolutePath();
                    long fileSize = entry.length();
                    String fileSizeStr = decimalFormatter.format(fileSize);
                    String newFileName = fileName + "|" + fileSizeStr ;
                    if(checksumReqd) {
                        String md6CheckSum = MD6Util.getMD6ChecksumAsHEX(fileName);
                        newFileName = newFileName + "|" + md6CheckSum;
                    }
                    //System.out.println("newFileName="+newFileName);
                    fileTree.add(newFileName);
                } else {
                    fileTree.addAll(getFileListFromFolder(entry.getAbsolutePath()));
                }
            }
        } catch(Exception e) {
            //
        }
        return fileTree;
    }
    /******************************************************************************************/
    public static String getDriveName(String physicalDriveLetter) throws Exception {
        Map hm = PropertiesLoader.load(physicalDriveLetter+":/DRIVE_NAME.TXT");
        String driveName = (String)hm.get("DRIVE_NAME");
        //String driveName=readFileContentsAsString(physicalDriveLetter+":/DRIVE_NAME.TXT");
        //if(driveName.equals("")) {
        //    return physicalDriveLetter;
        //}
        return driveName.trim();
    }
    /******************************************************************************************/
    public static String getLogicalDriveLetter(String driveLetter) throws Exception {
        Map hm = PropertiesLoader.load(driveLetter+":/DRIVE_NAME.TXT");
        String logicalDriveLetter = (String)hm.get("LOGIAL_DRIVE_LETTER");
        return logicalDriveLetter.trim();
    }
    /******************************************************************************************/
    public static void populteDrivesList() throws Exception {
          File[] paths;
          paths = File.listRoots();
          for(File path:paths) {
              System.out.println("Drive Letter: "+path.toString().substring(0,1));
              drivesList.add(path.toString().substring(0,1));
          }
    }
    /******************************************************************************************/
    public static String replaceLogicalWithPhysicalDriveLetter(String inputFolder) {
        for(String driveLetter : drivesList) {
            inputFolder = inputFolder.replaceAll("\\?",driveLetter);
        }
        System.out.println("Modified path="+inputFolder);
        return inputFolder;
    }
    /******************************************************************************************/
    public static String readFileContentsAsString(String fileName) throws Exception {
        StringBuffer returnStringBuffer = new StringBuffer();
        try {
            BufferedReader input = new BufferedReader(new FileReader(new File(fileName)));
            String line = null; // not declared within while loop
            while ((line = input.readLine()) != null) {
                returnStringBuffer.append(line);
            }
            input.close();
        } catch(Exception e) {
            return ""; 
        }
        return returnStringBuffer.toString();
    }

}
