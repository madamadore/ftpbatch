package it.matteoavanzini.batch.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtilImpl implements MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.smtp.from}")
    String from;

    @Override
    public void sendMail(String to, String object, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(object);
        mail.setText(message);

        mailSender.send(mail);
    }
    
}