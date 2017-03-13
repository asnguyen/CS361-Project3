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
		File my_file =new File(output);
		int colorRed=0;
		int colorGreen=0;
		int colorBlue=0;
		int alpha=0;
		int color=0;
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
			String binaryString = toBinary(s+"\n").toString();
			//String binaryString = "011";
			while(binaryString.length()>0)
			{
				//checking the bit 
				char c = binaryString.charAt(0);
				alpha  = (newImage.getRGB(pixelX,pixelY) >> 24) & 0xFF;
				switch(colorflag %3)
				{
					case 0:
						color = extractRed(newImage,pixelX,pixelY);
						break;
					case 1:
						color = extractGreen(newImage,pixelX,pixelY);
						break;
					case 2:
						color = extractBlue(newImage,pixelX,pixelY);
						break;
				}
				if(c == '0')				//color needs to be even
				{
					if(color % 2 == 1)		//color is odd
					{
						if(color==255)
							color-=1;
						else
							color+=1;
					}
					else					//color is already even
					{
						//nothing
					}
				}
				else						//color needs to be odd
				{
					if(color % 2 ==0)
					{
						if(color==255)
							color-=1;
						else
							color+=1;
					}
					else
					{
						//nothing
					}
				}

				int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
				newImage.setRGB(pixelX,pixelY,newPixel);
				try
				{
					ImageIO.write(newImage,file[1],my_file);
				}
				catch(IOException e){}


				//creating the new pixel and adding to the new image
				if(colorflag>=3 && colorflag % 3 ==0)
				{
					pixelX++;
					if(pixelX==width)
					{
						pixelX = 0;
						pixelY++;
					}
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
			//System.out.println("end of file. add 0's byte");
			//getting the argb value of the pixel
			colorRed   = extractRed(newImage,pixelX,pixelY);
			colorGreen = extractGreen(newImage,pixelX,pixelY);
			colorBlue  = extractBlue(newImage,pixelX,pixelY);
			alpha      = (newImage.getRGB(pixelX,pixelY) >> 24) & 0xFF;
			//encoding the bit to the pixel
			switch(colorflag % 3)
			{
					case 0: //red
						//System.out.println("RED " + colorRed);
						if(colorRed % 2 ==0)		//colorRed is even
						{
							colorRed+=0;
						}
						else						//colorRed is odd
						{
							if(colorRed==255)
								colorRed--;
							else
								colorRed++;
						}
						//System.out.println("RED " + colorRed);

						colorflag++;
						break;
					case 1: //green
						//System.out.println("GREEN " + colorGreen);
						if(colorGreen % 2 ==0)		//colorRed is even
						{
							colorGreen+=0;
						}
						else						//colorRed is odd
						{
							if(colorGreen==255)
								colorGreen--;
							else
								colorGreen++;
						}
						colorflag++;
						//System.out.println("GREEN " + colorGreen);
						break;
					case 2: //blue
						//System.out.println("BLUE " + colorBlue);
						if(colorBlue % 2 ==0)		//colorRed is even
						{
							colorBlue+=0;
						}
						else						//colorRed is odd
						{
							if(colorBlue==255)
								colorBlue--;
							else
								colorBlue++;
						}
						colorflag++;
						//System.out.println("BLUE " + colorBlue);
						break;
			}
			//creating the new pixel and adding to the new image
			if(colorflag>=3 && colorflag % 3 ==0)
			{
					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					newImage.setRGB(pixelX,pixelY,newPixel);
					try
					{
						ImageIO.write(newImage,file[1],my_file);
					}
					catch(IOException e){}
					pixelX++;
					if(pixelX==width)
					{
						pixelX = 0;
						pixelY++;
					}
					int temp  = newImage.getRGB(pixelX,pixelY);

			}
			else
			{

					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					newImage.setRGB(pixelX,pixelY,newPixel);
					try
					{
						ImageIO.write(newImage,file[1],my_file);
					}
					catch(IOException e){}
					int temp  = newImage.getRGB(pixelX,pixelY);

			}
		}
		try
		{
			ImageIO.write(newImage,file[1],my_file);
		}
		catch(IOException e){}
	}

	public static void encode(String image, String message, String output)
	{
		int pixelX  = 0;
		int pixelY  = 0;
		int colorflag    = 0; 			// colorflag % 3  = 0 red 1 green 2 blue
		boolean flag = false;					// flag 0 means even flag 1 means odd
		BufferedImage newImage = null;
		Scanner sc = null;
		File my_file =new File(output);
		int colorRed;
		int colorGreen;
		int colorBlue;
		int alpha;
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
			String binaryString = toBinary(s+"\n").toString();
			//String binaryString = "011";
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
				colorRed   = extractRed(newImage,pixelX,pixelY);
				colorGreen = extractGreen(newImage,pixelX,pixelY);
				colorBlue  = extractBlue(newImage,pixelX,pixelY);
				alpha      = (newImage.getRGB(pixelX,pixelY) >> 24) & 0xFF;
				//encoding the bit to the pixel
				//System.out.println(c);
				System.out.println("OLD RED " + colorRed);
				System.out.println("OLD GREEN " + colorGreen);
				System.out.println("OLD BLUE " + colorBlue);
				switch(colorflag % 3)
				{
					case 0: //red
						if(colorRed % 2 ==0)		//colorRed is even
						{
							if(flag)				//bit is a 1 so colorRed needs to be odd
							{
								if(colorRed==255)
									colorRed--;
								else
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
								if(colorRed==255)
									colorRed--;
								else
									colorRed++;
							}

						}
						System.out.println("NEW RED " + colorRed);
						System.out.println("GREEN " + colorGreen);
						System.out.println("BLUE " + colorBlue);
						colorflag++;
						break;
					case 1: //green
						//System.out.println("GREEN " + colorGreen);
						if(colorGreen % 2 ==0)		//colorGreen is even
						{
							if(flag)				//bit is a 1 so colorGreen needs to be odd
							{
								if(colorGreen==255)
									colorGreen--;
								else
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
								if(colorGreen==255)
									colorGreen--;
								else
									colorGreen++;
							}

						}
						colorflag++;
						//System.out.println("GREEN " + colorGreen);
						System.out.println("RED " + colorRed);
						System.out.println("NEW GREEN " + colorGreen);
						System.out.println("BLUE " + colorBlue);
						break;
					case 2: //blue
						//System.out.println("BLUE " + colorBlue);
						if(colorBlue % 2 ==0)		//colorBlue is even
						{
							if(flag)				//bit is a 1 so colorBlue needs to be odd
							{
								if(colorBlue==255)
									colorBlue--;
								else
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
								if(colorBlue==255)
									colorBlue--;
								else
									colorBlue++;
							}

						}
						colorflag++;
						//System.out.println("BLUE " + colorBlue);
						System.out.println("RED " + colorRed);
						System.out.println("GREEN " + colorGreen);
						System.out.println("NEW BLUE " + colorBlue);
						break;
				}
				//creating the new pixel and adding to the new image
				if(colorflag>=3 && colorflag % 3 ==0)
				{
					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					System.out.println((newPixel >> 16) & 0xff);
					System.out.println((newPixel >> 8) & 0xff);
					System.out.println((newPixel ) & 0xff);
					System.out.println(newPixel);
					System.out.println();
					newImage.setRGB(pixelX,pixelY,newPixel);
					//System.out.println(pixelX+" "+width);
					pixelX++;
					if(pixelX==width)
					{
						pixelX = 0;
						pixelY++;
					}
					//System.out.println();
					int temp  = newImage.getRGB(pixelX,pixelY);
					System.out.println(temp);
					System.out.println((temp >> 16) & 0xff);
					System.out.println((temp >> 8) & 0xff);
					System.out.println((temp ) & 0xff);
					System.out.println();
				}
				else
				{

					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					newImage.setRGB(pixelX,pixelY,newPixel);
					try
					{
						ImageIO.write(newImage,file[1],my_file);
					}
					catch(IOException e){}
					int temp  = newImage.getRGB(pixelX,pixelY);
					System.out.println((temp >> 16) & 0xff);
					System.out.println((temp >> 8) & 0xff);
					System.out.println((temp ) & 0xff);
					System.out.println();
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
			//System.out.println("end of file. add 0's byte");
			//getting the argb value of the pixel
			colorRed   = extractRed(newImage,pixelX,pixelY);
			colorGreen = extractGreen(newImage,pixelX,pixelY);
			colorBlue  = extractBlue(newImage,pixelX,pixelY);
			alpha      = (newImage.getRGB(pixelX,pixelY) >> 24) & 0xFF;
			//encoding the bit to the pixel
			switch(colorflag % 3)
			{
					case 0: //red
						//System.out.println("RED " + colorRed);
						if(colorRed % 2 ==0)		//colorRed is even
						{
							colorRed+=0;
						}
						else						//colorRed is odd
						{
							if(colorRed==255)
								colorRed--;
							else
								colorRed++;
						}
						//System.out.println("RED " + colorRed);
						System.out.println("RED " + colorRed);
						System.out.println("GREEN " + colorGreen);
						System.out.println("BLUE " + colorBlue);
						colorflag++;
						break;
					case 1: //green
						//System.out.println("GREEN " + colorGreen);
						if(colorGreen % 2 ==0)		//colorRed is even
						{
							colorGreen+=0;
						}
						else						//colorRed is odd
						{
							if(colorGreen==255)
								colorGreen--;
							else
								colorGreen++;
						}
						colorflag++;
						//System.out.println("GREEN " + colorGreen);
						System.out.println("RED " + colorRed);
						System.out.println("GREEN " + colorGreen);
						System.out.println("BLUE " + colorBlue);
						break;
					case 2: //blue
						//System.out.println("BLUE " + colorBlue);
						if(colorBlue % 2 ==0)		//colorRed is even
						{
							colorBlue+=0;
						}
						else						//colorRed is odd
						{
							if(colorBlue==255)
								colorBlue--;
							else
								colorBlue++;
						}
						colorflag++;
						//System.out.println("BLUE " + colorBlue);
						System.out.println("RED " + colorRed);
						System.out.println("GREEN " + colorGreen);
						System.out.println("BLUE " + colorBlue);
						break;
			}
			//creating the new pixel and adding to the new image
			if(colorflag>=3 && colorflag % 3 ==0)
			{
					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					newImage.setRGB(pixelX,pixelY,newPixel);
					try
					{
						ImageIO.write(newImage,file[1],my_file);
					}
					catch(IOException e){}
					pixelX++;
					if(pixelX==width)
					{
						pixelX = 0;
						pixelY++;
					}
					int temp  = newImage.getRGB(pixelX,pixelY);
					System.out.println((temp >> 16) & 0xff);
					System.out.println((temp >> 8) & 0xff);
					System.out.println((temp ) & 0xff);
					System.out.println();
			}
			else
			{

					int newPixel = (alpha << 24) | (colorRed<<16) | (colorGreen<<8) | colorBlue;
					newImage.setRGB(pixelX,pixelY,newPixel);
					try
					{
						ImageIO.write(newImage,file[1],my_file);
					}
					catch(IOException e){}
					int temp  = newImage.getRGB(pixelX,pixelY);
					System.out.println((temp >> 16) & 0xff);
					System.out.println((temp >> 8) & 0xff);
					System.out.println((temp ) & 0xff);
					System.out.println();
			}
		}
		try
		{
			ImageIO.write(newImage,file[1],my_file);
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
		int colorRed;
		int colorGreen;
		int colorBlue;
		//System.out.println(output);
		try
		{
			newImage = ImageIO.read(new File(image));
			newFile  = new FileWriter(output);
		}
		catch(IOException e){}
		for(int i = 0;i<10;++i)
		{
			//System.out.print("*");
			for(int j=0;j<10;++j)
			{
				colorRed   = extractRed(rgb,j,i);
				colorGreen = extractGreen(rgb,j,i);
				colorBlue  = extractBlue(rgb,j,i);


				//Red's bit
				if(!endofFile)
				{
					int temp = colorRed%2;
					if(temp==0)
					{
						zeroCount++;
						if(zeroCount == 8)
						{
							endofFile = true;
							break;
						}
						else
						{
							sbyte+=(""+temp);
							if(sbyte.length()==8)
							{
								char c = (char)Integer.parseInt(sbyte,2);
								//System.out.println(c);
								out+=c;
								sbyte = "";
							}
						}
					}
					else
					{
						zeroCount = 0;
						sbyte+=(""+temp);
							if(sbyte.length()==8)
							{
								char c = (char)Integer.parseInt(sbyte,2);
								//System.out.println(c);
								out+=c;
								sbyte = "";
							}

					}
				}
				//System.out.println(sbyte);
				//Green's bit
				if(!endofFile)
				{
					int temp = colorGreen%2;
					if(temp==0)
					{
						zeroCount++;
						if(zeroCount == 8)
						{
							endofFile = true;
							break;
						}
						else
						{
							sbyte+=(""+temp);
							if(sbyte.length()==8)
							{
								char c = (char)Integer.parseInt(sbyte,2);
								//System.out.println(c);
								out+=c;
								sbyte = "";
							}
						}
					}
					else
					{
						zeroCount = 0;
						sbyte+=(""+temp);
							if(sbyte.length()==8)
							{
								char c = (char)Integer.parseInt(sbyte,2);
								//System.out.println(c);
								out+=c;
								sbyte = "";
							}

					}
				}
				//System.out.println(sbyte);
				//Blue's bit
				if(!endofFile)
				{
					int temp = colorBlue%2;
					if(temp==0)
					{
						zeroCount++;
						if(zeroCount == 8)
						{
							endofFile = true;
							break;
						}
						else
						{
							sbyte+=(""+temp);
							if(sbyte.length()==8)
							{
								char c = (char)Integer.parseInt(sbyte,2);
								//System.out.println(c);
								out+=c;
								sbyte = "";
							}
						}
					}
					else
					{
						zeroCount = 0;
						sbyte+=(""+temp);
							if(sbyte.length()==8)
							{
								char c = (char)Integer.parseInt(sbyte,2);
								//System.out.println(c);
								out+=c;
								sbyte = "";
							}

					}
				}
				//System.out.println(sbyte);

			}	
		}
		try
		{
			newFile.write(out);
			//System.out.println(out);
		}
		catch(IOException e)
		{
			System.out.println("fail to write");
		}
		

	}
	public static int extractRed(BufferedImage temp, int x, int y)
	{
		int p = temp.getRGB(x,y);
		return (p >> 16) & 0xff;
	}

	public static int extractGreen(BufferedImage temp, int x, int y)
	{
		int p = temp.getRGB(x,y);
		return (p >> 8) & 0xff;
	}

	public static int extractBlue(BufferedImage temp, int x, int y)
	{
		int p = temp.getRGB(x,y);
		return (p) & 0xff;
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






















