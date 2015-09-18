import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Troy Liu on 2015,九月,17, 22:16.
 */
public class NetUtils {

	public static final int TIME_OUT = 1000;

	public static String getBundle(String urlMain) {
		StringBuilder stringBuilder = null;
		try {
			URL url = new URL(urlMain);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream inputStream;
			stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = null;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
			}
			bufferedReader.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	public static Map<String, String> getNotAvailableUrl(String urlMain) {
		Map<String, String> matterUrls = new HashMap<String, String>();
		String response = getBundle(urlMain);
		List<String> urlList = UrlMatcher.getUrlStr(response);
		for (String url : urlList) {
			checkUrl(url, matterUrls);
		}
		return matterUrls;
	}

	private static void checkUrl(String url, Map<String, String> map) {
		int responseCode = 0;
		try {
			System.out.print("checking [" + url + "]");
			URL tmp = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) tmp.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(TIME_OUT);
			responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("----->[OK]");
			} else {
				System.out.println("----->[BAD]" + "; Code--->" + responseCode);
				map.put(url, "BAD");
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[Time Out]");
			map.put(url, "TIMEOUT");
		}
	}

}
