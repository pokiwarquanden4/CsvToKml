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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
        String xmlFilePath = "C:/Users/Admin/Desktop/IT/ConvertToKML/KmlFile/" + fileName + ".xml";

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("root");
            document.appendChild(root);

            for (int i=0; i<headers.size(); i++){
                System.out.println(headers.get(i));
                Element input = document.createElement(headers.get(i));
                for (ArrayList<Object> value : values){
                    input.appendChild(document.createTextNode(String.valueOf(value.get(i))));
                }
                root.appendChild(input);
            }

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

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
