package yiban.ourck.service.search;

import java.util.List;
import java.util.Map;

public interface ManualQueryService extends QueryService {

	List<Map<String, String>> queryByParams(Map<String, String> param);
}
