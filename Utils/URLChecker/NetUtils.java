import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface ICallback {
	void finish(int code);

	void timeout();
}

/**
 * Created by Troy Liu on 2015,九月,17, 22:16.
 */
public class NetUtils {

	public static String getBundle(String urlMain) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			URL url = new URL(urlMain);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "text/html");
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("Content-Type", "utf-8");
			connection.setRequestProperty("Charset", "utf-8");
			connection.setRequestMethod("GET");
			InputStream inputStream;
			BufferedReader bufferedReader = null;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = connection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	public static Map<String, String> getNotAvailableUrl(String urlMain) {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		final Map<String, String> matterUrls = new HashMap<String, String>();
		String response = getBundle(urlMain);
		List<String> urlList = UrlMatcher.getUrlStr(response);
		for (final String url : urlList) {
			threadPool.execute(new ItemThread(url, new ICallback() {
				@Override
				public void finish(int code) {
					matterUrls.put(url, "BAD" + "[Code: " + code + "]");
				}

				@Override
				public void timeout() {
					matterUrls.put(url, "TIMEOUT");
				}
			}));
		}
		threadPool.shutdown();
		while (true) {
			if (threadPool.isTerminated()) {
				return matterUrls;
			}
		}
	}


}

class ItemThread implements Runnable {

	private final String url;
	private ICallback callback;

	public ItemThread(String url, ICallback callback) {
		this.callback = callback;
		this.url = url;
	}

	@Override
	public void run() {
		HttpURLConnection connection = null;
		try {
			URL tmp = new URL(url);
			connection = (HttpURLConnection) tmp.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
			connection.setRequestMethod("GET");
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(10000);
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("checking [" + url + "]" + "----->[OK]");
			} else {
				System.out.println("checking [" + url + "]" + "----->[BAD]" + "; Code--->" + responseCode);
				callback.finish(responseCode);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("checking [" + url + "]" + e.getMessage());
			callback.timeout();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
