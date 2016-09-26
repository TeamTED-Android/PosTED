package com.example.todor.restourantexample;

import android.util.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class WebService {

    private interface SoapResultHandler
    {
        void startTag(String name);
        void endTag(String name);
        void text(String text);
    }

    public class LicensingException extends Exception
    {
        private static final long serialVersionUID = 1L;

        //private String faultcode;
        //private String faultstring;
        //private String faultdetail;

        private String errortype;
        private String errormessage;

        public String getType()
        {
            return errortype;
        }

        @Override
        public String getMessage()
        {
            //return faultcode + "\r\n" + faultstring + "\r\n" + faultdetail;
            return errormessage;
        }

        public LicensingException(String type, String message)
        {
            super();
            errortype = type;
            errormessage = message;
            //faultcode = code;
            //faultstring = string;
            //faultdetail = detail;
        }

    }

    //private AndroidHttpClient client;
    private DefaultHttpClient client;
    //private SAXParser parser;
    private XmlPullParser parser;
    private XmlSerializer serializer;

    private String method;
    private HashMap<String, Object> parameters;

    //private Context Context;

    public WebService(/*Context context*/) {
        // TODO Auto-generated constructor stub
        //client = AndroidHttpClient.newInstance("FantaGift");
        //Context = context;
        try
        {
            client = new DefaultHttpClient();
            //parser = SAXParserFactory.newInstance().newSAXParser();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            serializer = factory.newSerializer();
            method = null;
            parameters = new HashMap<String, Object>();
        }
        catch(Exception ex)
        {

        }
    }

    public void setMethod(String name)
    {
        method = name;
        parameters.clear();
    }

    public void setParameter(String name, String value)
    {
        parameters.put(name, value);
    }

    public void setParameter(String name, int value)
    {
        parameters.put(name, Integer.toString(value));
    }

    public void setParameter(String name, float value)
    {
        parameters.put(name, Float.toString(value));
    }

    public void setParameter(String name, boolean value)
    {
        parameters.put(name, Boolean.toString(value));
    }

    public void setParameter(String name, byte[] value)
    {
        parameters.put(name, Base64.encodeToString(value, Base64.DEFAULT));
    }

    public void setParameter(String name, String[] value)
    {
        parameters.put(name, value);
    }

    private void InvokeMethod(SoapResultHandler handler) throws LicensingException
    {
        try
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            serializer.setOutput(stream, "utf-8");
            serializer.startDocument("utf-8", null);
            serializer.startTag(null, "soap:Envelope");
            serializer.attribute(null, "xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute(null, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute(null, "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.startTag(null, "soap:Body");
            serializer.startTag(null, method);
            serializer.attribute(null, "xmlns", "http://tempuri.org/");
            for(String parameter : parameters.keySet())
            {
                serializer.startTag(null, parameter);
                Object obj = parameters.get(parameter);
                if(obj instanceof String)
                {
                    serializer.text(parameters.get(parameter).toString());
                }
                else if(obj instanceof String[])
                {
                    for(String l : (String[])obj)
                    {
                        serializer.startTag(null, "long");
                        serializer.text(l);
                        serializer.endTag(null, "long");
                    }
                }
                serializer.endTag(null, parameter);
            }
            serializer.endTag(null, method);
            serializer.endTag(null, "soap:Body");
            serializer.endTag(null, "soap:Envelope");
            serializer.endDocument();
            //HttpPost request = new HttpPost("http://192.168.190.101/GiftOrgTest/GiftOrgService.asmx");
            //HttpPost request = new HttpPost("http://192.168.190.101/GiftOrgTest/GiftOrgService.asmx");
            //HttpPost request = new HttpPost("http://www.gift-organizer.com/GiftOrgService.asmx");

            //HttpPost request = new HttpPost("http://192.168.190.101/rPOSWebService/Service.asmx");
            HttpPost request = new HttpPost("http://www.outsourcingitservices.net/rPOSWebService/Service.asmx");

            //HttpPost request = new HttpPost("http://www.outsourcingitservices.net/GiftOrgTest/GiftOrgService.asmx");
            request.addHeader("SOAPAction", "http://tempuri.org/" + method);
            ByteArrayEntity entity = new ByteArrayEntity(stream.toByteArray());
            entity.setContentType("text/xml");
            entity.setContentEncoding("utf-8");
            request.setEntity(entity);

            //StringBuffer result = new StringBuffer();
            parser.setInput(client.execute(request).getEntity().getContent(), null);
            String[] successtags = new String[] {"soap:Envelope", "soap:Body", "%sResponse", "%sResult"};
            String[] errortags = new String[] {"soap:Envelope", "soap:Body", "soap:Fault"};
            for(int i=0; i<successtags.length; i++)
            {
                successtags[i] = String.format(successtags[i], method);
            }
            int tag = 0;
            boolean res = true;
            boolean error = false;
            String faultcode = null;
            String faultstring = null;
            String faultdetail = null;
            String errortagname = "";
            int type = parser.next();
            while(res && type != XmlPullParser.END_DOCUMENT)
            {
                switch(type)
                {
                    case XmlPullParser.START_TAG:
                        if(!error && tag < successtags.length)
                        {
                            if(!parser.getName().equals(successtags[tag]))
                            {
                                if(parser.getName().equals(errortags[tag]))
                                {
                                    error = true;
                                }
                                else
                                {
                                    res = false;
                                }
                            }
                        }
                        if(!error && tag >= successtags.length)
                        {
                            handler.startTag(parser.getName());
                        }
                        if(error)
                        {
                            errortagname = parser.getName();
                        }
                        tag++;
                        break;

                    case XmlPullParser.END_TAG:
                        tag--;
                        if(!error && tag >= successtags.length)
                        {
                            handler.endTag(parser.getName());
                        }
                        if(error)
                        {
                            errortagname = "";
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(!error && tag >= successtags.length)
                        {
                            handler.text(parser.getText());
                        }
                        if(error)
                        {
                            if(errortagname.compareToIgnoreCase("faultcode") == 0)
                            {
                                faultcode += parser.getText();
                            }
                            else if(errortagname.compareToIgnoreCase("faultstring") == 0)
                            {
                                faultstring += parser.getText();
                            }
                            else if(errortagname.compareToIgnoreCase("detail") == 0)
                            {
                                faultdetail += parser.getText();
                            }
                        }
                        break;

                }
                type = parser.next();
            }
            if(error)
            {
                throw new LicensingException(faultcode, faultstring);
            }
            //parser.parse(client.execute(request).getEntity().getContent(), handler);

        }
        catch(IOException ex)
        {
            //try
            //{
            //throw ex;
            //}
            //catch(Exception e)
            //{

            //}
            throw new LicensingException(ex.getClass().getName(), ex.getMessage());
            //ex.printStackTrace();
        } catch (IllegalStateException ex) {
            // TODO Auto-generated catch block
            throw new LicensingException(ex.getClass().getName(), ex.getMessage());
            //ex.printStackTrace();
        } catch (XmlPullParserException ex) {
            // TODO Auto-generated catch block
            throw new LicensingException(ex.getClass().getName(), ex.getMessage());
            //ex.printStackTrace();
        }

    }

    public String InvokeAsString() throws LicensingException
    {
        final StringBuffer result = new StringBuffer();
        InvokeMethod(new SoapResultHandler() {

            public void startTag(String name)
            {
                result.append("<" + name + ">");
            }

            public void endTag(String name)
            {
                result.append("</" + name + ">");
            }

            public void text(String text)
            {
                result.append(text);
            }

        });
        return result.toString();
    }

    public int InvokeAsInt() throws NumberFormatException, LicensingException
    {
		final StringBuffer resultbuffer = new StringBuffer();
		int result = -1;
		InvokeMethod(new SoapResultHandler() {
			
			public void startTag(String name)
			{
				//resultbuffer.append("<" + name + ">");
			}
			
			public void endTag(String name)
			{
				//resultbuffer.append("</" + name + ">");
			}
			
			public void text(String text)
			{
				resultbuffer.append(text);
			}

		});
        return Integer.parseInt(resultbuffer.toString());
    }

    public byte[] InvokeAsBinary() throws LicensingException
    {
        return Base64.decode(InvokeAsString(), Base64.DEFAULT);
    }

    public HashMap<String, ArrayList<HashMap<String, String>>> InvokeAsDataSet() throws LicensingException
    {
        final HashMap<String, ArrayList<HashMap<String, String>>> result = new HashMap<String, ArrayList<HashMap<String, String>>>();
        InvokeMethod(new SoapResultHandler() {

            String[] tags = new String[] {"diffgr:diffgram", "NewDataSet"};
            int tag = 0;
            boolean[] nameflags = new boolean[] {false, false};
            boolean res = false;
            ArrayList<HashMap<String, String>> table = null;
            HashMap<String, String> row = null;
            StringBuffer column = null;

            public void startTag(String name)
            {
                if(tag < tags.length)
                {
                    nameflags[tag] = name.equals(tags[tag]);
                    res = true;
                    for(int i=0; i<nameflags.length; i++)
                    {
                        if(i <= tag)
                        {
                            res &= nameflags[i];
                        }
                    }
                }
                if(res)
                {
                    if(tag == tags.length)
                    {
                        table = result.get(name);
                        if(table == null)
                        {
                            table = new ArrayList<HashMap<String, String>>();
                            result.put(name, table);
                        }
                        row = new HashMap<String, String>();
                        table.add(row);
                    }
                    else if(tag == tags.length + 1)
                    {
                        column = new StringBuffer();
                    }
                }
                tag++;
            }

            public void endTag(String name)
            {
                tag--;
                if(res && tag == tags.length + 1)
                {
                    row.put(name, column.toString());
                    column = null;
                }
            }

            public void text(String text)
            {
                if(res && column != null)
                {
                    column.append(text);
                }
            }

        });
        return result;
    }

    public ArrayList<HashMap<String, Object>> InvokeAsMap() throws LicensingException
    {
        final ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        InvokeMethod(new SoapResultHandler() {
            //HashMap<String, Object> MapBuf = new HashMap<String, Object>();
            StringBuffer Temp=null;
            Stack<Map<String, Object>> Position = new Stack<Map<String, Object>>();
            HashMap<String, Object> TempResult = new HashMap<String, Object>();

            public void startTag(String name)
            {
                Temp = new StringBuffer();
                ArrayList<Map> List = new ArrayList<Map>();
                TempResult = new HashMap<String, Object>();
                TempResult.put("Elements", List);
                TempResult.put("Name",name);
                if(!Position.isEmpty())
                    ((List)Position.peek().get("Elements")).add(TempResult);
                else
                    result.add(TempResult);
                Position.push(TempResult);
                //result.append("<" + name + ">");
            }

            public void endTag(String name)
            {
                //result.append("</" + name + ">");
                TempResult.put("Text",Temp.toString());
                Position.pop();

            }

            public void text(String text)
            {
                //result.append(text);
                Temp.append(text);
            }

        });
        return result;
    }



    public ArrayList<Category> InvokeAsList() throws LicensingException
    {

        final ArrayList<Category> result = new ArrayList<Category>();
        InvokeMethod(new SoapResultHandler() {
            //HashMap<String, Object> MapBuf = new HashMap<String, Object>();
            Category category;
            Product product;
            StringBuffer textAlt = new StringBuffer();

            public void startTag(String name)
            {
                if (parser.getName().equalsIgnoreCase("category")) {
                    category = new Category();
                }
                else if (parser.getName().equalsIgnoreCase("product")) {
                    product = new Product();
                }
                textAlt.delete(0, textAlt.length());
            }

            public void endTag(String name)
            {
                if (parser.getName().equalsIgnoreCase("category")) {
                    result.add(category);
                    category = null;
                }
                else if(parser.getName().equalsIgnoreCase("product")){
                    category.addProduct(product);
                    product = null;
                }
                else if (product != null){
                    if (parser.getName().equalsIgnoreCase("name")) {
                        product.setName(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("description")) {
                        product.setDescription(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("price")){
                        product.setPrice(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("Image")) {
                        product.setThumbnail(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("ID")){
                        product.setId(Integer.parseInt(textAlt.toString()));
                    }
                }
                else if (category != null) {
                    if (parser.getName().equalsIgnoreCase("name")) {
                        category.setName(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("ProductsCount")) {
                        category.setProductsCount(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("Image")) {
                        category.setThumbnail(textAlt.toString());
                    } else if (parser.getName().equalsIgnoreCase("ID")){
                        category.setId(Integer.parseInt(textAlt.toString()));
                    }
                }
            }

            public void text(String text)
            {
                textAlt.append(parser.getText());
            }

        });
        return result;
    }

}
