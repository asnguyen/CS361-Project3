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
		String newfile="";

		if(args[0].contains("E"))
		{
			//encode
			try
			{
				String[] filename = (args[1].split("\\."));
				newfile = filename[0]+"-steg."+filename[1];
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

			encode(rgbData,args[1],args[2],newfile);





		}
		else if (args[0].contains("D"))
		{
			//decode
			try
			{
				img = ImageIO.read(new File(args[1]));

			}
			catch(IOException e){}
			height      = img.getHeight();
			width       = img.getWidth();
			amountPixel = height * width;
			rgbData = img.getRGB(0,0,width,height,null,0,width);

			decode(rgbData,args[1],args[2]);
			
		}

	}

	public static void encode(int[] rgb, String image, String message, String output)
	{
		int pixelX  = 0;
		int pixelY  = 0;
		int colorflag    = 0; 			// colorflag % 3  = 0 red 1 green 2 blue
		boolean flag = false;					// flag 0 means even flag 1 means odd
		BufferedImage newImage = null;
		Scanner sc = null;
		try
		{
			newImage = ImageIO.read(new File(image));
			sc =new Scanner(new File(message));
		}
		catch(IOException e){}
		
		String[] file = image.split("\\.");
		
		//encoding the message
		while(sc.hasNextLine())
		{
			String s = sc.nextLine();
			//System.out.println("*");
			String binaryString = toBinary(s).toString()+"\n";
			System.out.println(binaryString);
			while(binaryString.length()>0)
			{
				//checking the bit 
				char c = binaryString.charAt(0);
				if(c == '1')
					flag = true;
				if(c == '0')
					flag = false;
				//getting the argb value of the pixel
				int colorRed   = extractRed(rgb,pixelX,pixelY);
				int colorGreen = extractGreen(rgb,pixelX,pixelY);
				int colorBlue  = extractBlue(rgb,pixelX,pixelY);
				int alpha      = (rgb[pixelY*width+pixelX] >> 24) & 0xFF;
				//encoding the bit to the pixel
				switch(colorflag % 3)
				{
					case 0: //red
						System.out.println("RED");
						if(colorRed % 2 ==0)		//colorRed is even
						{
							System.out.println(0);
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
							System.out.println(1);
							if(flag)				//bit is a 1 so colorRed needs to be odd
							{
								colorRed +=0;
							}
							else					//bit is a 1 so colorRed needs to be even
							{
								colorRed++;
							}

						}
						colorflag++;
						break;
					case 1: //green
						System.out.println("GREEN");
						if(colorGreen % 2 ==0)		//colorGreen is even
						{
							System.out.println(0);
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
							System.out.println(1);
							if(flag)				//bit is a 1 so colorGreen needs to be odd
							{
								colorGreen+=0;
							}
							else					//bit is a 1 so colorGreen needs to be even
							{
								colorGreen++;
							}

						}
						colorflag++;
						break;
					case 2: //blue
						System.out.println("BLUE");
						if(colorBlue % 2 ==0)		//colorBlue is even
						{
							System.out.println(0);
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
							System.out.println(1);
							if(flag)				//bit is a 1 so colorBlue needs to be odd
							{
								colorBlue+=0;
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
					pixelX++;
					if(pixelX==width)
					{
						pixelX = 0;
						pixelY++;
					}
					//ImageIO.write(newImage,"bmp",f);    <------- might need that 

				}
				//next bit
				if(binaryString.length()>1)
					binaryString = binaryString.substring(1);
				else
					binaryString = "";

			}
		}
		//encode the end of file byte
		for(int i = 0;i<8;++i)
		{
			//since the zero byte is all zero
			flag = false;
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
								colorRed +=0;
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
								colorGreen+=0;
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
								colorBlue+=0;
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
				//ImageIO.write(newImage,"bmp",f);    <------- might need that 
			}
		}
		try
		{
			ImageIO.write(newImage,file[1],new File(output));
		}
		catch(IOException e){}
	}

	public static void decode(int[] rgb, String image, String output)
	{
		BufferedImage newImage  = null;
		FileWriter newFile = null;
		String sbyte="";
		String out  ="";
		int zeroCount = 0;
		boolean endofFile = false;
		try
		{
			newImage = ImageIO.read(new File(image));
			newFile  = new FileWriter(new File(output));
		}
		catch(IOException e){}
		for(int i = 0;i<height;++i)
		{
			//System.out.print("*");
			for(int j=0;j<width;++j)
			{
				int colorRed   = extractRed(rgb,j,i);
				int colorGreen = extractGreen(rgb,j,i);
				int colorBlue  = extractBlue(rgb,j,i);
				//Red's bit
				if(!endofFile)
				{
					if(sbyte.length()<8)
					{
						int temp = colorRed%2;
						if(temp==0)
						{
							zeroCount++;
							if(zeroCount == 8)
							{
								endofFile =true;
							}
						}
						if(temp==1)
						{
							zeroCount=0;
						}
						sbyte+=(""+temp);
						if(sbyte.length()==8)
						{
							char c = (char)Integer.parseInt(sbyte,2);
							out+=c;
							//System.out.println(out);
							sbyte = "";
						}
					}
				}
				//Green's bit
				if(!endofFile)
				{
					if(sbyte.length()<8)
					{
						int temp = colorGreen%2;
						if(temp==0)
						{
							zeroCount++;
							if(zeroCount ==8)
								endofFile =true;
						}
						if(temp==1)
							zeroCount=0;
						sbyte+=(""+temp);
						if(sbyte.length()==8)
						{
							char c = (char)Integer.parseInt(sbyte,2);
							out+=c;
							sbyte = "";
						}
					}
				}
				//Blue's bit
				if(!endofFile)
				{
					if(sbyte.length()<8)
					{
						int temp = colorBlue%2;
						if(temp==0)
						{
							zeroCount++;
							if(zeroCount ==8)
								endofFile =true;
						}
						if(temp==1)
							zeroCount=0;
						sbyte+=(""+temp);
						if(sbyte.length()==8)
						{
							char c = (char)Integer.parseInt(sbyte,2);
							out+=c;
							sbyte = "";
						}
					}
				}
			}	
		}
		try
		{
			newFile.write(out);
		}
		catch(IOException e){}
		

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






















