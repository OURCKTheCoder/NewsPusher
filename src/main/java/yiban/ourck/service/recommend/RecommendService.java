package yiban.ourck.service.recommend;

import java.util.List;
import java.util.Map;

public interface RecommendService {
	
	/**
	 * 根据用户ID进行基于协同过滤的推荐。
	 * @param userId 输入的id
	 * @return 推荐结果
	 */
	List<Map<String, String>> recommend(String userId); 
	
}
