package com.webutils.app;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.utils.Log;
import com.webutils.WebContextUtil;
import com.webutils.WebUtilsConstants;
import com.webutils.abstracts.AbstractUser;

public class WebAppHttpInterceptor implements HandlerInterceptor {
	private static final Log LOG = new Log();
	public static final String JSESSIONID = "JSESSIONID";
	public static final String EMPTY_STRING = "";
	public static final int AUTH_ERROR_CODE = 401;
	public static final String AUTH_ERROR_MESSAGE = "Un-Authorised Request";
	public static final String BYPASS_URL_PREFIX = "/auth/";
	public static final String RESOURCES_URL_PREFIX = "/resources";
	public static final String COOKIES_PATH = "/";

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String jSessionId = extractSessionId(request.getCookies());
		String context = request.getRequestURI();
		request.setAttribute(JSESSIONID, jSessionId);
		LOG.trace("Called for " + context);
		AbstractUser user;
		if (context == null || context.contains(BYPASS_URL_PREFIX)
				|| context.contains(RESOURCES_URL_PREFIX)) {
			return true;
		} else {
			HttpSession session = request.getSession(true);
			user = (AbstractUser) session
					.getAttribute(WebUtilsConstants.CURRENT_SESSION_USER);
			if (user == null) {
				user = WebAppClient.getUser();
				session.setAttribute(WebUtilsConstants.CURRENT_SESSION_USER,
						user);
				user.setSessionID(session.getId());
				session.setMaxInactiveInterval(60);
			}
			// if (user == null) {
			// response.sendError(AUTH_ERROR_CODE, AUTH_ERROR_MESSAGE);
			// return false;
			// }
		}
		WebContextUtil.get().setUser(user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		try {
			AbstractUser user = WebContextUtil.get().getUser();
			if (user != null) {
				HttpSession session = request.getSession(true);
				if (WebContextUtil.get().wasUserValidated()) {

					session.setMaxInactiveInterval(user.getSessionTimout() * 60);
				} else if (WebContextUtil.get().wasUserInValidated()) {
					session.invalidate();
				}
				// UPDATE CAHCE
			}
		} catch (Exception e){
			LOG.error("afterComplettion", e);
		}finally {
			WebContextUtil.clear();
		}
	}

	public static String extractSessionId(Cookie[] cookies) {
		String jSessionID = EMPTY_STRING;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(JSESSIONID)) {
					jSessionID = cookie.getValue();
					break;
				}
			}
		}
		return jSessionID;
	}

	public static Cookie getSessionCookie(AbstractUser user) {
		return getSessionCookie(user, EMPTY_STRING);
	}

	public static Cookie getSessionCookie() {
		return getSessionCookie(null, EMPTY_STRING);
	}

	public static Cookie getSessionCookie(AbstractUser user, String defaultValue) {
		return getSessionCookie(user, defaultValue, COOKIES_PATH);
	}

	public static Cookie getSessionCookie(AbstractUser user,
			String defaultValue, String path) {
		Cookie cookie = null;
		if (user != null && user.isValid()) {
			cookie = new Cookie(JSESSIONID, user.getSessionID());
			cookie.setMaxAge(-1);
		} else {
			cookie = new Cookie(JSESSIONID, defaultValue);
			cookie.setMaxAge(0);
		}
		cookie.setPath(path);
		return cookie;
	}
}
