package it.matteoavanzini.batch.ftp;

import java.io.File;
import java.util.List;

public interface FTPUtil {
    List<File> downloadFiles();  
    void uploadFiles(List<File> files);   
}