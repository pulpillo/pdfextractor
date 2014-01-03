/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.vee.pdfextractor;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

/**
 *
 * @author Juan Pablo López Pulpillo <juanpablo.lopez@itc2.com>
 */
public class PDFExtractor {

	public enum TextType {

		TITLE,
		TEXT,
		NOTE
	}
	private File pdfFile;
	private List<PDFText> texts = new ArrayList<PDFText>();
	private boolean extracted = false;
	private Color textColor = new Color(255,237,0); //Default color yellow

	public static PDFExtractor create(File file) throws IOException{
		return new PDFExtractor(file);
	}

	public static PDFExtractor create(File file, Color textColor) throws IOException{
		return new PDFExtractor(file,textColor);
	}
	
	protected PDFExtractor(File file) throws IOException {
		pdfFile = file;
	}

	protected PDFExtractor(File file, Color textColor) throws IOException{
		this(file);
		this.textColor = textColor;
	}

	public List<PDFText> extract() {

		if (isNotExtracted()) {
			extractTexts();
		}

		return Collections.unmodifiableList(texts);
	}

	private boolean isNotExtracted(){
		return !extracted;
	}

	private void extractTexts() {

		extracted = true;
		PDDocument pdf = null;
		try {

			pdf = PDDocument.load(pdfFile);
			List<PDPage> pages = getPDFPages(pdf);
			
			for (PDPage page : pages) {
				extractPageTexts(page);
			}
		} catch (IOException ex) {
			Logger.getLogger(PDFExtractor.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				if (pdf != null) {
					pdf.close();
				}
			} catch (IOException ex) {
			}
		}

	}

	private List<PDPage> getPDFPages(PDDocument pdf) {
		List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();
		return pages;

	}

	private void extractPageTexts(PDPage page) throws IOException {

		List<PDAnnotation> la = page.getAnnotations();
		for (PDAnnotation pdfAnnot : la) {
			
			PDFText pdfText = PDFUtil.extractText(page, pdfAnnot , textColor);
			if (!pdfText.getType().equals(PDFText.TextType.EMPTY)) {
				Color extractColor = PDFUtil.extractColor(pdfAnnot);
				texts.add(pdfText);
			}
		}
	}
}
