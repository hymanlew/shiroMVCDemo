//package hyman.utils;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.*;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import java.io.*;
//
//public class PoiUtil {
//
//    /**
//     * Excel 转为　HTML
//     * @param fileName
//     * @param outputFile
//     * @throws FileNotFoundException
//     * @throws IOException
//     * @throws ParserConfigurationException
//     * @throws TransformerConfigurationException
//     * @throws TransformerException
//     */
//    public static void excelToHtml(String fileName, String outputFile)
//            throws FileNotFoundException, IOException, ParserConfigurationException,
//            TransformerConfigurationException, TransformerException {
//        InputStream is = new FileInputStream(fileName);
//
//        HSSFWorkbook excelBook = new HSSFWorkbook(is);
//
//        ExcelToHtmlConverter ethc = new ExcelToHtmlConverter(
//                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
//        ethc.setOutputColumnHeaders(false);
//        ethc.setOutputRowNumbers(false);
//
//        ethc.processWorkbook(excelBook);
//
//        Document htmlDocument = ethc.getDocument();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        DOMSource domSource = new DOMSource(htmlDocument);
//        StreamResult streamResult = new StreamResult(out);
//
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer serializer = tf.newTransformer();
//        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//        serializer.setOutputProperty(OutputKeys.METHOD, "html");
//        serializer.transform(domSource, streamResult);
//        out.close();
//
//        String htmlStr = new String(out.toByteArray());
//
//        htmlStr = htmlStr.replace("<h2>Sheet1</h2>", "")
//                .replace("<h2>Sheet2</h2>", "")
//                .replace("<h2>Sheet3</h2>", "")
//                .replace("<h2>Sheet4</h2>", "")
//                .replace("<h2>Sheet5</h2>", "");
//
//        writeFile(htmlStr, outputFile);
//    }
//
//    /**
//     * Word 转为　HTML
//     *
//     * @param fileName
//     * @param outputFile
//     * @throws IOException
//     * @throws ParserConfigurationException
//     * @throws TransformerException
//     */
//    public static void wordToHtml(String fileName, String outputFile) throws
//            IOException, ParserConfigurationException, TransformerException {
//        HWPFDocument wordDoc = new HWPFDocument(new FileInputStream(fileName));
//
//        WordToHtmlConverter wthc = new WordToHtmlConverter(
//                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
//
//        wthc.setPicturesManager(new PicturesManager() {
//
//            @Override
//            public String savePicture(byte[] bytes, PictureType pt, String string, float f, float f1) {
//                return string;
//            }
//
//        });
//
//        wthc.processDocument(wordDoc);
//        List<Picture> pics = wordDoc.getPicturesTable().getAllPictures();
//        if (null != pics && pics.size() > 0) {
//            for (Picture pic : pics) {
//                pic.writeImageContent(new FileOutputStream(pic.suggestFullFileName()));
//            }
//        }
//
//        Document htmlDocument = wthc.getDocument();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        DOMSource domSource = new DOMSource(htmlDocument);
//        StreamResult streamResult = new StreamResult(out);
//
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer serializer = tf.newTransformer();
//        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//        serializer.setOutputProperty(OutputKeys.METHOD, "html");
//        serializer.transform(domSource, streamResult);
//
//        out.close();
//        String htmlStr = new String(out.toByteArray());
//        writeFile(htmlStr, outputFile);
//    }
//
//    public static void writeFile(String content, String path) {
//        FileOutputStream fos = null;
//        BufferedWriter bw = null;
//        File file = new File(path);
//        try {
//            fos = new FileOutputStream(file);
//
//            bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
//            bw.write(content);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(PoiUtil.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(PoiUtil.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(PoiUtil.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                if (null != bw) {
//                    bw.close();
//                }
//                if (null != fos) {
//                    fos.close();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(PoiUtil.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//    /**
//     * @param args the command line arguments
//     * @throws TransformerException
//     * @throws ParserConfigurationException
//     * @throws IOException
//     * @throws TransformerConfigurationException
//     * @throws FileNotFoundException
//     */
//    public static void main(String[] args) throws FileNotFoundException, TransformerConfigurationException, IOException, ParserConfigurationException, TransformerException {
////        PoiUtil.excelToHtml("d:/工作记录表_黄鑫_201802.xls", "d:/test1.html");
//        PoiUtil.wordToHtml("d:/2.doc", "d:/test123.html");
//    }
//}
