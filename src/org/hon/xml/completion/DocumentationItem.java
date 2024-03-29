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

/*
 * DocumentationItem.java
 *
 * Created on June 28, 2006, 10:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.hon.xml.completion;

import java.net.URL;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.xml.axi.AXIComponent;
import org.netbeans.modules.xml.axi.AXIType;
import org.netbeans.modules.xml.axi.AbstractAttribute;
import org.netbeans.modules.xml.axi.AbstractElement;
import org.netbeans.modules.xml.axi.AnyAttribute;
import org.netbeans.modules.xml.axi.AnyElement;
import org.netbeans.modules.xml.axi.Attribute;
import org.netbeans.modules.xml.axi.datatype.Datatype;
import org.netbeans.modules.xml.schema.model.AppInfo;
import org.netbeans.modules.xml.schema.model.Attribute.Use;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.openide.util.NbBundle;
import org.w3c.dom.Element;

/**
 *
 * @author Samaresh
 */
public abstract class DocumentationItem implements CompletionDocumentation {
    
    private CompletionResultItem completionItem;
	
	public final static String APP_INFOS_USE_VARIABLES = "usesvariable";
    
    /**
     * Creates a new instance of DocumentationItem
     */
    public DocumentationItem(CompletionResultItem item) {
        this.completionItem = item;
    }
    
    public static DocumentationItem createDocumentationItem(CompletionResultItem item) {
        if(item.getAXIComponent() instanceof AbstractElement)
            return new ElementDocItem(item);
        if(item.getAXIComponent() instanceof AbstractAttribute)
            return new AttributeDocItem(item);
        
        return null;
    }
                
	@Override
    public abstract String getText();
    
    public final CompletionResultItem getCompletionItem() {
        return completionItem;
    }
    
	@Override
    public URL getURL() {
        return null;
    }
    
	@Override
    public CompletionDocumentation resolveLink(String link) {
        return null;
    }
    
	@Override
    public Action getGotoSourceAction() {
        return null;
    }
    
    static class ElementDocItem extends DocumentationItem {
                
        public ElementDocItem(CompletionResultItem item) {
            super(item);
        }
        
		@Override
        public String getText() {
            AXIComponent axiComponent = getCompletionItem().getAXIComponent();
            if(!(axiComponent instanceof AbstractElement))
                return null;
            AbstractElement element = (AbstractElement)axiComponent;
            String[] params = {"","","","",""};
            params[0] = element.getTargetNamespace();
            if(params[0] == null)
                params[0] = NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-No-TNS");
            params[1] = element.getName();
            params[2] = element.getDocumentation();
            if(params[2] == null)
                params[2] = NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-Element-No-Description");
            params[3] = formChildElementsHTML(element);
            if(params[3] == null)
                params[3] = NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-Element-No-Child-Elements");
            params[4] = formAttributesHTML(element);
            if(params[4] == null)
                params[4] = NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-Element-No-Attributes");
            return NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-Element", params);
        }
        
        private String formChildElementsHTML(AbstractElement element) {
            List<AbstractElement> children = element.getChildElements();
            if(children == null || children.isEmpty())
                return null;
            StringBuilder buffer = new StringBuilder();
            for(AbstractElement e: children) {
                String min = e.getMinOccurs();
                if(min != null && min.equals("1")) {
                    buffer.append("<b>").append(e.getName()).append("</b>");
                } else {
                    buffer.append(e.getName());
                }
                buffer.append(" ");
                if(e.supportsCardinality()) {
                    buffer.append("[").append(e.getMinOccurs()).append("..").append(e.getMaxOccurs()).append("]");
                }
                if(e instanceof AnyElement) {
                    buffer.append(" ");
                    buffer.append("{").append(e.getTargetNamespace()).append("}");
                }                
                buffer.append("<br>"); //NOI18N
            }
            
            return buffer.toString();
        }
        
        private String formAttributesHTML(AbstractElement element) {        
            List<AbstractAttribute> attrs = element.getAttributes();
            if(attrs == null || attrs.isEmpty())
                return null;
            StringBuilder buffer = new StringBuilder();
			buffer.append("<ul>");
            for(AbstractAttribute attr: attrs) {
				buffer.append("<li>");
                if(attr instanceof Attribute) {
					Attribute attribute = ((Attribute)attr);
                    Use use = attribute.getUse();
                    if(use != null && use == Use.REQUIRED) //NOI18N
                        buffer.append("<b>").append(attr.getName()).append("</b>");
                    else
                        buffer.append(attr.getName());
					buffer.append("<div>Type: ");
					
					AXIType type = attribute.getType();
					if(type instanceof Datatype) {
						buffer.append(((Datatype)type).getKind().getName());
						buffer.append("</div>");
					}
					String defaultValue = attribute.getDefault();
					if (defaultValue != null) {
						buffer.append("<div>Default: ");
						buffer.append(defaultValue);
						buffer.append("</div>");
					}
					String doc = attribute.getDocumentation();
					if (doc != null) {
						buffer.append("<div>");
						buffer.append(doc);
						buffer.append("</div>");
					}
					
					if (element.getPeer() != null && element.getPeer().getAnnotation() != null
							&& element.getPeer().getAnnotation().getAppInfos() != null) {
						for (AppInfo info : element.getPeer().getAnnotation().getAppInfos()) {
							Element infoEle = info.getAppInfoElement();
							String name = infoEle.getTagName();
							if (name.equals(APP_INFOS_USE_VARIABLES)) {
								if (infoEle.getNodeValue().equalsIgnoreCase("false")) {
									
								}
							}
						}
					}
					
                } else
                    buffer.append(attr.getName());
                
                if(attr instanceof AnyAttribute) {
                    buffer.append(" ");
                    buffer.append("{").append(attr.getTargetNamespace()).append("}");
                }                
                //buffer.append("<br>"); //NOI18N
				buffer.append("</li>");
            }
			buffer.append("</ul>");
            return buffer.toString();
        }
        
    }

        
    static class AttributeDocItem extends DocumentationItem {
                
        public AttributeDocItem(CompletionResultItem item) {
            super(item);
        }
        
		@Override
        public String getText() {
            AXIComponent axiComponent = getCompletionItem().getAXIComponent();
            if(!(axiComponent instanceof AbstractAttribute))
                return null;
            AbstractAttribute attribute = (AbstractAttribute)axiComponent;
            String[] params = {"","","","",""};                
            params[0] = attribute.getTargetNamespace();
            if(params[0] == null)
                params[0] = NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-No-TNS");            
            params[1] = attribute.getName();
            params[2] = attribute.getDocumentation();
            if(params[2] == null)
                params[2] = NbBundle.getMessage(DocumentationQuery.class,
                    "Documentation-Text-Attribute-No-Description");
            if(attribute instanceof Attribute) {
                AXIType type = ((Attribute)attribute).getType();
                if(type instanceof Datatype) {
                    params[3] = ((Datatype)type).getKind().getName();
                }
				
				params[4] = ((Attribute)attribute).getDefault();
				if (params[4] == null)
					params[4] = NbBundle.getMessage(DocumentationQuery.class,
						"Documentation-Text-Attribute-No-Default");
            }
            return NbBundle.getMessage(DocumentationQuery.class, "Documentation-Text-Attribute", params);
        }                
    }    
    
}
