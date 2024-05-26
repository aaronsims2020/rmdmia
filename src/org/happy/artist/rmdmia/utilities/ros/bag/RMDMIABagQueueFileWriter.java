package org.happy.artist.rmdmia.utilities.ros.bag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/** org.happy.artist.rmdmia.utilities.ros.RMDMIABagOutputStream - Create, and add
 *  ROS messages in the ROS RMDMIABagOutputStream format to a bag document. Output Plugin framework
 *  will support output of ROS Bags to filesystem, database, web service, or 
 *  any other number of bag file providers.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class RMDMIABagQueueFileWriter extends ByteArrayRecordQueue
{ 
    // getInstance method variables.
    private static RMDMIABagQueueFileWriter singleton;
// createHexField method variables
    private int field_length;
    StringBuilder hex = new StringBuilder();
    private int str_field_length;
    private StringBuilder str_hex = new StringBuilder();
    private byte[] str_field_length_bytes = new byte[4];       
  // createChunkHeader method variables  
    // Chunk compression key array
    private final static char[] CHUNK_COMPRESSION_KEY={'6','3','6','f','6','d','7','0','7','2','6','5','7','3','7','3','6','9','6','f','6','e'};
    // Chunk size key array
    private final static char[] CHUNK_SIZE_KEY={'7','3','6','9','7','a','6','5'};
    // 
    private StringBuilder chunk_header_sb = new StringBuilder();
    // Chunk Size
    private byte[] chunk_size_array = new byte[4];
    private int chunk_size;
    /** Chunk header compression type none. */
    public final static int CHUNK_COMPRESSION_NONE = 0;
    private final static char[] HEX_CHUNK_COMPRESSION_NONE = {'6','e','6','f','6','e','6','5'};
    /** Chunk header compression type bz2. */
    public final static int CHUNK_COMPRESSION_BZ2 = 1;
    private final static char[] HEX_CHUNK_COMPRESSION_BZ2 = {'6','2','7','a','3','2'};    
      
// padBagHeader method variables    
    private StringBuilder padding_sb = new StringBuilder();
    private int padding_length;
    private byte[] padding_length_bytes = new byte[4];    
// createBagHeader method variables    
    // Header field variables.
    private char[] hex_field_name;
    private char[] hex_field_value;
    private byte[] field_length_bytes = new byte[4];
    private final static char[] HEX_EQUALS = {'3','D'}; 
    // Message Data Header Record Type     
    private final static char[] FIELD_RECORD_TYPE_OP2 = {'0','0','0','0','0','0','0','8','6','f','7','0','3','D','0','2'};     
    // BagOutputStream Header Record Type 
    private final static char[] FIELD_RECORD_TYPE_OP3 = {'0','0','0','0','0','0','0','8','6','f','7','0','3','D','0','3'};    
    // Index Data Header Record Type     
    private final static char[] FIELD_RECORD_TYPE_OP4 = {'0','0','0','0','0','0','0','8','6','f','7','0','3','D','0','4'};     
    // Chunk Header Record Type 
    private final static char[] FIELD_RECORD_TYPE_OP5 = {'0','0','0','0','0','0','0','8','6','f','7','0','3','D','0','5'};
    // Chunk Info Header Record Type 
    private final static char[] FIELD_RECORD_TYPE_OP6 = {'0','0','0','0','0','0','0','8','6','f','7','0','3','D','0','6'};
    // Connection Header Record Type 
    private final static char[] FIELD_RECORD_TYPE_OP7 = {'0','0','0','0','0','0','0','8','6','f','7','0','3','D','0','7'};
    // Hex character arrays for space. 
    private final static char[] HEX_CHAR_ARRAY_SPACE = {'2','0'}; 
    // BagOutputStream Header Length 4096
    private final static int BAG_HEADER_LENGTH=4096;
    
    // BagOutputStream Header StringBuilder set to a length of 4096 pad with {'2','0'}
    private StringBuilder bag_header_sb = new StringBuilder(8192);
    // Index position length array
    private byte[] index_pos_length_array = new byte[8];
    // Connection count array
    private byte[] connection_count_array = new byte[4];   
    // Chunk count array
    private byte[] chunk_count_array = new byte[4];  
    // index pos length key array
    private final static char[] INDEX_POS_LENGTH_KEY={'6','9','6','e','6','4','6','5','7','8','5','f','7','0','6','f','7','3'};
    // Connection count key array
    private final static char[] CONN_COUNT_KEY={'6','3','6','f','6','e','6','e','5','f','6','3','6','f','7','5','6','e','7','4'};
    // Chunk count key array
    private final static char[] CHUNK_COUNT_KEY={'6','3','6','8','7','5','6','e','6','b','5','f','6','3','6','f','7','5','6','e','7','4'};
    private int sb_header_length;
    
