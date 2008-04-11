package mvnlink.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MavenPomParser
{
    private final IFile pom;

    public MavenPomParser(IFile pom)
    {
        this.pom = pom;
    }

    public MavenProjectModel parseModel()
    {
        MavenProjectModel model = new MavenProjectModel();
        ResolvingHandler handler = new ResolvingHandler(model);
        SAXParserFactory factory = SAXParserFactory.newInstance();

        InputStream input = null;
        try
        {
            SAXParser parser = factory.newSAXParser();
            input = pom.getContents();
            parser.parse(input, handler);
        }
        catch (SAXException e)
        {
            // TODO log a warning, for now we just ignore and pretend that we didnt find a version
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not parse pom: " + pom.getRawLocationURI(), e);
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Could not close input stream for: " +
                            pom.getRawLocationURI(), e);
                }
            }
        }

        if (model.isComplete())
        {
            return model;
        }
        else
        {
            // TODO log warning
            return model;
        }
    }

    private static class ResolvingHandler extends DefaultHandler
    {
        private final MavenProjectModel model;
        private Stack<String> elements = new Stack<String>();

        public ResolvingHandler(MavenProjectModel model)
        {
            this.model = model;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes)
                throws SAXException
        {
            elements.push(name);
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException
        {
            elements.pop();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {

            if (checkStack("project", "version"))
            {
                model.setVersion(new String(ch, start, length));
            }
            if (checkStack("project", "artifactId"))
            {
                model.setArtifactId(new String(ch, start, length));
            }
            if (checkStack("project", "groupId"))
            {
                model.setGroupId(new String(ch, start, length));
            }


            if (checkStack("project", "parent", "version"))
            {
                if (model.getVersion() == null)
                {
                    model.setVersion(new String(ch, start, length));
                }
            }
            if (checkStack("project", "parent", "groupId"))
            {
                if (model.getGroupId() == null)
                {
                    model.setGroupId(new String(ch, start, length));
                }
            }

        }

        private boolean checkStack(String... names)
        {
            if (!(elements.size() == names.length))
            {
                return false;
            }
            for (int i = 0; i < names.length; i++)
            {
                if (!elements.get(i).equalsIgnoreCase(names[i]))
                {
                    return false;
                }
            }
            return true;
        }

    }
}
