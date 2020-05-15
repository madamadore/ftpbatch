package it.matteoavanzini.batch;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import it.matteoavanzini.batch.ftp.FTPUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
class BatchApplicationTests {

	FakeFtpServer fakeFtpServer;
	Logger logger = LoggerFactory.getLogger(BatchApplicationTests.class);

	@Autowired
	private FTPUtil ftpUtil;

	@Before
	public void setUp() throws Exception {
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.setServerControlPort(921);
		
		UserAccount userAccount = new UserAccount("user", "Fuffologia", "/home/user");
		fakeFtpServer.addUserAccount(userAccount);

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry("/home/user"));
		fileSystem.add(new FileEntry("/home/user/file1.txt", "abcdef 1234567890"));
		//fileSystem.add(new FileEntry("/home/user/file2.pdf"));
		
		fakeFtpServer.setFileSystem(fileSystem);
		fakeFtpServer.start();
	}

	@After
	public void tearDown() throws Exception {
		fakeFtpServer.stop();
		File file = new File("file1.txt");
		file.delete();
	}

	@Test
	public void testFtpDownload() throws Exception {
		setUp();

		List<File> files = ftpUtil.downloadFiles();
		assertEquals(1, files.size());

		File file = files.get(0);
		assertNotNull(file);
		assertEquals("file1.txt", file.getName());
		assertEquals("abcdef 1234567890", fileContent(file));

		tearDown();
	}

	public String fileContent(File file) {
        try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
