import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Troy Liu on 2015,九月,17, 22:11.
 */
public class UrlMatcher {
	public static List<String> getUrlStr(String response) {
		Pattern pattern = Pattern.compile("[a-zA-z]+://[^\\s]*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(response);
		List<String> urls = new ArrayList<String>();
		while (matcher.find()) {
			String tmp = matcher.group();
			int index = tmp.lastIndexOf(")");
			tmp = tmp.substring(0, index);
			urls.add(tmp);
		}
		return urls;
	}


	public static String getUrlTitle(String urlResponse) {
		Pattern pattern = Pattern.compile("<title>.*?</title>");
		Matcher matcher = pattern.matcher(urlResponse);
		String title = null;
		while (matcher.find()) {
			title = matcher.group();
		}
		return title;
	}
}
