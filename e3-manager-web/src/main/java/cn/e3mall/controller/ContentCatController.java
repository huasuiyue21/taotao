package cn.e3mall.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategorService;

/**
 * 内容分类管理Controller
 * <p>Title: ContentCatController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class ContentCatController {
	
	@Autowired
	private ContentCategorService contentCategorService;

	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCatList(@RequestParam(name="id",defaultValue="0") Long parentId){
		List<EasyUITreeNode> list =	contentCategorService.getContentCatList(parentId);
		return list;
	}
	
	/**
	 * 添加分类节点
	 */
	@RequestMapping(value="/content/category/create", method=RequestMethod.POST)
	@ResponseBody
	public E3Result createContentCategory(Long parentId,String name) {
		//调用服务添加节点
		E3Result e3Result = contentCategorService.addContentCategoty(parentId, name);
		return e3Result;
	}
	
	/**
	 * 重命名分类节点名称
	 */
	@RequestMapping(value="/content/category/update",method=RequestMethod.POST)
	@ResponseBody
	public E3Result updateContentCategory(Long id,String name) {
		//调用服务修改节点
		E3Result e3Result = contentCategorService.updateContentCategoty(id, name);
		return e3Result;
	}
	
	/**
	 * 删除分类节点
	 */
	@RequestMapping(value="/content/category/delete/",method=RequestMethod.POST)
	@ResponseBody
	public E3Result delContentCategory(HttpServletResponse response,Long id) throws Exception{
		//调用服务修改节点
		E3Result e3Result =	contentCategorService.delContentCategoty(id);
		return e3Result;
	}
}
