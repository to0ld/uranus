package net.popbean.pf.security.service.impl;

import java.util.concurrent.TimeUnit;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.id.service.IDGenService;
import net.popbean.pf.mvc.interceptor.access.TokenType;
import net.popbean.pf.security.helper.ErrorConst;
import net.popbean.pf.security.service.AccessTokenService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
/**
 * 
 * @author to0ld
 *
 */
@Service("service/pf/token/redis")
public class AccessTokenServiceRedisImpl extends AbstractBusinessService implements AccessTokenService {
	//
	@Value("${is.dev}")
	protected boolean isDev = true;//是否为开发环境，应该分为prod/dev/test三个stage
	//
	@Autowired
	@Qualifier("redisTemplate")
	protected RedisTemplate<String, Integer> operations;
	@Autowired
	@Qualifier("service/pf/id/uuid")
	IDGenService<String> idgenService;
	public static final String COLL_TOKEN = "pb_access_token";
	//
	@Override
	public String gen(TokenType type) throws BusinessError {
		if(isDev){//开发状态就不用了吧
			return null;
		}
		//修改一下做法，改用
		String ret = idgenService.gen(null);
		if(TokenType.Session.equals(type)){//如果是sessoin级别，提供30分钟延时
			operations.opsForValue().set(ret, 1, 30, TimeUnit.MINUTES);//FIXME 我就不定义参数了，直接写死一个变量吧，反正改配置文件也是要上传的，将来等config service上了，我就整	
		}else{
			operations.opsForValue().set(ret, 1);
		}
		return ret;
	}

	@Override
	public void auth(TokenType type, String token) throws BusinessError {
		if(isDev){//开发状态就不用了吧
			return ;
		}
		if(StringUtils.isBlank(token)){
			ErrorBuilder.createBusiness().msg("没有token无法比较");
		}
		if(TokenType.Session.equals(type)){//不删除
			//找到就算完事
			boolean ret = operations.opsForValue().getOperations().hasKey(token);
			if(!ret){
				ErrorBuilder.createSys().msg("没有找到指定的token("+token+")").execute();
			}
		}else{//删除
			long ret = delete(token);
			if(ret==0){
				//没有找到，那就是木有啊，得抛出错误
				ErrorBuilder.createSys().code(ErrorConst.BAD_REQUEST).msg("没有找到指定的token("+token+")").execute();
			}
		}
	}
	private Long delete(String key) {//为了世界和平，支持返回删除数量
		final byte[] rawKey = rawKey(key);
		return operations.execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) {
				Long ret = connection.del(rawKey);
				return ret;
			}
		}, true);
	}
	
	private byte[] rawKey(Object key) {
		Assert.notNull(key, "non null key required");
		if (operations.getKeySerializer() == null && key instanceof byte[]) {
			return (byte[]) key;
		}
		RedisSerializer<Object> serializer = (RedisSerializer<Object>)operations.getKeySerializer();
		return serializer.serialize(key);
	}
}
