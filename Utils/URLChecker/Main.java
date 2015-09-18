import java.io.IOException;
import java.util.Map;

/**
 * Created by Troy Liu on 2015,九月,17, 22:10.
 */
public class Main {

	// 是否保存所有有连接问题的url
	public static final boolean saveAllMatterUrls = true;

	public static void main(String[] args) {
		long before = System.currentTimeMillis();
		String urlBundle = "https://raw.githubusercontent.com/tangqi92/Android-Tips/master/README.md";
		Map<String, String> matterUrls = NetUtils.getNotAvailableUrl(urlBundle);
		for (Map.Entry<String, String> entry : matterUrls.entrySet()) {
			System.out.println(entry.getValue() + "--->" + entry.getKey());
		}
		try {
			BadUrlsSaver.save(matterUrls, saveAllMatterUrls);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			long after = System.currentTimeMillis();
			System.out.println("耗时：" + (after - before) / 1000 + "s");
		}
	}
}
