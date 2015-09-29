/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurePasswordUtils
{

    public static String getSecurePassword( String password )
                    throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance( "SHA-256" );
        md.update( password.getBytes() );
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < byteData.length; i++ )
        {
            sb.append( Integer.toString( ( byteData[i] & 0xff ) + 0x100, 16 )
                              .substring( 1 ) );
        }
        StringBuffer hexString = new StringBuffer();
        for ( int i = 0; i < byteData.length; i++ )
        {
            String hex = Integer.toHexString( 0xff & byteData[i] );
            if ( hex.length() == 1 )
                hexString.append( '0' );
            hexString.append( hex );
        }
        return hexString.toString();
    }
}
