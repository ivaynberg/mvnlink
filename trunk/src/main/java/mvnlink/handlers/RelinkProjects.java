package mvnlink.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mvnlink.MavenPomParser;
import mvnlink.MavenProjectModel;
import mvnlink.PartialPath;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RelinkProjects extends AbstractHandler
{
    /**
     * The constructor.
     */
    public RelinkProjects()
    {
    }

    /**
     * the command has been executed, so extract extract the needed information from the application
     * context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Set<IJavaProject> projects = findJavaProjects();

        Map<PartialPath, IJavaProject> paths = findMavenProjects(projects);

        try
        {
            for (IJavaProject project : projects)
            {
                try
                {
                    boolean modified = false;
                    IClasspathEntry[] cpArray = project.getRawClasspath();
                    List<IClasspathEntry> cp = new ArrayList<IClasspathEntry>(cpArray.length);
                    cp.addAll(Arrays.asList(cpArray));

                    for (int i = 0; i < cpArray.length; i++)
                    {
                        final IClasspathEntry e = cpArray[i];
                        final PartialPath path = new PartialPath(e.getPath().toString());
                        final IJavaProject substitute = paths.get(path);
                        if (substitute != null)
                        {
                            cp.set(i, JavaCore.newProjectEntry(substitute.getProject()
                                    .getFullPath()));
                            modified = true;

                            System.out.println(project.getProject().getName() + ": " +
                                    path.toString() + " --> " + substitute.getProject().getName());

                        }
                    }
                    if (modified)
                    {
                        project.setRawClasspath(cp.toArray(new IClasspathEntry[cp.size()]),
                                new NullProgressMonitor());

                    }
                }
                catch (JavaModelException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        catch (RuntimeException e)
        {
            // TODO log error?
            e.printStackTrace();
        }

        return null;
    }

    private Set<IJavaProject> findJavaProjects()
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IProject[] projects = workspace.getRoot().getProjects();
        Set<IJavaProject> javaProjects = new HashSet<IJavaProject>(projects.length);
        for (IProject project : projects)
        {
            try
            {
                if (project.hasNature(JavaCore.NATURE_ID))
                {
                    javaProjects.add(JavaCore.create(project));
                }
            }
            catch (CoreException e)
            {
                // ignore
            }
        }
        return Collections.unmodifiableSet(javaProjects);

    }

    private Map<PartialPath, IJavaProject> findMavenProjects(Set<IJavaProject> projects)
    {
        Map<PartialPath, IJavaProject> projectToVersion = new HashMap<PartialPath, IJavaProject>();
        for (IJavaProject javaProject : projects)
        {
            IFile pom = javaProject.getProject().getFile("pom.xml");
            if (pom.exists())
            {
                MavenPomParser parser = new MavenPomParser(pom);
                MavenProjectModel model = parser.parseModel();
                if (model != null)
                {
                    projectToVersion.put(new PartialPath(model.toJarPath()), javaProject);
                }
            }
        }
        return projectToVersion;
    }
}
