package org.happy.artist.rmdmia.utilities;

/**
 *
 * @author copied from http://stackoverflow.com/questions/3008043/list-all-files-from-directories-and-subdirectories-in-java
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryReader {

  static int spc_count=-1;
  public static GenericExtFilter filter = new GenericExtFilter(".msg");
  public static void Process(File aFile) {
    spc_count++;
    String spcs = "";
    for (int i = 0; i < spc_count; i++)
      spcs += " ";
    if(aFile.isFile())
    {
        if(aFile.getName().indexOf(".msg")!=-1)
        {
            try {
                System.out.println(spcs + "[FILE] " + aFile.getAbsolutePath());
                System.out.println("md5sum:" + bytesToHex(getMD5Sum(aFile)));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(DirectoryReader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DirectoryReader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DirectoryReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    else if (aFile.isDirectory()) {
//      System.out.println(spcs + "[DIR] " + aFile.getName());
      File[] listOfFiles = aFile.listFiles();
      if(listOfFiles!=null) {
        for (int i = 0; i < listOfFiles.length; i++)
          Process(listOfFiles[i]);
      } else {
        System.out.println(spcs + " [ACCESS DENIED]");
      }
    }
    spc_count--;
  }

  public static void main(String[] args) {
    String nam = "D:/";
    File aFile = new File(nam);
    Process(aFile);
  }

      // Need to give credit on this method being from the following URL: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    public static String bytesToHex(byte[] bytes) {
    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for ( int j = 0; j < bytes.length; j++ ) {
        v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}
  
  public static byte[] getMD5Sum(File file) throws NoSuchAlgorithmException, FileNotFoundException, IOException
  {
      System.out.println("Test: " + file.getAbsolutePath());
      MessageDigest md = MessageDigest.getInstance("MD5");
    InputStream is = new FileInputStream(file.getAbsolutePath());
    try {
      is = new DigestInputStream(is, md);
      // read stream to EOF as normal...
    }
    finally {
      is.close();
    }
    byte[] digest = md.digest();
    return digest;
  }
  
  // Borrowed FileFilter from http://www.mkyong.com/java/how-to-find-files-with-certain-extension-only/
  // inner class, generic extension filter
	public static class GenericExtFilter implements FilenameFilter {
 
		private String ext;
 
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}
 
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}
}