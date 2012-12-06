/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

public class S2ZDataObject extends MultiDataObject {

	public S2ZDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
		super(pf, loader);

	}

	@Override
	protected Node createNodeDelegate() {
		return new S2ZDataNode(this, getLookup());
	}

	@Override
	public Lookup getLookup() {
		return getCookieSet().getLookup();
	}
}
