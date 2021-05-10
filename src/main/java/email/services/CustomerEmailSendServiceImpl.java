package email.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import email.entities.CustomerEmailSend;
import email.repositories.CustomerEmailSendRepo;

@Service("customerEmailSendService")
@Transactional
public class CustomerEmailSendServiceImpl implements CustomerEmailSendService {

	@Autowired
	private CustomerEmailSendRepo customerEmailSendRepo;
	
	@Override
	public void save(String custEmail, String fullname, String cardno, String fileName, String ky, String cardType,
			String status) {
		CustomerEmailSend eml = new CustomerEmailSend();
		eml.setCustEmail(custEmail);
		eml.setFullname(fullname);
		eml.setCardno(cardno);
		eml.setFileName(fileName);
		eml.setKy(ky);
		eml.setCardType(cardType);
		eml.setStatus(status);
		customerEmailSendRepo.save(eml);
	}
	
	@Override
	public List<CustomerEmailSend> findAllByKyAndCardTypeAndStatus(String ky, String cardType, String status){
		return customerEmailSendRepo.findAllByKyAndCardTypeAndStatus(ky, cardType, status);
	}
	
	@Override
	public List<CustomerEmailSend> findAllByKyAndCardType(String ky, String cardType){
		return customerEmailSendRepo.findAllByKyAndCardType(ky, cardType);
	}

	@Override
	public void updateStatusByFilename(String fileName, String status) {
		customerEmailSendRepo.updateStatusByFilename(fileName, status);
	}
}
