package de.mpg.mpdl.auth.exception;

public class ErrorMessage {
	
	private int status;
	private String message;
	private String url;
	
	public ErrorMessage(int status, String message, String url) {
		this.status = status;
		this.message = message;
		this.url = url;
	}
	
	public ErrorMessage(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
