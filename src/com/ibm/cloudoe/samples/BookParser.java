package com.ibm.cloudoe.samples;

/**
 * A library (currently comprising a single method) that parses a book
 * text and generates a Book entity.
 * 
 * @author akira
 */
public final class BookParser {

	public static final String TITLE_TAG = "Title: ";
	public static final String AUTHOR_TAG = "Author: ";
	
	/**
	 * A private default constructor in a final class means this
	 * class is actually a method lib. 
	 */
	private BookParser() {}
	
	/**
	 * Not a parser to be proud of, but enough for this proof of
	 * concept.
	 * 
	 * The beginning of the files have the format:
	 * 
	 * <pre>
	 * The Project Gutenberg EBook of Dubliners, by James Joyce
	 * 
	 * This eBook is for the use of anyone anywhere at no cost and with
	 * almost no restrictions whatsoever.  You may copy it, give it away or
	 * re-use it under the terms of the Project Gutenberg License included
	 * with this eBook or online at www.gutenberg.org
	 * 
	 * Title: Dubliners
	 * 
	 * Author: James Joyce
	 * 
	 * Posting Date: December 13, 2008 [EBook #2814]
	 * </pre>
	 * 
	 * But there is no promise from the Project Gutenberg that all files
	 * will follow this format. That is why we are only testing this
	 * system for books that are known to work.
	 * 
	 * @param bookString
	 * @return
	 */
	public static Book parse(String bookString) {
		
		/* 
		 * Text files from the project Gutenberg use \r\n to end lines,
		 * perhaps to please Windows users. So we need to check if lines
		 * end with \r after removing \n.
		 */
		
		int titleStart = bookString.indexOf(TITLE_TAG) + TITLE_TAG.length();
		int titleEnd = bookString.indexOf("\n", titleStart);
		String title = bookString.substring(titleStart, titleEnd);
		if (title.endsWith("\r")) {
			title = title.substring(0, title.length() - 1);
		}
		
		int authorStart = bookString.indexOf(AUTHOR_TAG) + AUTHOR_TAG.length();
		int authorEnd = bookString.indexOf("\n", authorStart);
		String author = bookString.substring(authorStart, authorEnd);
		if (author.endsWith("\r")) {
			author = author.substring(0, author.length() - 1);
		}
		
		return new Book(title, author);
	}
	
}
