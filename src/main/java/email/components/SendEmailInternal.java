package email.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import email.SpringConfigurationValueHelper;
import email.SpringContextHelper;
import email.controller.EmailThread;
import email.entities.CustomerEmailSend;
import email.entities.EmailInfo;
import email.services.CustomerEmailSendService;

@SpringComponent
@Scope("prototype")
public class SendEmailInternal extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SendEmailInternal.class);
	private final transient Label lbTitle = new Label("GỬI EMAIL SAO KÊ IN SCB ");
	private transient TextField tfFolderPath;
	private final transient ComboBox cbbCardType;
	private final transient ComboBox cbbMonth;
	private final transient ComboBox cbbYear;
	private final transient Button btSendInScb;
	private final transient Button btCheckFileNotSend;
	private final transient Label lbDescNumOfFileNotSend;
	private transient Label lbNumOfFileNotSend;
	
	private SpringConfigurationValueHelper configurationHelper;
	private CustomerEmailSendService customerEmailSendService;
	
	public SendEmailInternal() {
		super();
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		configurationHelper = (SpringConfigurationValueHelper) helper.getBean("springConfigurationValueHelper");
		customerEmailSendService = (CustomerEmailSendService) helper.getBean("customerEmailSendService");
		
		lbTitle.setStyleName(ValoTheme.LABEL_H3);
		
		final HorizontalLayout layoutRow1 = new HorizontalLayout();
		layoutRow1.setSpacing(true);
		
		final HorizontalLayout layoutRow2 = new HorizontalLayout();
		layoutRow2.setSpacing(true);
		
		final HorizontalLayout layoutRow3 = new HorizontalLayout();
		layoutRow3.setSpacing(true);
		
		final HorizontalLayout layoutRow4 = new HorizontalLayout();
		layoutRow4.setSpacing(true);
		
		final Label lbCardType = new Label("Chọn loại thẻ:");
		cbbCardType = new ComboBox();
		cbbCardType.addItems("VS","MC");
		cbbCardType.setItemCaption("VS", "VisaCardFiles");
		cbbCardType.setItemCaption("MC", "MasterCardFiles");
		cbbCardType.setValue("MC");
		cbbCardType.addValueChangeListener(event -> {
			String cardBrn = event.getProperty().getValue().toString();
			tfFolderPath.setValue(folderPathFiles(cardBrn));
			;
		});
		
		layoutRow1.addComponents(lbCardType, cbbCardType);
		layoutRow1.setComponentAlignment(lbCardType, Alignment.MIDDLE_LEFT);
		layoutRow1.setComponentAlignment(cbbCardType, Alignment.MIDDLE_LEFT);

		final Label lbFoderPath = new Label("Thư mục:");
		tfFolderPath = new TextField();
		tfFolderPath.setWidth(500, Unit.PIXELS);
		tfFolderPath.setHeight(100, Unit.PERCENTAGE);
		tfFolderPath.setValue(folderPathFiles(cbbCardType.getValue().toString()));
		
		
		layoutRow2.addComponents(lbFoderPath, tfFolderPath);
		layoutRow2.setComponentAlignment(lbFoderPath, Alignment.MIDDLE_LEFT);
		layoutRow2.setComponentAlignment(tfFolderPath, Alignment.MIDDLE_LEFT);
		
		
		final Label lbMonth = new Label("Tháng:");
		cbbMonth = new ComboBox();
		cbbMonth.addItems("01","02","03","04","05","06","07","08","09","10","11","12");
		int monthDefault= Calendar.getInstance().get(Calendar.MONTH) + 1;
		String sMonthDefault = String.format("%02d", monthDefault);
		cbbMonth.setValue(sMonthDefault);
		cbbMonth.setWidth(60, Unit.PIXELS);
		
		final Label lbYear = new Label("Năm:");
		cbbYear = new ComboBox();
		cbbYear.addItems("2013","2014","2015","2016","2017","2018","2019","2020");
		int yearDefault= Calendar.getInstance().get(Calendar.YEAR);
		String sYearDefault = String.format("%04d", yearDefault);
		cbbYear.setValue(sYearDefault);
		cbbYear.setWidth(80, Unit.PIXELS);
		
		
		btSendInScb = new Button("Gởi E-mail IN SCB");
		btSendInScb.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSendInScb.addClickListener(event -> {
			sendEmails();

		});
		
		btCheckFileNotSend = new Button("Kiểm tra SL file chưa gửi");
		btCheckFileNotSend.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btCheckFileNotSend.addClickListener(event -> {
			long count = 0;
			try (Stream<Path> files = Files.list(Paths.get(tfFolderPath.getValue() + "\\notsend"))) {
				count = files.count();
				String sNumOfFileNotSend = "<div style='color:red; font-weight: bold'>" + String.valueOf(count) + "</div>";
				lbNumOfFileNotSend.setContentMode(ContentMode.HTML);
			    lbNumOfFileNotSend.setValue(sNumOfFileNotSend);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.error(e);
			}
		});
		
		layoutRow3.addComponents(lbMonth, cbbMonth, lbYear, cbbYear, btSendInScb, btCheckFileNotSend);
		layoutRow3.setComponentAlignment(lbMonth, Alignment.MIDDLE_LEFT);
		layoutRow3.setComponentAlignment(cbbMonth, Alignment.MIDDLE_LEFT);
		layoutRow3.setComponentAlignment(lbYear, Alignment.MIDDLE_RIGHT);
		layoutRow3.setComponentAlignment(cbbYear, Alignment.MIDDLE_LEFT);
		layoutRow3.setComponentAlignment(btSendInScb, Alignment.MIDDLE_CENTER);
		layoutRow3.setComponentAlignment(btCheckFileNotSend, Alignment.MIDDLE_CENTER);
		
		
		lbDescNumOfFileNotSend = new Label("SL file chưa gửi:");
		lbNumOfFileNotSend = new Label();
		layoutRow4.addComponents(lbDescNumOfFileNotSend, lbNumOfFileNotSend);
		
		addComponent(lbTitle);
		addComponent(layoutRow1);
		addComponent(layoutRow2);
		addComponent(layoutRow3);
		addComponent(layoutRow4);
		
	}
	
	private String folderPathFiles(String cardBrn) {
		String sFolderPathFiles = "";
		switch(cardBrn) {
			case "VS": 
				sFolderPathFiles = configurationHelper.getPathFileRoot() + "\\VisaCardFiles\\INSCB";
				break;
			case "MC": 
				sFolderPathFiles = configurationHelper.getPathFileRoot() + "\\MasterCardFiles\\INSCB";
				break;
		}
		return sFolderPathFiles;
	}
	
	private void sendEmails() {
		long startProgTime = System.currentTimeMillis();
		LOGGER.info("Start program with time = " + startProgTime);
		
		List<EmailInfo> emailList = new ArrayList<EmailInfo>();
		
		String kySaoKe = cbbYear.getValue().toString() + cbbMonth.getValue().toString();
		
		List<CustomerEmailSend> listCustEmail =	customerEmailSendService.findAllByKyAndCardTypeAndStatus(kySaoKe, cbbCardType.getValue().toString(), "N");
    	LOGGER.info("Total " + listCustEmail.size() + " e-mail of " + kySaoKe + " to send");
		
		for(CustomerEmailSend cust : listCustEmail) {
			EmailInfo eml = new EmailInfo();
    		eml.setToRecipient(cust.getCustEmail().trim());
    		eml.setSubject("BANG SAO KE DIEN TU THE TIN DUNG SCB CREDIT CARD " + cust.getFullname().trim() + " " + cbbMonth.getValue() + "/" + cbbYear.getValue());
    		eml.setFileAttachment(folderPathFiles(cbbCardType.getValue().toString()) + "\\notsend\\" + cust.getFileName() + ".pdf");   		
//    		eml.setFileAttachment(folderPathFiles(cbbCardType.getValue().toString()) + "\\notsend\\" + "20190915800000313740" + ".pdf");
    		eml.setFullname(cust.getFullname());
    		emailList.add(eml);
		}
		
		int n = emailList.size() < 20 ? emailList.size() : 20; //Number of thread

		if(emailList.size()>0) {
			List<List<EmailInfo>> sublistEmails = choppedList(emailList,n);
			
			String threadName = "";
			for(int i=0;i<n;i++) {
				switch (i) 
				{
		            case 0: threadName = (i+1) + "st";
		                    break;
		            case 1: threadName = (i+1) + "nd";
		            		break;
		            case 2: threadName = (i+1) + "rd";
		    				break;
		    		default: threadName = (i+1) + "th";
				}
				EmailThread eml = new EmailThread(threadName, startProgTime, i, sublistEmails,folderPathFiles(cbbCardType.getValue().toString()) + "\\send");
				eml.start();
			}
		}
		else
		{
			LOGGER.info("Không có thông tin sao kê thẻ tín dụng nào để gởi e-mail");
		}
	}
	
	static <T> List<List<T>> choppedList(List<T> list, final int totalSublist) {
	    List<List<T>> parts = new ArrayList<List<T>>();
	    final int listSize = list.size();
	    final int numberItemOfSublist = listSize/totalSublist;
	    for (int i = 0; i < numberItemOfSublist*totalSublist; i += numberItemOfSublist) {
	    	if(i>=numberItemOfSublist*(totalSublist-1)) {
	    		parts.add(new ArrayList<T>(
    	            list.subList(i, Math.min(listSize, i + (listSize + 1 -(totalSublist*numberItemOfSublist)))))
    	        );
	    	}
	    	else
		        parts.add(new ArrayList<T>(
		            list.subList(i, Math.min(listSize, i + numberItemOfSublist)))
		        );
	    }
	    return parts;
	}

}
