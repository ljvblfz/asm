/***
 * ASM tests
 * Copyright (c) 2002-2004 France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.objectweb.asm.signature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Thomas Hallgren
 */
public class SignatureTest extends TestCase {

  private String line;

  
  public SignatureTest( String name, String line) {
    super(name);
    this.line = line;
  }


  public static Test suite() throws IOException {
    TestSuite suite = new TestSuite();

    InputStream is = SignatureTest.class.getResourceAsStream( "signatures.txt");
    BufferedReader r = new BufferedReader( new InputStreamReader( is));

		String line;
		while((line = r.readLine()) != null) {
			if(line.length() < 2)
				continue;

      suite.addTest( new SignatureTest( "testSignature", line));
		}
		r.close();
    
    return suite;
  }
  

  public void testSignature() throws Exception {
    SignatureWriter wrt = new SignatureWriter();
    // System.out.println( "Parsing: \"" + signature + "\"");
    String signature = line.substring(2);
    SignatureReader rdr = new SignatureReader(signature);

    char type = line.charAt(0);
    switch(type) {
      case 'C':
        rdr.acceptClass(wrt);
        break;
      case 'M':
        rdr.acceptMethod(wrt);
        break;
      case 'T':
        rdr.acceptType(wrt);
        break;
      default:
        fail( "Invalid test type "+type);
    }
    
    assertEquals(signature, wrt.toString());
	}
  
  public String getName() {
    // TODO Auto-generated method stub
    return super.getName() +" "+line;
  }

}
