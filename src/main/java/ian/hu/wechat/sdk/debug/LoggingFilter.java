package ian.hu.wechat.sdk.debug;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Priority(Integer.MIN_VALUE)
public class LoggingFilter implements ContainerRequestFilter, ClientRequestFilter, ContainerResponseFilter, ClientResponseFilter, WriterInterceptor, ReaderInterceptor {

    private static final Log logger = LogFactory.getLog(LoggingFilter.class);

    public void filter(ClientRequestContext context) throws IOException {
        logger.info(String.format("URI: %s", context.getUri().toString()));
        logHttpHeaders(context.getStringHeaders(), "Request");
    }

    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        logHttpHeaders(responseContext.getHeaders(), "Response");
    }

    public void filter(ContainerRequestContext context) throws IOException {
        logHttpHeaders(context.getHeaders(), "Request");
    }

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        logHttpHeaders(responseContext.getStringHeaders(), "Response");
    }

    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        byte[] buffer = IOUtils.toByteArray(context.getInputStream());
        if (buffer.length < 3096) {
            logger.info("The contents of response body is: \n" + new String(buffer, "UTF-8") + "\n");
        } else {
            logger.info("The contents of response body is too long, the first 2048 bytes are: \n" + new String(buffer, 0, 2048, "UTF-8"));
            logger.info("\nand the end 1024 bytes are: \n" + new String(buffer, buffer.length - 1024, 1024, "UTF-8"));
        }
        context.setInputStream(new ByteArrayInputStream(buffer));
        return context.proceed();
    }

    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        OutputStreamWrapper wrapper = new OutputStreamWrapper(context.getOutputStream());
        context.setOutputStream(wrapper);
        context.proceed();
        byte[] buffer = wrapper.getBytes();
        if (buffer.length < 3096) {
            logger.info("The contents of request body is: \n" + new String(buffer, "UTF-8") + "\n");
        } else {
            logger.info("The contents of request body is too long, the first 2048 bytes are: \n" + new String(buffer, 0, 2048, "UTF-8"));
            logger.info("\nand the end 1024 bytes are: \n" + new String(buffer, buffer.length - 1024, 1024, "UTF-8"));
        }
    }

    protected void logHttpHeaders(MultivaluedMap<String, String> headers, String type) {
        StringBuilder msg = new StringBuilder(String.format("The %s HTTP headers are: \n", type));
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            msg.append(entry.getKey()).append(": ");
            for (int i = 0; i < entry.getValue().size(); i++) {
                msg.append(entry.getValue().get(i));
                if (i < entry.getValue().size() - 1) {
                    msg.append(", ");
                }
            }
            msg.append("\n");
        }
        logger.info(msg.toString());
    }

    protected static class OutputStreamWrapper extends OutputStream {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private final OutputStream output;

        private OutputStreamWrapper(OutputStream output) {
            this.output = output;
        }

        @Override
        public void write(int i) throws IOException {
            buffer.write(i);
            output.write(i);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b);
            output.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
            output.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            output.flush();
        }

        @Override
        public void close() throws IOException {
            output.close();
        }

        public byte[] getBytes() {
            return buffer.toByteArray();
        }
    }
}

