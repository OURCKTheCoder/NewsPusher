package yiban.ourck.service.recommend;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.beans.factory.annotation.Autowired;

import yiban.ourck.service.search.QueryService;

public class SolrRecommendServiceImpl implements RecommendService {
	
	@Autowired
	private QueryService userQService;
	
	@Autowired
	private SolrClient userQClient;
	
	@Override
	public List<Map<String, String>> recommend(String userId) {
		List<Map<String, String>> recommList = new LinkedList<Map<String, String>>();
		
		Set<String> recordSet = getUserRecord(userId);
		if(recordSet == null) return null;				// If userId is invalid;
		if(recordSet.size() == 0) return recommList;	// If this user has no reading history.
		
		Set<String> total = null;
		try {
			total = getAllRecommendation(recordSet);
			
			// Calculating intersection & Convert to Map<>.
			total.removeAll(recordSet); // Remove all of the records this user has been read.
			for(String archiveId : total) {
				Map<String, String> recommItem = new HashMap<String, String>();
				recommItem.put("archive_id", archiveId);
				recommList.add(recommItem);
			}
		} catch (SolrServerException e) {
			System.err.println("Something happened on Solr server!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return recommList;
	}

	/**
	 * 获得用户的阅读历史。<p>
	 * TODO 当查询不到用户时 应该当作异常来处理吗？
	 * @param userId 用户ID
	 * @return 当查无此人时返回null，否则返回阅读文章记录的ID Set。
	 */
	private Set<String> getUserRecord(String userId) {
		// Get user(s) by ID.
		List<Map<String, String>> matchedUsers = 
				userQService.query(null, null, userId); // 1 result at most.
		if(matchedUsers.size() == 0) return null;
		
		// Get user's reading history & Save it to a Set.
		Set<String> recordSet = new HashSet<String>();
		Map<String, String> userInfo = matchedUsers.get(0);
		String record = userInfo.get("archive_ids");
		Collections.addAll(recordSet, record.split(" "));
		
		return recordSet;
	}
	
	/**
	 * 根据用户的阅读记录，使用搜索引擎获取所有推荐条目。
	 * @param readingRecord 阅读记录
	 * @return 所有推荐条目
	 * @throws SolrServerException 当Solr端出现异常
	 * @throws IOException IO异常
	 */
	private Set<String> getAllRecommendation(Set<String> readingRecord) throws SolrServerException, IOException {
		// Convert Set<> to "q" param.
		StringBuilder stb = new StringBuilder();
		int i = 0;
		for(String record : readingRecord) {
			stb.append(record);
			if(++i < readingRecord.size()) stb.append(" "); // TODO 可以考虑在以后数据量大了之后加上AND 要求更严格的匹配 达到更精准的推荐 
		}
		if(readingRecord.size() == 0) stb.append("*"); // 对于没有阅读记录的用户，随便找点东西推荐给他。即"q=*"。
		
		// Query other user's history by this user's.
		Map<String, String> qParams = new HashMap<>();
		qParams.put("df", "archive_ids");
		qParams.put("q", stb.toString());
		qParams.put("fl", "score, open_id, archive_ids");
		QueryResponse response = userQClient.query(new MapSolrParams(qParams));
		
		// Collect the results.
		Set<String> total = new HashSet<String>();
		for(SolrDocument doc : response.getResults()) {
			String singleUserRec = doc.getFieldValue("archive_ids").toString();
			Collections.addAll(total, singleUserRec.split(" "));
		}
		
		return total;
	}
}
