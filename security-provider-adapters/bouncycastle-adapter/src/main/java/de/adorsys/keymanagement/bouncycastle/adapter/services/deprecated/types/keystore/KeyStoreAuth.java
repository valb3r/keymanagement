package de.adorsys.keymanagement.bouncycastle.adapter.services.deprecated.types.keystore;

import de.adorsys.keymanagement.bouncycastle.adapter.services.deprecated.types.keystore.exceptions.KeyStoreAuthException;

/**
 * Authorization entity to read keystore or both keystore and key in it.
 */
public class KeyStoreAuth {
    private final ReadStorePassword readStorePassword;
    private final ReadKeyPassword readKeyPassword;

    public KeyStoreAuth(ReadStorePassword readStorePassword, ReadKeyPassword readKeyPassword) {
        this.readStorePassword = readStorePassword;
        this.readKeyPassword = readKeyPassword;
    }

    public ReadStorePassword getReadStorePassword() {
        if (readStorePassword == null) {
            throw new KeyStoreAuthException("Access to READ STORE PASSWORD not allowed.");
        }
        return readStorePassword;
    }

    public ReadKeyPassword getReadKeyPassword() {
        if (readKeyPassword == null) {
            throw new KeyStoreAuthException("Access to READ KEY PASSWORD not allowed");
        }
        return readKeyPassword;
    }

    @Override
    public String toString() {
        return "KeyStoreAuth{" +
                readStorePassword +
                ", " + readKeyPassword +
                '}';
    }
}
