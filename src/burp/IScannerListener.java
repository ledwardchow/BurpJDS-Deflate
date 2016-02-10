/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;

/*
 * @(#)IScannerListener.java
 *
 * Copyright PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Free Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */
/**
 * Extensions can implement this interface and then call
 * <code>IBurpExtenderCallbacks.registerScannerListener()</code> to register a
 * Scanner listener. The listener will be notified of new issues that are
 * reported by the Scanner tool. Extensions can perform custom analysis or
 * logging of Scanner issues by registering a Scanner listener.
 */
public interface IScannerListener
{
    /**
     * This method is invoked when a new issue is added to Burp Scanner's
     * results.
     *
     * @param issue An
     * <code>IScanIssue</code> object that the extension can query to obtain
     * details about the new issue.
     */
    void newScanIssue(IScanIssue issue);
}
