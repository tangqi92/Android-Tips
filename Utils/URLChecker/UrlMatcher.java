import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Troy Liu on 2015,九月,17, 22:11.
 */
public class UrlMatcher {
	public static List<String> getUrlStr(String response) {
		Pattern pattern = Pattern.compile("(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");
		Matcher matcher = pattern.matcher(response);
		List<String> urlList = new ArrayList<String>();
		while (matcher.find()) {
			urlList.add(matcher.group());
		}
		return urlList;
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
