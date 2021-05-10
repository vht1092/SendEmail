package email.entities;

import java.io.Serializable;

import javax.persistence.Entity;

import org.hibernate.annotations.Immutable;

//@Entity
//@Immutable
public class EmailInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String subject;
	private String toRecipient;
	private String ccRecipient;
	private String bccRecipient;
	private String fileAttachment;
	private String fullname;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getToRecipient() {
		return toRecipient;
	}
	public void setToRecipient(String toRecipient) {
		this.toRecipient = toRecipient;
	}
	public String getCcRecipient() {
		return ccRecipient;
	}
	public void setCcRecipient(String ccRecipient) {
		this.ccRecipient = ccRecipient;
	}
	public String getBccRecipient() {
		return bccRecipient;
	}
	public void setBccRecipient(String bccRecipient) {
		this.bccRecipient = bccRecipient;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	
}
