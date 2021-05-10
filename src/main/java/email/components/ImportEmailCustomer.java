package email.components;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import email.SpringConfigurationValueHelper;
import email.SpringContextHelper;
import email.entities.CustomerEmailSend;
import email.repositories.CustomerEmailSendRepo;
import email.services.CustomerEmailSendService;
import email.views.MainUI;

import org.apache.poi.ss.usermodel.CellType;

@SpringComponent
@Scope("prototype")
@Component
public class ImportEmailCustomer extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ImportEmailCustomer.class);
	
	private final transient Label lbTitle = new Label("IMPORT FILE EMAIL KHÁCH HÀNG TRƯỚC KHI GỬI EMAIL");
	private TextField tfFolderPath;
	private final transient ComboBox cbbCardType;
	private final transient ComboBox cbbMonth;
	private final transient ComboBox cbbYear;
	private final transient Button btChooseFile;
	private final transient Button btImportFileMail;
	private final transient Button btCheckNumOfEmail;
	private final transient Button btCheckNumOfFile;
	private final transient Button btExportLogFile;
	private transient long sNumofFileInScb;
	private transient long sNumofFileOutScb;
	private transient long sNumberOfEmail;
	private TextField tfImportFileName;
	
	private SpringConfigurationValueHelper configurationHelper;
	
	private CustomerEmailSendService customerEmailSendService;
	
	public ImportEmailCustomer() {
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
		
		final Label lbImportInfo = new Label();
		lbImportInfo.setContentMode(ContentMode.HTML);
		lbImportInfo.setValue(htmlImportInformation(sNumofFileInScb, sNumofFileOutScb, sNumberOfEmail).toString());
		
		final Label lbCardType = new Label("Chọn loại thẻ:");
		cbbCardType = new ComboBox();
//		cbbCardType.setCaption("Chọn loại thẻ:");
		cbbCardType.addItems("VS","MC");
		cbbCardType.setItemCaption("VS", "VisaCardFiles");
		cbbCardType.setItemCaption("MC", "MasterCardFiles");
		cbbCardType.setValue("MC");
		
		cbbCardType.addValueChangeListener(event -> {
			String cardBrn = event.getProperty().getValue().toString();
			folderPathFiles(cardBrn);
			sNumofFileInScb = 0;
			sNumofFileOutScb = 0;
			sNumberOfEmail = 0;
			lbImportInfo.setValue(htmlImportInformation(sNumofFileInScb, sNumofFileOutScb, sNumberOfEmail).toString());
		});
		

		final Label lbFoderPath = new Label("Thư mục:");
		tfFolderPath = new TextField();
//		tfFolderPath.setCaption("Thư mục:");
		tfFolderPath.setWidth(500, Unit.PIXELS);
		tfFolderPath.setHeight(70, Unit.PERCENTAGE);
		tfFolderPath.setDescription("Thư mục chứa file sao kê gởi e-mail theo loại thẻ (VS/MC)");
		folderPathFiles(cbbCardType.getValue().toString());
		
		layoutRow1.addComponents(lbCardType, cbbCardType,lbFoderPath, tfFolderPath);
		layoutRow1.setComponentAlignment(lbCardType, Alignment.MIDDLE_LEFT);
		layoutRow1.setComponentAlignment(cbbCardType, Alignment.MIDDLE_LEFT);
		layoutRow1.setComponentAlignment(lbFoderPath, Alignment.MIDDLE_RIGHT);
		layoutRow1.setComponentAlignment(tfFolderPath, Alignment.MIDDLE_LEFT);
		
		
		final Label lbMonth = new Label("Tháng sao kê:");
		cbbMonth = new ComboBox();
//		cbbMonth.setCaption("Tháng sao kê:");
		cbbMonth.addItems("01","02","03","04","05","06","07","08","09","10","11","12");
		int monthDefault= Calendar.getInstance().get(Calendar.MONTH) + 1;
		String sMonthDefault = String.format("%02d", monthDefault);
		cbbMonth.setValue(sMonthDefault);
		cbbMonth.setWidth(60, Unit.PIXELS);
		
		final Label lbYear = new Label("Năm:");
		cbbYear = new ComboBox();
//		cbbYear.setCaption("Năm:");
		cbbYear.addItems("2013","2014","2015","2016","2017","2018","2019","2020");
		int yearDefault= Calendar.getInstance().get(Calendar.YEAR);
		String sYearDefault = String.format("%04d", yearDefault);
		cbbYear.setValue(sYearDefault);
		cbbYear.setWidth(80, Unit.PIXELS);
		
		
		final Label lbFileEmail = new Label("FILE EMAIL KH:");
		final Label lbFileChooseStatus = new Label("No file choosen");
		
		btChooseFile = new Button("Choose file");
		btChooseFile.setStyleName(ValoTheme.BUTTON_SMALL);
		
		btChooseFile.addClickListener(event -> {
		
//			StreamSource source = new StreamSource()
//		    {
//		        public java.io.InputStream getStream()
//		        {
//			        try
//			        {
//			        	FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
//			            dialog.setMode(FileDialog.LOAD);
//			            dialog.setVisible(true);
//			            String file = dialog.getFile();
//			            System.out.println(file + " chosen.");
//			            
//			            String helpFilePath =  "/datafiles/";
//			            File helpFile = new File(helpFilePath);
//			            FileInputStream helpFileInputStream = new FileInputStream(helpFile);
//			            return helpFileInputStream;
//			        } catch (FileNotFoundException e)
//			        {
//			            // TODO Auto-generated catch block
//			            e.printStackTrace();
//			        }
//			        return null;
//		        }
//		    };
//		    String filename =  "/datafiles/help.pdf";
//		    StreamResource resource = new StreamResource(source, filename);
//		    resource.setMIMEType("application/pdf");
//		    resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);
//		    BrowserWindowOpener opener = new BrowserWindowOpener(resource);
//		    opener.extend(helpButton);
		});
		
		btImportFileMail = new Button("IMPORT FILE MAIL KH");
		btImportFileMail.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btImportFileMail.setDescription("Import file e-mail khách hàng theo kỳ (tháng/năm) & loại thẻ (VS/MC)");
		btImportFileMail.addClickListener(event -> {
	        try {
	        	File f = new File(configurationHelper.getPathFileRoot() + "\\" + tfImportFileName.getValue() + ".xlsx");
				ByteArrayInputStream bis = new ByteArrayInputStream(Files.readAllBytes(Paths.get(f.getPath())));

		        Workbook workbook;
	        	
	            if (f.getName().endsWith("xls")) {
	                workbook = new HSSFWorkbook(bis);
	            } else if (f.getName().endsWith("xlsx")) {
	                workbook = new XSSFWorkbook(bis);
	            } else {
	                throw new IllegalArgumentException("Received file does not have a standard excel extension.");
	            }
	            
	            Sheet sheet = workbook.getSheetAt(0);
	            for (Row row : sheet) {
	               if (row.getRowNum() > 0 && cbbYear.getValue().equals(row.getCell(4).toString().substring(0, 4))
	            	&& cbbMonth.getValue().equals(row.getCell(4).toString().substring(4, 6))) {
	            	   String kySaoKe = row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC ? String.valueOf((int)row.getCell(4).getNumericCellValue()) : String.valueOf(row.getCell(4));
	            	   String cardNo = row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC ? String.valueOf((int)row.getCell(2).getNumericCellValue()) : String.valueOf(row.getCell(2));
	            	   String fileName = row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC ? String.valueOf((int)row.getCell(3).getNumericCellValue()) : String.valueOf(row.getCell(3));
	            	   
	            	   LOGGER.info("Rownum " + row.getRowNum() + " inserted - " + "Cust email: " + row.getCell(0) + ", Full name: " + row.getCell(1) 
	       	   			+ ", Card no: " + cardNo + ", File name: " + fileName + ", Ky: " + kySaoKe
	       	   			+ ", Card type: " + row.getCell(5) + ", Status: " + row.getCell(6));
	            	   
	            	   customerEmailSendService.save(row.getCell(0).toString(), row.getCell(1).toString(), cardNo, fileName, kySaoKe, row.getCell(5).toString(), row.getCell(6).toString());
	            	   
	               }
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	            LOGGER.error(e);
	        }
            
		});
		
		
		final Label lbImportFileName = new Label("File name:");
		tfImportFileName = new TextField();
		tfImportFileName.setDescription("File name (không bao gồm .xlsx) ở thư mục " + configurationHelper.getPathFileRoot() + " để import vào database theo kỳ (năm/tháng) & loại thẻ (VS/MC)");
		
		
		lbFileEmail.setVisible(false);
		btChooseFile.setVisible(false);
		lbFileChooseStatus.setVisible(false);
		
		layoutRow2.addComponents(lbMonth, cbbMonth, lbYear, cbbYear, lbFileEmail, btChooseFile, lbImportFileName, tfImportFileName, lbFileChooseStatus, btImportFileMail);
		layoutRow2.setComponentAlignment(lbMonth, Alignment.MIDDLE_LEFT);
		layoutRow2.setComponentAlignment(cbbMonth, Alignment.MIDDLE_LEFT);
		layoutRow2.setComponentAlignment(lbYear, Alignment.MIDDLE_RIGHT);
		layoutRow2.setComponentAlignment(cbbYear, Alignment.MIDDLE_LEFT);
		layoutRow2.setComponentAlignment(lbFileEmail, Alignment.MIDDLE_RIGHT);
		layoutRow2.setComponentAlignment(btChooseFile, Alignment.MIDDLE_CENTER);
		layoutRow2.setComponentAlignment(lbFileChooseStatus, Alignment.MIDDLE_LEFT);
		layoutRow2.setComponentAlignment(btImportFileMail, Alignment.MIDDLE_RIGHT);
		layoutRow2.setComponentAlignment(lbImportFileName, Alignment.MIDDLE_CENTER);
		layoutRow2.setComponentAlignment(tfImportFileName, Alignment.MIDDLE_CENTER);
		
		
		btCheckNumOfEmail = new Button("KIỂM TRA SL EMAIL KH IMPORT");
		btCheckNumOfEmail.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btCheckNumOfEmail.addClickListener(event -> {
			String kySaoKe = cbbYear.getValue().toString() + cbbMonth.getValue().toString();
			sNumberOfEmail = customerEmailSendService.findAllByKyAndCardTypeAndStatus(kySaoKe, cbbCardType.getValue().toString(),"N").size();
			lbImportInfo.setValue(htmlImportInformation(sNumofFileInScb, sNumofFileOutScb, sNumberOfEmail).toString());
		});
		
		btCheckNumOfFile = new Button("KIỂM TRA SL FILE SAO KÊ");
		btCheckNumOfFile.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btCheckNumOfFile.setDescription("Kiểm tra số lượng file sao kê theo loại thẻ (VS/MC)");
		btCheckNumOfFile.addClickListener(event -> {
			try {
				Stream<Path> filesInSCB = Files.list(Paths.get(tfFolderPath.getValue() + "\\INSCB\\notsend"));
				Stream<Path> filesOutSCB = Files.list(Paths.get(tfFolderPath.getValue() + "\\OUTSCB\\notsend"));
				sNumofFileInScb = filesInSCB.count();
				sNumofFileOutScb = filesOutSCB.count();
				lbImportInfo.setValue(htmlImportInformation(sNumofFileInScb, sNumofFileOutScb, sNumberOfEmail).toString());
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error(e);
			}
		});
		
		btExportLogFile = new Button("XUẤT FILE LOG GỞI SAO KÊ");
		btExportLogFile.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btExportLogFile.setDescription("Xuất file log gởi sao kê theo kỳ (tháng/năm) & loại thẻ (VS/MC)");
		btExportLogFile.addClickListener(event -> {
			XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Export Worksheet");
	        
	        String kySaoKe = cbbYear.getValue().toString() + cbbMonth.getValue().toString();
	        List<CustomerEmailSend> listCustEmail = customerEmailSendService.findAllByKyAndCardType(kySaoKe, cbbCardType.getValue().toString());
	        
	        int rowNum = 0;
	        System.out.println("Creating excel");
	        LOGGER.info("Creating excel");

	        if(rowNum == 0) {
            	Object[] rowHeader = {"CUSTEMAIL","FULLNAME","CARDNO","FILENAME","KY","CARD_BRN","STATUS_SEND"};
            	int colNum = 0;	 
            	Row row = sheet.createRow(rowNum++);         	
            	for (Object field : rowHeader) {
            		Cell cell = row.createCell(colNum++, org.apache.poi.ss.usermodel.CellType.STRING);
            		cell.setCellValue((String)field);
            	}           	
	        }
	       
	        for (CustomerEmailSend cust: listCustEmail) {
	            Row row = sheet.createRow(rowNum++);
	            int colNum = 0;	        
	            Class<?> c = cust.getClass();
		        Field[] fields = c.getDeclaredFields();
		        
		        for(Field field : fields ){
		        	Cell cell = row.createCell(colNum++);
		             try {
		            	 System.out.println(field.getName());
		            	 field.setAccessible(true);
		            	 Object objField =  field.get(cust);
		                  if (objField instanceof String) {
			                    cell.setCellValue((String) field.get(cust) );
			                } else if (objField instanceof Integer) {
			                    cell.setCellValue((Integer) field.get(cust) );
			                }
		                  
		             } catch (IllegalArgumentException e1) {
		            	 e1.printStackTrace();
		            	 LOGGER.error(e1);
		             } catch (IllegalAccessException e1) {
		            	 e1.printStackTrace();
		            	 LOGGER.error(e1);
		             }
		        }
	        }
			
	        try {
	            FileOutputStream outputStream = new FileOutputStream(configurationHelper.getPathFileRoot() + "\\" + cbbCardType.getValue() + "_" + kySaoKe + ".xlsx");
	            workbook.write(outputStream);
	            workbook.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	            LOGGER.error(e);
	        } catch (IOException e) {
	            e.printStackTrace();
	            LOGGER.error(e);
	        }

	        System.out.println("Done");
	        LOGGER.info("Export excel file " + cbbCardType.getValue() + "_" + kySaoKe + ".xlsx completed");
		});
		
		layoutRow3.addComponents(btCheckNumOfEmail, btCheckNumOfFile, btExportLogFile);
		
		addComponent(lbTitle);
		addComponent(layoutRow1);
		addComponent(layoutRow2);
		addComponent(layoutRow3);
		addComponent(lbImportInfo);
		
		
	}
	
	private void folderPathFiles(String cardBrn) {
		switch(cardBrn) {
			case "VS": 
				Path pathVisa = Paths.get(configurationHelper.getPathFileRoot() + "\\VisaCardFiles");
				Path pathVisaInScb = Paths.get(pathVisa + "\\INSCB");
				Path pathVisaInScbSend = Paths.get(pathVisaInScb + "\\send");
				Path pathVisaInScbNotsend = Paths.get(pathVisaInScb + "\\notsend");
				Path pathVisaOutScb = Paths.get(pathVisa + "\\OUTSCB");
				Path pathVisaOutScbSend = Paths.get(pathVisaOutScb + "\\send");
				Path pathVisaOutScbNotsend = Paths.get(pathVisaOutScb + "\\notsend");
				
				try {
					if (!Files.exists(pathVisa)) {
						Files.createDirectory(pathVisa);
					}
					if (!Files.exists(pathVisaInScb)) {
						Files.createDirectory(pathVisaInScb);
					}
					if (!Files.exists(pathVisaInScbSend)) {
						Files.createDirectory(pathVisaInScbSend);
					}
					if (!Files.exists(pathVisaInScbNotsend)) {
						Files.createDirectory(pathVisaInScbNotsend);
					}
					if (!Files.exists(pathVisaOutScb)) {
						Files.createDirectory(pathVisaOutScb);
					}
					if (!Files.exists(pathVisaOutScbSend)) {
						Files.createDirectory(pathVisaOutScbSend);
					}
					if (!Files.exists(pathVisaOutScbNotsend)) {
						Files.createDirectory(pathVisaOutScbNotsend);
					}
					tfFolderPath.setValue(pathVisa.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOGGER.error(e);
				}
				break;
			case "MC": 
				Path pathMaster = Paths.get(configurationHelper.getPathFileRoot() + "\\MasterCardFiles");
				Path pathMasterInScb = Paths.get(pathMaster + "\\INSCB");
				Path pathMasterInScbSend = Paths.get(pathMasterInScb + "\\send");
				Path pathMasterInScbNotsend = Paths.get(pathMasterInScb + "\\notsend");
				Path pathMasterOutScb = Paths.get(pathMaster + "\\OUTSCB");
				Path pathMasterOutScbSend = Paths.get(pathMasterOutScb + "\\send");
				Path pathMasterOutScbNotsend = Paths.get(pathMasterOutScb + "\\notsend");
				
				try {
					if (!Files.exists(pathMaster)) {
						Files.createDirectory(pathMaster);
					}
					if (!Files.exists(pathMasterInScb)) {
						Files.createDirectory(pathMasterInScb);
					}
					if (!Files.exists(pathMasterInScbSend)) {
						Files.createDirectory(pathMasterInScbSend);
					}
					if (!Files.exists(pathMasterInScbNotsend)) {
						Files.createDirectory(pathMasterInScbNotsend);
					}
					if (!Files.exists(pathMasterOutScb)) {
						Files.createDirectory(pathMasterOutScb);
					}
					if (!Files.exists(pathMasterOutScbSend)) {
						Files.createDirectory(pathMasterOutScbSend);
					}
					if (!Files.exists(pathMasterOutScbNotsend)) {
						Files.createDirectory(pathMasterOutScbNotsend);
					}
					tfFolderPath.setValue(pathMaster.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOGGER.error(e);
				}
				break;
		}
	}
	
	private StringBuilder htmlImportInformation(long sNumofFileInScb, long sNumofFileOutScb, long sNumberOfEmail) {
		return new StringBuilder("<table style='width:100%,text-align:left'>"
				+ "<tr style='height: 30px'><td>SL file INSCB: </td><td style='color:red; font-weight: bold'>" + sNumofFileInScb + "</td></tr>"
				+ "<tr style='height: 30px'><td>SL file OUTSCB: </td><td style='color:red; font-weight: bold'>" + sNumofFileOutScb + "</td></tr>"
				+ "<tr style='height: 30px'><td>SL email KH: </td><td style='color:red; font-weight: bold'>" + sNumberOfEmail + "<td></tr></table>");
	}
}
