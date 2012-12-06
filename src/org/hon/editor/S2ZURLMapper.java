/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.archive.zip.ZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = URLMapper.class)
public class S2ZURLMapper extends URLMapper {
	
	private final Map<String, S2ZFileSystem> fileSystemCache = new HashMap<String, S2ZFileSystem>();

	public S2ZURLMapper() {
		TFile.setDefaultArchiveDetector(new TArchiveDetector("s2z", new ZipDriver(IOPoolLocator.SINGLETON)));
	}
	
	@Override
	public URL getURL(FileObject fo, int type) {
		URL retURL = null;
		try {
			if (fo.getFileSystem() instanceof S2ZFileSystem) {
				S2ZFileSystem fs = (S2ZFileSystem) fo.getFileSystem();
				try {
					retURL = new TFile(fs.getRootFile(), fo.getPath()).toURI().toURL();
				} catch (MalformedURLException ex) {
					Exceptions.printStackTrace(ex);
				}
			}
		} catch (FileStateInvalidException ex) {
			Exceptions.printStackTrace(ex);
		}
		return retURL;
	}
	
	@Override
	public FileObject[] getFileObjects(URL url) {
		if ("s2z".equalsIgnoreCase(url.getProtocol())) {
			try {
				TFile entr = new TFile(url.toURI());
				URL fileRoot;
				if (entr.isArchive()) {
					fileRoot = url;
				} else {
					TFile t2 = entr.getEnclArchive();
					URI t3 = t2.toURI();
					URL t4 = t3.toURL();
					fileRoot = entr.getEnclArchive().toURI().toURL();
				}
				S2ZFileSystem fs = findFileSystem(fileRoot);
				String res = "";
				if (!entr.isArchive()) {
					res = entr.getEnclEntryName();
				}
				FileObject fob = fs.findResource(res);
				if (fob != null) {
					return new FileObject[]{ fob };
				}
			} catch (URISyntaxException ex) {
				Exceptions.printStackTrace(ex);
			} catch (MalformedURLException ex) {
				Exceptions.printStackTrace(ex);
			}
		}
		return null;
	}
	
	private S2ZFileSystem findFileSystem(URL url) {
		String key = url.toString();
        if (!fileSystemCache.containsKey(key)) {
            fileSystemCache.put(key, createFileSystem(url));
        }
        S2ZFileSystem result = fileSystemCache.get(key);
        return result;
    }
	
	private S2ZFileSystem createFileSystem(URL url) {
		S2ZFileSystem fs = null;
		try {
			 fs = new S2ZFileSystem(new TFile(url.toURI()));
		} catch (URISyntaxException ex) {
			Exceptions.printStackTrace(ex);
		}
		return fs;
	}
}
