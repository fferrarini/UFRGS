package br.ufrgs.enq;



import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;

import org.jfree.chart.JFreeChart;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFChart {

	public static void createPDFChart(JFreeChart chart, String name) {
		chart.setTitle((String) null);
		if (!name.toLowerCase().endsWith(".pdf"))
			name += ".pdf";

		String folder = "ImagesPDF";
		File folderDiagonalPDF = new File(folder);
		if (!folderDiagonalPDF.exists())
			folderDiagonalPDF.mkdir();

		// Finally create a PDF for it
		try {
			FileOutputStream outputStream = new FileOutputStream(folder + "/"
					+ name);

			int width = 480;
			int height = 400;
			Document document = new Document(new Rectangle(width,height));
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tp = cb.createTemplate(width, height);
			Graphics2D g2d = tp.createGraphics(width, height,
					new DefaultFontMapper());

			chart.draw(g2d, new Rectangle2D.Double(0, 0, width, height));

			g2d.dispose();
			cb.addTemplate(tp, 0, 0);
			document.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());

		}
	}
	
}