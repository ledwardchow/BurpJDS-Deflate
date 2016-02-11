/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

import java.awt.Component;
import java.util.Arrays;

public class ZlibOnlyTab implements IMessageEditorTabFactory
{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;

    //
    // implement IMessageEditorTabFactory
    //
    
    public ZlibOnlyTab(IBurpExtenderCallbacks callbacks, IExtensionHelpers helpers) {
        this.callbacks = callbacks;
        this.helpers = helpers;
    }
    
    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable)
    {
        // create a new instance of our custom editor tab
        return new ZlibInputTab(controller, editable, helpers, callbacks);
    }

    //
    // class implementing IMessageEditorTab
    //

    class ZlibInputTab implements IMessageEditorTab
    {
        private boolean editable;
        private ITextEditor txtInput;
        private byte[] currentMessage;
        private IExtensionHelpers helpers;
        private byte[] postbody;
        private IBurpExtenderCallbacks callbacks;

        public ZlibInputTab(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks)
        {
            this.editable = editable;
            txtInput = callbacks.createTextEditor();
            txtInput.setEditable(editable);
            this.helpers = helpers;
            this.callbacks = callbacks;
        }

        //
        // implement IMessageEditorTab
        //

        @Override
        public String getTabCaption()
        {
            return "Inflated";
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
            catch (Exception ex ) {
                callbacks.printOutput(ex.getMessage());
                return false;
            }
        }

        @Override
        public void setMessage(byte[] content, boolean isRequest)
        {
            txtInput.setText(content);
            if (postbody == null)
            {
                txtInput.setEditable(false);
            }
            else
            {
                try {
                txtInput.setText(ZlibUtils.decompress(postbody));
                }
                catch (Exception e) {}                
            }
            // remember the displayed content
            currentMessage = content;
        }
        
          
        

        @Override
        public byte[] getMessage()
        {
            if (txtInput.isTextModified())
            {
                byte[] text = txtInput.getText();
                byte[] recompressedMessage = null;
                String blah = new String(currentMessage);
                int newlineposition = blah.indexOf("\r\n\r\n");
                byte[] postheader = Arrays.copyOfRange(currentMessage, 0, newlineposition+4);
                try {
                    recompressedMessage = ZlibUtils.compress(txtInput.getText());
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
