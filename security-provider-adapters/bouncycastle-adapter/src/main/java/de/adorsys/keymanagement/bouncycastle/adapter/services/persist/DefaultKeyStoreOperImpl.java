package de.adorsys.keymanagement.bouncycastle.adapter.services.persist;

import de.adorsys.keymanagement.api.keystore.KeyStoreOper;
import de.adorsys.keymanagement.api.metadata.KeyMetadataOper;
import de.adorsys.keymanagement.api.metadata.NoOpMetadataPersistence;
import de.adorsys.keymanagement.api.types.source.KeySet;
import de.adorsys.keymanagement.api.types.template.ProvidedKeyTemplate;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyEntry;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.function.Supplier;

public class DefaultKeyStoreOperImpl implements KeyStoreOper {

    private final KeyMetadataOper metadataOper;

    @Inject
    public DefaultKeyStoreOperImpl(KeyMetadataOper metadataOper) {
        this.metadataOper = metadataOper;
    }

    @Override
    @SneakyThrows
    public KeyStore generate(KeySet keySet) {
        return generate(keySet, () -> null);
    }

    @Override
    @SneakyThrows
    public KeyStore generate(KeySet keySet, Supplier<char[]> defaultPassword) {
        return generate(keySet, defaultPassword, metadataOper);
    }

    @Override
    @SneakyThrows
    public KeyStore generateWithoutMetadata(KeySet keySet) {
        return generate(keySet, () -> null, new NoOpMetadataPersistence());
    }

    @Override
    @SneakyThrows
    public KeyStore generateWithoutMetadata(KeySet keySet, Supplier<char[]> defaultPassword) {
        return generate(keySet, defaultPassword, new NoOpMetadataPersistence());
    }

    @Override
    public String addToKeyStoreAndGetName(KeyStore ks, ProvidedKeyTemplate entry, Supplier<char[]> defaultPassword) {
        if (entry instanceof ProvidedKeyEntry) {
            return doAddToKeyStoreAndGetName(ks, (ProvidedKeyEntry) entry, defaultPassword);
        }
        if (entry instanceof ProvidedKeyPair) {
            return doAddToKeyStoreAndGetName(ks, (ProvidedKeyPair) entry, defaultPassword);
        }
        if (entry instanceof ProvidedKey) {
            return doAddToKeyStoreAndGetName(ks, (ProvidedKey) entry, defaultPassword);
        }
        throw new IllegalArgumentException("Unsupported entry: " + entry.getClass());
    }

    @SneakyThrows
    private KeyStore generate(KeySet keySet, Supplier<char[]> defaultPassword, KeyMetadataOper useMetadataOper) {
        KeyStore ks = KeyStore.getInstance("UBER"); // FIXME - BCFKS from provided load store config
        ks.load(null);

        keySet.getKeyEntries().forEach(it -> {
            String alias = doAddToKeyStoreAndGetName(ks, it, defaultPassword);
            useMetadataOper.persistMetadata(alias, it.getMetadata(), ks);
        });

        keySet.getKeys().forEach(it -> {
            String alias = doAddToKeyStoreAndGetName(ks, it, defaultPassword);
            useMetadataOper.persistMetadata(alias, it.getMetadata(), ks);
        });

        keySet.getKeyPairs().forEach(it -> {
            String alias = doAddToKeyStoreAndGetName(ks, it, defaultPassword);
            useMetadataOper.persistMetadata(alias, it.getMetadata(), ks);
        });

        return ks;
    }

    @SneakyThrows
    private String doAddToKeyStoreAndGetName(KeyStore ks, ProvidedKeyEntry entry, Supplier<char[]> defaultPassword) {
        String name = entry.generateName();
        ks.setEntry(
                name,
                entry.getEntry(),
                getPasswordProtection(entry, defaultPassword)
        );
        return name;
    }

    @SneakyThrows
    private String doAddToKeyStoreAndGetName(KeyStore ks, ProvidedKeyPair pair, Supplier<char[]> defaultPassword) {
        String name = pair.generateName();
        ks.setKeyEntry(
                name,
                pair.getPrivate(),
                getPassword(pair, defaultPassword),
                pair.getCertificates().toArray(new Certificate[0])
        );

        return name;
    }

    @SneakyThrows
    private String doAddToKeyStoreAndGetName(KeyStore ks, ProvidedKey key, Supplier<char[]> defaultPassword) {
        String name = key.generateName();
        KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry((SecretKey) key.getKey());
        ks.setEntry(
                name,
                entry,
                getPasswordProtection(key, defaultPassword)
        );
        return name;
    }

    private KeyStore.PasswordProtection getPasswordProtection(
            ProvidedKeyTemplate template, Supplier<char[]> defaultPassword
    ) {
        return new KeyStore.PasswordProtection(getPassword(template, defaultPassword));
    }

    private char[] getPassword(ProvidedKeyTemplate key, Supplier<char[]> defaultPassword) {
        if (null == key.getPassword()) {
            char[] defaultPasswordValue = defaultPassword.get();
            if (null == defaultPasswordValue) {
                throw new IllegalArgumentException("Key-password is missing and default key password is not set");
            }

            return defaultPasswordValue;
        }

        return key.getPassword().get();
    }
}
