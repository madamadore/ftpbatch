package it.matteoavanzini.ftpbatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileCopyUtils;

@SpringBootTest
class FtpbatchApplicationTests {

	FakeFtpServer fakeFtpServer;
	Logger logger = LoggerFactory.getLogger(FtpbatchApplicationTests.class);

	@Autowired
	private FTPUtil ftpUtil;

	@Before
	public void setUp() throws Exception {
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.setServerControlPort(921); // use any free port
		
		UserAccount userAccount = new UserAccount("user", "password", "/home/user");
		fakeFtpServer.addUserAccount(userAccount);

		FileSystem fileSystem = new WindowsFakeFileSystem();
		fileSystem.add(new DirectoryEntry("c:\\data"));
		fileSystem.add(new FileEntry("c:\\data\\file1.txt", "abcdef 1234567890"));
		fileSystem.add(new FileEntry("c:\\data\\run.exe"));
		
		fakeFtpServer.setFileSystem(fileSystem);
		fakeFtpServer.start();
	}

	@After
	public void tearDown() throws Exception {
		fakeFtpServer.stop();
	}

	@Test
	public void testFtpDownload() throws Exception {
		setUp();
		List<File> files = ftpUtil.downloadFiles();
		assertEquals(1, files.size());

		File file = files.get(0);
		assertNotNull(file);
		assertEquals("data.txt", file.getName());
		assertEquals("abcde 12345", fileContent(file));
	}

	public String fileContent(File file) {
        try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
