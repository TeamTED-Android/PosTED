package com.example.todor.restourantexample;

/**
* Created by todor on 21.07.14.
*/
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLPullParser {
    ArrayList<Category> categories;
    private Category category;
    private String text;
    private Product product;

    public XMLPullParser() {
        categories = new ArrayList<Category>();
    }



    public ArrayList<Category> parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("category")) {
                            category = new Category();
                        }
                        else if (tagname.equalsIgnoreCase("product")) {
                            product = new Product();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("category")) {
                            categories.add(category);
                        }
                        else if(tagname.equalsIgnoreCase("product")){
                            category.addProduct(product);
                            product = null;
                        }
                        else if (product != null){
                            if (tagname.equalsIgnoreCase("name")) {
                                product.setName(text);
                            } else if (tagname.equalsIgnoreCase("description")) {
                                product.setDescription(text);
                            } else if (tagname.equalsIgnoreCase("price")){
                                product.setPrice(text);
                            } else if (tagname.equalsIgnoreCase("Thumbnail")) {
                                product.setThumbnail(text);
                            }
                        }
                        else if (tagname.equalsIgnoreCase("name")) {
                            category.setName(text);
                        } else if (tagname.equalsIgnoreCase("ProductsCount")) {
                            category.setProductsCount(text);
                        } else if (tagname.equalsIgnoreCase("Thumbnail")) {
                            category.setThumbnail(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return categories;
    }
}