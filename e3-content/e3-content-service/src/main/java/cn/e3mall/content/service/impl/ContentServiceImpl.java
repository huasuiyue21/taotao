package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mysql.fabric.xmlrpc.base.Data;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
import cn.e3mall.pojo.TbContentExample;
/**
 * 内容管理Service
 * <p>Title: ContentServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	
	@Override
	public E3Result addContent(TbContent content) {
		// 将内容数据插入到内容表
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入数据库
		contentMapper.insert(content);
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
		return E3Result.ok();
	}

	/**
	 * 根据内容分类id查询内容列表
	 * <p>Title: geTbContentListByCid</p>
	 * <p>Description: </p>
	 * @param id
	 * @return
	 * @see cn.e3mall.content.service.ContentService#geTbContentListByCid(long)
	 */
	@Override
	public List<TbContent> geTbContentListByCid(long cid) {
		//查询缓存
		try {
			//如果缓存中有直接响应结果
			String json = jedisClient.hget(CONTENT_LIST, cid+"");
			if (StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//如果没有查询数据库
		TbContentExample example = new TbContentExample();
		cn.e3mall.pojo.TbContentExample.Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		//把结果添加到缓存
		try {
			jedisClient.hset(CONTENT_LIST, cid+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
