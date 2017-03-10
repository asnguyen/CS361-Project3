import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


public class Steganography
{
	String filename = "";
	int numPixel    = 0;
	int imageHeight = 0;
	int imageWidth  = 0;

	public static void main(String[] args) throws java.io.IOException
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File("inputImage.bmp"));
		}
		catch(IOException e)
		{

		}
		int height = img.getHeight();
		int width  = img.getWidth();

		int amountPixel = height * width;

		System.out.println(height + " " + width + " " + img.getRGB(30,30));

		int[] rgbData = img.getRGB(0,0,width,height,null,0,width);
		int y = 1;
		int x = 1;
		int colorRed   = (rgbData[y*width+x] >> 16 ) & 0xFF;
		int colorGreen = (rgbData[y*width+x] >> 8  ) & 0xFF;
		int colorBlue  = (rgbData[y*width+x]       ) & 0xFF;

		System.out.println(colorRed);
		System.out.println(colorGreen);
		System.out.println(colorBlue);
	}

}





















