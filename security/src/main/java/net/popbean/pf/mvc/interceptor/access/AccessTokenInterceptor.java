package net.popbean.pf.mvc.interceptor.access;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.helper.ErrorConst;
import net.popbean.pf.security.service.AccessTokenService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.AbstractView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author to0ld
 *
 */
public class AccessTokenInterceptor extends HandlerInterceptorAdapter {
	public static final String X_ACCESS_TOKEN = "X-ACCESS-TOKEN";
	@Value("${is.dev}")
	protected boolean isDev = true;//是否为开发环境，应该分为prod/dev/test三个stage
	@Autowired
	@Qualifier("service/pf/token/redis")
	AccessTokenService tokenService;
	//
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			if(WeixinAuth.isWeixin(request)){// 通过微信认证，允许(这是意外的小插曲，先这么放吧)，到时候再处理吧
				return WeixinAuth.auth(request);
			}
			TokenType token_type = getTokenType(handler);
			if(token_type == null){//如果没有，就不扯淡了
				return true;
			}
			if(isData(request)){//目前只检查了使用request body方式传递的
				String request_token = request.getHeader(X_ACCESS_TOKEN);
				tokenService.auth(token_type, request_token);							
			}
		} catch (Exception e) {
			//如果是bad_request，那么就redirect
			if(e instanceof BusinessError){
				String code = ((BusinessError)e).code;
				if(ErrorConst.BAD_REQUEST.equals(code)){//如果是指定的类型，那就抛出吧
					redirect(request);//FIXME 多么适合去callback换java8啊
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		//FIXME 在这里主要是为了种token,生成，并且写入到mav变量中
		TokenType token_type = getTokenType(handler);
		if(token_type == null){
			return;
		}
		if(!isData(request)){//暂定只有view才需要token吧
			String token = tokenService.gen(token_type);//要是报错了，还是得处理的哈swiftly
			modelAndView.addObject(X_ACCESS_TOKEN, token);			
		}
	}
	/**
	 * 需要加token
	 * @param handler
	 * @return
	 */
	private TokenType getTokenType(Object handler){
		HandlerMethod ha = (HandlerMethod)handler;
		AccessToken at = ha.getMethod().getDeclaredAnnotation(AccessToken.class);
		if(at == null){
			return null;
		}
		return at.type();
	}
	private boolean isData(HttpServletRequest request){
		if(request.getContentType()!=null && request.getContentType().indexOf(MediaType.APPLICATION_JSON.toString())!=-1){
			return true;
		}
		return false;
	}
	private void redirect(HttpServletRequest request) throws ModelAndViewDefiningException {
		//FIXME 如果是数据请求就返回json，如果页面请求就返回界面
		if(isData(request)){//应该返回一个json object
			throw new ModelAndViewDefiningException(new ModelAndView(new AbstractView() {
				@Override
				protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.addHeader("Content-Type",  "application/json;charset=UTF-8");
					//
					JSONObject ret = JO.gen("status",0,"error","无效访问，或者无标识头吧");
					//
					response.getWriter().write(JSON.toJSONString(ret));
					response.flushBuffer();
				}
			}));
		}else{
			throw new ModelAndViewDefiningException(new ModelAndView("error/bad_request"));
		}
	}
	//
	private static class WeixinAuth{
		public static boolean isWeixin(HttpServletRequest request){
			String signature = request.getParameter("signature");
	        String timestamp = request.getParameter("timestamp");  
	        String nonce = request.getParameter("nonce");
	        if (signature != null && timestamp != null && nonce != null){
	        	return true;
	        }
	        return false;
		}
		public static boolean auth(HttpServletRequest req){// FIXME 应该使用微信认证，但项目在war包下，没有引入，暂时使用是否带微信认证的头来判断
			return true;
		}
	}
}
