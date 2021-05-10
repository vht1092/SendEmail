package email.entities;

import java.io.Serializable;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
@Table(name = "SEND_EMAIL")
@NamedQuery(name = "CustomerEmailSend.findAll", query = "SELECT f FROM CustomerEmailSend f")
public class CustomerEmailSend implements Serializable {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;


	@Column(name = "CUSTEMAIL", nullable = false, length = 100)
	private String custEmail;
	
	@Column(name = "FULLNAME", nullable = false, length = 120)
	private String fullname;
	
	@Column(name = "CARDNO", nullable = false, length = 15)
	private String cardno;
	
	@Id
	@Column(name = "FILENAME", unique = true, nullable = false, length = 25)
	private String fileName;
	
	@Column(name = "KY", nullable = false, length = 6)
	private String ky;
	
	@Column(name = "CARD_BRN", nullable = false, length = 2)
	private String cardType;
	
	@Column(name = "STATUS_SEND", nullable = false, length = 1)
	private String status;

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getKy() {
		return ky;
	}

	public void setKy(String ky) {
		this.ky = ky;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	

}
