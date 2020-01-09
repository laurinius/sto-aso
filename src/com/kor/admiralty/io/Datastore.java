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
package com.kor.admiralty.io;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.kor.admiralty.Configuration;
import com.kor.admiralty.Globals;
import com.kor.admiralty.beans.AdmAssignment;
import com.kor.admiralty.beans.Admiral;
import com.kor.admiralty.beans.Admirals;
import com.kor.admiralty.beans.Event;
import com.kor.admiralty.beans.Ship;

import static com.kor.admiralty.Globals.*;
import static com.kor.admiralty.ui.resources.Strings.ExceptionDialog.*;

public class Datastore {

	private static final Logger logger = Logger.getGlobal();
	private static SortedMap<String, File> FILES = new TreeMap<String, File>();
	private static SortedMap<String, Ship> SHIPS = new TreeMap<String, Ship>();
	private static SortedMap<String, Event> EVENTS = new TreeMap<String, Event>();
	private static SortedMap<String, AdmAssignment> ASSIGNMENTS = new TreeMap<String, AdmAssignment>();
	private static SortedMap<String, String> RENAMED = new TreeMap<String, String>();
	private static SortedMap<String, String> TRAITS = new TreeMap<String, String>();
	private static SortedMap<String, ImageIcon> ICONCACHE = new TreeMap<String, ImageIcon>();
	private static SortedMap<String, BufferedImage> ICONS = new TreeMap<>();
	private static Admirals ADMIRALS = null;
	private static boolean ICONCACHE_CHANGED = false;

	private static transient JAXBContext admiralsContext;
	private static transient Marshaller admiralsMarshaller;
	private static transient Unmarshaller admiralsUnmarshaller;
	private static final File FOLDER_CURRENT = file(".");

	static {
		init();
	}
	
	public static File getCurrentFolder() {
		return FOLDER_CURRENT;
	}

	public static SortedMap<String, Ship> getAllShips() {
		if (SHIPS.isEmpty()) {
			loadShipDatabase();
		}
		return SHIPS;
	}
	
	public static SortedMap<String, Event> getEvents() {
		if (EVENTS.isEmpty()) {
			loadEvents();
		}
		return EVENTS;
	}

	public static SortedMap<String, AdmAssignment> getAssignments() {
		if (ASSIGNMENTS.isEmpty()) {
			loadAssignments();
		}
		return ASSIGNMENTS;
	}
	
	public static SortedMap<String, String> getRenamedShips() {
		if (RENAMED.isEmpty()) {
			loadRenamedShips();
		}
		return RENAMED;
	}
	
	public static SortedMap<String, String> getTraits() {
		if (TRAITS.isEmpty()) {
			loadTraits();
		}
		return TRAITS;
	}
	
	public static SortedMap<String, ImageIcon> getCachedIcons() {
		if (ICONCACHE.isEmpty()) {
			loadCachedIcons();
		}
		return ICONCACHE;
	}

	public static SortedMap<String, BufferedImage> getIcons() {
		if (ICONS.isEmpty()) {
			loadIcons();
		}
		return ICONS;
	}
	
	public static boolean isDataFilesStale() {
		for (String filename : Globals.DATA_FILES) {
			if (!file(filename).exists()) {
				return true;
			}
		}
		long now = System.currentTimeMillis();
		long lastUpdated = Configuration.getDataUpdateLastUpdated();
		return now > (lastUpdated + (86_400_000L * Configuration.getDataUpdateInterval()));
	}

	public static void setIconCacheChanged(boolean change) {
		ICONCACHE_CHANGED = change;
	}

	public static void preserveIconCache() {
		if (ICONCACHE_CHANGED) {
			saveCachedIcons();
		}
	}

	private static void loadShipDatabase() {
		File file = file(FILENAME_SHIPCACHE);

		SHIPS.clear();
		try (Reader reader = loadFile(file)) {
			ShipDatabaseParser.loadShipDatabase(reader, SHIPS);
		} catch (IOException cause) {
			logger.log(Level.WARNING, String.format(ErrorReading, file.getName()), cause);
		}
	}
	
	private static void loadEvents() {
		File file = file(FILENAME_EVENTS);
		EVENTS.clear();
		try (Reader reader = loadFile(file)) {
			EventsParser.loadEvents(reader, EVENTS);
		} catch (IOException cause) {
			logger.log(Level.WARNING, String.format(ErrorReading, file.getName()), cause);
		}
	}
	
	private static void loadAssignments() {
		File file = file(FILENAME_ASSIGNMENTS);
		ASSIGNMENTS.clear();
		try (Reader reader = loadFile(file)) {
			AssignmentsParser.loadAssignments(reader, ASSIGNMENTS);
		} catch (IOException cause) {
			logger.log(Level.WARNING, String.format(ErrorReading, file.getName()), cause);
		}
	}
	
