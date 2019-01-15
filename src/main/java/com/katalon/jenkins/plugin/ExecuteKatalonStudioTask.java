package com.katalon.jenkins.plugin;

import com.google.common.base.Throwables;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class ExecuteKatalonStudioTask extends Builder {

    private String version;

    private String location;

    private String executeArgs;

    @DataBoundConstructor
    public ExecuteKatalonStudioTask(String version, String location, String executeArgs) {
        this.version = version;
        this.location = location;
        this.executeArgs = executeArgs;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExecuteArgs() {
        return executeArgs;
    }

    public void setExecuteArgs(String executeArgs) {
        this.executeArgs = executeArgs;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> abstractBuild, Launcher launcher, BuildListener buildListener)
            throws InterruptedException, IOException {
        try {
            FilePath workspace = abstractBuild.getWorkspace();

            if (workspace != null) {
                String workspaceLocation = workspace.getRemote();

                if (workspaceLocation != null) {

                    KatalonUtils.executeKatalon(
                            buildListener,
                            this.version,
                            this.location,
                            workspaceLocation,
                            this.executeArgs);

                }
            }

        } catch (Exception e) {
            String stackTrace = Throwables.getStackTraceAsString(e);
            LogUtils.log(buildListener, stackTrace);
        }
        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> { // Publisher because Notifiers are a type of publisher
        @Override
        public String getDisplayName() {
            return "Execute Katalon Studio Tests"; // What people will see as the plugin name in the configs
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true; // We are always OK with someone adding this  as a build step for their job
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
