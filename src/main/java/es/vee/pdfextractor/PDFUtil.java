/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.vee.pdfextractor;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.util.PDFTextStripperByArea;

/**
 *
 * @author Juan Pablo López Pulpillo <juanpablo.lopez@itc2.com>
 */
public class PDFUtil {

	public static Color extractColor(PDAnnotation annot) {
		PDGamma colour = annot.getColour();
		return new Color(colour.getR(), colour.getG(), colour.getB());
	}

	public static PDFText extractHighlightText(PDPage page, PDAnnotation pdfAnnot, Color textColor) throws IOException {

		PDFTextStripperByArea stripper = new PDFTextStripperByArea();

		PDRectangle rect = pdfAnnot.getRectangle();
		stripper.setSortByPosition(true);
		stripper.addRegion(Integer.toString(0), getRectangle(page, rect));
		stripper.extractRegions(page);

		String textForRegion = removeEndOfLineCharacters(stripper.getTextForRegion(Integer.toString(0)));

		return new PDFText(textForRegion, PDFText.TextType.TEXT,PDFUtil.extractColor(pdfAnnot),textColor ,rect);
	}

	private static Rectangle2D.Float getRectangle(PDPage page, PDRectangle rect) {

		float x = rect.getLowerLeftX() - 1;
		float y = rect.getUpperRightY() - 1;
		float width = rect.getWidth() + 2;
		float height = rect.getHeight() + rect.getHeight() / 4;
		int rotation = page.findRotation();
		if (rotation == 0) {
			PDRectangle pageSize = page.findMediaBox();
			y = pageSize.getHeight() - y;
		}
		Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);

		return awtRect;

	}

	public static PDFText extractNoteText(PDAnnotation pdfAnnot, Color textColor) throws IOException {

		PDRectangle rect = pdfAnnot.getRectangle();
		return new PDFText(removeEndOfLineCharacters(pdfAnnot.getContents()), PDFText.TextType.NOTE,Color.GRAY,textColor, rect);
	}

	public static PDFText extractText(PDPage page, PDAnnotation pdfAnnot, Color textColor) throws IOException {

		PDFText pdfText = new PDFText("", PDFText.TextType.EMPTY,Color.black, textColor, pdfAnnot.getRectangle());
					
		if (pdfAnnot.getSubtype().equals("Highlight")) {
			pdfText = PDFUtil.extractHighlightText(page, pdfAnnot,textColor);
		} else if (pdfAnnot.getSubtype().equals("Text")) {
			if (pdfAnnot.getContents() != null) {
				pdfText = PDFUtil.extractNoteText(pdfAnnot,textColor);
			}
		}

		return pdfText;
	}

	private static String removeEndOfLineCharacters(String str){
		return str.replaceAll("(\\r|\\n)", " ");
	}
}
