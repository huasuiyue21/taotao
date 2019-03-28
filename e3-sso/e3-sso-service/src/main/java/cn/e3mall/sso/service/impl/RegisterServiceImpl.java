package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.RegisterService;

/**
 * 用户注册处理Service
 * <p>Title: RegisterServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class RegisterServiceImpl implements RegisterService {

	@Autowired
	private TbUserMapper tbUserMapper;
	
	@Override
	public E3Result checkData(String param, int type) {
		//根据不同的type生成不同的查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//1：用户名  2：手机号  3：邮箱
		if(type == 1) {
			criteria.andUsernameEqualTo(param);
		}else if(type == 2) {
			criteria.andPhoneEqualTo(param);
		}else if(type == 3) {
			criteria.andEmailEqualTo(param);
		}else {
			E3Result.build(400, "数据类型错误");
		}
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list != null && list.size()>0) {
			return E3Result.ok(false);
		}
		return E3Result.ok(true);
	}

	@Override
	public E3Result resgister(TbUser tbUser) {
		if("".equals(tbUser.getUsername()) || "".equals(tbUser.getPassword()) || 
				"".equals(tbUser.getPhone())) {
			return E3Result.build(400, "用户数据不完整");
		}
		E3Result result = checkData(tbUser.getUsername(), 1);
		if(!(boolean) result.getData()) {
			return E3Result.build(400, "此用户名已经被占用");
		}
		result = checkData(tbUser.getPhone(), 2);
		if(!(boolean)result.getData()) {
			return E3Result.build(400, "此号码已经被占用");
		}
		//补全pojo属性
		tbUser.setCreated(new Date());
		tbUser.setUpdated(new Date());
		//对密码进行md5加密
		String md5Pass = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
		tbUser.setPassword(md5Pass);
		tbUserMapper.insert(tbUser);
		return E3Result.ok();
	}

}
