package ian.hu.wechat.sdk.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 序列化为XML或者从XML反序列化
 */
public class MashallerUtils {
    private static final ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>(64);

    protected static JAXBContext getJaxbContext(Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz should not be null'");
        }
        JAXBContext jaxbContext = jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(clazz);
                jaxbContexts.putIfAbsent(clazz, jaxbContext);
            } catch (JAXBException ex) {
                throw new IllegalArgumentException(
                        "Could not instantiate JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
            }
        }
        return jaxbContext;
    }

    public static Marshaller getMarshaller(Class clazz) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            return jaxbContext.createMarshaller();
        } catch (JAXBException ex) {
            throw new IllegalArgumentException(
                    "Could not create Marshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
        }
    }

    public static Unmarshaller getUnmarshaller(Class clazz) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new IllegalArgumentException(
                    "Could not create Unmarshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deserialize a xml string to T
     *
     * @param xml   Xml string
     * @param clazz T's class
     * @param <T>   Type param
     * @return The instance of T from xml
     * @throws RuntimeException
     */
    public static <T> T fromXml(String xml, Class<T> clazz) {
        try {
            //noinspection unchecked
            return (T) getUnmarshaller(clazz).unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize a entity to xml string
     *
     * @param entity Entity object
     * @return Xml string
     * @throws RuntimeException
     */
    public static String toXml(Object entity) {
        StringWriter sw = new StringWriter();
        try {
            getMarshaller(entity.getClass()).marshal(entity, sw);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }
}
