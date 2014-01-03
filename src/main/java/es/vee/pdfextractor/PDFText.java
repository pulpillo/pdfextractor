/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.vee.pdfextractor;

import com.lowagie.text.Font;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.common.PDRectangle;


/**
 *
 * @author Juan Pablo López Pulpillo <juanpablo.lopez@itc2.com>
 */
public class PDFText {

	public enum TextType {

		TITLE,
		TEXT,
		NOTE,
		EMPTY
	}
	
	private String text;
	private TextType type;
	private PDRectangle rect;
	private Color color;

	public PDFText(String text, TextType type, Color color, Color textColor ,PDRectangle rect) {
		if(color.equals(textColor)){
			color = Color.black;	
		}else{
			if(type.equals(TextType.TEXT)){
				type = TextType.TITLE;
			}
		}

		this.text = text;
		this.type = type;
		this.rect = rect;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public TextType getType() {
		return type;
	}

	public Float getOrder(){
		return rect.getUpperRightX();
	}	

	public Color getColor(){
		return color;
	}

	public Font getFont(){
		Font pFont = new Font();
		pFont.setColor(color);
		switch(type){
			case NOTE:
				pFont.setStyle(Font.ITALIC);
				break;
			case TITLE:
				pFont.setStyle(Font.BOLD);
				break;
		}
		
		return pFont;
		
	}
	
}
