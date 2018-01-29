package edu.whu.liwei.tests;

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

public class SegmentTest {

	private static List<String> segment(String text, Collection<String> stopwords) {
		Set<String> stopwordsSet = new HashSet<String>();
		stopwordsSet.addAll(stopwords);
		List<String> list = new ArrayList<>();
	    StringReader re = new StringReader(text);
	    IKSegmenter ik = new IKSegmenter(re, true);
	    Lexeme lex;
	    try {
			while ((lex = ik.next()) != null) {
				String word = lex.getLexemeText();
				if (!stopwordsSet.contains(word)) 
					list.add(word);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return list;
	}
	   
	public static void main(String[] args) throws IOException {
		String testString = "�����α༭�������꣩���������������Ƽ���˵�����в��෹����ࣾ��������������ģ����";
		List<String> stopwords = Files.readAllLines(new File("src\\resources\\stopwords.txt").toPath());
		System.out.println(segment(testString, stopwords));
	}
}
