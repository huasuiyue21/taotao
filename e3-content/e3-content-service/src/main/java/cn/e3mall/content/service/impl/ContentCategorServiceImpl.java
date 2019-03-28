package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategorService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;
import cn.e3mall.pojo.TbContentExample;
/**
 * 内容分类管理Service
 * <p>Title: ContentCategorServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ContentCategorServiceImpl implements ContentCategorService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCatList(long parentId) {
		// 根据parentid查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> caList = contentCategoryMapper.selectByExample(example);
		//转化成EasyUITreeNode的列表
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : caList) {
			EasyUITreeNode node  = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到列表
			nodeList.add(node);
		}
		return nodeList;
	}

	@Override
	public E3Result addContentCategoty(long parentId, String name) {
		// 创建一个tb_content_category表对应的pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//设置pojo属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//1(正常),2(删除)
		contentCategory.setStatus(1);
		//默认排序是1
		contentCategory.setSortOrder(1);
		//新添加的节点一定是叶子节点
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的isparent属性。如果不是true改为true
		//根据parentid查询父节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()) {
			parent.setIsParent(true);
			//跟新到数据库
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果,返回E3Result，包含pojo
		return E3Result.ok(contentCategory);
	}

	@Override
	public E3Result updateContentCategoty(long id, String name) {
		// 创建一个tb_content_category表对应的pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//给修改条件
		contentCategory.setId(id);
		contentCategory.setName(name);
		//更新到数据库
		contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
		return E3Result.ok(contentCategory);
	}

	@Override
	public E3Result delContentCategoty(long id) throws Exception{
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andCreatedIsNotNull();
		List<TbContentCategory> tbList = contentCategoryMapper.selectByExample(example);
		for (TbContentCategory list : tbList) {
			if (list.getParentId().equals(id)) {
				
				System.out.println("不能删除");
				break;
			}else {
				contentCategoryMapper.deleteByPrimaryKey(list.getParentId());
			}
		}
		
//		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
//		if (tbContentCategory.getIsParent().equals(false)) {
//			contentCategoryMapper.deleteByPrimaryKey(id);
//		}
		return E3Result.ok();
	}

}
