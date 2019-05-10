package yiban.ourck.service.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

public class NewsMaintanceServiceImpl implements NewsMaintainceService {
	
	@Override
	public void coreDBSync(String coreName, String mask) throws Exception {
		if(coreName == null || mask == null) {
			StringBuilder stb = new StringBuilder();
			stb.append(" <p>- USAGE: java -jar DIHTrigger [CoreName] [MaskCode] <p>");
			stb.append(" - The [MaskCode] is a 2-digits decimal number, <p>");
			stb.append("   converted by [binary number] represents DIH request params <p>");
			stb.append("   indicates how this import task should work. <p><p>");
			stb.append(" - The [binary number] is a 6 bit digit, <p>");
			stb.append("   it takes forms like this: <p>");
			stb.append("       [isFull][isVerbose][isClean][isCommit][isOptimized][isDebug] <p><p>");
			stb.append(" - Every digit's value(1 or 0) refers to TRUE or FALSE of an arg. <p>");
			stb.append(" - For example: <p>");
			stb.append("       java -jar DIHTrigger 74 <p>");
			stb.append("   operates a [Full][Verbose][Clean][Commit][Not Optimized][Not Debug] import. <p><p>");
			stb.append(" - P.S. 74(D) = 111100(B). <p>");
			stb.append(" - Send regards to CHMOD & CHGRP <p>");
			
			throw new Exception(stb.toString());
		}
		
		boolean[] params = DIHTrigger.decimalMaskCodeToBooleanAry(mask);
		
		DIHTrigger t = new DIHTrigger(coreName);
		t.invoke(params[0], params[1], params[2], params[3], params[4], params[5]);
	}

	/**
	 * TODO DOIT!
	 */
	@Override
	public void cluster() {

	}

}

/**
 * 用于Solr DIH模块的触发器。
 * @deprecated 硬编码。
 * @author ourck
 */
class DIHTrigger  {
	private static final String SOLR_URL = "http://localhost:8983/solr";
	private static final String REQUEST_SUFFIX = "/dataimport?_=1528679701870&indent=on&wt=json"; // What's this number?
	
	private final String requested_core;
	private final String requestURL;
	
	public DIHTrigger(String coreName) {
		requested_core = coreName;
		
		StringBuilder stb = new StringBuilder();
		stb.append(SOLR_URL);
		stb.append("/").append(requested_core).append(REQUEST_SUFFIX);
		requestURL = stb.toString();
	}
	
	public void invoke(Boolean isFull,
						Boolean isVerbose,
						Boolean isClean,
						Boolean isCommit,
						Boolean isOptimized,
						Boolean isDebug)
	{
		Connection ct = Jsoup.connect(requestURL);
		Map<String, String> body = new HashMap<String, String>();

		ct.header("Referer", SOLR_URL)
		  .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) "
		  		+ "AppleWebKit/537.36 (KHTML, like Gecko) "
		  		+ "Chrome/63.0.3239.84 Safari/537.36");
		
		body.put("core", requested_core);
		body.put("name", "dataimport"); //???
		body.put("verbose", isVerbose.toString());
		body.put("clean", isClean.toString());
		body.put("commit", isCommit.toString());
		body.put("optimize", isOptimized.toString());
		body.put("debug", isDebug.toString());
		if(isFull) body.put("command", "full-import");
		else body.put("command", "delta-import");
		
		try {
			Response response = ct.ignoreContentType(true)
					.method(Method.POST)
					.data(body)
					.execute();
			System.out.println(response.body());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean[] decimalMaskCodeToBooleanAry(String args) {
		
		Integer digit, firstbit, secondbit;
		boolean[] params = new boolean[6];
		try {
			digit = Integer.parseInt(args);
			if(digit < 0 && digit > 99) throw new NumberFormatException();
			
			firstbit = digit / 10;
			secondbit = digit % 10;
			String binInfo;
			binInfo = Integer.toBinaryString(firstbit);
			StringBuilder stb = new StringBuilder();
			for(int i = 0; i < 3 - binInfo.length(); i++) {
				stb.append("0");
			}
			binInfo = stb.append(binInfo).toString();
			binInfo = binInfo.concat(Integer.toBinaryString(secondbit));
			
			char[] digits = binInfo.toCharArray();
			for(int i = 0; i < digits.length; i++) {
				if(digits[i] == '0') params[i] = false;
				else params[i] = true;
			}
		} catch (NumberFormatException e) {
			System.err.println("[!] Invalid Input!");
			System.exit(0);
		}
		
		return params; 
	}
	
}
