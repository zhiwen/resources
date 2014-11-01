package com.resources.web.controller.index;

import org.springframework.web.servlet.view.velocity.VelocityToolboxView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

public class IndexVelocityViewResolver extends VelocityViewResolver {

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        if (this.getViewClass().isAssignableFrom(VelocityToolboxView.class)) {
            this.setViewClass(IndexVelocityToolboxView.class);
        }
    }
}
