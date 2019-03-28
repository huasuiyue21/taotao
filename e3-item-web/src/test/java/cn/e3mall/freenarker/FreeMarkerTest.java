package cn.e3mall.freenarker;

import static org.hamcrest.CoreMatchers.endsWith;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerTest {

	@Test
	public void FreeMarkerTest() throws Exception{
		//1、创建一个模版文件
		//2、创建按一个Configuration
		Configuration configuration = new Configuration(Configuration.getVersion());
		//3.设置模版文件保存的目录
		configuration.setDirectoryForTemplateLoading(new File("D:/eclipse_project/JAVAEE/e3-item-web/src/main/webapp/WEB-INF/file"));
		//4.模版文件的编码格式，默认是utf-8e
		configuration.setDefaultEncoding("utf-8");
		//5.加载一个模版文件，创建一个模版对象
//		Template template = configuration.getTemplate("hello.ftl");
		Template template = configuration.getTemplate("student.ftl");
		//6、创建一个数据集。可以是pojo也可以是map。推荐使用map
		Map data =new HashMap<>();
		data.put("hello", "hello freemarker!");
		//创建一个studnet对象
		Student student = new Student(1, "小米", 18, "ds");
		data.put("student", student);
		//7.创建一个Writer对象，指定输出文件路径及文件名。
//		Writer out = new FileWriter(new File("E:/学习视频/宜立方商城/e3mall/fremarker/hello.txt"));
		Writer out = new FileWriter(new File("E:/学习视频/宜立方商城/e3mall/fremarker/student.html"));
		//8.生成静态页面
		template.process(data, out);
		//9.关闭流
		out.close();
}
}
