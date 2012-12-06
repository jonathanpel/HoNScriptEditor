/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.hon.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import org.hon.options.HoNEditorPanel;
import org.hon.xml.completion.spi.CompletionContext;
import org.hon.xml.completion.spi.CompletionModelProvider;
import org.hon.xml.completion.spi.CompletionModelProvider.CompletionModel;
import org.hon.xml.completion.util.CompletionContextImpl;
import org.hon.xml.completion.util.CompletionModelEx;
import org.netbeans.modules.xml.schema.model.SchemaModel;
import org.netbeans.modules.xml.schema.model.SchemaModelFactory;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.Lookups;

@org.openide.util.lookup.ServiceProvider(service=org.hon.xml.completion.spi.CompletionModelProvider.class)
public class S2ZCompletionModelProvider extends CompletionModelProvider {
    
    public S2ZCompletionModelProvider() {
    }

    /**
     * Returns a list of CompletionModel. Default implementation looks for
     * schemaLocation attribute in the document and if specified creates model
     * for each schema mentioned in there.
     */    
	@Override
    public List<CompletionModel> getModels(CompletionContext context) {
		if(context == null ||
           context.getPrimaryFile() == null)
			return null;
        SchemaModel sm = createMetaSchemaModel(context.getPrimaryFile().getMIMEType());
        if(sm == null)
            return null;        
        CompletionModel cm = new CompletionModelEx((CompletionContextImpl)context, "xsd", sm); //NOI18N
        List<CompletionModel> models = new ArrayList<CompletionModel>();
        models.add(cm);
        return models;
    }
    
    private SchemaModel createMetaSchemaModel(String mimeType) {
        try {
            //InputStream in = getClass().getResourceAsStream("HoNSchema.xsd"); //NOI18N
			Preferences pref = NbPreferences.forModule(HoNEditorPanel.class);
			
			String path = "";
			String alternative = "";
			if (mimeType.equals("text/entity+xml")) {
				path = pref.get("xsdPath", "");
				alternative = "HoNSchema.xsd";
			}
			else if (mimeType.equals("text/interface+xml")) {
				path = pref.get("xsdInterfacePath", "");
				alternative = "HoNInterfaceSchema.xsd";
			}
			else
				return null;
			File f = new File(path);
			if (!f.isFile() || !f.canRead())
			{
				f = InstalledFileLocator.getDefault().locate(
						alternative,
						null,
						false);
			}
			InputStream in = new FileInputStream(f);
            javax.swing.text.Document d = AbstractDocumentModel.
                    getAccessProvider().loadSwingDocument(in);
	    ModelSource ms = new ModelSource(Lookups.singleton(d), false);
            SchemaModel m = SchemaModelFactory.getDefault().createFreshModel(ms);
            m.sync();
            return m;
        } catch (Exception ex) {
            //just catch
        } 
        return null;
    }
        
}