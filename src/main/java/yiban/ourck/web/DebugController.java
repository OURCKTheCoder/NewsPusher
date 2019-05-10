package yiban.ourck.web;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yiban.ourck.service.search.QueryService;

@RestController
@RequestMapping("/dbg")
public class DebugController {

	@Autowired
	QueryService manQueryService;
	
	@RequestMapping("/newest")
	public String getNewestNews() {
		List<Map<String, String>> lst = manQueryService.query(0, 10);
		
		JSONArray jobj = new JSONArray();
		int counter = 0;
		for(Map<String, String> obj : lst) {
			Map<String, String> data = obj;
			JSONObject jData = new JSONObject(data);
			jData.put("item_id" , ++counter);
			jobj.put(jData);
		}
		
		return jobj.toString();
	}
}
