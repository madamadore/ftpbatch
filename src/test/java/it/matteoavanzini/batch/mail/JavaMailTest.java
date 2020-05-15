package it.matteoavanzini.batch.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JavaMailTest {
    
    @Autowired
    MailUtil mailUtil;
    
    @Test
    public void testSendMail() {
        mailUtil.sendMail("caiofior@abbrevia.it", "Ciao", "Questo è un <b>Test</b>");
    }
}