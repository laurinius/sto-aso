/*******************************************************************************
 * Copyright (C) 2015, 2019 Dave Kor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.kor.admiralty;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class Globals {

	private static final StyleSheet STYLESHEET_GLOBAL = new HTMLEditorKit().getStyleSheet();
	private static final String CSS_TRAIT = 
			"h1{color:white;font-size:110%;font-weight:bold;font-style:italic;line-height:1.0;margin-bottom:0em}" + 
			"h2{color:white;font-size:100%;font-weight:bold;line-height:1.0;margin-bottom:0em}" + 
			"p{color:white}" + 
			"ul{color:white}";
	
	public static final StyleSheet STYLESHEET_TRAIT = customStyleSheet(CSS_TRAIT);
	public static final int MAX_ASSIGNMENTS = 3;
	public static final int SOLVER_DEPTH = 10;
	public static final long UPDATE_INTERVAL = 7L * 24L * 60L * 60L * 1000L; // Check for updates every 7 days
	public static final String FILENAME_ADMIRALS = "admirals.xml";
	public static final String FILENAME_SHIPCACHE = "data/ships.csv";
	public static final String FILENAME_SHIPCACHE_USER = "ships.csv";
	public static final String FILENAME_EVENTS = "data/events.csv";
	public static final String FILENAME_ASSIGNMENTS = "data/assignments.csv";
	public static final String FILENAME_RENAMED = "data/renamed.csv";
	public static final String FILENAME_TRAITS = "data/traits.csv";
	public static final String FILENAME_ICONS = "data/icons.zip";
	public static final String DIRECTORY_ICONS_USER = "icons";
	public static final String FILENAME_ICONCACHE = "iconcache.zip";
	public static final String FILENAME_NEWCACHE = "newiconcache.zip";
	public static final String FILENAME_MD5_EXT = ".md5";
	public static final String DATA_FILES[] = { FILENAME_SHIPCACHE, FILENAME_EVENTS, FILENAME_ASSIGNMENTS,
												FILENAME_RENAMED, FILENAME_TRAITS, FILENAME_ICONS};
	public static boolean DEBUG;

	static {
		try {
			DEBUG = System.getenv("USERDOMAIN").equals("ENTERPRISE");
		} catch (Throwable cause) {
			DEBUG = false;
		}
	};

	private static StyleSheet customStyleSheet(String css) {
		StyleSheet stylesheet = new StyleSheet();
		stylesheet.addStyleSheet(STYLESHEET_GLOBAL);
		stylesheet.addRule(css);
		return stylesheet;
	}
	
	public static boolean isTimestampFresh(long time) {
		return time > (System.currentTimeMillis() - UPDATE_INTERVAL);
	}
	
	public static boolean isTimestampStale(long time) {
		return !isTimestampFresh(time);
	}
	
}
