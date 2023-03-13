package com.example.converttokml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConvertToKmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvertToKmlApplication.class, args);

        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("kml");
            Attr root_attr = document.createAttribute("xmlns");
            root_attr.setValue("http://earth.google.com/kml/2.0");
            root.setAttributeNode(root_attr);
            document.appendChild(root);

            // doc element
            Element docs = document.createElement("Document");
            root.appendChild(docs);

            // create extendedData
            try (BufferedReader br = new BufferedReader(new FileReader(csv_file))) {
                String line;
                int countLine = 0;
                String[] header = null;
                while ((line = br.readLine()) != null) {

                    String[] values = line.trim().replaceAll("[^a-zA-Z0-9,]", "").split(",");

                    // create simple element
                    if (countLine > 0 && header != null) {

                        // placemark
                        Element placemark = document.createElement("Placemark");
                        docs.appendChild(placemark);

                        // create schema data
                        Element ext = document.createElement("ExtendedData");
                        Element schema = document.createElement("SchemaData");
                        schema.setAttribute("schemaUrl", "#S_pge_SSSIIDDII");
                        ext.appendChild(schema);
                        placemark.appendChild(ext);

                        for (int i = 0; i < values.length; i++) {
                            Element simpleData = document.createElement("SimpleData");
                            simpleData.setAttribute("name", header[i]);
                            simpleData.appendChild(document.createTextNode(values[i]));
                            schema.appendChild(simpleData);
                        }

                        // Point
                        Element point = document.createElement("Point");
                        Element coordinates = document.createElement("coordinates");
                        coordinates.appendChild(document.createTextNode(values[5] + " , " + values[6]));
                        point.appendChild(coordinates);
                        placemark.appendChild(point);

                    } else {
                        header = values;
                    }

                    countLine++;
                }
            }

            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("D:\\ETS Project\\KMZ\\kmz_file\\create_map.kml"));
            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException | TransformerException pce) {
        }
    }

}
