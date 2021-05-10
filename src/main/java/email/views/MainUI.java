package email.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import email.components.ImportEmailCustomer;
import email.components.SendEmailExternal;
import email.components.SendEmailInternal;

@SpringUI
@Theme("mytheme")
public class MainUI extends UI {

	//private static final Logger LOGGER = LoggerFactory.getLogger(MainUI.class);
	private static final Logger LOGGER = LogManager.getLogger(MainUI.class);

	private static final long serialVersionUID = 1383791387363344726L;
	
	private final transient Label lbHeader = new Label("GỬI EMAIL SAO KÊ THẺ TÍN DỤNG QUỐC TẾ!");
	private final transient FormLayout formLayout = new FormLayout();
	private final transient VerticalLayout mainLayout = new VerticalLayout();
	
	private final VerticalLayout headerLayout = new VerticalLayout(new Label());
	private final VerticalLayout footerLayout = new VerticalLayout();
	private final VerticalLayout contentLayout = new VerticalLayout();
	
	private final transient TabSheet tabsheet = new TabSheet();

	@Override
	protected void init(VaadinRequest request) {
		
		lbHeader.setStyleName(ValoTheme.MENU_TITLE);
		lbHeader.setHeight(1, Unit.CM);
		
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);

		final ImportEmailCustomer importEmailCustomer = new ImportEmailCustomer();
		final SendEmailInternal sendEmailInternal = new SendEmailInternal();
		final SendEmailExternal sendEmailExternal = new SendEmailExternal();
		
		VerticalLayout tabImport = new VerticalLayout();
		tabImport.addComponent(importEmailCustomer);
		tabsheet.addTab(tabImport, "Import file email KH");

		VerticalLayout tabInScb = new VerticalLayout();
		tabInScb.addComponent(sendEmailInternal);
		tabsheet.addTab(tabInScb,"Gửi email INSCB");
		
		VerticalLayout tabOutScb = new VerticalLayout();
		tabOutScb.addComponent(sendEmailExternal);
		tabsheet.addTab(tabOutScb,"Gửi email OUTSCB");
		
		formLayout.setWidth(80, Unit.PERCENTAGE);
		formLayout.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		
		formLayout.addComponent(lbHeader);
		formLayout.addComponent(tabsheet);
		
		
		//TANVH1 20190924
		mainLayout.addComponent(formLayout);
		mainLayout.setComponentAlignment(formLayout, Alignment.TOP_CENTER);
		contentLayout.setSizeFull();
		contentLayout.addComponent(mainLayout);
		
		final Label lbFooter = new Label();
		lbFooter.setContentMode(ContentMode.HTML);
		String sFooter = "<div style='color:lightgray; font-style: italic; padding-left:1em; padding-bottom:1px;'>" + "Ứng dụng được phát triển bởi P.KTT&VH&NHS" + "</div>";
		lbFooter.setValue(sFooter);
		
		footerLayout.addComponent(lbFooter);
		
		final VerticalLayout mainLayout = new VerticalLayout(headerLayout, contentLayout, footerLayout);
		mainLayout.setSizeFull();
        mainLayout.setExpandRatio(contentLayout, 1);
        setContent(mainLayout);
		
//		mainLayout.addComponent(formLayout);
//		mainLayout.setComponentAlignment(formLayout, Alignment.TOP_CENTER);
//		
//		setContent(mainLayout);
		
	}
	

}
