package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

/**
 * 购物车处理Controller
 * <p>Title: CarController</p>3223edqwewqeqweqw
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class CarController {

	@Autowired
	private ItemService itemService;
	@Autowired
	private CartService cartService;
	@Value("${COOKIE_CART_EXPIPE}")
	private Integer COOKIE_CART_EXPIPE;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,@RequestParam(defaultValue="1")Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		//判断用户是否登陆
		TbUser user = (TbUser) request.getAttribute("user");
		if(user != null) {
			//保存到服务端
			cartService.addCart(user.getId(), itemId, num);
			return "cartSuccess";
		}
		//从cookie中取购物车列表
		List<TbItem> cartList = getCarListFromCookie(request);
		//判断商品在商品列表中是否存在
		boolean falh = false;
		for(TbItem tbItem:cartList) {
			if(tbItem.getId() == itemId.longValue()) {
				falh = true;
				//如果存在数量相加
				tbItem.setNum(tbItem.getNum() + num);
				break;
			}
		}
		//如果不存在，根据商品id查询商品。得到一个TbItem对象
		if (!falh) {
			TbItem tbItem = itemService.getItemById(itemId);
			//取一张图片
			String image = tbItem.getImage();
			if(StringUtils.isBlank(image)) {
				tbItem.setImage(image.split(",")[0]);
			}
			//设置购买商品数量
			tbItem.setNum(num);
			//把商品添加到商品列表
			cartList.add(tbItem);
		}
		//写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIPE, true);
		//返回添加成功页面
		return "cartSuccess";
	} 	 

	
	/**
	 * 从cookie中取购物车列表来处理
	 */
	private List<TbItem> getCarListFromCookie(HttpServletRequest request){
		String json = CookieUtils.getCookieValue(request,"cart",true);
		//判断json是否为空
		if(StringUtils.isBlank(json)) {
			return new ArrayList<>();
		}
		//把json转换成商品列表
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	/**
	 * 展示购物车列表
	 * <p>Title: showCatLisst</p>
	 * <p>Description: </p>
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String showCatLisst(HttpServletRequest request,HttpServletResponse response) {
		//从cookie中取购物车列表
		List<TbItem> cartList = getCarListFromCookie(request);
		//判断用户是否为登陆状态
		TbUser user = (TbUser) request.getAttribute("user");
		//如果是登陆状态
		if(user != null) {
			//从cookie中取购物车列表
			//如果不为抠门那个，把cookie中的购物车商品和服务端的购物车商品合并
			cartService.mergeCart(user.getId(), cartList);
			//从cookie中把购物车删除
			CookieUtils.deleteCookie(request, response, "cart");
			//从服务端取购物车列表
			cartList  = cartService.getCartList(user.getId());
		}
		
		//把列表传给页面
		request.setAttribute("cartList", cartList);
		return "cart";
	}
	
	/**
	 * 跟新购物车数量
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateCartNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		//判断用户是否为登陆状态
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			cartService.updateCartNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		//从cookie中取购物车列表
		List<TbItem> cartList = getCarListFromCookie(request);
		//遍历商品列表找到对应的商品
		for (TbItem tbItem : cartList) {
			if (tbItem.getId().longValue() == itemId) {
				//更新数量
				tbItem.setNum(num);
				break;
			}
		}
		//写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIPE, true);
		//返回成功
		return E3Result.ok();
	}
	
	/**
	 * 删除购物车商品
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request,
			HttpServletResponse response) {
		//判断用户是否为登陆状态
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			cartService.deleteCarItem(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		List<TbItem> cartList = getCarListFromCookie(request);
		for (TbItem tbItem : cartList) {
			if(tbItem.getId().longValue() == itemId) {
				//删除商品
				cartList.remove(tbItem);
				break;
			}
		}
		//写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIPE, true);
		//返回逻辑视图
		return "redirect:/cart/cart.html";
	}
}
