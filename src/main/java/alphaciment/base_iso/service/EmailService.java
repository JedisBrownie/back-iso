package alphaciment.base_iso.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

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


    public void mailSendingTest() throws IOException {
        String apiKey = System.getenv("SENDGRID_API_KEY");
        System.out.println(System.getenv("SENDGRID_API_KEY"));

        SendGrid sendGrid = new SendGrid(apiKey);

        Email from = new Email("natana.ralambomanana@hotmail.com");
        String subject = "Bonjour Nomena!";
        Email to = new Email("natana.ralambomanana@gmail.com");
        Content content = new Content("text/plain", "This is a test email sent using SendGrid.");
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            System.out.println("Response Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Response Headers: " + response.getHeaders());

        } catch (IOException ex) {
            System.err.println("Error occurred while sending email: " + ex.getMessage());
            throw ex;
        }
    }
}
