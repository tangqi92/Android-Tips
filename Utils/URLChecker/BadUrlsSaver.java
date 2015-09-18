import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Troy Liu on 2015,九月,17, 23:21.
 */
public class BadUrlsSaver {

	/**
	 * 保存有问题的网址到repo目录下
	 *
	 * @param map
	 * @param isSaveAlLMatterUrls
	 * @throws IOException
	 */
	public static void save(Map<String, String> map, boolean isSaveAlLMatterUrls) throws IOException {
		File file = new File("..\\badUrls.txt");
		if (file.exists()) {
			System.out.println("删除先前文件: " + (file.delete() ? "成功" : "失败"));
		}
		FileWriter writer = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (isSaveAlLMatterUrls) {
				bufferedWriter.write(entry.getValue() + "\t\t" + entry.getKey() + "\r\n");
			} else {
				if (entry.getValue().equals("TIMEOUT")) {
					bufferedWriter.write(entry.getValue() + "\t\t" + entry.getKey() + "\r\n");
				}
			}
		}
		bufferedWriter.close();
	}

}
