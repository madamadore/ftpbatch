package it.matteoavanzini.batch.ftp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
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
        //ftpClient.setControlEncoding("UTF-8");
        //ftpClient.setAutodetectUTF8(true);

        //ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        
        ftpClient.connect(server, port);
        ftpClient.login(username, password);

        //System.setProperty(FTPClient.FTP_SYSTEM_TYPE_DEFAULT, FTPClientConfig.SYST_UNIX);
        //ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }

    private void disconnect() throws IOException {
        ftpClient.disconnect();
    }
        
    @Override
    public List<File> downloadFiles() {

        List<File> result = new ArrayList<>();
        try {
            connect();
            FTPListParseEngine engine = ftpClient.initiateListParsing();
            while (engine.hasNext()) {
                FTPFile[] files = engine.getNext(25);  // "page size" you want
                for (FTPFile remote : files) {
                    if (!remote.isDirectory()) {
                        File local = saveFileLocally(remote);
                        result.add(local);
                    }
                }
            }
            
        } catch (IOException e) {
            logger.info(e.getMessage());
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private File saveFileLocally(FTPFile remote) throws IOException {
        
        String localPathAndName = remote.getName();
        File local = new File(localPathAndName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        boolean success = ftpClient.retrieveFile(remote.getName(), byteArrayOutputStream);
        
        if (success) {
            try (OutputStream localOutputStream = new FileOutputStream(local)) {
                byteArrayOutputStream.writeTo(localOutputStream);
            }
        } else {
            throw new IOException("Retrieve file failed: " + remote.getName());
        }
        return local;
    }

    @Override
    public void uploadFiles(List<File> files) {
        
    }
    
}