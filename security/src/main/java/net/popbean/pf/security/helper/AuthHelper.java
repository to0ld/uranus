package net.popbean.pf.security.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.popbean.pf.mvc.bind.support.SessionArgumentResolver;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * 暂时先放session中，稳定后，放到redis中，以timeout时间设定生命周期
 * @author to0ld
 *
 */
public class AuthHelper {
	public static void setClientEnv(HttpServletRequest request, SecuritySession client) {
		HttpSession session = request.getSession(true);
		session.setAttribute(SessionArgumentResolver.TOKEN, client);
	}

	public static SecuritySession getClientEnv(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (SecuritySession) session.getAttribute(SessionArgumentResolver.TOKEN);
		} else {
			return null;
		}
	}

	public static void removeClientEnv(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(SessionArgumentResolver.TOKEN);
		}
	}
}
