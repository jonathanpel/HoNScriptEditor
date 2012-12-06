/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;

import java.io.IOException;
import org.hon.xml.cookies.HoNValidateXMLSupport;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;
import org.xml.sax.InputSource;

public class InterfaceDataObject extends MultiDataObject {

	public InterfaceDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
		super(pf, loader);
		CookieSet cookies = getCookieSet();
		
		InputSource is = DataObjectAdapters.inputSource(this);
		cookies.add(new CheckXMLSupport(is));
        cookies.add(new HoNValidateXMLSupport(is, pf.getMIMEType()));
		
		cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
	}

	@Override
	protected Node createNodeDelegate() {
		return new DataNode(this, Children.LEAF, getLookup());
	}

	@Override
	public Lookup getLookup() {
		return getCookieSet().getLookup();
	}
}
