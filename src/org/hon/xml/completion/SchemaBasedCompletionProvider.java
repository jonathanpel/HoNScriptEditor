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

package org.hon.xml.completion;

import javax.swing.text.JTextComponent;
import org.hon.xml.completion.util.CompletionUtil;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.xml.text.syntax.XMLSyntaxSupport;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Samaresh (Samaresh.Panda@Sun.Com)
 */
//@MimeRegistration(mimeType = "text/entity+xml", service = CompletionProvider.class)
@MimeRegistrations({
    @MimeRegistration(mimeType="text/entity+xml", service=CompletionProvider.class),
    @MimeRegistration(mimeType="text/interface+xml", service=CompletionProvider.class)
})
public class SchemaBasedCompletionProvider implements CompletionProvider {
    
    /**
     * Creates a new instance of SchemaBasedCompletionProvider
     */
    public SchemaBasedCompletionProvider() {
    }
    
	@Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        BaseDocument doc = Utilities.getDocument(component);
	if ( typedText ==null || typedText.trim().length() ==0 ){
            return 0;
        }
        if(doc == null)
            return 0;
        XMLSyntaxSupport support = ((XMLSyntaxSupport)doc.getSyntaxSupport());
        if(support.noCompletion(component) || !CompletionUtil.canProvideCompletion(doc)) {
            return 0;
        }
        
        return COMPLETION_QUERY_TYPE;
    }
        
    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType == COMPLETION_QUERY_TYPE || queryType == COMPLETION_ALL_QUERY_TYPE) {
            return new AsyncCompletionTask(new CompletionQuery(CompletionUtil.getPrimaryFile()), component);
        }
        
        return null;
    }
    
        
}
