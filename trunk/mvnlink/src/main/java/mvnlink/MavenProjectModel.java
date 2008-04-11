package mvnlink;

public class MavenProjectModel
{
    private String version;
    private String artifactId;
    private String groupId;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public boolean isComplete()
    {
        return version != null && artifactId != null && groupId != null;
    }

    public String toJarPath()
    {
        return "/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" +
                artifactId + "-" + version + ".jar";
    }


}
