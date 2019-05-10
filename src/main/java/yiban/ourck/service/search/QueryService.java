package yiban.ourck.service.search;

import java.util.*;

import org.springframework.lang.Nullable;

/**
 * 通用的Solr搜索引擎检索接口。
 * @author ourck
 */
public interface QueryService {

	/**
	 * 对指定文档集合进行查询
	 * @param from 返回自第from条目开始、to条目结束的搜索结果
	 * @param to 返回自第from条目开始、to条目结束的搜索结果
	 * @param words 关键词
	 * @return 查询结果。错误时应返回null。
	 */
	List<Map<String, String>> query(@Nullable Integer from,
								   @Nullable Integer to,
								   String... words);
	
}
