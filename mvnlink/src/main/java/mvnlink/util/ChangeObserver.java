package mvnlink.util;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

public interface ChangeObserver
{
    void started();
    void changed(IJavaProject owner, IClasspathEntry original, IJavaProject replacement);
    void finished();
}
