/*
 *  Copyright (C) 2011 
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

package recipemix.controllers.exceptions;

/**
 *
 * @author 
 */
public class UserExistsException extends Exception {

    /**
     * Creates a new instance of <code>UserExistsException</code> without detail message.
     */
    public UserExistsException() {
        super("User with the given username already exists");
    }


    /**
     * Constructs an instance of <code>UserExistsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UserExistsException(String msg) {
        super(msg);
    }
}
