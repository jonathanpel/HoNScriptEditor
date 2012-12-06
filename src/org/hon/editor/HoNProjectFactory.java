package org.hon.editor;


import java.io.IOException;
import java.util.Enumeration;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

@org.openide.util.lookup.ServiceProvider(service=ProjectFactory.class)
public class HoNProjectFactory implements ProjectFactory {

    public static final String PROJECT_DIR = "resources0.s2z";

    @Override
    public boolean isProject(FileObject projectDirectory) {
        return projectDirectory.getFileObject(PROJECT_DIR) != null;
    }

    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new HoNProject(dir, state) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        FileObject projectRoot = project.getProjectDirectory();
        if (projectRoot.getFileObject(PROJECT_DIR) == null) {
            throw new IOException("Project dir " + projectRoot.getPath() +
                    " deleted," +
                    " cannot save project");
        }
        //Force creation of the texts dir if it was deleted:
       // ((HoNProject) project).getTextFolder(true);
    }
    
    public static boolean s2zExist(FileObject dir, boolean rec)
    {
        Enumeration<? extends FileObject> childs = dir.getChildren(rec);
        boolean found = false;
        while (found == false && childs.hasMoreElements())
        {
            if (((FileObject)childs.nextElement()).getExt().equals("s2z"))
            {
                found = true;
            }
        }
        
        return found;
    }
}