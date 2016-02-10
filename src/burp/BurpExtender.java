/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

public class BurpExtender implements IBurpExtender
{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;
        
        // obtain an extension helpers object
        helpers = callbacks.getHelpers();
        
        // set our extension name
        callbacks.setExtensionName("BurpJDS-Deflate");
        
        // register ourselves as a message editor tab factory
        callbacks.registerMessageEditorTabFactory(new ZlibOnlyTab(callbacks, helpers));
        callbacks.registerMessageEditorTabFactory(new DeserialisedTab(callbacks, helpers));
        
    }

}
