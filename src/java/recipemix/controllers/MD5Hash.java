/*
 *  Copyright (C) 2011 Alvaro Monge (amonge at csulb dot edu)
 *  California State University Long Beach (CSULB) ALL RIGHTS RESERVED
 * 
 *  Use of this software is authorized for CSULB students in Dr. Monge's classes, so long
 *  as this copyright notice remains intact. Students must request permission from Dr. Monge
 *  if the code is to be used in other venues outside of Dr. Monge's classes.
 * 
 *  This program is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 */

package recipemix.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro Monge (amonge at csulb dot edu)
 */
public class MD5Hash {

    private static final char[] HEXADECIMAL = { '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * Create the MD5 hash for a password.
     * This method comes from the example described in this blog entry:
     * http://blogs.sun.com/swchan/entry/jdbcrealm_in_glassfish
     * @param password the password String to be hashed
     * @return the MD5 hash of the password
     * @throws Exception
     */
    public static String hashPassword(String password) {
        MessageDigest md = null;
        String hashedPassword;
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();

            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder(2 * bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                int low = (int)(bytes[i] & 0x0f);
                int high = (int)((bytes[i] & 0xf0) >> 4);
                sb.append(HEXADECIMAL[high]);
                sb.append(HEXADECIMAL[low]);
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MD5Hash.class.getName()).log(Level.SEVERE, null, ex);
            hashedPassword = password;
        }

        return hashedPassword;
    }

}
