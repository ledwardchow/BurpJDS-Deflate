/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Edward
 */
public class XMLConverter {
    
    private static XStream xstream = new XStream(new DomDriver());
    protected static ClassLoader loader;
    private static final String LIB_DIR = "./libs/";
    private static Object obj;

    public static byte[] fromXML(byte[] original, IExtensionHelpers helpers)
    {
        String xml = helpers.bytesToString(original).replace("\n", "");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try {
             try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                 xstream.setClassLoader(getSharedClassLoader()); 
                 oos.writeObject(xstream.fromXML(xml));
                 oos.flush();
             }

         } catch (Exception ex) {
             System.out.println("Error deserializing from XML to Java object " + ex.getMessage());
         }
        return baos.toByteArray();
    }

    public static byte[] toXML(byte[] plaintext, IExtensionHelpers helpers) throws IOException, ClassNotFoundException
    {
        String xml = null;
        try {
            CustomLoaderObjectInputStream is = null;
            ByteArrayInputStream bais = new ByteArrayInputStream(plaintext);
            is = new CustomLoaderObjectInputStream(bais, XMLConverter.getSharedClassLoader());
            obj = is.readObject();
            xml = xstream.toXML(obj);
            System.out.println("XML: " + xml);        
            is.close();
        } 
        catch (Exception ex) {
                System.out.println("Error deserializing from Java object to XML  " + ex.getMessage());
        }
        return xml.getBytes();
    }

    public static ClassLoader getSharedClassLoader()
    {
        if(loader == null) {
            refreshSharedClassLoader();
        }
        return loader;
    }

    protected static ClassLoader createURLClassLoader(String libDir)
    {
        File dependencyDirectory = new File(libDir);
        File[] files = dependencyDirectory.listFiles();
        ArrayList<URL> urls = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".jar")) {
                try {
                    System.out.println("Loading: " + files[i].getName());
                    urls.add(files[i].toURI().toURL());
                } catch (MalformedURLException ex) {
                    Logger.getLogger(BurpExtender.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("!! Error loading: " + files[i].getName());
                }
            }
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]));
    }

    public static void refreshSharedClassLoader()
    {
        loader = createURLClassLoader(LIB_DIR);
    }
    
}
