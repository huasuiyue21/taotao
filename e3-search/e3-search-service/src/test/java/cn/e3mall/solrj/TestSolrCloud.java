package cn.e3mall.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.junit.Test;

public class TestSolrCloud {

	@Test
	public void testAddDocument() throws Exception{
		//创建一个集群的连接，应该使用CloudSolrServer创建
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.134:2181,192.168.25.134:2182,192.168.25.134:2183");
		//zKHost：zookeeper的地址列表
		//设置一个defultCollection属性
		cloudSolrServer.setDefaultCollection("collection2");
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		//向文档中添加域
		document.setField("id", "solrcloud01");
		document.setField("item_title", "测试商品01");
		document.setField("item_price", 123);
		//把文件写入索引库
		cloudSolrServer.add(document);
		//提交
		cloudSolrServer.commit();
	}
	
	@Test
	public void testQueryDocument() throws Exception{
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.134:2181,192.168.25.134:2182,192.168.25.134:2183");
		cloudSolrServer.setDefaultCollection("collection2");
		//创建一个文档对象
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		QueryResponse queryResponse = cloudSolrServer.query(query);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		System.out.println("总记录数："+solrDocumentList.size());
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
		}
	}
}
