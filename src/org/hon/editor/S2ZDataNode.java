/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hon.editor;


import de.schlichtherle.truezip.file.TFile;
import java.net.URL;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

final class S2ZDataNode extends DataNode {
    private static final RequestProcessor RP = new RequestProcessor(S2ZDataNode.class.getName(), 1, false, false);

    public S2ZDataNode(S2ZDataObject obj, Lookup look) {
        this(obj, new DummyChildren(), look);
    }

    private S2ZDataNode(S2ZDataObject obj, DummyChildren c, Lookup look) {
        super(obj, c, look);
        c.attachJarNode(this);
        //setIconBaseWithExtension("org/netbeans/modules/java/jarloader/jar.gif"); // NOI18N
    }

	@Override
    public Action getPreferredAction() {
        return null;
    }

    private static Children childrenFor(FileObject arch) {
        if (!FileUtil.isArchiveFile(arch)) {
            // Maybe corrupt, etc.
            return Children.LEAF;
        }
		FileObject rt = null;
		try {
			rt = URLMapper.findFileObject(new TFile(arch.getPath()).toURI().toURL());
		} catch (Exception ex) {
			Exceptions.printStackTrace(ex);
		}
		
		if (rt != null) {
			DataFolder fold = DataFolder.findFolder(rt);
			Children child = fold.createNodeChildren(DataFilter.ALL);
			return child;
        } else {
            return Children.LEAF;
        }
    }

    /**
     * There is no nice way to lazy create delegating node's children.
     * So, in order to fix #83595, here is a little hack that schedules
     * replacement of this dummy children on addNotify call.
     */
    final static class DummyChildren extends Children implements Runnable {

        private S2ZDataNode node;

		@Override
        protected void addNotify() {
            super.addNotify();
            assert node != null;
            RP.post(this);
        }

        private void attachJarNode(S2ZDataNode node) {
            this.node = node;
        }

		@Override
        public void run() {
            node.setChildren(childrenFor(node.getDataObject().getPrimaryFile()));
        }

		@Override
        public boolean add(final Node[] nodes) {
            // no-op
            return false;
        }
		
		@Override
        public boolean remove(final Node[] nodes) {
            // no-op
            return false;
        }

    }
}