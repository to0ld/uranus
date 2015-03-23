package net.popbean.pf.config.vo;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.security.vo.SecuritySession;

/**
 * 用于存储吧
 * 
 * @author to0ld
 *
 */
public interface IConfigValueStore extends IValueObject {
	public void merge(ConfigModel model, SecuritySession session);
}
