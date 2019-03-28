package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;

public interface ContentCategorService {

	List<EasyUITreeNode> getContentCatList(long parentId);
	
	E3Result addContentCategoty(long parentId,String name);
	
	E3Result updateContentCategoty(long id,String name);
	
	E3Result delContentCategoty(long id) throws Exception;
}
