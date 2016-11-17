package app.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HostsFileParser {
	public static HashSet<String> parse(String file, String nodeType)
			throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		JSONObject jsonFile = (JSONObject) parser.parse(new FileReader(file));
		JSONArray jsonArray = (JSONArray) jsonFile.get(nodeType);
		@SuppressWarnings("unchecked")
		Iterator<String> nodesIteraor = jsonArray.iterator();
		HashSet<String> nodes = new HashSet<String>();
		while (nodesIteraor.hasNext())
			nodes.add(nodesIteraor.next());
		return nodes;

	}
}
