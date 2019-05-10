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

public class FuzzyNewsQueryServiceImpl implements QueryService {
	
	@Autowired
	private String boostFml;
	
	@Autowired
	private String availableNewsFields;
	
	@Autowired
	private SolrClient chdNewsQClient;
	
	private List<Map<String, String>> queryByPages(Integer from, Integer off, String... words) throws IOException, SolrServerException {
		if(from == null) from = 0;
		if(off == null) off = 10;
		StringBuilder stb = new StringBuilder();
				
		for(int i = 0; i < words.length; i++) {
			stb.append(words[i]);
			if(i < words.length - 1) stb.append(" ");
		}
		
		// TODO 设置最好写到Spring配置文件中
		Map<String, String> params = new HashMap<String, String>();
		params.put("fl", availableNewsFields);
		params.put("defType", "edismax");
		params.put("qf", "title^2 content^1");
		params.put("start", "" + from);
		params.put("rows", "" + off);
		params.put("q", stb.toString());
		params.put("boost", boostFml);
		
		QueryResponse re = chdNewsQClient.query(new MapSolrParams(params));

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for(SolrDocument doc : re.getResults()) {
			Map<String, String> data = new HashMap<>(); 
			for(String field : doc.getFieldNames()) {
				data.put(field, doc.getFieldValue(field).toString());
			}
			result.add(data);
		}
		
		return result;
	}

	@Override
	public List<Map<String, String>> query(Integer from, Integer off, String... words) {
		List<Map<String, String>> result = null;
			try {
				result = queryByPages(from, off, words);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				System.err.println("Something happened on Solr server!");
			}
		return result;
	}
}
