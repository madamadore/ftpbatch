package it.matteoavanzini.batch.mail;

public interface MailUtil {
    void sendMail(String to, String object, String message);
}