// addMessage method variables    
    // Message Data Header StringBuilder
    private StringBuilder message_data_header_sb = new StringBuilder();
    // Message data time array
    private byte[] message_data_time_array = new byte[8];
    // Message data conn array
    private byte[] message_data_conn_array = new byte[4];   
    // Message data time key array
    private final static char[] MESSAGE_DATA_TIME_KEY={'7','4','6','9','6','d','6','5'};
    // Message data conn key array
    private final static char[] MESSAGE_DATA_CONN_KEY={'6','3','6','f','6','e','6','e'};    
// addConnectionHeader variables    
    // Connection Header key array
    private final static char[] CONNECTION_CONN_KEY={'6','3','6','f','6','e','6','e'};
    // Connection Header Topic key array
    private final static char[] CONNECTION_TOPIC_KEY={'7','4','6','f','7','0','6','9','6','3'};
    // 
    private StringBuilder connection_header_sb = new StringBuilder();
    // Connection ID little endian int 4 byte
    private byte[] conn_array = new byte[4];
    private int conn;    
// start method variables.    
        // First line of the ROS BagOutputStream file (includes line separator). Append this line in a StringBuilder object.
    private final static char[] bag_format_version={'2','3','5','2','4','f','5','3','4','2','4','1','4','7','2','0','5','6','3','2','2','e','3','0','0','a'};
    // StringBuilder for appending Records sequentially
    private StringBuilder sb = new StringBuilder();
    // 0 value 4 byte array
    private final static char[] ZERO_VALUE_INT_BYTES={'0','0','0','0','0','0','0','0'};
    private File file;
    private FileOutputStream out;
    
    public RMDMIABagQueueFileWriter()
    {
        // Set File Write Frequency to every 5 ms.
        super(5);
    }
    
    public void start(String file_path, int COMPRESSION_TYPE)
    {
        try 
        {
            setName("ROS_BAG_FILE_WRITER_THREAD");
            start();
            this.file = new File(file_path);
            this.out = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) 
            {
                file.createNewFile();
            }
            
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        // Step 1: Append the bag format version to start the bag document.
        sb.append(bag_format_version);
        // Step 2: Add BagOutputStream Header - initialize all header values with 0
        sb.append(createBagHeader(0, 0, 0));
        // Step 3: Get the Chunk Header (specify compression on file.)
        if(COMPRESSION_TYPE==RMDMIABagQueueFileWriter.CHUNK_COMPRESSION_BZ2)
        {
            System.out.println("BZip2 not implemented. Defaulting to none.");
            // TODO: Probably need to generate a bzip2 chunk before chunk size is specified? Need to research.
            sb.append(createChunkHeader(COMPRESSION_TYPE, 0));            
        // Step 4: Generate the Chunk Record Data, - A 4 byte 0 for Compression Type none.  
            // TODO: verify if 0 byte data length is incorrect for type bz2...
            sb.append(ZERO_VALUE_INT_BYTES);
        }
        else
        {
            sb.append(createChunkHeader(COMPRESSION_TYPE, 0));
        // Step 4: Generate the Chunk Record Data, - A 4 byte 0 for Compression Type none.
            sb.append(ZERO_VALUE_INT_BYTES);
        }
        try
        {
            // Add bytes to queue.
           add(HexToBytes.hexToBytes(sb.toString()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Clear the StringBuilder        
            sb.delete(0, sb.length());      
        }        
    }
    
    /** Call when recording is done. */
    public void end() throws IOException
    {
        // Stop the Thread.
        halt();
 //       System.out.println("getState():" + getState());
        try 
        {
            if (out != null) 
            {
                out.flush();
                out.close();
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }    

    /** Return singleton instance of RMDMIABagRandomAccessFile. */
    public static RMDMIABagQueueFileWriter getInstance()
    {
        if(singleton==null)
        {
            RMDMIABagQueueFileWriter.singleton = new RMDMIABagQueueFileWriter();
        }
        return RMDMIABagQueueFileWriter.singleton;
    }
 
    /** Add connection header Hex in a String. */
    public synchronized void addConnectionHeader(char[] topic, int conn, char[] connection_header_hex) throws IOException
    {
        // clear the current StringBuilder
        connection_header_sb.delete(0, connection_header_sb.length());
        // Create Connection Header
        // op7 header field
        connection_header_sb.append(FIELD_RECORD_TYPE_OP7);
        // connection topic field
        connection_header_sb.append(createHexField(CONNECTION_TOPIC_KEY, RMDMIABagQueueFileWriter.stringToHex(String.valueOf(topic))));       
        // connection conn id header field
        // connection conn id int to little-endian long - 4` bytes
        conn_array[0] = (byte)((conn) & 0xff);
        conn_array[1] = (byte)((conn >> 8) & 0xff);  
        conn_array[2] = (byte)((conn >> 16) & 0xff);  
        conn_array[3] = (byte)((conn >> 24) & 0xff);          
        connection_header_sb.append(createHexField(CONNECTION_CONN_KEY, RMDMIABagQueueFileWriter.bytesToHex(conn_array)));
        
        // Re-using sb_header_length (this may need to be separated in the future... If it is called in an non-synchronous manner.
        this.sb_header_length=connection_header_sb.length()/2;
        conn_array[0] = (byte)((sb_header_length) & 0xff);
        conn_array[1] = (byte)((sb_header_length >> 8) & 0xff);  
        conn_array[2] = (byte)((sb_header_length >> 16) & 0xff);  
        conn_array[3] = (byte)((sb_header_length >> 24) & 0xff);    
        // insert the header length into the header
        connection_header_sb.insert(0,RMDMIABagQueueFileWriter.bytesToHex(conn_array)); 
        // Append Connection Message Header Data to Connection Header 
        connection_header_sb.append(connection_header_hex);        
        add(HexToBytes.hexToBytes(connection_header_sb.toString()));
    }
 
    /** Return the message data header Hex in a String. message data must be in ROS Serialization format. */
    public synchronized void addMessage(long time, int conn, char[] message_data_hex) throws IOException
    {
        // clear the current StringBuilder
        message_data_header_sb.delete(0, message_data_header_sb.length());
        // Create Message Data Header
        // op2 header field
        message_data_header_sb.append(FIELD_RECORD_TYPE_OP2);
        // time header field
        // long time to little-endian long - 8 bytes
        message_data_time_array[0] = (byte)((time) & 0xff);
        message_data_time_array[1] = (byte)((time >> 8) & 0xff);  
        message_data_time_array[2] = (byte)((time >> 16) & 0xff);  
        message_data_time_array[3] = (byte)((time >> 24) & 0xff);
        message_data_time_array[4] = (byte)((time >> 32) & 0xff);
        message_data_time_array[5] = (byte)((time >> 40) & 0xff);  
        message_data_time_array[6] = (byte)((time >> 48) & 0xff);  
        message_data_time_array[7] = (byte)((time >> 56) & 0xff);  
        message_data_header_sb.append(createHexField(MESSAGE_DATA_TIME_KEY, RMDMIABagQueueFileWriter.bytesToHex(message_data_time_array)));
        // conn header field
        // int conn to little-endian integer - 4 bytes
        message_data_conn_array[0] = (byte)((conn) & 0xff);
        message_data_conn_array[1] = (byte)((conn >> 8) & 0xff);  
        message_data_conn_array[2] = (byte)((conn >> 16) & 0xff);  
        message_data_conn_array[3] = (byte)((conn >> 24) & 0xff);        
        message_data_header_sb.append(createHexField(MESSAGE_DATA_CONN_KEY, RMDMIABagQueueFileWriter.bytesToHex(message_data_conn_array)));
        
        // Re-using sb_header_length (this may need to be separated in the future... If it is called in an non-synchronous manner.
        this.sb_header_length=message_data_header_sb.length()/2;
        message_data_conn_array[0] = (byte)((sb_header_length) & 0xff);
        message_data_conn_array[1] = (byte)((sb_header_length >> 8) & 0xff);  
        message_data_conn_array[2] = (byte)((sb_header_length >> 16) & 0xff);  
        message_data_conn_array[3] = (byte)((sb_header_length >> 24) & 0xff);    
        // insert the header length into the header
        message_data_header_sb.insert(0,RMDMIABagQueueFileWriter.bytesToHex(message_data_conn_array)); 
        // append message_data
        message_data_header_sb.append(message_data_hex);
        add(HexToBytes.hexToBytes(message_data_header_sb.toString()));
    } 
    
    /** Return the bag header Hex in a String. */
    private String createBagHeader(long index_pos_length, int conn_count, int chunk_count)
    {
        // clear the current StringBuilder
        bag_header_sb.delete(0, bag_header_sb.length());
        // Create BagOutputStream Header
        // op3 header field
        bag_header_sb.append(FIELD_RECORD_TYPE_OP3);
        // index_pos header field
        // index position length to little-endian long - 8 bytes
        index_pos_length_array[0] = (byte)((index_pos_length) & 0xff);
        index_pos_length_array[1] = (byte)((index_pos_length >> 8) & 0xff);  
        index_pos_length_array[2] = (byte)((index_pos_length >> 16) & 0xff);  
        index_pos_length_array[3] = (byte)((index_pos_length >> 24) & 0xff);
        index_pos_length_array[4] = (byte)((index_pos_length >> 32) & 0xff);
        index_pos_length_array[5] = (byte)((index_pos_length >> 40) & 0xff);  
        index_pos_length_array[6] = (byte)((index_pos_length >> 48) & 0xff);  
        index_pos_length_array[7] = (byte)((index_pos_length >> 56) & 0xff);  
        bag_header_sb.append(createHexField(INDEX_POS_LENGTH_KEY, RMDMIABagQueueFileWriter.bytesToHex(index_pos_length_array)));
        // connection count header field
        // index position length to little-endian integer - 4 bytes
        connection_count_array[0] = (byte)((conn_count) & 0xff);
        connection_count_array[1] = (byte)((conn_count >> 8) & 0xff);  
        connection_count_array[2] = (byte)((conn_count >> 16) & 0xff);  
        connection_count_array[3] = (byte)((conn_count >> 24) & 0xff);        
        bag_header_sb.append(createHexField(CONN_COUNT_KEY, RMDMIABagQueueFileWriter.bytesToHex(connection_count_array)));
        // chunk count header field
        // chunk count to little-endian long - 4` bytes - reuses connection_count_array since it was already copied to StringBuilder.
        connection_count_array[0] = (byte)((chunk_count) & 0xff);
        connection_count_array[1] = (byte)((chunk_count >> 8) & 0xff);  
        connection_count_array[2] = (byte)((chunk_count >> 16) & 0xff);  
        connection_count_array[3] = (byte)((chunk_count >> 24) & 0xff);          
        bag_header_sb.append(createHexField(CHUNK_COUNT_KEY, RMDMIABagQueueFileWriter.bytesToHex(connection_count_array)));

        // Insert the header length minus the padding
        this.sb_header_length=bag_header_sb.length()/2;
        connection_count_array[0] = (byte)((sb_header_length) & 0xff);
        connection_count_array[1] = (byte)((sb_header_length >> 8) & 0xff);  
        connection_count_array[2] = (byte)((sb_header_length >> 16) & 0xff);  
        connection_count_array[3] = (byte)((sb_header_length >> 24) & 0xff);    
        // insert the header length into the header
        bag_header_sb.insert(0,RMDMIABagQueueFileWriter.bytesToHex(connection_count_array));      
        
        //Pad BagOutputStream Header with Hex 0x20
        padBagHeader();
        return bag_header_sb.toString();
    }
    
    /** Pad the BagOutputStream Header StringBuilder HEX_CHAR_ARRAY_SPACE. */
    private void padBagHeader() 
    {
        // clear the current StringBuilder
        padding_sb.delete(0, padding_sb.length());
        while (bag_header_sb.length()+padding_sb.length() < 8192) 
        {
            padding_sb.append(HEX_CHAR_ARRAY_SPACE);
        }
        // append 4 byte int length little endian of padding to bag_header_sb
        
        this.padding_length=padding_sb.length()/2;
        padding_length_bytes[0] = (byte)((padding_length) & 0xff);
        padding_length_bytes[1] = (byte)((padding_length >> 8) & 0xff);  
        padding_length_bytes[2] = (byte)((padding_length >> 16) & 0xff);  
        padding_length_bytes[3] = (byte)((padding_length >> 24) & 0xff);
        // append 4 byte little endian integer length of padding bytes.
        bag_header_sb.append(RMDMIABagQueueFileWriter.bytesToHex(padding_length_bytes));                
        // append padding
        bag_header_sb.append(padding_sb.toString());
    }  
      
    /** Return the chunk header Hex in a String. */
    private String createChunkHeader(int compression_type, int size)
    {
        // clear the current StringBuilder
        chunk_header_sb.delete(0, chunk_header_sb.length());
        // Create Chunk Header
        // op5 header field
        chunk_header_sb.append(FIELD_RECORD_TYPE_OP5);
        // chunk compression field
        if(compression_type==RMDMIABagQueueFileWriter.CHUNK_COMPRESSION_BZ2)
        {
            // BZ2 Compression
            chunk_header_sb.append(createHexField(CHUNK_COMPRESSION_KEY, String.valueOf(HEX_CHUNK_COMPRESSION_BZ2))); 
        }
        else 
        {
            // No compression
            chunk_header_sb.append(createHexField(CHUNK_COMPRESSION_KEY, HEX_CHUNK_COMPRESSION_NONE));             
        }
// TODO: Handle Size field based on compression type.        
        
        // chunk size header field
        // chunk size to little-endian long - 4` bytes
        chunk_size_array[0] = (byte)((chunk_size) & 0xff);
        chunk_size_array[1] = (byte)((chunk_size >> 8) & 0xff);  
        chunk_size_array[2] = (byte)((chunk_size >> 16) & 0xff);  
        chunk_size_array[3] = (byte)((chunk_size >> 24) & 0xff);          
        chunk_header_sb.append(createHexField(CHUNK_SIZE_KEY, RMDMIABagQueueFileWriter.bytesToHex(chunk_size_array)));
        // Re-using sb_header_length (this may need to be separated in the future... If it is called in an non-synchronous manner.
        this.sb_header_length=chunk_header_sb.length()/2;
        chunk_size_array[0] = (byte)((sb_header_length) & 0xff);
        chunk_size_array[1] = (byte)((sb_header_length >> 8) & 0xff);  
        chunk_size_array[2] = (byte)((sb_header_length >> 16) & 0xff);  
        chunk_size_array[3] = (byte)((sb_header_length >> 24) & 0xff);    
        // insert the header length into the header
        chunk_header_sb.insert(0,RMDMIABagQueueFileWriter.bytesToHex(chunk_size_array));
        
        return chunk_header_sb.toString();
    }
    
    /** Create a BagOutputStream record header field key/value pair. */
    private String createHexField(char[] hex_field_name, char[] hex_field_value)
    {
        if(hex.length()!=0)
        {
            hex.delete(0, hex.length());
        }
        // length + String to hex + 0x3D + value)
        hex.append(hex_field_name);
        hex.append(HEX_EQUALS);
        hex.append(hex_field_value);
        this.field_length=hex.length();
        field_length_bytes[0] = (byte)((field_length >> 0) & 0xff);
        field_length_bytes[1] = (byte)((field_length >> 8) & 0xff);  
        field_length_bytes[2] = (byte)((field_length >> 16) & 0xff);  
        field_length_bytes[3] = (byte)((field_length >> 24) & 0xff); 
        hex.insert(0, RMDMIABagQueueFileWriter.bytesToHex(field_length_bytes));
        return hex.toString();
    }
    
    /** Create a BagOutputStream record header field key/value pair. */
    private String createHexField(char[] hex_field_name, String hex_field_value)
    {
        if(str_hex.length()!=0)
        {
            str_hex.delete(0, str_hex.length());
        }
        // length + String to hex + 0x3D + value)
        str_hex.append(hex_field_name);
        str_hex.append(HEX_EQUALS);
        str_hex.append(hex_field_value);
        this.str_field_length=str_hex.length();
        str_field_length_bytes[0] = (byte)((str_field_length) & 0xff);
        str_field_length_bytes[1] = (byte)((str_field_length >> 8) & 0xff);  
        str_field_length_bytes[2] = (byte)((str_field_length >> 16) & 0xff);  
        str_field_length_bytes[3] = (byte)((str_field_length >> 24) & 0xff); 
        str_hex.insert(0, RMDMIABagQueueFileWriter.bytesToHex(str_field_length_bytes));
        return str_hex.toString();
    }    
    
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    /** Return a Hex String. */
    private static String stringToHex(String text)
    {
        byte[] buf=text.getBytes();
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return String.valueOf(chars);
    }    
    
    // Need to give credit on this method being from the following URL: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    private static String bytesToHex(byte[] bytes) 
    {
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
    
    public static void main(String[] args)
    {
        RMDMIABagQueueFileWriter bag = new RMDMIABagQueueFileWriter();
        System.out.println("Bag Header: ".concat(bag.createBagHeader(((long)878657565), 256, 7))); 
    // TODO: Implement and test with BZ2 support.
        System.out.println("Chunk Header: ".concat(bag.createChunkHeader(RMDMIABagQueueFileWriter.CHUNK_COMPRESSION_NONE, 0)));
    }      

    @Override
    public void process(byte[] bytes) {
        try 
        {
 //           System.out.println("record bytes:" + bytes.length);
            out.write(bytes);
            out.flush();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(RMDMIABagQueueFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /*
    // BagOutputStream connection count
    private int bag_connection_count=0;
    /** Increment the BagOutputStream Connection Count. */
/*    private void incrementBagConnectionCount()
    {
        // Increment the connection count variable
        this.bag_connection_count = bag_connection_count + 1;
        // TODO: Update the file bytes for the BagOutputStream Header value.
        
    }

    // Chunk connection count
    private int bag_chunk_count=0;    
    */ 
    /** Increment the BagOutputStream Chunk Count. */
  /*  private void incrementBagChunkCount()
    {
        // Increment the chunk count variable
        this.bag_chunk_count = bag_chunk_count + 1;
        // TODO: Update the file bytes for the BagOutputStream Header value.        
    }
    */    
      
/*    
    // Index Data Header StringBuilder
    private StringBuilder index_header_sb = new StringBuilder();
    // Index version array
    private byte[] index_ver_array = new byte[4];
    // Index count array
    private byte[] index_count_array = new byte[4];   
    // Index conn array
    private byte[] index_conn_array = new byte[4];  
    // index version key array
    private final static char[] INDEX_VER_KEY={'7','6','6','5','7','2'};
    // Index count key array
    private final static char[] INDEX_COUNT_KEY={'6','3','6','f','7','5','6','e','7','4'};
    // Index conn key array
    private final static char[] INDEX_CONN_KEY={'6','3','6','f','6','e','6','e'};
*/
    /** Return the index data header Hex in a String. */
/*    public String createIndexDataHeader(int index_ver, int index_count, int index_conn)
    {
        // clear the current StringBuilder
        index_header_sb.delete(0, index_header_sb.length());
        // Create Index Data Header
        // op4 header field
        index_header_sb.append(FIELD_RECORD_TYPE_OP4);
        // ver header field
        // ver int to little-endian long - 8 bytes
        index_ver_array[0] = (byte)((index_ver) & 0xff);
        index_ver_array[1] = (byte)((index_ver >> 8) & 0xff);  
        index_ver_array[2] = (byte)((index_ver >> 16) & 0xff);  
        index_ver_array[3] = (byte)((index_ver >> 24) & 0xff);
 
        index_header_sb.append(createHexField(INDEX_VER_KEY, BagOutputStream.bytesToHex(index_ver_array)));
        // index count header field
        // index count int to little-endian integer - 4 bytes
        index_count_array[0] = (byte)((index_count) & 0xff);
        index_count_array[1] = (byte)((index_count >> 8) & 0xff);  
        index_count_array[2] = (byte)((index_count >> 16) & 0xff);  
        index_count_array[3] = (byte)((index_count >> 24) & 0xff);        
        index_header_sb.append(createHexField(INDEX_COUNT_KEY, BagOutputStream.bytesToHex(index_count_array)));
        // Index conn header field
        // index conn int to little-endian long - 4` bytes
        index_conn_array[0] = (byte)((index_conn) & 0xff);
        index_conn_array[1] = (byte)((index_conn >> 8) & 0xff);  
        index_conn_array[2] = (byte)((index_conn >> 16) & 0xff);  
        index_conn_array[3] = (byte)((index_conn >> 24) & 0xff);          
        index_header_sb.append(createHexField(INDEX_CONN_KEY, BagOutputStream.bytesToHex(index_conn_array)));

        // Re-using sb_header_length (this may need to be separated in the future... If it is called in an non-synchronous manner.
        this.sb_header_length=index_header_sb.length()/2;
        index_conn_array[0] = (byte)((sb_header_length) & 0xff);
        index_conn_array[1] = (byte)((sb_header_length >> 8) & 0xff);  
        index_conn_array[2] = (byte)((sb_header_length >> 16) & 0xff);  
        index_conn_array[3] = (byte)((sb_header_length >> 24) & 0xff);    
        // insert the header length into the header
        index_header_sb.insert(0,BagOutputStream.bytesToHex(index_conn_array));         
        
        return index_header_sb.toString();
    }    
    
    
    // Chunk Info Header StringBuilder
    private StringBuilder chunk_info_header_sb = new StringBuilder();
    // Chunk Info chunk position array
    private byte[] chunk_info_chunk_pos_array = new byte[8];
    // Chunk Info start time array
    private byte[] chunk_info_start_time_array = new byte[8];
    // Chunk Info end time array
    private byte[] chunk_info_end_time_array = new byte[8];
    // Chunk info count array
    private byte[] chunk_info_chunk_count_array = new byte[4];   
    // Chunk info version array
    private byte[] chunk_info_chunk_ver_array = new byte[4];  
    // Chunk info chunk pos key array
    private final static char[] CHUNK_INFO_CHUNK_POS_KEY={'6','3','6','8','7','5','6','e','6','b','5','f','7','0','6','f','7','3'};
    // Chunk info start time key array
    private final static char[] CHUNK_INFO_START_TIME_KEY={'7','3','7','4','6','1','7','2','7','4','5','f','7','4','6','9','6','d','6','5'}; 
    // Chunk info end time key array
    private final static char[] CHUNK_INFO_END_TIME_KEY={'6','5','6','e','6','4','5','f','7','4','6','9','6','d','6','5'};      
    // Chunk info chunk count key array
    private final static char[] CHUNK_INFO_COUNT_KEY={'6','3','6','f','7','5','6','e','7','4'};
    // Chunk info record version array
    private final static char[] CHUNK_INFO_VER_KEY={'7','6','6','5','7','2'};
*/
    /** Return the chunk info header Hex in a String. */
/*    public String createChunkInfoHeader(long chunk_position, long start_time, long end_time, int chunk_count, int chunk_info_record_version)
    {
        // clear the current StringBuilder
        chunk_info_header_sb.delete(0, chunk_info_header_sb.length());
        // Create Chunk Info Header
        // op6 header field
        chunk_info_header_sb.append(FIELD_RECORD_TYPE_OP6);

        // chunk info chunk pos header field
        // chunk position long to little-endian long - 8 bytes
        chunk_info_chunk_pos_array[0] = (byte)((chunk_position) & 0xff);
        chunk_info_chunk_pos_array[1] = (byte)((chunk_position >> 8) & 0xff);  
        chunk_info_chunk_pos_array[2] = (byte)((chunk_position >> 16) & 0xff);  
        chunk_info_chunk_pos_array[3] = (byte)((chunk_position >> 24) & 0xff);
        chunk_info_chunk_pos_array[4] = (byte)((chunk_position >> 32) & 0xff);
        chunk_info_chunk_pos_array[5] = (byte)((chunk_position >> 40) & 0xff);  
        chunk_info_chunk_pos_array[6] = (byte)((chunk_position >> 48) & 0xff);  
        chunk_info_chunk_pos_array[7] = (byte)((chunk_position >> 56) & 0xff);  
        chunk_info_header_sb.append(createHexField(CHUNK_INFO_CHUNK_POS_KEY, BagOutputStream.bytesToHex(chunk_info_chunk_pos_array)));
        
        // chunk info start time long to little-endian long - 8 bytes
        chunk_info_start_time_array[0] = (byte)((start_time) & 0xff);
        chunk_info_start_time_array[1] = (byte)((start_time >> 8) & 0xff);  
        chunk_info_start_time_array[2] = (byte)((start_time >> 16) & 0xff);  
        chunk_info_start_time_array[3] = (byte)((start_time >> 24) & 0xff);
        chunk_info_start_time_array[4] = (byte)((start_time >> 32) & 0xff);
        chunk_info_start_time_array[5] = (byte)((start_time >> 40) & 0xff);  
        chunk_info_start_time_array[6] = (byte)((start_time >> 48) & 0xff);  
        chunk_info_start_time_array[7] = (byte)((start_time >> 56) & 0xff);  
        chunk_info_header_sb.append(createHexField(CHUNK_INFO_START_TIME_KEY, BagOutputStream.bytesToHex(chunk_info_start_time_array)));        

        // chunk info end time long to little-endian long - 8 bytes
        chunk_info_end_time_array[0] = (byte)((end_time) & 0xff);
        chunk_info_end_time_array[1] = (byte)((end_time >> 8) & 0xff);  
        chunk_info_end_time_array[2] = (byte)((end_time >> 16) & 0xff);  
        chunk_info_end_time_array[3] = (byte)((end_time >> 24) & 0xff);
        chunk_info_end_time_array[4] = (byte)((end_time >> 32) & 0xff);
        chunk_info_end_time_array[5] = (byte)((end_time >> 40) & 0xff);  
        chunk_info_end_time_array[6] = (byte)((end_time >> 48) & 0xff);  
        chunk_info_end_time_array[7] = (byte)((end_time >> 56) & 0xff);  
        chunk_info_header_sb.append(createHexField(CHUNK_INFO_END_TIME_KEY, BagOutputStream.bytesToHex(chunk_info_end_time_array)));  
        
        // chunk info chunk count header field
        // chunk count int to little-endian integer - 4 bytes
        chunk_info_chunk_count_array[0] = (byte)((chunk_count) & 0xff);
        chunk_info_chunk_count_array[1] = (byte)((chunk_count >> 8) & 0xff);  
        chunk_info_chunk_count_array[2] = (byte)((chunk_count >> 16) & 0xff);  
        chunk_info_chunk_count_array[3] = (byte)((chunk_count >> 24) & 0xff);        
        chunk_info_header_sb.append(createHexField(CHUNK_INFO_COUNT_KEY, BagOutputStream.bytesToHex(chunk_info_chunk_count_array)));

        // chunk info ver header field
        // chunk info ver int to little-endian long - 4` bytes
        chunk_info_chunk_ver_array[0] = (byte)((chunk_info_record_version) & 0xff);
        chunk_info_chunk_ver_array[1] = (byte)((chunk_info_record_version >> 8) & 0xff);  
        chunk_info_chunk_ver_array[2] = (byte)((chunk_info_record_version >> 16) & 0xff);  
        chunk_info_chunk_ver_array[3] = (byte)((chunk_info_record_version >> 24) & 0xff);          
        chunk_info_header_sb.append(createHexField(CHUNK_INFO_VER_KEY, BagOutputStream.bytesToHex(chunk_info_chunk_ver_array)));

        // Re-using sb_header_length (this may need to be separated in the future... If it is called in an non-synchronous manner.
        this.sb_header_length=chunk_info_header_sb.length()/2;
        chunk_info_chunk_ver_array[0] = (byte)((sb_header_length) & 0xff);
        chunk_info_chunk_ver_array[1] = (byte)((sb_header_length >> 8) & 0xff);  
        chunk_info_chunk_ver_array[2] = (byte)((sb_header_length >> 16) & 0xff);  
        chunk_info_chunk_ver_array[3] = (byte)((sb_header_length >> 24) & 0xff);    
        // insert the header length into the header
        chunk_info_header_sb.insert(0,BagOutputStream.bytesToHex(chunk_info_chunk_ver_array));         
        
        return chunk_info_header_sb.toString();
    }
*/        
    //  Records
    /** Message data header (op=0x02)
     *  The following fields are guaranteed to appear in a message data header,
     *  fields in the format (Field - Description - Format - Length):
     * 
     *  conn - ID for connection on which message arrived - little-endian integer - 4 bytes
     *  time - time at which the message was received - little-endian long integer - 8 bytes
     * 
     *  The data in these records is the serialized message data in the ROS serialization format.
     */    
    
    // Records (first record)
    /** BagOutputStream header record (op=0x03) - The bag header record occurs once in the file as the first record.
     *  The following fields are guaranteed to appear in the bag header record, 
     *  fields in the format (Field - Description - Format - Length):
     * 
     *  index_pos - offset of first record after the chunk section - little-endian long integer -  8 bytes
     *  conn_count - number of unique connections in the file - little-endian integer - 4 bytes
     *  chunk_count - number of chunk records in the file - little-endian integer - 4 bytes
     * 
     *  The bag header record is padded out by filling data with ASCII space 
     *  characters (0x20) so that additional information can be added after the 
     *  bag file is recorded. Currently, this padding is such that the header is 
     *  4096 bytes long.
     */
    
    /** Index data header (op=0x04)
     *  The following fields are guaranteed to appear in an index data header,
     *  fields in the format (Field - Description - Format - Length):
     *  
     *  ver - index data record version - little-endian integer - 4 bytes
     *  conn - connection ID - little-endian integer - 4 bytes
     *  count - number of messages on conn in the preceding chunk - little-endian integer - 4 bytes
     * 
     *  The data in these records depends on the version in the header. 
     *  The current version is version 1, which consists of count repeating occurrences 
     *  of timestamps, chunk record offsets and message offsets, in the format 
     *  (Field - Description - Format - Length):
     * 
     *  time - time at which the message was received - little-endian long integer - 8 bytes
     *  offset - offset of message data record in uncompressed chunk data - little-endian integer - 4 bytes
     */
    
    /** Chunk record (op=0x05)
     *  The following fields are guaranteed to appear in a chunk record,
     *  fields in the format (Field - Description - Format - Length):
     * 
     *  compression - compression type for the data - character string - variable 
     *  size - size in bytes of the uncompressed chunk - little-endian integer - 4 bytes
     * 
     *  The supported compression values are "none" and "bz2". The compressed 
     *  size of the chunk can be found in the data_len field of the record.
     *  The data for a chunk record consists of message data and connection records, 
     *  compressed using the method specified in the chunk record header.
     */    
    
    /** Chunk info header (op=0x06)
     *  The following fields are guaranteed to appear in a chunk info header,
     *  fields in the format (Field - Description - Format - Length):
     * 
     *  ver - chunk info record version - little-endian integer - 4 bytes
     *  chunk_pos - offset of the chunk record - little-endian long integer - 8 bytes
     *  start_time - timestamp of earliest message in the chunk - little-endian long integer - 8 bytes
     *  end_time - timestamp of latest message in the chunk - little-endian long integer - 8 bytes
     *  count - number of connections in the chunk - little-endian integer - 4 bytes
     * 
     *  The data in these records depends on the version in the header. 
     *  The current version is version 1, which consists of count repeating occurrences 
     *  of connection ID's and message counts:
     * 
     *  conn - connection id - little-endian long integer - 4 bytes
     *  count - number of messages that arrived on this connection in the chunk - little-endian long integer - 4 bytes
     */      
    
    /** Connection record header (op=0x07)
     *  The following fields must appear in a connection record header,
     *  fields in the format (Field - Description - Format - Length):
     *  
     *  conn - unique connection ID - little-endian integer - 4 bytes
     *  topic - topic on which the messages are stored - character string - variable
     * 
     *  The data consists of a string containing the connection header in
     *  the same format as a bag record header. The following fields must appear 
     *  in the connection header: topic, type, md5sum, message_definition. Optional 
     *  fields include: callerid, latching.
     * 
     *  Two topic fields exist (in the record and connection headers). This is 
     *  because messages can be written to the bag file on a topic different from
     *  where they were originally published.
     */
  
    
    
}
