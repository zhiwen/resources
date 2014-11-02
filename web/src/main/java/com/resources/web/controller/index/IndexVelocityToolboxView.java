package com.resources.web.controller.index;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;
import org.springframework.web.util.NestedServletException;

public class IndexVelocityToolboxView extends VelocityToolboxView {

    @Override
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws Exception {

        String targetPath = (String) context.get(IndexManager.buildedTemplatePath);

        if (StringUtils.isBlank(targetPath)) {
            super.mergeTemplate(template, context, response);
            return;
        }

        PrintWriter pw = null;
        FileOutputStream fos = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            pw = new PrintWriter(out);
            template.merge(context, pw);
            pw.flush();

            response.getOutputStream().write(out.toByteArray());

            fos = new FileOutputStream(targetPath);
            fos.write(out.toByteArray());
            fos.flush();
        } catch (MethodInvocationException ex) {
            Throwable cause = ex.getWrappedThrowable();
            throw new NestedServletException("Method invocation failed during rendering of Velocity view with name '"
                                             + getBeanName() + "': " + ex.getMessage() + "; reference ["
                                             + ex.getReferenceName() + "], method '" + ex.getMethodName() + "'",
                                             cause == null ? ex : cause);
        } finally {
            if (null != pw) {
                pw.close();
            }
            if (null != fos) {
                fos.close();
            }
        }
    }
}
