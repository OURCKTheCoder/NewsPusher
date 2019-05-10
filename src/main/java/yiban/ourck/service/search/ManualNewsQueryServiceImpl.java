package yiban.ourck.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.beans.factory.annotation.Autowired;

public class ManualNewsQueryServiceImpl implements ManualQueryService {

	@Autowired
	private SolrClient chdNewsQClient;
	
	/**
	 * 按参数进行新闻查询。
	 * @param params Solr标准查询参数
	 * @return 查询结果
	 */
	public List<Map<String, String>> queryByParams(Map<String, String> params) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {
			QueryResponse response = chdNewsQClient.query(new MapSolrParams(params));
			for(SolrDocument doc : response.getResults()) {
				Map<String, String> data = new HashMap<>(); 
				for(String field : doc.getFieldNames()) {
					data.put(field, doc.getFieldValue(field).toString());
				}
				result.add(data);
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * 默认行为是查询最新的若干条新闻。
	 * @param from 自...开始
	 * @param off 偏移量 
	 * @param words 关键词。<b>请注意</b>，这里提供的关键词是完整的q参数。这意味着所提供的每个关键字必须形如"K:V"。
	 * @return 查询结果
	 */
	public List<Map<String, String>> query(Integer from, Integer off, String... words) {
		if(from == null) from = 0;
		if(off == null) off = 10;
		String qWord = null; 
		if(words == null || words.length == 0) qWord = "*:*";
		else {
			StringBuilder stb = new StringBuilder();
			for(int i = 0; i < words.length; i++) {
				stb.append(words[i]);
				if(i != words.length - 1) stb.append(" AND ");
			}
			qWord = stb.toString();
		}
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("q", qWord);
		m.put("sort", "createtime desc");
		m.put("start", "" + from);
		m.put("rows", "" + off);
		return queryByParams(m);
	}
}
