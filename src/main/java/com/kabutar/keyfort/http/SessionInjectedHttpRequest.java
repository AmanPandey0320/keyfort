package com.kabutar.keyfort.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import net.bytebuddy.asm.Advice.This;

/**
 * Custom httprequest class with session cookie
 */
public class SessionInjectedHttpRequest extends HttpServletRequestWrapper {

	private Cookie[] cookies;

	public SessionInjectedHttpRequest(HttpServletRequest request, Cookie sessionCookie) {
		super(request);
		
		//add session cookie
		Cookie[] originalCookies = request.getCookies();

		if (originalCookies == null) {
			this.cookies = new Cookie[] { sessionCookie };
		}else {
			this.cookies = new Cookie[originalCookies.length + 1];
			System.arraycopy(originalCookies, 0, this.cookies, 0, originalCookies.length);
			this.cookies[originalCookies.length] = sessionCookie;
		}

	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return this.cookies;
	}
	
	

}
