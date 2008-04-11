package mvnlink.util;

public class PartialPath
{
    private final String path;

    public PartialPath(String path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException("path cannot be null");
        }
        this.path = path;
    }

    @Override
    public int hashCode()
    {
        return path.substring(Math.max(0, path.lastIndexOf("/"))).hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj == null)
        {
            return false;
        }
        else if (obj instanceof PartialPath)
        {
            PartialPath other = (PartialPath)obj;
            return other.path.endsWith(path) || path.endsWith(other.path);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return path.toString();
    }
}
