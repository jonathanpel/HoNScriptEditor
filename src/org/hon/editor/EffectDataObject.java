/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import java.io.IOException;
import org.hon.xml.cookies.HoNValidateXMLSupport;
import org.netbeans.modules.xml.XMLDataObjectLook;
import org.netbeans.modules.xml.cookies.DataObjectCookieManager;
import org.netbeans.modules.xml.sync.DataObjectSyncSupport;
import org.netbeans.modules.xml.sync.Synchronizator;
import org.netbeans.modules.xml.text.TextEditorSupport;
import org.netbeans.modules.xml.text.syntax.XMLKit;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.xml.sax.InputSource;

public class EffectDataObject extends MultiDataObject implements XMLDataObjectLook{
	
	private transient final DataObjectCookieManager cookieManager;
	private transient Synchronizator synchronizator;
	
	public EffectDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
		super(pf, loader);
		CookieSet cookies = getCookieSet();
		cookieManager = new DataObjectCookieManager (this, cookies);
		InputSource is = DataObjectAdapters.inputSource(this);
		cookies.add(new CheckXMLSupport(is));
        cookies.add(new HoNValidateXMLSupport(is, pf.getMIMEType()));
		TextEditorSupport.TextEditorSupportFactory editorFactory =
            new TextEditorSupport.TextEditorSupportFactory (this, "text/effect+xml");
		editorFactory.registerCookies (cookies);
	}

	@Override
	protected Node createNodeDelegate() {
		return new DataNode(this, Children.LEAF, getLookup());
	}

	@Override
	public Lookup getLookup() {
		return getCookieSet().getLookup();
	}

	@Override
	public Synchronizator getSyncInterface() {
		if (synchronizator == null) {
			synchronizator = new DataObjectSyncSupport (EffectDataObject.this);
		}
		return synchronizator;
	}

	@Override
	public DataObjectCookieManager getCookieManager() {
		return cookieManager;
	}
}
