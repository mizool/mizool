package com.github.mizool.core.rsa;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.ConfigurationException;

@UtilityClass
public class RsaKeys
{
    private final Pattern HEADER_FOOTER_AND_LINE_BREAKS = Pattern.compile(
        "(?:-----(?:BEGIN|END).+?(?:PRIVATE|PUBLIC) KEY-----|\\n|\\r)");

    public RSAPrivateKey privateKeyFromPkcs8(String src)
    {
        String strippedSrc = stripHeaderFooterAndLineBreaks(src);
        KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(strippedSrc));
        KeyFactory keyFactory = getKeyFactory();
        return generatePrivateKey(keySpec, keyFactory);
    }

    public RSAPublicKey publicKeyFromX509(String src)
    {
        String strippedSrc = stripHeaderFooterAndLineBreaks(src);
        KeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(strippedSrc));
        KeyFactory keyFactory = getKeyFactory();
        return generatePublicKey(keySpec, keyFactory);
    }

    public RSAPublicKey publicKeyFromPrivateKey(RSAPrivateKey rsaPrivateKey)
    {
        KeySpec keySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(),
            ((RSAPrivateCrtKey) rsaPrivateKey).getPublicExponent());
        KeyFactory keyFactory = getKeyFactory();
        return generatePublicKey(keySpec, keyFactory);
    }

    private RSAPrivateKey generatePrivateKey(KeySpec keySpec, KeyFactory keyFactory)
    {
        RSAPrivateKey result;
        try
        {
            result = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
        catch (InvalidKeySpecException e)
        {
            throw new ConfigurationException("Invalid key specification", e);
        }
        return result;
    }

    private RSAPublicKey generatePublicKey(KeySpec keySpec, KeyFactory keyFactory)
    {
        RSAPublicKey result;
        try
        {
            result = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
        catch (InvalidKeySpecException e)
        {
            throw new ConfigurationException("Invalid key specification", e);
        }
        return result;
    }

    private String stripHeaderFooterAndLineBreaks(String src)
    {
        return HEADER_FOOTER_AND_LINE_BREAKS.matcher(src).replaceAll("");
    }

    private KeyFactory getKeyFactory()
    {
        KeyFactory keyFactory;
        try
        {
            keyFactory = KeyFactory.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new ConfigurationException("RSA algorithm not found", e);
        }
        return keyFactory;
    }
}