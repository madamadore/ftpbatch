package it.matteoavanzini.ftpbatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FTPUtilImpl implements FTPUtil {

    private Logger logger = LoggerFactory.getLogger(FTPUtilImpl.class);
    private FTPClient ftpClient;

    @Value("${ftp.server.url}")
    private String server;
    @Value("${ftp.server.port}")
    private int port;

    @Value("${ftp.server.username}")
    private String username;
    @Value("${ftp.server.password}")
    private String password;


    private void connect() throws IOException {
        //int iPort = Integer.parseInt(port);
        ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(username, password);
    }

    private void disconnect() throws IOException {
        ftpClient.disconnect();
    }
        
    @Override
    public List<File> downloadFiles() {

        List<File> result = new ArrayList<>();

        try {
            connect();

            //FTPFile[] files = ftpClient.listFiles("/Users/emme/temp/");
            FTPListParseEngine engine = ftpClient.initiateListParsing();
            while (engine.hasNext()) {
                FTPFile[] files = engine.getNext(25);  // "page size" you want
                for (FTPFile remote : files) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    boolean success = ftpClient.retrieveFile(remote.getName(), byteArrayOutputStream);
                    
                    if (success) {
                        try(OutputStream outputStream = new FileOutputStream(remote.getName())) {
                            byteArrayOutputStream.writeTo(outputStream);
                        }
                        File local = new File(remote.getName());
                        result.add(local);
                    } else {
                        throw new IOException("Retrieve file failed: " + remote.getName());
                    }
                }
            }

            disconnect();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return result;
    }

    @Override
    public void uploadFiles(List<File> files) {
        
    }
    
}