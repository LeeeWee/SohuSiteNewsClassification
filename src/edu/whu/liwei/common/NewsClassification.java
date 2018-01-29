package edu.whu.liwei.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;  
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList; 

public class NewsClassification {
	
	// regex expression
	private static final String noPatternString = "<docno>(.*?)</docno>";
	private static final String urlPatternString = "<url>(.*?)</url>";
	private static final String titlePatternString = "<contenttitle>(.*?)</contenttitle>";
	private static final String contentPatternString = "<content>(.*?)</content>";
	
	private static List<DocBean> docList;
	private static Map<String, Integer> urlMap = new HashMap<String, Integer>();
	
	/**
	 * get all lines in file by given encoding
	 */
	public static String readEntire(String fileName, String encoding) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}
	
	
	/**
	 * get docBeans in given xml file
	 * @param fileName input fileNmae
	 */
	public static List<DocBean> getDocBeansByDom(String fileName) throws Exception {
		// initial dbFatory and db
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder db = dbFactory.newDocumentBuilder();
        // get file inputStream by 'utf-8' encoding
		ByteArrayInputStream inputStream = new ByteArrayInputStream(readEntire(fileName, "gbk").getBytes("utf-8"));
		// parse given file, and return a Document Object
		Document document = db.parse(inputStream); 
		// get all element with label 'doc', and return a NodeList object by order
		NodeList docNodeList = document.getElementsByTagName("doc"); 
		List<DocBean> docBeanList = new ArrayList<DocBean>();

		// iterate docNodeList
		for (int i = 0; i < docNodeList.getLength(); i++) {
			DocBean docBean = new DocBean();
			// get the i-th docBean Node
			Node node = docNodeList.item(i);
			// get the attributes of the i-th node
			NamedNodeMap nameNodeMap = node.getAttributes();
			// get the child nodes
			NodeList cList = node.getChildNodes();
			for (int j = 0; j < cList.getLength(); j++) {
				Node cNode = cList.item(j);
				if (node.getNodeName().equals("docno"))
					docBean.setNo(cNode.getFirstChild().getTextContent());
				if (node.getNodeName().equals("url"))
					docBean.setUrl(cNode.getFirstChild().getTextContent());
				if (node.getNodeName().equals("contenttitle"))
					docBean.setTitle(cNode.getFirstChild().getTextContent());
				if (node.getNodeName().equals("content"))
					docBean.setContent(cNode.getFirstChild().getTextContent());
			}
			docBeanList.add(docBean);
		}
		return docBeanList;
	}
	
	/**
	 * get docBeans in given xml file, read file line by line
	 */
	public static List<DocBean> getDocBeans(String fileName) throws Exception {
		List<DocBean> docBeanList = new ArrayList<DocBean>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "gbk"));
		String line = "";
		int docCount = 0;
		while ((line = reader.readLine()) != null) {
			if (line.equals("<doc>")) {
				DocBean docBean = new DocBean();
				while (!(line = reader.readLine()).equals("</doc>")) {
					if (Pattern.matches(noPatternString, line)) 
						docBean.setNo(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
					else if (Pattern.matches(urlPatternString, line)) {
						docBean.setUrl(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
						// put index url into url map
						String indexUrl = line.substring(line.indexOf("http://") + 7, line.indexOf("com/")) + "com";
						if (!urlMap.containsKey(indexUrl)) 
							urlMap.put(indexUrl, 0);
						urlMap.put(indexUrl, urlMap.get(indexUrl) + 1); 
					}
					else if (Pattern.matches(titlePatternString, line)) 
						docBean.setTitle(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
					else if (Pattern.matches(contentPatternString, line)) 
						docBean.setContent(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
				}
				// set category, if category is empty, ignoring.
				docBean.setCategoryByUrl();
				if (docBean.getCategory().equals(""))
					continue;
				docCount ++;
				docBeanList.add(docBean);
			} else 
				continue;
			if (docCount % 20000 == 0) 
				System.out.println(docCount + " docBeans processed.");
		}
		reader.close();
		System.out.println("Get " + docCount + " docBeans from file '" + fileName + "'");
		return docBeanList;
	}
	
	/**
	 * get docBeans in all files under given directory
	 */
	public static List<DocBean> getDocBeansUnderDirectory(String dirPath) throws Exception {
		List<DocBean> docBeanList = new ArrayList<DocBean>();
		// get files under given directory
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			System.out.println(dirPath + " isn't a directory path!");
			return docBeanList;
		} 
		File[] files = dir.listFiles();
		int count = 0;
		// for file in dir, put docs in file to docBeansList
		for (File file : files) {
			String filePath = file.getAbsolutePath();
			if (filePath.endsWith(".txt")) {
				docBeanList.addAll(getDocBeans(filePath));
				count++;
			}
		}
		System.out.println(count + " files processed!");
		return docBeanList;
	}
	
	/**
	 * classify docBeans by category
	 * @param docBeanList input docBeans list
	 * @return map category to docBean List
	 */
	public static HashMap<String, List<DocBean>> classificationByCategory(List<DocBean> docBeanList) {
		HashMap<String, List<DocBean>> classifiedDocs = new HashMap<String, List<DocBean>>();
		for (DocBean docBean : docBeanList) {
			String category = docBean.getCategory();
			if (!classifiedDocs.containsKey(category)) {
				List<DocBean> docs = new ArrayList<DocBean>();
				classifiedDocs.put(category, docs);
			}
			classifiedDocs.get(category).add(docBean);
		}
		return classifiedDocs;
	}
	
	/**
	 * save docBeans to text file by category
	 */
	public static void saveToDirByCategory(String outputDir, List<DocBean> docBeanList) {
		System.out.println("Saving to " + outputDir);
		// if output dir doesn't exist, create it
		File dir = new File(outputDir);
		if (!dir.exists()) 
			dir.mkdirs();
		// classy docBeans by category
		HashMap<String, List<DocBean>> classifiedDocs = classificationByCategory(docBeanList);
		for (Map.Entry<String, List<DocBean>> entry : classifiedDocs.entrySet()) {
			// create text file of temp category
			try {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(new File(dir, entry.getKey())), "utf-8"));
				for (DocBean docBean : entry.getValue()) {
					if (!docBean.getContent().equals("")) {
						writer.write(docBean.getContent().trim());
						writer.newLine();
					}
				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * save docBeans to text file by category, split data into training and test data by testPercentage
	 */
	public static void saveToDirByCategory(String outputDir, List<DocBean> docBeanList, Double testPercentage) {
		System.out.println("Saving training and test data to " + outputDir);
		// shuffle docBean list
		Collections.shuffle(docBeanList);
		// split training and test data
		int splitIndex = (int)((1 - testPercentage) * docBeanList.size());
		List<DocBean> trainingData = docBeanList.subList(0, splitIndex);
		List<DocBean> testData = docBeanList.subList(splitIndex, docBeanList.size());
		saveToDirByCategory(new File(outputDir, "train").getAbsolutePath(), trainingData);
		saveToDirByCategory(new File(outputDir, "test").getAbsolutePath(), testData);
		System.out.println("Finished saving!");
	}

	/**
	 * save docBeans's category and content to text file, each line's form: category,	content
	 * @param output
	 * @param docBeanList
	 */
	public static void saveCategoryAndContent(String output, List<DocBean> docBeanList) {
		System.out.println("Saving category and content to " + output);
		File outputFile = new File(output);
		outputFile.getParentFile().mkdirs();
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
			for (DocBean docBean : docBeanList) {
				if (!docBean.getContent().equals("")) {
					writer.write(docBean.getCategory() + "," + docBean.getContent().trim());
					writer.newLine();
				}
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCategoryAndSegContent(String output, List<DocBean> docBeanList, Segment segment) {
		System.out.println("Saving category and content to " + output);
		File outputFile = new File(output);
		outputFile.getParentFile().mkdirs();
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
			int total = docBeanList.size();
			int count = 0, lineNumber = 1;
			for (DocBean docBean : docBeanList) {
				count ++;
				if (count % 20000 == 0) 
					System.out.println(count + "/" + total + " lines handled!");
				if (!docBean.getContent().equals("")) {
					writer.write(lineNumber + ":" + docBean.getCategory() + "," + segment.segmentToString(docBean.getContent()));
					writer.newLine();
					lineNumber++;
				}
			}
			writer.close();
			System.out.println("Finished saving!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveCategoryAndContent(String outputDir, List<DocBean> docBeanList, Double testPercentage) {
		System.out.println("Saving training and test data to " + outputDir);
		// shuffle docBean list
		Collections.shuffle(docBeanList);
		// split training and test data
		int splitIndex = (int)((1 - testPercentage) * docBeanList.size());
		List<DocBean> trainingData = docBeanList.subList(0, splitIndex);
		List<DocBean> testData = docBeanList.subList(splitIndex, docBeanList.size());
		saveCategoryAndContent(new File(outputDir, "train.dat").getAbsolutePath(), trainingData);
		saveCategoryAndContent(new File(outputDir, "test.dat").getAbsolutePath(), testData);
	}
	
	public static void saveCategoryAndSegContent(String outputDir, List<DocBean> docBeanList, Double testPercentage, Segment segment) {
		System.out.println("Saving training and test data to " + outputDir);
		// shuffle docBean list
		Collections.shuffle(docBeanList);
		// split training and test data
		int splitIndex = (int)((1 - testPercentage) * docBeanList.size());
		List<DocBean> trainingData = docBeanList.subList(0, splitIndex);
		List<DocBean> testData = docBeanList.subList(splitIndex, docBeanList.size());
		saveCategoryAndSegContent(new File(outputDir, "train.dat").getAbsolutePath(), trainingData, segment);
		saveCategoryAndSegContent(new File(outputDir, "test.dat").getAbsolutePath(), testData, segment);
		System.out.println("Finished saving!");
	}
	
	
	public static void main(String[] args) throws Exception {
//		String fileName = "C:\\Users\\Liwei\\Desktop\\NaiveBayesClassifier\\SogouCS.reduced";
//		String outputdir = "C:\\Users\\Liwei\\Desktop\\NaiveBayesClassifier\\sohu_news";
//		String outputdir2 = "C:\\Users\\Liwei\\Desktop\\NaiveBayesClassifier\\sohu_newsgroups";
		String stopwords = "src/resources/stopwords.txt";
		Segment segment = new Segment(stopwords);
		String fileName = "/Users/liwei/Documents/postgraduate-classes/并行计算技术/SogouCS.reduced";
		String outputdir = "/Users/liwei/Documents/postgraduate-classes/并行计算技术/sohu_news";
		String outputdir2 = "/Users/liwei/Documents/postgraduate-classes/并行计算技术/sohu_newsgroups";
		docList = getDocBeansUnderDirectory(fileName);
		System.out.println("url map size: " + urlMap.size());
		Map<String, Integer> sortedUrlMap = new LinkedHashMap<String, Integer>();
		// sort url by value
		urlMap.entrySet().stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue())
			.forEachOrdered(x -> sortedUrlMap.put(x.getKey(), x.getValue()));
		for (Map.Entry<String, Integer> entry : sortedUrlMap.entrySet()) 
			System.out.println(entry.getKey() + " : " + entry.getValue());
		// save to local by category
		saveToDirByCategory(outputdir2, docList, 0.1);
		saveCategoryAndSegContent(outputdir, docList, 0.1, segment);
	}
}