	private static void loadRenamedShips() {
		File file = file(FILENAME_RENAMED);
		RENAMED.clear();
		try (Reader reader = loadFile(file)) {
			RenamedShipParser.loadRenamedShips(reader, RENAMED);
		} catch (IOException cause) {
			logger.log(Level.WARNING, String.format(ErrorReading, file.getName()), cause);
		}
	}
	
	private static void loadTraits() {
		File file = file(FILENAME_TRAITS);
		TRAITS.clear();
		try (Reader reader = loadFile(file)) {
			TraitsParser.loadTraits(reader, TRAITS);
		} catch (IOException cause) {
			logger.log(Level.WARNING, String.format(ErrorReading, file.getName()), cause);
		}
	}
	
	private static void loadCachedIcons() {
		File file = file(FILENAME_ICONCACHE);
		ICONCACHE.clear();
		IconLoader.loadCachedIcons(file, ICONCACHE);
	}

	private static void loadIcons() {
		File file = file(FILENAME_ICONS);
		ICONS.clear();
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				try (InputStream is = zipFile.getInputStream(entry)) {
					ICONS.put(entry.getName(), ImageIO.read(is));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveCachedIcons() {
		File oldFile = file(FILENAME_ICONCACHE);
		if (oldFile.exists()) {
			oldFile.delete();
		}

		File newFile = file(FILENAME_NEWCACHE);
		IconLoader.saveCachedIcons(newFile, ICONCACHE);

		newFile.renameTo(oldFile);
	}

	/*/
	private static long getCacheTime() {
		return System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
	}
	//*/

	public static Admirals getAdmirals() {
		if (ADMIRALS == null) {
			try {
				ADMIRALS = loadAdmirals(file(FILENAME_ADMIRALS));
			} catch (JAXBException cause) {
				logger.log(Level.WARNING, String.format(ErrorReading, FILENAME_ADMIRALS), cause);
			}
			for (Admiral admiral : ADMIRALS.getAdmirals()) {
				admiral.validateShips();
				admiral.activateShips();
			}
		}
		return ADMIRALS;
	}

	public static void updateDataFiles() {
		if (Configuration.isDataUpdateEnabled() && isDataFilesStale()) {
			for(String filename : DATA_FILES) {
				File file = Datastore.file(filename);
				String url = url(filename);
				downloadFile(file, url);
			}
			Configuration.setDataUpdateLastUpdated(System.currentTimeMillis());
		}
	}

	private static String url(String filename) {
		return String.format(Configuration.getDataUpdateUrl(), filename);
	}

	private static void downloadFile(File file, String remoteName) {
		File tempFile = new File(file.toString() + ".temp");
		try {
			URL url = new URL(remoteName);
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			fileOutputStream.close();
			file.delete();
			tempFile.renameTo(file);
		} catch (MalformedURLException cause) {
			logger.log(Level.WARNING, "Malformed URL: " + remoteName, cause);
		} catch (IOException cause) {
			logger.log(Level.WARNING, "Error while downloading " + remoteName, cause);
		}
	}
	
	public static void setAdmirals(Admirals admirals) {
		try {
			saveAdmirals(file(FILENAME_ADMIRALS), admirals);
		} catch (JAXBException cause) {
			logger.log(Level.WARNING, String.format(ErrorWriting, FILENAME_ADMIRALS), cause);
		}
	}

	public static Admirals loadAdmirals(File file) throws JAXBException {
		if (!file.exists()) {
			ADMIRALS = new Admirals();
			saveAdmirals(file, ADMIRALS);
		}
		return (Admirals)admiralsUnmarshaller.unmarshal(file);
	}

	public static void saveAdmirals(File file, Admirals admirals) throws JAXBException {
		admiralsMarshaller.marshal(admirals, file);
	}
	
	public static boolean exportShips(File file, Collection<Ship> ships) {
		try {
			PrintStream out = new PrintStream(file);
			for(Ship ship : ships) {
				out.println(ship.getDisplayName());
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static int importShips(File file, Admiral admiral) {
		int counter = 0;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			
			SortedMap<String, Ship> ships = getAllShips();
			String line = reader.readLine();
			while (line != null) {
				line = line.trim();
				Ship ship = ships.get(line.toLowerCase());
			    if (ship != null) {
			    	admiral.addActive(line);
			    	counter++;
			    }
			    line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return counter;
	}

	private static void init() {
		// Setup JAXB parser for admirals.xml file
		try {
			admiralsContext = JAXBContext.newInstance(Admirals.class);
			admiralsMarshaller = admiralsContext.createMarshaller();
			admiralsMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			admiralsUnmarshaller = admiralsContext.createUnmarshaller();
		} catch (Throwable cause) {
			logger.log(Level.WARNING, ErrorInitJAXB, cause);
		}
	}
	
	public static File file(String filename) {
		if (!FILES.containsKey(filename)) {
			FILES.put(filename, new File(filename));
		}
		return FILES.get(filename);
	}

	private static Reader loadFile(File file) {
		if (file.exists()) {
			try {
				return new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[1024];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
	}
	
}
