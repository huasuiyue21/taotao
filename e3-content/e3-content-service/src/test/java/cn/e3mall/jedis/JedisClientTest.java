package cn.e3mall.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClient;

public class JedisClientTest {

	@Test
	public void testClientTesst() throws Exception{
		//初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获得jedisClient对象
		JedisClient jedisClient = (JedisClient) applicationContext.getBean(JedisClient.class);
		jedisClient.set("mytest", "wo yao bian you qian ");
		String string = jedisClient.get("mytest");
		System.out.println(string);
	}
}
