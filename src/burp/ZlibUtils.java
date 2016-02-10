/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 *
 * @author Edward
 */
public class ZlibUtils {
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
        Inflater inflater = new Inflater();   
        inflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
        byte[] buffer = new byte[1024];  
        while (!inflater.finished()) {  
            int count = inflater.inflate(buffer);  
            outputStream.write(buffer, 0, count);  
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();   
        return output;  
    }  
        
    public static byte[] compress(byte[] data) throws IOException {  
        Deflater deflater = new Deflater();  
        deflater.setInput(data);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
        deflater.finish();  
        byte[] buffer = new byte[1024];   
        while (!deflater.finished()) {  
            int count = deflater.deflate(buffer); // returns the generated code... index  
            outputStream.write(buffer, 0, count);   
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        return output;  
    }
}
