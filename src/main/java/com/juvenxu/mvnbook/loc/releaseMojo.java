package com.juvenxu.mvnbook.loc;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Goal which counts lines of code of a project
 *
 * @goal release
 */
public class releaseMojo
        extends AbstractMojo {


    /**
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDirectory;

    /**
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    private File testSourceDirectory;

    /**
     * @parameter expression="${project.build.resources}"
     * @required
     * @readonly
     */
    private List<Resource> resources;

    /**
     * @parameter expression="${project.build.testResources}"
     * @required
     * @readonly
     */
    private List<Resource> testResources;

    /**
     * The file types which will be included for counting
     *
     * @parameter
     */
    private String[] includes;

    private static final String[] INCLUDES_DEFAULT = {"center.common.api.version", "center.config.api.version"};

    public void execute()
            throws MojoExecutionException {

        getLog().info("replace pom begin");
        getLog().info("basedir=" + basedir);

        if (includes == null || includes.length == 0) {
            includes = INCLUDES_DEFAULT;
        }
        getLog().info("includes=" + Arrays.asList(includes).toString());

        try {
            // File baseDirParentFile = new File(basedir.getParent());
            // System.out.println("baseDirParentFile=" + baseDirParentFile);
            List<File> collected = new ArrayList<File>();
            collectFiles(collected, basedir, new Integer(2));//1代表一层，只替换 baseDir下的pom文件
            getLog().info("pom path=" + collected.toString());

            for (File sourceFile : collected) {
                replacePom(sourceFile);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to replace pom.", e);
        }
        getLog().info("replace pom end");

    }

    private void collectFiles(List<File> collected, File file, Integer recursionTimes) {
        recursionTimes--;
        if (recursionTimes < 0) return;
        if (file.isFile()) {
            if (file.getName().equals("pom.xml")) {
                collected.add(file);
            }
        } else {
            for (File sub : file.listFiles()) {
                collectFiles(collected, sub, recursionTimes);
            }
        }
    }

    private void replacePom(File pomPath) {
        if (!pomPath.exists()) {
            return;
        }
//        List<String> propertyList = new ArrayList<>();
//        propertyList.add("center.common.api.version");
//        propertyList.add("center.config.api.version");
//        propertyList.add("version");

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(pomPath);
            for (String property : includes) {
                replaceProperties(doc, property);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            // 设置编码类型
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StreamResult result = new StreamResult(new FileOutputStream(pomPath));
            transformer.transform(domSource, result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void replaceProperties(Document doc, String propertiy) {
        NodeList list = doc.getElementsByTagName(propertiy);
        for (int i = 0; i < list.getLength(); i++) {
            Element version = (Element) list.item(i);
            String versionText = version.getTextContent();
            version.setTextContent(versionText.replace("-SNAPSHOT", ""));
        }
    }

}
