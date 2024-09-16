package alphaciment.base_iso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender eMailSender;

    public void sendEmail(String toEmail,String subject,String body) throws Exception{
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("isma.raveloson@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            
            eMailSender.send(message);
    
            System.out.println("Mail envoyé avec succés...");
        }catch(MailException e){
            throw e;
        }
    }

    
}
