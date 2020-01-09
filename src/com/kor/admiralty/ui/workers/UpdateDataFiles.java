package com.kor.admiralty.ui.workers;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import com.kor.admiralty.Configuration;
import com.kor.admiralty.io.Datastore;

import static com.kor.admiralty.Globals.*;

public class UpdateDataFiles extends SwingWorker<Properties, Boolean> {

	protected static final Logger logger = Logger.getLogger(UpdateDataFiles.class.getName());

	@Override
	protected Properties doInBackground() throws Exception {
		for(String filename : DATA_FILES) {
			File file = Datastore.file(filename);
			String url = url(filename);
			SwingWorkerExecutor.downloadFile(file, url);
		}
		return null;
	}

	@Override
	public void done() {

	}
	
	protected static String url(String filename) {
		return String.format(Configuration.getDataUpdateUrl(), filename);
	}

	public static void main(String args[]) {
		logger.info(Datastore.file(".").toString());
		SwingWorkerExecutor.updateDataFiles();
	}

}
