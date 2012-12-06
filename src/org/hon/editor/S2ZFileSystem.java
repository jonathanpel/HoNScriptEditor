/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Oloko
 */
public class S2ZFileSystem extends AbstractFileSystem {
	
	private TFile root = new TFile(".");
	private transient Object closeSync = new Object();
	
	private S2ZFileSystem() {
		Impl impl = new Impl(this);
		this.change = impl;
		this.info = impl;
		this.list = impl;
		DefaultAttributes a = new InnerAttrs(this, info, change, impl);
		this.attr = a;
	}
	
	public S2ZFileSystem(TFile root) {
		this();
		try {
			setRoot(root);
		} catch (PropertyVetoException ex) {
			Exceptions.printStackTrace(ex);
		}
	}
	
	@SuppressWarnings("deprecation") // need to set it for compat
	private void _setSystemName(String s) throws PropertyVetoException {
		setSystemName(s);
	}

	private void setRoot(TFile root) throws PropertyVetoException {
		String oldDisplayName = getDisplayName();
		if (getRefreshTime() > 0) {
			setRefreshTime(0);
		}
		synchronized (closeSync) {
			
			_setSystemName(computeSystemName(root));
			this.root = new TFile(root);
			FileObject newRoot = refreshRoot();
			if (newRoot != null) {
				firePropertyChange(PROP_ROOT, null, newRoot);
			}
		}
		firePropertyChange(PROP_DISPLAY_NAME, oldDisplayName, getDisplayName());
	}
	
	public TFile getRootFile() {
		return this.root;
	}
	
	protected String computeSystemName(File rootFile) {
		String retVal = rootFile.getAbsolutePath().replace(File.separatorChar, '/');
		
		return ((Utilities.isWindows() || (Utilities.getOperatingSystem() == Utilities.OS_OS2))) ? retVal.toLowerCase() : retVal;
	}
	
	@Override
	public String getDisplayName() {
		return this.root != null ? this.root.getName() : "error";
	}

	@Override
	public boolean isReadOnly() {
		return !this.root.canWrite() && this.root.exists();
		//throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public static class Impl extends Object implements AbstractFileSystem.List, AbstractFileSystem.Info,
        AbstractFileSystem.Change {
		
		private S2ZFileSystem fs;
		
		private Impl(S2ZFileSystem fs) {
			this.fs = fs;
		}
		
		@Override
		public String[] children(String f) {
			return new TFile(this.fs.root, f).list();
		}

		@Override
		public Date lastModified(String name) {
			return new Date(new TFile(this.fs.root, name).lastModified());
		}

		@Override
		public boolean folder(String name) {
			return new TFile(this.fs.root, name).isDirectory();
		}

		@Override
		public boolean readOnly(String name) {
			TFile f = new TFile(this.fs.root, name);
			return !f.canWrite() && f.exists();
		}

		@Override
		public String mimeType(String name) {
			return null;
			//throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public long size(String name) {
			return new TFile(this.fs.root, name).length();
		}

		@Override
		public InputStream inputStream(String name) throws FileNotFoundException {
			return new TFileInputStream(new TFile(this.fs.root, name));
		}

		@Override
		public OutputStream outputStream(String name) throws IOException {
			return new TFileOutputStream(new TFile(this.fs.root, name));
		}

		@Override
		public void lock(String name) throws IOException {
			//throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void unlock(String name) {
			//throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void markUnimportant(String name) {
			//throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void createFolder(String name) throws IOException {
			new TFile(this.fs.root, name).mkdir();
		}

		@Override
		public void createData(String name) throws IOException {
			new TFile(this.fs.root, name).createNewFile();
		}

		@Override
		public void rename(String oldName, String newName) throws IOException {
			new TFile(this.fs.root, oldName).mv(new TFile(this.fs.root, newName));
		}

		@Override
		public void delete(String name) throws IOException {
			new TFile(this.fs.root, name).rm();
		}
		
	}
	
	private static class InnerAttrs extends DefaultAttributes {
		
		private S2ZFileSystem fs;
		
		public InnerAttrs (S2ZFileSystem fs, AbstractFileSystem.Info info,
				AbstractFileSystem.Change change, AbstractFileSystem.List list) {
			super(info, change, list);
			this.fs = fs;
		}
		
		@Override
        public Object readAttribute(String name, String attrName) {
            if (attrName.equals("java.io.File")) { // NOI18N

                return new TFile(this.fs.root, name);
            }

            return super.readAttribute(name, attrName);
        }
	}
}
