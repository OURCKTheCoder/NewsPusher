package yiban.ourck.web;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yiban.ourck.service.recommend.RecommendService;
import yiban.ourck.service.search.QueryService;
import yiban.ourck.service.search.NewsMaintainceService;
import yiban.ourck.service.search.SolrSuffixSuggestService;

@RestController
@RequestMapping("/news")
public class NewsQueryController {
	
	@Autowired
	QueryService ordQueryService;

	@Autowired
	RecommendService newsRecommService;
	
	@Autowired
	SolrSuffixSuggestService suffixService;
	
	@Autowired
	NewsMaintainceService solrManService;
	
//	@RequestMapping("/")
//	public String index() {
//		return "SolrAssist is on your serve!";
//	}
	
	@RequestMapping(value = "/query", produces="application/json;charset=utf-8")
	public String query(String[] words, @Nullable Integer from, @Nullable Integer to) {
		List<Map<String, String>> lst = ordQueryService.query(from, to, words);
		
		JSONArray jobj = new JSONArray();
		int counter = 0;
		for(Map<String, String> obj : lst) {
			Map<String, String> data = obj;
			JSONObject jData = new JSONObject(data);
			jData.put("item_id" , ++counter);
			jobj.put(jData);
		}
		
		return jobj.toString(2); // TODO Json存在转义问题：会把引号转化为Unicode编码的 #值#。https://ourck.top/?p=554
	}
	
	@RequestMapping("/chssuggest")
	public String chssuggest(String key) {
		String suggest = null;
		suggest = suffixService.getSuggest(key);
		return suggest;
	}
	
	@RequestMapping("/recommend")
	public String recommend(String userId) {
		List<Map<String, String>> result = newsRecommService.recommend(userId);
		
		JSONObject data = new JSONObject();
		data.put("docs", result);
		data.put("success", true);
		
		return data.toString();
	}
	
	@RequestMapping("/dbsync")
	public String dbsync(String mask) {
		JSONObject data = new JSONObject();
		try {
			solrManService.coreDBSync("news_on_chd", mask);
			solrManService.coreDBSync("users_on_chd", mask);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("success", false);
			data.put("detail", e.getMessage());
			return data.toString();
		}
		data.put("success", true);
		return data.toString();
	}
	
//	// TODO 缺少R的必须组件、需要重新建库
//	@RequestMapping("/update")
//	public String update() {
//		
//	}
	
}
