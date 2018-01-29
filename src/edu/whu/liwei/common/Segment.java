package edu.whu.liwei.common;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class Segment {

	private Collection<String> stopwords;
	
	/**
	 * Constructor
	 */
	public Segment() {}
	
	/**
	 * Constructor with stopwords
	 * @param stopwordsPath
	 */
	public Segment(String stopwordsPath) {
		try {
			List<String> stopwordsList = Files.readAllLines(new File(stopwordsPath).toPath());
			stopwords = new HashSet<String>();
			stopwords.addAll(stopwordsList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> segment(String text) {
		List<String> list = new ArrayList<>();
	    StringReader re = new StringReader(text);
	    IKSegmenter ik = new IKSegmenter(re, true);
	    Lexeme lex;
	    String regex = "^[\u4e00-\u9fa5]+$"; // filter words contains numbers and charaters
	    try {
			while ((lex = ik.next()) != null) {
				String word = lex.getLexemeText();
				if (stopwords != null && stopwords.contains(word)) 
					continue;
				if (!word.matches(regex)) 
					continue;
				list.add(word);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return list;
	}
	
	
	public String segmentToString(String text) {
		StringBuilder builder = new StringBuilder();
		StringReader re = new StringReader(text);
	    IKSegmenter ik = new IKSegmenter(re, true);
	    Lexeme lex;
	    String regex = "^[\u4e00-\u9fa5]+$"; // filter words contains numbers and charaters
	    try {
			while ((lex = ik.next()) != null) {
				String word = lex.getLexemeText();
				if (stopwords != null && stopwords.contains(word)) 
					continue;
				if (!word.matches(regex)) 
					continue;
				builder.append(word + " ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return builder.toString();
	}
}
