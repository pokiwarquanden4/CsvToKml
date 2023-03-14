package com.example.converttokml.Service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

@Service
@Getter
@Setter
public class Convert {
    private ArrayList<String> headers;
    private ArrayList<ArrayList<Object>> values;
    public void createFile(String fileName) {
        String kmlPath = "C:/Users/Admin/Desktop/IT/ConvertToKML/KmlFile/" + fileName + ".kml";

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element kml = document.createElement("kml");
            kml.setAttribute("xmlns", "http://earth.google.com/kml/2.0");
            kml.setAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2");
            kml.setAttribute("xmlns:kml", "http://www.opengis.net/kml/2.2");
            kml.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
            document.appendChild(kml);

            // doc element
            Element docs = document.createElement("Document");
            kml.appendChild(docs);

            // name
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(fileName));
            docs.appendChild(name);

            // open
            Element open = document.createElement("open");
            open.appendChild(document.createTextNode("1"));
            docs.appendChild(open);

            // Schema
            Element schema = document.createElement("Schema");
            schema.setAttribute("name", fileName);
            schema.setAttribute("id", "S_" + fileName + "_SSSIIDDII");
            docs.appendChild(schema);

            for (int i=0; i<headers.size(); i++){
                Element simpleField = document.createElement("SimpleField");
                simpleField.setAttribute("id", "S_pge_SSSIIDDII");

                Element displayName = document.createElement("displayName");
                displayName.appendChild(document.createTextNode(headers.get(i)));
                simpleField.appendChild(displayName);

                schema.appendChild(simpleField);
            }

            //Folder
            Element folder = document.createElement("Folder");
            docs.appendChild(folder);

            //Folder -> name
            Element folderName = document.createElement("name");
            folderName.appendChild(document.createTextNode(fileName));
            folder.appendChild(folderName);

            //Placemark
            for(int i=0; i<values.size() ; i++){
                String longitud = "";
                String latitud = "";

                Element placemark = document.createElement("Placemark");
                folder.appendChild(placemark);

                Element extendedData = document.createElement("ExtendedData");
                placemark.appendChild(extendedData);

                Element schemaData = document.createElement("SchemaData");
                schemaData.setAttribute("schemaUrl" , "#S_" + fileName + "_SSSIIDDII");
                extendedData.appendChild(schemaData);

                Element point = document.createElement("Point");
                placemark.appendChild(point);

                Element coordinates = document.createElement("coordinates");


                for (int j=0 ; j<values.get(i).size() ; j++){


                    Element simpleData = document.createElement("SimpleData");
                    simpleData.setAttribute("name", headers.get(j));
                    simpleData.appendChild(document.createTextNode(String.valueOf(values.get(i).get(j))));
                    extendedData.appendChild(simpleData);

                    if(headers.get(j).equalsIgnoreCase("Longitud")){
                        longitud = String.valueOf(values.get(i).get(j));
                        System.out.println(longitud);
                        System.out.println(longitud.length());
                    }
                    if(headers.get(j).equalsIgnoreCase("Latitud")){
                        latitud = String.valueOf(values.get(i).get(j));
                        System.out.println(latitud);
                        System.out.println(latitud.length());
                    }
                    if(longitud.length() != 0 && latitud.length() != 0){
                        System.out.println("ressult");
                        coordinates.appendChild(document.createTextNode(longitud + "," + latitud + ",0"));
                        longitud = "";
                        latitud = "";
                        point.appendChild(coordinates);
                    }
                }
            }

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(kmlPath));
            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void readFile(String fileName) throws FileNotFoundException {
        //Reset ArrayList
        headers = new ArrayList<>();
        values = new ArrayList<>();

        //Read file logic
        boolean firstLine = true;
        File file = new File("C:/Users/Admin/Desktop/IT/ConvertToKML/CsvFile/" + fileName + ".csv");
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()){
            String data = myReader.nextLine();
            if (firstLine){
                firstLine = false;
                String[] header = data.split(",");
                for (String input : header){
                    headers.add(convertString(input));
                }
            }else {
                ArrayList<Object> innerValue = new ArrayList<>();
                Object[] value = data.split(",");
                for (Object input : value){
                    innerValue.add(input);
                }
                values.add(innerValue);
            }
        }
    }

    public String convertString(String value){
        return value.replaceAll(" ", "_");
    }
}
