package miles;

//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;
//import org.eclipse.jdt.internal.core.search.StringOperation;
//import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
//import java.io.StringWriter;
//import java.io.Writer;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
//import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSOutput;


//@SuppressWarnings("deprecation")
public final class XMLFormatter {

    public static String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);

            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

            DOMImplementationLS impl = 
                (DOMImplementationLS)registry.getDOMImplementation("LS");

            LSSerializer writer = impl.createLSSerializer();
//            DOMStringList s = writer.getDomConfig().getParameterNames();
//            for(int i=0; i<s.getLength(); i+=1) System.out.println(s.item(i));
            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);
            LSOutput output = impl.createLSOutput();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            output.setByteStream(bos);
            writer.write(document, output);
            return bos.toString();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
