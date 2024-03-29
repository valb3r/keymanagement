package de.adorsys.keymanagement.api.generator;

import de.adorsys.keymanagement.api.types.KeySetTemplate;
import de.adorsys.keymanagement.api.types.source.KeySet;
import de.adorsys.keymanagement.api.types.template.generated.Encrypting;
import de.adorsys.keymanagement.api.types.template.generated.Secret;
import de.adorsys.keymanagement.api.types.template.generated.Signing;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;

public interface KeyGenerator {

    ProvidedKey secret(Secret template);
    ProvidedKeyPair signing(Signing template);
    ProvidedKeyPair encrypting(Encrypting template);
    KeySet fromTemplate(KeySetTemplate template);
}
