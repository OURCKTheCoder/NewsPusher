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

public class UserQueryServiceImpl implements QueryService {

	@Autowired
	private SolrClient userQClient;
	
	@Override
	public List<Map<String, String>> query(Integer from, Integer to, String... words) {
		List<Map<String, String>> results = null;
		String userId = words[0];

		QueryResponse response;
		try {
			response = executeUserQuery(userId);
			results = parseUserQueryResponse(response);
		} catch (SolrServerException e) {
			System.err.println("Something happened on Solr server!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return results;
	}

	private QueryResponse executeUserQuery(String userId) throws SolrServerException, IOException {
		// Initializing params.
		// TODO 固定参数考虑放到配置文件中！
		// TODO mm参数考虑一下？或者默认就是100%的匹配？
		Map<String, String> qParams = new HashMap<String, String>();
		qParams.put("q", userId);
		qParams.put("df", "open_id");
		
		// Execute querying.
		QueryResponse response = userQClient.query(new MapSolrParams(qParams));
		
		return response;
	}
	
	private List<Map<String, String>> parseUserQueryResponse(QueryResponse response) {
		// Collect results.
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		for(SolrDocument doc : response.getResults()) {
			Map<String, String> result = new HashMap<String, String>();
			
			for(String key : doc.getFieldNames()) {
				result.put(key, doc.getFieldValue(key).toString());
			}
			results.add(result);
		}
		return results;
	}
}
