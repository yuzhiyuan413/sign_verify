package com.bonc.encrypt.interface_encryption.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream buffer;

    private ServletOutputStream out;

    public MyHttpServletResponseWrapper(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        buffer = new ByteArrayOutputStream();
        out = new WrapperOutputStream(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
    }

    public byte[] getData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    public String getContent() throws IOException {
        flushBuffer();
        return buffer.toString();
    }


    class WrapperOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos;

        public WrapperOutputStream(ByteArrayOutputStream bos) {
            this.bos = bos;
        }

        @Override
        public void write(int b) {
            bos.write(b);
        }

        @Override
        public boolean isReady() {

            // TODO Auto-generated method stub
            return false;

        }

        @Override
        public void setWriteListener(WriteListener arg0) {

            // TODO Auto-generated method stub

        }
    }
}
