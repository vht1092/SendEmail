package email.services;

import java.util.List;

import email.entities.CustomerEmailSend;

public interface CustomerEmailSendService {
	void save(String custEmail, String fullname, String cardno, String fileName, String ky, String cardType, String status);
	List<CustomerEmailSend> findAllByKyAndCardTypeAndStatus(String ky, String cardType, String status);
	List<CustomerEmailSend> findAllByKyAndCardType(String ky, String cardType);
	void updateStatusByFilename(String fileName, String status);
}
