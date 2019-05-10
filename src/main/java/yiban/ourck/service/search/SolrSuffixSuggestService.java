package yiban.ourck.service.search;

public interface SolrSuffixSuggestService {
	
	/**
	 * 向在线的Solr服务器发送从输入字符串提取的后缀词素以获取拼写建议<p>
	 * 按后缀提供建议。即从输入截取末尾的中文词组，按这个词组提供建议。
	 * @param str 关键词，可以直接是用户的输入。
	 * @return JSON结果
	 */
	String getSuggest(String str);
	
}
