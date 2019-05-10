package yiban.ourck.service.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.json.JSONObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.jsoup.Connection.Response;
import static org.jsoup.Connection.Method;

public class SolrSuffixSuggestServiceImpl implements SolrSuffixSuggestService {

	private static final String URL_ARG = "/suggest?q=";

	@Autowired
	private String solrUrl;
	
	@Autowired
	private String coreName; 
	
	@Autowired
	private Analyzer analyzer;

	/**
	 * 从用户输入中获得词素流的最末尾一个词
	 * @param str 用户输入的字串
	 * @return 获取的词素
	 * @throws IOException Tokenize失败时抛出
	 */
	private String getLastTerm(String str) throws IOException {
		TokenStream ts = analyzer.tokenStream("title", new StringReader(str));
		ts.reset();
		
		CharTermAttribute term  = ts.addAttribute(CharTermAttribute.class);
		StringBuilder stb = new StringBuilder();
		while(ts.incrementToken()) { // Tokenizing
			stb.append(term + " ");
		} 
		ts.end();
		ts.close();
		String[] terms = stb.toString().split(" ");
		
		return terms[terms.length - 1];
	}
	
	/**
	 * 向在线的Solr服务器发送单个词素以获取拼写建议
	 * @param word 中文词素
	 * @return JSON格式的建议内容
	 * @throws IOException 与服务器通信时发送GET请求失败
	 */
	private String getSuggestByWord(String word) throws IOException {
		Connection ct = Jsoup.connect(solrUrl + coreName + URL_ARG + word);
		Response re = ct.ignoreContentType(true)
						.method(Method.GET)
						.execute();
		return re.body();
	}
	
	/**
	 * 向在线的Solr服务器发送从输入字符串提取的后缀词素以获取拼写建议
	 * @param str 输入的字符串
	 * @return JSON格式的建议内容
	 * @throws IOException 与服务器通信时发送GET请求失败 或者无法获得词素
	 */
	public @Nullable String getSuggest(String str) {
		String term = null;
		try {
			term = getLastTerm(str);
			return getSuggestByWord(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return term;
	}
	
	/**
	 * 返回与这个SuffixSuggester绑定的文档集合信息和请求地址
	 * @return JSON对象，包含文档集合信息和请求地址
	 */
	@Override
	public String toString() {
		Map<String, String> info = new HashMap<String, String>();
		info.put("Core name", coreName);
		info.put("Request url", solrUrl + coreName + URL_ARG);
		JSONObject jobj = new JSONObject(info);
		return jobj.toString();
	}
	
}
