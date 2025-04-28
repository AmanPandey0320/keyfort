package com.kabutar.keyfort.http;

/**
 * 
 */
public class Cookie {
	private String key;
	private String value;
	private Boolean httpOnly;
	private Boolean secure;
	private String sameSite;
	private Integer maxAge;
	
	public Cookie(String key, String value, Boolean httpOnly, Boolean secure, String sameSite, Integer maxAge) {
		this.key = key;
		this.value = value;
		this.httpOnly = httpOnly;
		this.secure = secure;
		this.sameSite = sameSite;
		this.maxAge = maxAge;
	}
	
	public String getCookie() {
		StringBuilder builder = new StringBuilder();
		
		//add cookie value
		builder.append(this.key+"="+this.value);
		
		//add Max-Age
		builder.append("; Max-Age="+Integer.toString(this.maxAge));
		
		//add Path
		builder.append("; Path=/;");
		
		//add secure
		if(this.secure) {
			builder.append("; Secure");
		}
		
		//add Http-only
		if(this.httpOnly) {
			builder.append("; HttpOnly");
		}
		
		builder.append("; SameSite="+this.sameSite);
		
		return builder.toString();
	}
	
	
}
