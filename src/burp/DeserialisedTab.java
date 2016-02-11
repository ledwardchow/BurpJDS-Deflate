/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.awt.Component;
import java.util.Arrays;

public class DeserialisedTab implements IMessageEditorTabFactory
{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    //
    // implement IMessageEditorTabFactory
    //
    
    public DeserialisedTab(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
    }
    
    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable)
    {
        // create a new instance of our custom editor tab
        return new DeserialisedInputTab(controller, editable, helpers, callbacks);
    }

    //
    // class implementing IMessageEditorTab
    //

    class DeserialisedInputTab implements IMessageEditorTab
    {
        private boolean editable;
        private ITextEditor txtInput;
        private byte[] currentMessage;
        private byte[] postbody;
        private IExtensionHelpers helpers;
        private IBurpExtenderCallbacks callbacks;

        public DeserialisedInputTab(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks)
        {
            this.editable = editable;
            this.helpers = helpers;
            this.callbacks = callbacks;

            // create an instance of Burp's text editor, to display our deserialized data
            txtInput = callbacks.createTextEditor();
            txtInput.setEditable(editable);
        }

        //
        // implement IMessageEditorTab
        //

        @Override
        public String getTabCaption()
        {
            return "Deserialised";
        }

        @Override
        public Component getUiComponent()
        {
            return txtInput.getComponent();
        }

        @Override
        public boolean isEnabled(byte[] content, boolean isRequest)
        {
            try {
                // enable this tab for requests containing deflate data
                String blah = new String(content);
                int newlineposition = blah.indexOf("\r\n\r\n");
                postbody = Arrays.copyOfRange(content, newlineposition+4, content.length);
                if (postbody.length > 0) {
                    return (postbody[0] == 0x78);
                }
                else {            
                    return false;
                }   
            }
            catch (Exception ex) {
                callbacks.printOutput(ex.getStackTrace().toString());
                return false;
            }                     
        }

        @Override
        public void setMessage(byte[] content, boolean isRequest)
        {
            txtInput.setText("Could not locate class within jars".getBytes());
            String blah = new String(content);
            int newlineposition = blah.indexOf("\r\n\r\n");
            byte[] postbody = Arrays.copyOfRange(content, newlineposition+4, content.length);
            if (postbody == null)
            {
                // clear our display
                txtInput.setEditable(false);
            }
            else
            {
                try {
                    byte[] xmltest = XMLConverter.toXML(ZlibUtils.decompress(postbody), helpers);
                    if (xmltest[0] != (byte) 0x3c) 
                    {
                        txtInput.setText("Could not locate class within jars".getBytes());
                    }
                    else {
                        txtInput.setText(xmltest);
                    }
                }
                catch (Exception e) {}                
            }
            // remember the displayed content
            currentMessage = content;
        }

        @Override
        public byte[] getMessage()
        {
            // determine whether the user modified the deserialized data
            if (txtInput.isTextModified())
            {
                // reserialize the data
                byte[] text = txtInput.getText();
                byte[] recompressedMessage = null;
                String blah = new String(currentMessage);
                int newlineposition = blah.indexOf("\r\n\r\n");
                byte[] postheader = Arrays.copyOfRange(currentMessage, 0, newlineposition+4);
                try {
                    recompressedMessage = ZlibUtils.compress(XMLConverter.fromXML(txtInput.getText(), helpers));
                }
                catch (Exception e) {}
                return concat(postheader, recompressedMessage);
            }
            else return currentMessage;
        }
        
        public byte[] concat(byte[] a, byte[] b) {
            int aLen = a.length;
            int bLen = b.length;
            byte[] c= new byte[aLen+bLen];
            System.arraycopy(a, 0, c, 0, aLen);
            System.arraycopy(b, 0, c, aLen, bLen);
            return c;
         }

        @Override
        public boolean isModified()
        {
            return txtInput.isTextModified();
        }

        @Override
        public byte[] getSelectedData()
        {
            return txtInput.getSelectedText();
        }       
        
    }    
}
