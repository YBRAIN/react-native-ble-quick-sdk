package ble.rni;


import android.util.Log;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String SUFFIX_UUID = "-0000-0000-0000-000000000000";
    public static final String PREFIX_UUID = "0000";

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


//+ (NSString *)stringFromHex : (NSData*) hexVal {
    public static String stringFromHex(byte[] dataBuffer) {

        //  const unsigned char *dataBuffer = (const unsigned char *)[hexVal bytes];


        //NSUInteger          dataLength  = [hexVal length];
        int dataLength = dataBuffer.length;

        //NSMutableString     *hexString  = [NSMutableString stringWithCapacity:(dataLength * 2)];
        char[] hexString = new char[dataLength * 2];

        for (int i = 0; i < dataLength; i++) {
            //[hexString appendString:[NSString stringWithFormat:@ "%02x", (unsigned int) dataBuffer[i]]];
            int v = dataBuffer[i] & 0xFF;
            hexString[i * 2] = hexArray[v >>> 4];
            hexString[i * 2 + 1] = hexArray[v & 0x0F];

        }

        //NSLog(@"stringFromHex is  %@", hexString);
        Log.d("","stringFromHex is " + hexString);

        //return [NSString stringWithString:hexString];
        return new String(hexString);
    }


   // + (NSArray *)numberArrayFromHex : (NSData*) hexVal {
    public  static  int [] numberArrayFromHex( byte[] dataBuffer)  {

        //const unsigned char *dataBuffer = (const unsigned char *)[hexVal bytes];

        //NSUInteger          dataLength  = [hexVal length];
        Integer         dataLength  = dataBuffer.length;


        //NSMutableArray* nsarr = [[NSMutableArray alloc] initWithCapacity:1];
        int[] nsarr = 	new int[dataLength];

        for (int i = 0; i < dataLength; ++i)
        {
            //unsigned char val = (unsigned char)dataBuffer[i];
            Byte val = dataBuffer[i];

            //[nsarr addObject:[NSNumber numberWithUnsignedChar:val]];

            nsarr[i] = dataBuffer[i];

        }

//		NSLog(@"numberArrayFromHex : %@", [nsarr componentsJoinedByString:@","]);
        Log.d("", "numberArrayFromHex : " + nsarr );

        return nsarr;
    }

    // only for android use
    public static UUID convert16bitTo128bitUUID(String uuid16bitStr) {

        if (4 == uuid16bitStr.length() ) {
            uuid16bitStr = PREFIX_UUID + uuid16bitStr + SUFFIX_UUID ;
            return UUID.fromString(uuid16bitStr);
        }
        else if(32 == uuid16bitStr.length() )
        {
            return UUID.fromString(uuid16bitStr);
        }

        return null;
    }

    // only for android use
    public static String uuid2Str(UUID uuid) {
        String UUID128bitStr = uuid.toString();

        String regExStr = PREFIX_UUID + "(.{4})" + SUFFIX_UUID;

        Pattern pattern = Pattern.compile(regExStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(UUID128bitStr);
        if (matcher.matches()) {
            return matcher.group(1); // return 16 bit if found
        } else {
            return UUID128bitStr;
        }

    }

}