package email.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import email.entities.CustomerEmailSend;

@Repository
public interface CustomerEmailSendRepo extends CrudRepository<CustomerEmailSend, Long> {

	List<CustomerEmailSend> findAllByKyAndCardTypeAndStatus(String ky, String cardType, String status);
	
	List<CustomerEmailSend> findAllByKyAndCardType(String ky, String cardType);
	
	@Modifying
	@Query(value = "update CustomerEmailSend t set t.status=:status WHERE t.fileName=:filename")
	void updateStatusByFilename(@Param(value = "filename") String filename, @Param(value = "status") String status);
}
