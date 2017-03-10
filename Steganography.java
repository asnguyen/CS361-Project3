import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


public class Steganography
{
	static int width;
	static int height;
	static int[] rgbData;
	static int bitCount;
	static int amountPixel;
	public static void main(String[] args) throws java.io.IOException
	{
		//arg[0] encode or decode
		//arg[1] image file
		//arg[2] message file
		FileInputStream fstream;
		Scanner sc;
		FileWriter fw = null;
		BufferedImage img = null;

		if(args[0].contains("E"))
		{
			//encode
			try
			{
				String[] filename = (args[1].split("\\."));
				String newfile = filename[0]+"-steg."+filename[1];
				System.out.println(newfile);
				fstream = new FileInputStream(args[2]);
				img     = ImageIO.read(new File(args[1]));
				fw      = new FileWriter(newfile);
				sc      = new Scanner(args[2]);
			}
			catch(IOException e){}
			height      = img.getHeight();
			width       = img.getWidth();
			amountPixel = height * width;
			rgbData = img.getRGB(0,0,width,height,null,0,width);





		}
		else if (args[0].contains("D"))
		{
			//decode
		}

	}

	public static void encode(int[] rgb, String image, String message)
	{
		int pixelX  = 0;
		int pixelY  = 0;
		int colorflag    = 0; 			// colorflag % 3  = 0 red 1 green 2 blue
		int flag = 0;					// flag 0 means even flag 1 means odd
		BufferedImage newImage = ImageIO.write(image);
		Scanner sc =new Scanner(message);
		while(sc.hasNextLine())
		{
			String s = sc.nextLine();
			String binaryString = toBinary(s).toString();
			while(binaryString.length()>0)
			{
				//checking the bit 
				char c = binaryString.char(0);
				if(c == '1')
					flag = 1;
				if(c == '0')
					flag = 0;
				//getting the argb value of the pixel
				int colorRed   = extractRed(rgb,pixelX,pixelY);
				int colorGreen = extractGreen(rgb,pixelX,pixelY);
				int colorBlue  = extractBlue(rgb,pixelX,pixelY);
				int alpha      = (rgb[pixelY*width+pixelX] >> 24) & 0xFF;
				//encoding the bit to the pixel
				switch(colorflag % 3)
				{
					case 0: //red
						if(colorRed % 2 ==0)		//colorRed is even
						{
							if(flag)				//bit is a 1 so colorRed needs to be odd
							{
								colorRed++;
							}
							else					//bit is a 0 so colorRed needs to be even
							{
								colorRed+=0;
							}
						}
						else						//colorRed is odd
						{
							if(flag)				//bit is a 1 so colorRed needs to be odd
							{
								colorRed +=0
							}
							else					//bit is a 1 so colorRed needs to be even
							{
								colorRed++;
							}

						}
						colorflag++;
						break;
					case 1: //green
						if(colorGreen % 2 ==0)		//colorGreen is even
						{
							if(flag)				//bit is a 1 so colorGreen needs to be odd
							{
								colorGreen++;
							}
							else					//bit is a 0 so colorGreen needs to be even
							{
								colorGreen+=0;
							}
						}
						else						//colorGreen is odd
						{
							if(flag)				//bit is a 1 so colorGreen needs to be odd
							{
								colorGreen+=0
							}
							else					//bit is a 1 so colorGreen needs to be even
							{
								colorGreen++;
							}

						}
						colorflag++;
						break;
					case 2: //blue
						if(colorBlue % 2 ==0)		//colorBlue is even
						{
							if(flag)				//bit is a 1 so colorBlue needs to be odd
							{
								colorBlue++;
							}
							else					//bit is a 0 so colorBlue needs to be even
							{
								colorBlue+=0;
							}
						}
						else						//colorBlue is odd
						{
							if(flag)				//bit is a 1 so colorBlue needs to be odd
							{
								colorBlue+=0
							}
							else					//bit is a 1 so colorBlue needs to be even
							{
								colorBlue++;
							}

						}
						colorflag++;
						break;
				}
				//creating the new pixel and adding to the new image
				if(colorflag>=3 && colorflag % 3 ==0)
				{
					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					newImage.setRGB(pixelX,pixelY,newPixel);
				}
				//next bit
				if(binaryString.length>1)
					binaryString = binaryString.substring(1);
				else
					binaryString = "";

			}
		}
		//encode the end of file byte
		
	}


	public static int extractRed(int[] rgb, int x, int y)
	{
		return (rgb[y*width+x] >> 16) & 0xFF;
	}

	public static int extractGreen(int[] rgb, int x, int y)
	{
		return (rgb[y*width+x] >> 8) & 0xFF;
	}

	public static int extractBlue(int[] rgb, int x, int y)
	{
		return (rgb[y*width+x]) & 0xFF;
	}


	public static StringBuilder toBinary(String s)
	{
		byte[] bytes = s.getBytes();
		StringBuilder binary = new StringBuilder();
		for(byte b : bytes)
		{
			int val  = b;
			for(int i=0;i<8;++i)
			{
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;

			}
		}
		return binary;
	}

}






















