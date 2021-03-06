package org.hibernate.eclipse.mapper.editors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.common.HibernateExtension;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.nature.HibernateNature;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;

public class StructuredTextViewerConfigurationUtil {

	static public IJavaProject findJavaProject(ITextViewer viewer) {
		
		if(viewer==null) return null;
		
		IStructuredModel existingModelForRead = StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
	
		if(existingModelForRead==null) return null;
		
		IJavaProject javaProject = null;
		try {
			String baseLocation = existingModelForRead.getBaseLocation();
			// 20041129 (pa) the base location changed for XML model
			// because of FileBuffers, so this code had to be updated
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=79686
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IProject project = null;
			if (filePath.segmentCount() > 0) {
				project = root.getProject(filePath.segment(0));
			}
			if (project != null) {
				javaProject = JavaCore.create(project);
			}
		}
		catch (Exception ex) {
			MapperPlugin.getDefault().logException(ex);
		}
		return javaProject;
	}
	
	static public IJavaProject findJavaProject(IDocument doc) {
		IResource resource = getProject(doc);
		if(resource!=null) {
			IProject project = resource.getProject();
			if(project!=null) {
				return JavaCore.create(project);
			}
		}
		return null;
	}
	
	static public IJavaProject findJavaProject(ContentAssistRequest request) {
		IResource resource = getProject(request);
		if(resource!=null) {
			IProject project = resource.getProject();
			if(project!=null) {
				return JavaCore.create(project);
			}
		}
		return null;
	}
	
	static public IProject getProject(ContentAssistRequest request) {
		
		if (request != null) {
			IStructuredDocumentRegion region = request.getDocumentRegion();
			if (region != null) {
				IDocument document = region.getParentDocument();
				return getProject( document );
			}
		}
		return null;
	}

	static public IProject getProject(IDocument document) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				String baselocation = model.getBaseLocation();
				if (baselocation != null) {
					// copied from JSPTranslationAdapter#getJavaProject
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IPath filePath = new Path(baselocation);
					if (filePath.segmentCount() > 0) {
						return root.getProject(filePath.segment(0));								
					}
				}
			} 
			return null;
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
	
	public static IService getService(ISourceViewer sourceViewer) {
		IJavaProject javaProject = findJavaProject(sourceViewer);
		HibernateNature hibnat = HibernateNature.getHibernateNature(javaProject);
		if (hibnat != null) {
			ConsoleConfiguration cc = hibnat.getDefaultConsoleConfiguration();
			if (cc != null) {
				HibernateExtension extension = cc.getHibernateExtension();
				if (extension != null) {
					return extension.getHibernateService();
				}
			}
		} 
		// return hibernate 3.5 service by default
		// TODO find a way to return the correct service
		return ServiceLookup.findService("3.5"); //$NON-NLS-1$
	}
	
}
