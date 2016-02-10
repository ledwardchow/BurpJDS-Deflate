#BurpJDS-Deflate

Extends the burpjdser Burp Extender plugin to use Deflate/Inflate compression on HTTP traffic prior to deserialising. In addition to the XML-format deserialisation it also leaves a tab with raw inflated data.

Much copypasta from:
https://github.com/khai-tran/BurpJDSer
https://github.com/IOActive/BurpJDSer-ng
https://github.com/nccgroup/JDSer-ngng/

IOActive's readme is below, this one works the same way:

##Usage

###1) Find and download client *.jar files
Few methods to locate the required jar files containing the classes we'll be deserializing.
* In case of a .jnlp file use [jnpdownloader](https://code.google.com/p/jnlpdownloader/)
* Locating jars in browser cache
* Looking for .jar in burp proxy history

Finally, create a "libs/" directory next to your burp.jar and put all the jars in it.

###2) Start Burp plugin
Download from [here](https://github.com/ledwardchow/raw/master/dist/BurpJDS-Deflate.jar) and simply load it in the Extender tab, the Output window will list all the loaded jars from ./libs/ 


###3) Inspect serialized Java traffic
Serialized Java content will automagically appear in the Deserialized Java input tab in appropriate locations (proxy history, interceptor, repeater, etc.)
Any changes made to the XML will serialize back once you switch to a different tab or send the request.

**Please note that if you mess up the XML schema or edit an object in a funny way, the re-serialization will fail and the error will be displayed in the input tab**

JARs reload when the extender is loaded. Everything is written to stdout (so run java -jar burpsuite.jar) and look for error messages/problems there.

cheers