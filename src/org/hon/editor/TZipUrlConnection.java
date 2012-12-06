/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.openide.util.URLStreamHandlerRegistration;

public class TZipUrlConnection extends URLConnection {
	
	public static final String PROTOCOL = "s2z"; // NOI18N

	@URLStreamHandlerRegistration(protocol = PROTOCOL)
	public static class Handler extends URLStreamHandler {
		@Override
		public URLConnection openConnection(URL u) throws IOException {
			return new TZipUrlConnection(u);
		}
	}

	public TZipUrlConnection(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {
	}
} 