package mvnlink.util;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

public class LoggingChangeObserver implements ChangeObserver
{
    private StringBuilder builder;
    boolean changes;

    public LoggingChangeObserver()
    {
    }

    public void changed(IJavaProject owner, IClasspathEntry original, IJavaProject replacement)
    {
        changes = true;

        String jar = original.getPath().toString();
        int filePos = jar.lastIndexOf("/");
        if (filePos > 0)
        {
            jar = jar.substring(filePos + 1);
        }
        builder.append(owner.getProject().getName()).append(": ").append(jar).append(" -> ")
                .append(replacement.getProject().getName()).append("\n");
    }

    public String toString()
    {
        return builder.toString();
    }

    public void finished()
    {
        if (changes == false)
        {
            builder.append("No changes made");
        }
    }

    public void started()
    {
        builder = new StringBuilder();
        builder
                .append("Change Log:\n-----------------------------------------------------------\n");
        changes = false;
    }

}
