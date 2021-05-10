package email.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.vaadin.server.VaadinServlet;

import email.SpringConfigurationValueHelper;
import email.SpringContextHelper;
import email.components.SendEmailInternal;
import email.entities.EmailInfo;
import email.services.CustomerEmailSendService;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class EmailThread extends Thread{
	private static final Logger LOGGER = LogManager.getLogger(EmailThread.class);
	private Thread t;
    private String threadName;
    private long startTime;
    private int threadNo;
    List<List<EmailInfo>> sublistEmails;
    private String attachFileMoveToFolder;
    private CustomerEmailSendService customerEmailSendService;
    
    @Autowired
    private JavaMailSender javaMailSender;
 
    public EmailThread(String _threadName,long startTm,int threadOfNumber,List<List<EmailInfo>> subListEmails, String moveToFolder) {
        threadName = _threadName;
        startTime = startTm;
        t = new Thread(this,threadName);
        threadNo = threadOfNumber;
        sublistEmails = subListEmails;
        attachFileMoveToFolder = moveToFolder;
        LOGGER.info("Creating " + t);
    }
    
    @Override
	public void run() {
    	LOGGER.info("Running " + threadName);
		LOGGER.info("Begin " + t +" time: " + System.currentTimeMillis());
		List<EmailInfo> listEmails = new ArrayList<EmailInfo>();
		listEmails.addAll(sublistEmails.get(threadNo));
		try {
			sendEmail(listEmails);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LOGGER.info("End " + t + "  time: " + System.currentTimeMillis() + "\tTotal time: " + (double)(System.currentTimeMillis()-startTime)/1000);
	}
    
//    private void sendEmail(List<EmailInfo> emailToRecipients) {
//    	final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
//		customerEmailSendService = (CustomerEmailSendService) helper.getBean("customerEmailSendService");
//    	try {
//    		for(int i=0; i<emailToRecipients.size();i++)
//    		{
//    			ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
////    	        ExchangeCredentials credentials = new WebCredentials("tanvh1@scb.com.vn", "##Vht1092", "outlook.office365.com"); 
//    	        ExchangeCredentials credentials = new WebCredentials("cardcenter@scb.com.vn", "$cb927", "outlook.office365.com"); 
//    	        service.setCredentials(credentials);
//    	        service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx")); 
//    	        EmailMessage msg = new EmailMessage(service);
//    	        msg.setSubject((threadNo+i+(threadNo==0?0:threadNo-1)) + " - "+ emailToRecipients.get(i).getSubject() + " - " + t + " - " + i); 
//    	        msg.setBody(MessageBody.getMessageBodyFromText(emailToRecipients.get(i).getSubject() + " - " + t + " - " + i)); 
//    	        msg.getToRecipients().add(emailToRecipients.get(i).getToRecipient()); 
//    	        msg.getCcRecipients().add(emailToRecipients.get(i).getCcRecipient()); 
//    	        msg.getBccRecipients().add(emailToRecipients.get(i).getBccRecipient()); 
//    	        msg.getAttachments().addFileAttachment(emailToRecipients.get(i).getFileAttachment()); 
//    	        msg.send();
//    	        
//    	        File f = new File(emailToRecipients.get(i).getFileAttachment());
//    	        String fileName = StringUtils.substringBefore(f.getName(), ".");
//    	        //TEST 
////    	        moveFileSend(emailToRecipients.get(i).getFileAttachment(),attachFileMoveToFolder, fileName +"_send.pdf");
//    	        
//    	        customerEmailSendService.updateStatusByFilename(fileName, "Y");
//    	       
//    	        long endTime = System.currentTimeMillis();
//    			long totalTime = endTime - startTime;
//    			LOGGER.info("Email " + emailToRecipients.get(i).getToRecipient()  + " " + emailToRecipients.get(i).getFullname().trim()  + " " + t + " - " + i + " is done. Time = " + (double)totalTime/1000 + "seconds");
//    		}
//    		
//    	} catch (Exception e) {
//            e.printStackTrace();
//            LOGGER.error(e);
//        }
//    }
//    
    private void sendEmail(List<EmailInfo> emailToRecipients) throws MessagingException, IOException {
    	final SpringContextHelper sphelper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		customerEmailSendService = (CustomerEmailSendService) sphelper.getBean("customerEmailSendService");
    	try {
    		for(int i=0; i<emailToRecipients.size();i++)
    		{
    	        
//    	        MimeMessage msg = javaMailSender.createMimeMessage();
//
//    	        // true = multipart message
//    	        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
//    			
//    	        helper.setTo(emailToRecipients.get(i).getToRecipient());
//
//    	        helper.setSubject((threadNo+i+(threadNo==0?0:threadNo-1)) + " - "+ emailToRecipients.get(i).getSubject() + " - " + t + " - " + i);
//
//    	        // default = text/plain
//    	        //helper.setText("Check attachment for image!");
//
//    	        // true = text/html
//    	        helper.setText((emailToRecipients.get(i).getSubject() + " - " + t + " - " + i), true);
//
//    			// hard coded a file path
//    	        //FileSystemResource file = new FileSystemResource(new File("path/android.png"));
//
////    	        helper.addAttachment("my_photo.png", emailToRecipients.get(i).getFileAttachment());
//
//    	        javaMailSender.send(msg);
    	        
    			SimpleMailMessage msg = new SimpleMailMessage();
    	        msg.setTo("umbala1092@gmail.com");

    	        msg.setSubject("Testing send emailSCB");
    	        msg.setText("Test email send out");

    	        getJavaMailSender().send(msg);
//    	        javaMailSender.send(msg);
    	        
    	        File f = new File(emailToRecipients.get(i).getFileAttachment());
    	        String fileName = StringUtils.substringBefore(f.getName(), ".");
    	        //TEST 
//    	        moveFileSend(emailToRecipients.get(i).getFileAttachment(),attachFileMoveToFolder, fileName +"_send.pdf");
    	        
    	        customerEmailSendService.updateStatusByFilename(fileName, "Y");
    	       
    	        long endTime = System.currentTimeMillis();
    			long totalTime = endTime - startTime;
    			LOGGER.info("Email " + emailToRecipients.get(i).getToRecipient()  + " " + emailToRecipients.get(i).getFullname().trim()  + " " + t + " - " + i + " is done. Time = " + (double)totalTime/1000 + "seconds");
    		}
    		
    	} catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }
    
    private void moveFileSend(String fromFile, String toFolder, String newFilename) {
    	Path temp;
		try {
			temp = Files.move(Paths.get(fromFile), Paths.get(toFolder + "\\" + newFilename));
			if(temp != null) 
 	        { 
 	            LOGGER.info("File " + Paths.get(fromFile).getFileName() + " renamed and moved successfully");
 	        } 
 	        else
 	        { 
 	           LOGGER.info("Failed to move file " + Paths.get(fromFile).getFileName());
 	        } 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error(e);
		} 
    	  
    	       
    }
    
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.office365.com");
        mailSender.setPort(587);
         
        mailSender.setUsername("cardcenter@scb.com.vn");
        mailSender.setPassword("$cb927");
         
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
         
        return mailSender;
    }
}
