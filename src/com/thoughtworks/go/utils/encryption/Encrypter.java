/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.utils.encryption;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.h2.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class Encrypter {

    private final String cipherLocation;

    public Encrypter(String cipherLocation) {
        this.cipherLocation = cipherLocation;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: <executing jar> [path-to-go-cipher-file]");
            System.err.println("Example: java -cp goutils.jar com.thoughtworks.go.utils.encryption.Encrypter /etc/go/cipher");
            System.exit(1);
        }
        String cipherLocation = args[0];
        new Encrypter(cipherLocation).process();
    }

    private void process() throws IOException, InvalidCipherTextException {
        if (verifyCipher()) {
            System.out.println("Using cipher file at " + cipherLocation);
            System.out.println("Enter plain text password: ");
            char[] password = System.console().readPassword();
            System.out.println("Confirm plain text password: ");
            char[] confirmPassword = System.console().readPassword();
            if (validateConfirmation(password, confirmPassword)) {
                File cipherFile = new File(cipherLocation);
                String cipherText = cipher(FileUtils.readFileToByteArray(cipherFile), String.valueOf(confirmPassword));
                System.out.println(String.format("Encrypted text for plain text %s is %s", String.valueOf(confirmPassword), cipherText));
            } else {
                System.err.println("Password and confirmation do not match. Aborting...");
            }
        } else {
            throw new RuntimeException("Could not find cipher file at " + cipherLocation);
        }
    }

    private boolean validateConfirmation(char[] password, char[] confirmPassword) {
        return StringUtils.equals(String.valueOf(password), String.valueOf(confirmPassword));
    }

    private boolean verifyCipher() {
        File cipherFile = new File(cipherLocation);
        return cipherFile.exists();
    }

    String cipher(byte[] key, String plainText) throws InvalidCipherTextException {
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()));
        KeyParameter keyParameter = new KeyParameter(Hex.decode(key));
        cipher.init(true, keyParameter);
        byte[] plainTextBytes = plainText.getBytes();
        byte[] cipherTextBytes = new byte[cipher.getOutputSize(plainTextBytes.length)];
        int outputLength = cipher.processBytes(plainTextBytes, 0, plainTextBytes.length, cipherTextBytes, 0);
        cipher.doFinal(cipherTextBytes, outputLength);
        return Base64.encodeBase64String(cipherTextBytes).trim();
    }
}
